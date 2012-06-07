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
package testmanager.reporting.service.excel;

import java.io.File;
import java.util.List;

import testmanager.reporting.domain.reporting.TestRunData;

/**
 * The Class TestRunXLSGenerator.
 *
 * @author Istvan_Pamer
 */
public interface TestRunXLSGenerator {

    /**
     * Creates the test run xls.
     *
     * @param runDataList the run data list
     * @return the file
     */
    public File createTestRunXLS(List<TestRunData> runDataList) throws Exception;

}
