/**
 * TestManager - test tracking and management system.
 * Copyright (C) 2012  Istvan Pamer
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package testmanager.reporting.service.reporting;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import testmanager.reporting.dao.TestRunDao;
import testmanager.reporting.domain.reporting.ErrorComment;
import testmanager.reporting.domain.reporting.ReportDTO;
import testmanager.reporting.util.DateUtil;

/**
 * The Class DataLifecycleManager. Manages the internal and database data life cycle.
 *
 * @author Istvan_Pamer
 */
public class DataLifecycleManager {

    protected final Log logger = LogFactory.getLog(getClass());

    private static final int SLEEP_TIME = 50;
    private static final int MAX_RETRY = 100;

    @Autowired
    private RunManager runManager;
    @Autowired
    private ErrorCommentManager errorCommentManager;
    @Autowired
    private TestRunDao testRunDao;
    private AtomicInteger operating = new AtomicInteger(0); // indicates if the DLM is currently operating. Jobs will stop accordingly.
    private List<ReportDTO> startDtos = new ArrayList<ReportDTO>();
    private List<ReportDTO> stopDtos = new ArrayList<ReportDTO>();

    public boolean isOperating() {
        return operating.equals(0);
    }

    private void setOperatingStart() {
        operating.incrementAndGet();
    }

    private void setOperatingStop() {
        operating.decrementAndGet();
    }

    public void storeStartDto(ReportDTO dto) {
        startDtos.add(dto);
    }

    public void storeStopDto(ReportDTO dto) {
        stopDtos.add(dto);
    }

    /**
     * Clean comments in the memory and in database.
     * Should be called only from MemoryCleanJob. Used as a recurring job.
     */
    public synchronized void cleanComments() {
        setOperatingStart();
        if (waitToOperate()) {
            // 1. clean memory comments
            errorCommentManager.cleanComments();
            // 2. clean DB comments
            testRunDao.cleanTestRunMessage();
        }
        setOperatingStop();
        addStoredDtos();
    }

    /**
     * Removes the old test runs from the database.
     * Called from the UI.
     *
     * @param daysToKeep the days to keep, older data will be deleted
     */
    public synchronized void removeOldTestRuns(int daysToKeep) {
        setOperatingStart();
        if (waitToOperate()) {
            testRunDao.cleanOldTestRunData(DateUtil.getDateBeforeDays(daysToKeep));
        }
        setOperatingStop();
        addStoredDtos();
    }

    /**
     * Deleting data: stop the reporting service, gather incoming data in a queue, run the delete, restart the service and install data from the queue.
     *
     * @param setId the set id
     * @param removeFromDB true if remove from database
     * @return true, if successful
     */
    public synchronized boolean deleteSetRun(String setId, boolean removeFromDB) {
        boolean result = false;
        setOperatingStart();
        if (waitToOperate()) {
            // delete the required data
            // remove from memory
            SetRunManager set = runManager.getRunningSetsMap().remove(setId);
            // remove from DB
            if (set != null && removeFromDB) {
                testRunDao.deleteSetRun(set.getSetName(), set.getStartDate());
            }
            // remove linkings from ErrorCommentManager
            errorCommentManager.removeSetLinkings(set);
            result = true;
        }
        setOperatingStop();
        addStoredDtos();

        if (result) {
            logger.info("DELETE SUCCESSFUL! Run: " + setId);
        } else {
            logger.info("DELETE FAILED! Run: " + setId);
        }
        return result;
    }

    /**
     * Removes an error comment from the system.
     *
     * @param errorMessage the error message
     * @param id the id
     */
    public synchronized boolean removeComment(String errorMessage, Integer id) {
        boolean result = false;
        if (errorCommentManager.getErrorComments().get(errorMessage) != null) {
            setOperatingStart();
            if (waitToOperate()) {
                // delete the required data
                ErrorComment removed = errorCommentManager.getErrorComments().get(errorMessage).remove(id);
                removed.setErrorMessage(errorMessage);
                deleteComment(removed);
                result = true;
            }
            setOperatingStop();
            addStoredDtos();

            if (result) {
                logger.info("DELETE SUCCESSFUL! Message: " + errorMessage + " id: " + id);
            } else {
                logger.info("DELETE FAILED! Message: " + errorMessage + " id: " + id);
            }
        }
        return result;
    }

    /**
     * Removes a comment from the system. Clears error comment IDs linked to this comment.
     * Called from DataLifecycleManager.
     *
     * @param removed the error comment for removal
     */
    private void deleteComment(ErrorComment removed) {
        if (removed != null) {
            // remove from memory
            String[] s;
            SetRunManager setRunManager;
            for (String testId : removed.getLinkedIds()) {
                try {
                    setRunManager = null;
                    s = testId.split("<>", 2);
                    if (runManager.getRunningSetsMap().containsKey(s[0]) && runManager.getRunningSetsMap().get(s[0]).getTestRunData(s[1]) != null) {
                        //runManager.getRunningSetsMap().get(s[0]).getTestRunData(s[1]).setErrorCommentId(null);
                        setRunManager = runManager.getRunningSetsMap().get(s[0]);
                    }
                    if (runManager.getFinishedSetsMap().containsKey(s[0]) && runManager.getFinishedSetsMap().get(s[0]).getTestRunData(s[1]) != null) {
                        //runManager.getFinishedSetsMap().get(s[0]).getTestRunData(s[1]).setErrorCommentId(null);
                        setRunManager = runManager.getRunningSetsMap().get(s[0]);
                    }
                    if (setRunManager != null) {
                        // Set the error id null
                        setRunManager.getTestRunData(s[1]).setErrorCommentId(null);
                        // Decrease statistic on the set
                        if (setRunManager.getTypeStats().get(removed.getType()).decrementAndGet() == 0) {
                            setRunManager.getTypeStats().remove(removed.getType());
                        }
                    }
                } catch (Exception e) {
                    logger.error("Failed to delete comment: " + testId);
                }
            }
            // remove from DB
            testRunDao.deleteErrorComment(removed);
        }
    }

    /**
     * Controls DLM operations.
     *
     * @return true, if is ready to operate
     */
    private boolean waitToOperate() {
        int temp = 0;
        // operating.get() > 1 - means that we want only 1 DB access operation at a time
        while ((runManager.isOperating() || operating.get() > 1) && temp < MAX_RETRY) {
            try {
                Thread.sleep(SLEEP_TIME);
                temp++;
            } catch (InterruptedException e) {
                break;
            }
        }
        return temp < MAX_RETRY;
    }

    /**
     * Add DTOs which were stored while there was a DLM operation.
     *
     * @return true, if successful
     */
    private boolean addStoredDtos() {
        // TODO: A problem can rise if the report of the test start and stop is shorter then the job time (time between setOperatingStop and Start).
        // If the report start is stored and the report stop is reported while addStoredDto-s is working and the start is not reported when the end is.
        // Though this is very unlikely because a test run should be much longer then a job time.
        boolean result = true;
        for (ReportDTO dto : startDtos) {
            if (!runManager.startTest(dto)) {
                result = false;
            }
        }
        for (ReportDTO dto : stopDtos) {
            if (runManager.stopTest(dto)) {
                result = false;
            }
        }
        return result;
    }

    public ErrorCommentManager getErrorCommentManager() {
        return errorCommentManager;
    }

}
