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
package testmanager.reporting.service.linkgeneration;

import testmanager.reporting.domain.reporting.TestRunData;

/**
 * The LinkGeneratorStrategy Interface. This should be implemented for all projects.
 * If a link generator is found for a test set, then it will be used to generate links for it's test case reports.
 *
 * @author Istvan_Pamer
 */
public interface LinkGeneratorStrategy {

    /**
     * Generate link for the test case report.
     *
     * @param testRunData the test run data
     * @return the link for the test case report.
     */
    String generateLink(TestRunData testRunData);

}
