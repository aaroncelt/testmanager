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
package testmanager.reporting.service.dbsynch;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import testmanager.reporting.dao.TestRunDao;
import testmanager.reporting.dao.dto.TestRunDto;
import testmanager.reporting.service.reporting.RunManager;
import testmanager.reporting.util.DateUtil;

/**
 * The Class DBSynchronizer.
 *
 * @author Istvan_Pamer
 */
public class DBSynchronizer {

    @Autowired
    private TestRunDao testRunDao;
    @Autowired
    private RunManager runManager;
    private int daysToLoad = 7;

    /**
     * Initiates the test run memory database. Reads all test runs from the memory that are younger then a week.
     */
    public void init() {
        synchFromDate(DateUtil.getDateBeforeDays(daysToLoad));	// synch data younger then a week
    }

    /**
     * Synch the test run memory database. Data that are younger then the given date will be synchronized.
     *
     * @param date the date
     */
    public void synchFromDate(Date date) {
        List<TestRunDto> dtos = testRunDao.getTestRunFromDate(date);
        // Load the data to the operating memory
        runManager.loadTestRunData(dtos);
    }

    public void setDaysToLoad(int daysToLoad) {
        this.daysToLoad = daysToLoad;
    }

}
