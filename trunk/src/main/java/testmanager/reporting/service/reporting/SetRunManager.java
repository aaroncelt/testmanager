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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import testmanager.reporting.domain.reporting.ReportDTO;
import testmanager.reporting.domain.reporting.ResultState;
import testmanager.reporting.domain.reporting.TestRunData;
import testmanager.reporting.util.CheckPointUtil;
import testmanager.reporting.util.TimeUtil;

/**
 * The Class TestSetRunManager. Holds information about a run of a set.
 *
 * @author Istvan_Pamer
 */
public class SetRunManager {

    protected final Log logger = LogFactory.getLog(getClass());

    // sent by the user
    private String setName;
    private Date startDate;
    // calculated
    private String id;
    private long executionTime;
    private String displayExecutionTime;
    // test set statistics
    private Map<ResultState, AtomicInteger> resultStats = new HashMap<ResultState, AtomicInteger>();
    private Map<String, AtomicInteger> typeStats = new HashMap<String, AtomicInteger>();
    private int customStatistic = 0;

    private Map<String, TestRunData> runningTests = new ConcurrentHashMap<String, TestRunData>();
    private Map<String, TestRunData> finishedTests = new ConcurrentHashMap<String, TestRunData>();

    private SetRunManager(String setName, Date startDate) {
        this.setName = setName;
        this.startDate = startDate;
    }

    /**
     * Creates the test run data.
     * Generates ID for itself.
     *
     * @param setName the set name
     * @param startDate the start date
     * @return the sets the run manager
     */
    public static SetRunManager createSetRunManager(String setName, Date startDate) {
        SetRunManager result = new SetRunManager(setName, startDate);
        result.id = SetRunManager.generateID(setName, startDate);
        return result;
    }

    /**
     * Generate id.
     *
     * @param setName the set name
     * @param startDate the start date
     * @return the string
     */
    public static String generateID(String setName, Date startDate) {
        return setName + ";;" + startDate;
    }

    /**
     * Start test.
     *
     * @param testRunData the test run data
     * @return true, if successful
     */
    synchronized boolean startTest(TestRunData testRunData) {
        boolean result = true;
        String id = testRunData.getId();

        if (runningTests.containsKey(id) || finishedTests.containsKey(id)) {
            logger.error("TEST START FAIL - reason: Test already ran: " + testRunData);
            result = false;
        } else {
            runningTests.put(id, testRunData);
        }

        return result;
    }

    /**
     * Stop test.
     *
     * @param dto the dto
     * @return true, if successful
     */
    synchronized TestRunData stopTest(ReportDTO dto) {
        TestRunData testRunData = null;
        String id = TestRunData.generateID(dto.getTestName(), dto.getParamName());

        if (runningTests.containsKey(id)) {
            testRunData = runningTests.remove(id);
            // calculate properties
            testRunData.setErrorMessage(dto.getMsg());
            testRunData.setStopDate(new Date());
            testRunData.setExecutionTime(testRunData.getStopDate().getTime() - testRunData.getStartDate().getTime());
            testRunData.setState(ResultState.valueOf(dto.getResult()));
            testRunData.setEnvironment(dto.getEnv());
            CheckPointUtil.addCheckPoints(dto, testRunData);
            // save test run
            finishedTests.put(id, testRunData);
            // calculate statistics
            incrementStats(testRunData);
        } else {
            // no such test
            logger.error("TEST STOP FAIL - reason: No such test: " + dto);
        }

        return testRunData;
    }

    /**
     * On test run stop or load, set the test set statistics.
     *
     * @param testRunData the test run data
     */
    void incrementStats(TestRunData testRunData) {
        this.executionTime += testRunData.getExecutionTime();
        this.displayExecutionTime = TimeUtil.getElapsedTimeString(executionTime);
        if (resultStats.get(testRunData.getState()) == null) {
            resultStats.put(testRunData.getState(), new AtomicInteger(1));
        } else {
            resultStats.get(testRunData.getState()).addAndGet(1);
        }
    }

    /**
     * Gets the finished tests.
     * Used from RunManager when loading tests from the DB.
     *
     * @return the finished tests
     */
    public Map<String, TestRunData> getFinishedTests() {
        return finishedTests;
    }

    /**
     * Gets the test run data.
     *
     * @param id the id
     * @return the test run data
     */
    public TestRunData getTestRunData(String id) {
        TestRunData result = runningTests.get(id);
        if (result == null) {
            result = finishedTests.get(id);
        }
        return result;
    }

    /**
     * Gets all test run data.
     * Should be called by the RunManager.
     *
     * @return the test run data
     */
    public List<TestRunData> getAllTestRunData() {
        List<TestRunData> result = new ArrayList<TestRunData>(runningTests.values());
        result.addAll(finishedTests.values());
        return result;
    }

    public int getRunningTestNumber() {
        return runningTests.keySet().size();
    }

    public int getFinishedTestNumber() {
        return finishedTests.keySet().size();
    }

    public String getId() {
        return id;
    }

    public long getExecutionTime() {
        return executionTime;
    }

    public String getDisplayExecutionTime() {
        return displayExecutionTime;
    }

    public String getSetName() {
        return setName;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Map<ResultState, AtomicInteger> getResultStats() {
        return resultStats;
    }

    public AtomicInteger getResultStatPassed() {
        return (resultStats.get(ResultState.PASSED) == null) ? new AtomicInteger(0) : resultStats.get(ResultState.PASSED);
    }

    public AtomicInteger getResultStatFailed() {
        return (resultStats.get(ResultState.FAILED) == null) ? new AtomicInteger(0) : resultStats.get(ResultState.FAILED);
    }

    public AtomicInteger getResultStatNA() {
        return (resultStats.get(ResultState.NOT_AVAILABLE) == null) ? new AtomicInteger(0) : resultStats.get(ResultState.NOT_AVAILABLE);
    }

    public Map<String, AtomicInteger> getTypeStats() {
        return typeStats;
    }

    public int getCustomStatistic() {
        return customStatistic;
    }

    public void setCustomStatistic(int customStatistic) {
        this.customStatistic = customStatistic;
    }

    public Map<String, String> getEnvironment() {
        Map<String, String> map = null;
        if (!finishedTests.keySet().isEmpty()) {
            map = finishedTests.get(finishedTests.keySet().iterator().next()).getEnvironment();
        }
        return map;
    }

}
