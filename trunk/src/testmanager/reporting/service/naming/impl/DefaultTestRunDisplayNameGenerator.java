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
package testmanager.reporting.service.naming.impl;

import testmanager.reporting.domain.reporting.TestRunData;
import testmanager.reporting.service.naming.TestRunDisplayNameGenerator;

/**
 * The Class DefaultTestRunDisplayNameGenerator.
 *
 * @author Istvan_Pamer
 */
public class DefaultTestRunDisplayNameGenerator implements TestRunDisplayNameGenerator {

    @Override
    public String generateUniqueTestID(TestRunData testRunData) {
        return testRunData.getTestName() + ";;" + testRunData.getParamName();
    }

    @Override
    public String generateTestName(TestRunData testRunData) {
        return testRunData.getTestName();
    }

    @Override
    public String generateParamName(TestRunData testRunData) {
        return testRunData.getParamName();
    }

    @Override
    public String generateErrorMessage(TestRunData testRunData) {
        return testRunData.getErrorMessage();
    }

}
