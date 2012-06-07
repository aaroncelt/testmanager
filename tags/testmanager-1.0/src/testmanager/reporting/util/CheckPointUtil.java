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
package testmanager.reporting.util;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import testmanager.reporting.domain.reporting.CheckPoint;
import testmanager.reporting.domain.reporting.ReportDTO;
import testmanager.reporting.domain.reporting.ResultState;
import testmanager.reporting.domain.reporting.TestRunData;

/**
 * The Class CheckPointUtil.
 *
 * @author Istvan_Pamer
 */
public class CheckPointUtil {

    protected static final Log logger = LogFactory.getLog(CheckPointUtil.class);

    private static final String MESSAGE = "message";
    private static final String MAIN_TYPE = "mainType";
    private static final String SUB_TYPE = "subType";
    private static final String STATE = "state";

    /**
     * Adds check points from the DTO to the test data.
     *
     * @param dto the dto
     * @param testRunData the test run data
     */
    public static void addCheckPoints(ReportDTO dto, TestRunData testRunData) {
        if (testRunData != null && dto != null && dto.getCheckPoints() != null) {
            for (Map<String, String> map : dto.getCheckPoints()) {
                CheckPoint checkPoint = new CheckPoint();
                checkPoint.setMessage(map.get(MESSAGE));
                checkPoint.setMainType(map.get(MAIN_TYPE));
                checkPoint.setSubType(map.get(SUB_TYPE));
                try {
                    checkPoint.setState(ResultState.valueOf(map.get(STATE)));
                } catch (Exception e) {
                    logger.warn("Can not get the value of the state when converting CheckPoint. State: " + map.get(STATE));
                }
                if (StringUtils.isNotBlank(checkPoint.getMessage())
                        && checkPoint.getMainType() != null
                        && checkPoint.getSubType() != null
                        && checkPoint.getState() != null) {
                    testRunData.addCheckPoint(checkPoint);
                } else {
                    logger.warn("Failed to add CheckPoint: " + checkPoint.toString());
                }
            }
        }
    }

}
