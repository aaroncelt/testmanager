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
package testmanager.reporting.service.job;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import testmanager.reporting.service.reporting.DataLifecycleManager;
import testmanager.reporting.service.reporting.RunManager;
import testmanager.reporting.service.reporting.SetRunManager;
import testmanager.reporting.util.DateUtil;

/**
 * The Class MemoryCleanJob.
 *
 * @author Istvan_Pamer
 */
public class MemoryCleanJob {

    protected final Log logger = LogFactory.getLog(getClass());

    @Autowired
    private RunManager runManager;
    @Autowired
    private DataLifecycleManager dataLifecycleManager;
    private int daysToKeepInMemory;
    private int daysToKeepInDatabase;

    public void setDaysToKeepInMemory(int daysToKeepInMemory) {
        this.daysToKeepInMemory = daysToKeepInMemory;
    }
    public void setDaysToKeepInDatabase(int daysToKeepInDatabase) {
        this.daysToKeepInDatabase = daysToKeepInDatabase;
    }

    /**
     * Cleans old data from the memory and database. Will be invoked by Spring.
     */
    public void cleanMemory() {
        logger.info("Clean Job STARTED.");
        // Remove set runs where start date older then daysToKeep
        for (SetRunManager setRunManager : runManager.getRunningSets()) {
            if (setRunManager.getStartDate().before(DateUtil.getDateBeforeDays(daysToKeepInMemory))) {
                // Remove the set from memory
                dataLifecycleManager.deleteSetRun(setRunManager.getId(), false);
            }
        }
        // Remove old Test Run Data from the database
        dataLifecycleManager.removeOldTestRuns(daysToKeepInDatabase);
        // Remove comments without links from the memory and database
        dataLifecycleManager.cleanComments();
        // Clean Story Table
        runManager.getStoryTable().cleanStoryTable();
        logger.info("Clean Job STOPPED.");
    }

}
