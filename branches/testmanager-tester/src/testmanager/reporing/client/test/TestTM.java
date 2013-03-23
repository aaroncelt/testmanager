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
package testmanager.reporing.client.test;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;

import testmanager.reporting.client.ReportManager;
import testmanager.reporting.client.ReportManager.ResultState;
import testmanager.reporting.client.ReportParams;
import testmanager.reporting.client.ReportParams.CheckPointState;

public class TestTM {
    private static final String TEST_SET_PREFIX = "testSet-";

    private static final String TEST_PARAM_NAME = "testParamName";

    private static final String TEST_NAME_PREFIX = "testName-";

    private static Logger LOGGER = Logger.getLogger(TestTM.class.getName());

    private static final String URI_ROOT = "/testmanager/app";
    private static final String PORT = "8080";
    private static final String HOST = "localhost";

    private static final String TEST_SCRIPT_ID_PREFIX = "testScriptId-";

    private static final String TEST_SCRIPT_NAME_PREFIX = "testScriptName-";

    private Integer numberOfThreads;
    private Integer numberOfSets;
    private Integer numberOfTestcases;
    private Integer numberOfCheckpoints;
    private Map<String, String> environment;

    private Integer activeThreads = 0;
    private Set<Thread> startedThreads = new HashSet<Thread>();

    public TestTM(Integer numberOfThreads, Integer numberOfSets, Integer numberOfTestcases, Integer numberOfCheckpoints, Map<String, String> environment) {
        this.numberOfThreads = numberOfThreads;
        this.numberOfSets = numberOfSets;
        this.numberOfTestcases = numberOfTestcases;
        this.numberOfCheckpoints = numberOfCheckpoints;
        this.environment = environment;
    }

    public void runTestReport() {
        ReportManager rm = new ReportManager(HOST, PORT, URI_ROOT);
        if (rm.isMainServiceReachable()) {
            for (int i = 0; i < numberOfSets; i++) {
                startReportingThread(i);
            }
            for (Thread thread : startedThreads) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    LOGGER.warning(e.getMessage());
                }
            }
            LOGGER.info("Reporting finished.");
        } else {
            LOGGER.warning("TestManager not accessible. Terminating...");
        }
    }

    private void startReportingThread(int setNumber) {
        ReportingThread reportingThread = new ReportingThread(setNumber);
        reportingThread.start();
        startedThreads.add(reportingThread);
    }

    private void reportTestSetResult(Integer setNumber) {
        ReportManager rm = new ReportManager(HOST, PORT, URI_ROOT);
        Date setStartDate = new Date();
        for (int i = 0; i < numberOfTestcases; i++) {
            ReportParams rp = new ReportParams();
            rp.setTestParam("TEST_SCRIPT_ID", TEST_SCRIPT_ID_PREFIX + i);
            rp.setTestParam("TEST_SCRIPT_NAME", TEST_SCRIPT_NAME_PREFIX + i);
            for (Entry<String, String> env : environment.entrySet()) {
                rp.setEnvironment(env.getKey(), env.getValue());
            }
            rm.reportTestStart(TEST_NAME_PREFIX + i, TEST_PARAM_NAME, TEST_SET_PREFIX + setNumber, setStartDate, rp);
            for (int j = 0; j < numberOfCheckpoints; j++) {
                rp.setCheckPoint("CheckPoint-" + j, "MainType", "SubType", CheckPointState.PASSED);
            }
            rm.reportTestStop(TEST_NAME_PREFIX + i, TEST_PARAM_NAME, TEST_SET_PREFIX + setNumber, setStartDate, ResultState.PASSED, rp);
        }
        LOGGER.info("Result reported for Set #" + setNumber);
    }

    private class ReportingThread extends Thread {

        private static final int RETRY_INTERVAL = 10000;
        private Integer setNumber;

        private ReportingThread(Integer setNumber) {
            this.setNumber = setNumber;
        }

        @Override
        public void run() {
            if (activeThreads >= numberOfThreads) {
                try {
                    Thread.sleep(RETRY_INTERVAL);
                } catch (InterruptedException e) {
                    LOGGER.warning("Thread for set #" + setNumber + " has been interrupted.");
                }
                this.run();
                return;
            }
            activeThreads++;
            reportTestSetResult(setNumber);
            activeThreads--;
        }
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        Map<String, String> environment = new HashMap<String, String>();
        environment.put("envKey1", "envValue1");
        environment.put("envKey2", "envValue2");
        new TestTM(3, 10, 10, 10, environment).runTestReport();
    }
}
