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

import testmanager.reporting.client.ReportManager;
import testmanager.reporting.client.ReportManager.ResultState;
import testmanager.reporting.client.ReportParams;
import testmanager.reporting.client.ReportParams.CheckPointState;


public class TestTM {

    /**
     * @param args
     */
    public static void main(String[] args) {

        ReportManager rm = new ReportManager("localhost", "8080", "/TestManager/app");

        System.out.println("Testing if service reachable:");
        boolean serviceOK = rm.isMainServiceReachable();
        System.out.println(serviceOK);

        if (serviceOK) {
            Date now = new Date();
            ReportParams rp = new ReportParams();

//            testOne(rm, now, rp);
//            testFew(rm, now, rp);
            testMany(rm, now, rp);

            // Concurrency test
//			Date now1 = new Date();
//			createThread(new ReportManager("localhost", "8080", "/TestManager_reporting/app"), now1, new ReportParams()).start();
//			createThread(new ReportManager("localhost", "8080", "/TestManager_reporting/app"), new Date(now1.getTime() + 100000), new ReportParams()).start();
        }

    }

//    private static Thread createThread(final ReportManager rm, final Date now, final ReportParams rp) {
//        return new Thread() {
//            @Override
//            public void run() {
//                testMany(rm, now, rp);
//            }
//        };
//    }

    private static void testOne(ReportManager rm, Date now, ReportParams rp) {
        rp.setTestParam("TEST_SCRIPT_ID", "testId111");
        rp.setTestParam("TEST_SCRIPT_NAME", "testName111");
        rp.setEnvironment("ek1", "ev1");
        rp.setEnvironment("ek2", "ev2");
        rm.reportTestStart("test1", "param1", "./testing/testlist.txt", now, rp);
        rp.setErrorMessage("Exception 1");
        rp.setCheckPoint("CheckPoint 1 error message", "HOME_PAGE", "USER_LOGIN", CheckPointState.PASSED);
        rp.setCheckPoint("CheckPoint 2 error message", "HOME_PAGE", "TEST_MODULE", CheckPointState.FAILED);
        rm.reportTestStop("test1", "param1", "./testing/testlist.txt", now, ResultState.FAILED, rp);
    }

    private static void testFew(ReportManager rm, Date now, ReportParams rp) {
        rm.reportTestStart("test1", "param1", "set1", now, null);
        rm.reportTestStart("test2", "param1", "set1", now, null);
        rm.reportTestStart("test1", "param1", "set2", now, null);
        rm.reportTestStart("test2", "param1", "set2", now, null);
        rm.reportTestStart("test3", "param1", "set2", now, null);	// not finished
        rm.reportTestStart("test1", "param1", "./testing/testlist.txt", now, null);

        rm.reportTestStop("test1", "param1", "set1", now, ResultState.PASSED, null);
        rp.setErrorMessage("Can not find element: XY.");
        rp.setTestParam("TEST_SCRIPT_ID", "testId111");
        rp.setTestParam("TEST_SCRIPT_NAME", "testName111");
        rp.setEnvironment("ek1", "ev1");
        rp.setEnvironment("ek2", "ev2");
        rp.setCheckPoint("CheckPoint 1 error message", "HOME_PAGE", "USER_LOGIN", CheckPointState.PASSED);
        rp.setCheckPoint("CheckPoint 2 error message", "HOME_PAGE", "TEST_MODULE", CheckPointState.FAILED);
        rm.reportTestStop("test2", "param1", "set1", now, ResultState.FAILED, rp);
        rm.reportTestStop("test1", "param1", "set2", now, ResultState.ABORTED, null);
        rp.setErrorMessage("Booking system not available.");
        rm.reportTestStop("test2", "param1", "set2", now, ResultState.NOT_AVAILABLE, rp);
        rm.reportTestStop("test1", "param1", "./testing/testlist.txt", now, ResultState.PASSED, null);
    }

    private static void testMany(ReportManager rm, Date now, ReportParams rp) {
        int max = 100;
        rp.setTestParam("TEST_SCRIPT_ID", "testId111");
        rp.setTestParam("TEST_SCRIPT_NAME", "testName111");
        rp.setEnvironment("ek1", "ev1");
        rp.setEnvironment("ek2", "ev2");
        for (int i = 1; i <= max; i++) {
            rm.reportTestStart("test" + i, "param1", "concurrentSet", now, rp);
        }

        rp.setErrorMessage("Exception 1");
        for (int i = 1; i <= max; i++) {
            // Intentionally fail 20% of the cases for testing purposes
            rm.reportTestStop("test" + i, "param1", "concurrentSet", now, (i > max - max / 5) ? ResultState.FAILED : ResultState.PASSED, rp);
        }
    }

}
