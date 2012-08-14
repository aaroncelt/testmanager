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
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import testmanager.reporting.dao.TestRunDao;
import testmanager.reporting.dao.converter.TestRunConverter;
import testmanager.reporting.dao.dto.TestRunDto;
import testmanager.reporting.domain.reporting.ReportDTO;
import testmanager.reporting.domain.reporting.TestRunData;
import testmanager.reporting.service.linkgeneration.LinkGeneratorFactory;
import testmanager.reporting.service.naming.TestRunDisplayNameGenerator;

/**
 * The Class TestRunManager. Holds information about the executed sets.
 *
 * @author Istvan_Pamer
 */
public class RunManager {

    protected final Log logger = LogFactory.getLog(getClass());

    @Autowired
    private LinkGeneratorFactory linkGeneratorFactory;
    @Autowired
    private ErrorCommentManager errorCommentManager;
    @Autowired
    private TestRunDao testRunDao;
    @Autowired
    private DataLifecycleManager dataLifecycleManager;
    @Autowired
    private TestRunDisplayNameGenerator testRunDisplayNameGenerator;
    private Set<String> errorTypes;	// set from context-service.xml
    private AtomicInteger operating = new AtomicInteger(0);

    // <set id, TestSetRunManager>
    private Map<String, SetRunManager> runningSets = new ConcurrentHashMap<String, SetRunManager>();
    private Map<String, SetRunManager> finishedSets = new ConcurrentHashMap<String, SetRunManager>();	// how to determinate if a set is finished? or just don't

    /**
     * Start test.
     *
     * @param dto the dto
     * @return true, if successful
     */
    public synchronized boolean startTest(ReportDTO dto) {
        // SET ID identifies: [a particular set]'s run
        // TEST ID identifies: [a particular test with a particular parameter]'s run
        boolean result = true;
        if (!dataLifecycleManager.isOperating()) {
            setOperatingStart();
            SetRunManager setRunManager;
            String id = SetRunManager.generateID(dto.getSetName(), dto.getSetStartDate());
            if (runningSets.containsKey(id)) {
                setRunManager = runningSets.get(id);
            } else {
                setRunManager = SetRunManager.createSetRunManager(dto.getSetName(), dto.getSetStartDate());
                runningSets.put(id, setRunManager);
            }
            // START test
            TestRunData testRunData = createTestRunDataFromReportDTO(dto, setRunManager);
            testRunData.setDisplayTestName(testRunDisplayNameGenerator.generateTestName(testRunData));
            testRunData.setDisplayParamName(testRunDisplayNameGenerator.generateParamName(testRunData));
            result = setRunManager.startTest(testRunData);
            setOperatingStop();
        } else {
            // store data for later start
            dataLifecycleManager.storeStartDto(dto);
        }
        return result;
    }

    private TestRunData createTestRunDataFromReportDTO(ReportDTO dto, SetRunManager setRunManager) {
        TestRunData testRunData = TestRunData.createTestRunData(dto.getTestName(), dto.getParamName(), setRunManager,
                linkGeneratorFactory.getLinkGeneratorStrategy(dto.getSetName()), errorCommentManager);
        testRunData.setParams(dto.getTestparams());
        testRunData.setEnvironment(dto.getEnv());
        testRunData.generateResultLink();
        return testRunData;
    }

    /**
     * Stop test.
     *
     * @param dto the dto
     * @return true, if successful
     */
    public synchronized boolean stopTest(ReportDTO dto) {
        boolean result = true;
        if (!dataLifecycleManager.isOperating()) {
            setOperatingStart();
            SetRunManager setRunManager;
            String id = SetRunManager.generateID(dto.getSetName(), dto.getSetStartDate());
            if (runningSets.containsKey(id)) {
                setRunManager = runningSets.get(id);
                // STOP the test
                TestRunData testRunData = setRunManager.stopTest(dto);
                if (testRunData != null) {
                    testRunData.setDisplayErrorMessage(testRunDisplayNameGenerator.generateErrorMessage(testRunData));
                    result = true;
                } else {
                    result = false;
                }
            } else {
                // no such set
                logger.error("TEST STOP FAIL - reason: No such test set. Possibly the test start was not recorded.");
                result = false;
            }
            // persist test run
            if (result) {
                TestRunData testRunData = getTestRunData(id, TestRunData.generateID(dto.getTestName(), dto.getParamName()));
                testRunDao.insertTestRun(TestRunConverter.convertDataToDto(testRunData));
            }
            setOperatingStop();
        } else {
            // store data for later start
            dataLifecycleManager.storeStopDto(dto);
        }
        return result;
    }

    public boolean isOperating() {
        return operating.equals(0);
    }

    private void setOperatingStart() {
        operating.incrementAndGet();
    }

    private void setOperatingStop() {
        operating.decrementAndGet();
    }

    /**
     * Gets the test run data.
     *
     * @param setId the set id
     * @param testId the test id
     * @return the test run data
     */
    public TestRunData getTestRunData(String setId, String testId) {
        TestRunData result = null;
        if (runningSets.get(setId) != null) {
            result = runningSets.get(setId).getTestRunData(testId);
        }
        if (result == null && finishedSets.get(setId) != null) {
            result = finishedSets.get(setId).getTestRunData(testId);
        }
        return result;
    }

    /**
     * Gets all test run data.
     *
     * @return the test run data
     */
    public List<TestRunData> getAllTestRunData(String setId) {
        List<TestRunData> result = new ArrayList<TestRunData>();
        if (runningSets.get(setId) != null) {
            result.addAll(runningSets.get(setId).getAllTestRunData());
        }
        if (finishedSets.get(setId) != null) {
            result.addAll(finishedSets.get(setId).getAllTestRunData());
        }
        return result;
    }

    /**
     * Load test run data into the operating memory.
     * Note that all of the data fields have to be populated in advance.
     *
     * @param dataList the data list
     */
    public void loadTestRunData(List<TestRunDto> dtoList) {
        SetRunManager setRunManager;
        String id;
        for (TestRunDto dto : dtoList) {
            id = SetRunManager.generateID(dto.getSetName(), dto.getSetStartDate());
            if (runningSets.containsKey(id)) {
                setRunManager = runningSets.get(id);
            } else {
                setRunManager = SetRunManager.createSetRunManager(dto.getSetName(), dto.getSetStartDate());
                runningSets.put(id, setRunManager);
            }
            // Create the test data
            TestRunData data = TestRunConverter.convertDtoToData(dto, setRunManager, linkGeneratorFactory.getLinkGeneratorStrategy(dto.getSetName()), errorCommentManager);
            // set display properties
            data.setDisplayTestName(testRunDisplayNameGenerator.generateTestName(data));
            data.setDisplayParamName(testRunDisplayNameGenerator.generateParamName(data));
            data.setDisplayErrorMessage(testRunDisplayNameGenerator.generateErrorMessage(data));
            // save run
            setRunManager.getFinishedTests().put(data.getId(), data);
            // calculate stats
            setRunManager.incrementStats(data);
        }
    }

    /**
     * Save test run comment.
     *
     * @param setId the set id
     * @param testId the test id
     * @param type the type
     * @param comment the comment
     * @return true, if successful
     */
    public synchronized boolean saveTestRunComment(String setId, String testId, String type, String comment) {
        boolean result = false;
        TestRunData data = getTestRunData(setId, testId);
        result = doSaveTestRunComment(data, comment, type);
        return result;
    }

    /**
     * Save all suggested test run comment for a set run.
     *
     * @param setId the set id
     * @return true, if successful
     */
    public synchronized boolean saveAllTestRunComment(String setId) {
        boolean result = false;
        List<TestRunData> datas = getAllTestRunData(setId);
        for (TestRunData data : datas) {
            if (data.getErrorComment() == null && doSaveTestRunComment(data, data.getErrorCommentSuggestion(), data.getErrorTypeSuggestion())) {
                result = true;
            }
        }
        return result;
    }

    /**
     * Actually saves the test run comment.
     *
     * @param data the data
     * @param comment the comment
     * @param type the type
     * @return true, if successful
     */
    private synchronized boolean doSaveTestRunComment(TestRunData data, String comment, String type) {
        boolean result = false;
        if (data != null && StringUtils.isNotBlank(comment)) {
            // save comment
            data.setErrorComment(comment, type);
            // update TestRunData in the DB
            testRunDao.updateTestRun(TestRunConverter.convertDataToDto(data));
            result = true;
        } // else data not found
        return result;
    }

    Map<String, SetRunManager> getRunningSetsMap() {
        return runningSets;
    }

    Map<String, SetRunManager> getFinishedSetsMap() {
        return finishedSets;
    }

    public Collection<SetRunManager> getRunningSets() {
        return runningSets.values();
    }

    public Collection<SetRunManager> getFinishedSets() {
        return finishedSets.values();
    }

    public SetRunManager getSetRunManager(String setId) {
        SetRunManager setRunManager = runningSets.get(setId);
        if (setRunManager == null) {
            setRunManager = finishedSets.get(setId);
        }
        return setRunManager;
    }

    public Set<String> getErrorTypes() {
        return errorTypes;
    }

    public void setErrorTypes(Set<String> errorTypes) {
        this.errorTypes = errorTypes;
    }

}
