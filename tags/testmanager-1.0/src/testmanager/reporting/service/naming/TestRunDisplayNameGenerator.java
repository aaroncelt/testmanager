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
package testmanager.reporting.service.naming;

import testmanager.reporting.domain.reporting.TestRunData;

/**
 * The Interface TestRunDisplayNameGenerator. Generates display names for test runs.
 *
 * @author Istvan_Pamer
 */
public interface TestRunDisplayNameGenerator {

    /**
     * This function must return a unique test ID within your test system for the given test run.
     *
     * @param testRunData the test run data
     * @return the string
     */
    String generateUniqueTestID(TestRunData testRunData);

    /**
     * Generate display test name.
     *
     * @param testRunData the test run data
     * @return the string
     */
    String generateTestName(TestRunData testRunData);

    /**
     * Generate display param name.
     *
     * @param testRunData the test run data
     * @return the string
     */
    String generateParamName(TestRunData testRunData);

    /**
     * Generate display error message.
     *
     * @param testRunData the test run data
     * @return the string
     */
    String generateErrorMessage(TestRunData testRunData);

}
