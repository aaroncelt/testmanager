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
package testmanager.reporting.dao;

import java.util.Date;
import java.util.List;

import testmanager.reporting.dao.dto.TestRunDto;
import testmanager.reporting.domain.reporting.ErrorComment;

/**
 * The Interface TestRunDao.
 *
 * @author Istvan_Pamer
 */
public interface TestRunDao {

    /**
     * Insert test run.
     *
     * @param testRunData the test run data
     */
    void insertTestRun(TestRunDto testRunData);

    /**
     * Update test run.
     *
     * @param testRunData the test run data
     */
    void updateTestRun(TestRunDto testRunData);

    /**
     * Gets the test run from date.
     *
     * @param date the date
     * @return the test run from date
     */
    List<TestRunDto> getTestRunFromDate(Date date);

    /**
     * Delete set run.
     *
     * @param testRunData the test run data
     */
    void deleteSetRun(String setName, Date setStartDate);

    /**
     * Delete error comment.
     *
     * @param removed the removed
     */
    void deleteErrorComment(ErrorComment removed);

    /**
     * Clean all unused test run message.
     */
    void cleanTestRunMessage();

    /**
     * Removes the old test runs from the database.
     *
     * @param keepFromDate keep data younger then this date, older data will be deleted
     */
    void cleanOldTestRunData(Date keepFromDate);
}
