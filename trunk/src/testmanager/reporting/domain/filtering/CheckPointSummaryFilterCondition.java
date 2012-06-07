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
package testmanager.reporting.domain.filtering;

import java.util.Map;

import testmanager.reporting.domain.reporting.CheckPoint;

/**
 * The Class SetSummaryFilterCondition.
 *
 * @author Istvan_Pamer
 */
public class CheckPointSummaryFilterCondition {

    private static final String CHECK_MESSAGE_INCLUDE = "selectedIncm";	// messages for include
    private static final String CHECK_MESSAGE_EXCLUDE = "selectedExcm";	// messages for exclude
    private static final String CHECK_COMMENT_INCLUDE = "selectedIncc";	// comments for include
    private static final String CHECK_COMMENT_EXCLUDE = "selectedExcc";	// comments for exclude
    private static final String CHECK_TYPE_INCLUDE = "selectedInct";	// types for exclude
    private static final String CHECK_TYPE_EXCLUDE = "selectedExct";	// types for exclude

    // custom filters, selected by the user.
    Map<String, Map<String, String>> selectedsMap;	//	e.g.: {"selectedInct":{},"selectedExct":{"1-2":"BUG"}}

    /**
     * Instantiates a new sets the summary filter condition.
     *
     * @param selectedsMap the condition map, selected by the user
     */
    public CheckPointSummaryFilterCondition(Map<String, Map<String, String>> selectedsMap) {
        this.selectedsMap = selectedsMap;
    }

    /**
     * Validate a TestRunData against the selected conditions which were given on instantiation.
     *
     * @param data the data
     * @return true, if successful
     */
    public boolean validateCondition(CheckPoint cp) {
        boolean result = false;
        if (selectedsMap != null) {
            for (String category : selectedsMap.keySet()) {
                // message
                if (CHECK_MESSAGE_INCLUDE.equals(category) && cp.getMessage() != null) {
                    result = checkValues(category, cp.getMessage(), true);
                    if (result) { break; }
                } else if (CHECK_MESSAGE_EXCLUDE.equals(category) && cp.getMessage() != null) {
                    result = checkValues(category, cp.getMessage(), false);
                    if (result) { break; }
                // type
                } else if (CHECK_COMMENT_INCLUDE.equals(category) && cp.getMainType() != null) {
                    result = checkValues(category, cp.getMainType(), true);
                    if (result) { break; }
                    result = checkValues(category, cp.getSubType(), true);
                    if (result) { break; }
                } else if (CHECK_COMMENT_EXCLUDE.equals(category) && cp.getMainType() != null) {
                    result = checkValues(category, cp.getMainType(), false);
                    if (result) { break; }
                    result = checkValues(category, cp.getSubType(), false);
                    if (result) { break; }
                // result
                } else if (CHECK_TYPE_INCLUDE.equals(category) && cp.getState() != null) {
                    result = checkValues(category, cp.getState().toString(), true);
                    if (result) { break; }
                } else if (CHECK_TYPE_EXCLUDE.equals(category) && cp.getState() != null) {
                    result = checkValues(category, cp.getState().toString(), false);
                    if (result) { break; }
                }
            }
        }
        return result;
    }

    private boolean checkValues(String category, String data, boolean include) {
        boolean result = false;
        String temp = data.replaceAll(" ", "").toLowerCase();
        for (String value : selectedsMap.get(category).values()) {
            if (include && temp.contains(value.replaceAll(" ", "").toLowerCase())) {
                result = true;
                break;
            } else if (!include && !temp.contains(value.replaceAll(" ", "").toLowerCase())) {
                result = true;
                break;
            }
        }
        return result;
    }

    // This solution uses AND operation to join conditions
    /*public boolean validateCondition(TestRunData data) {
        int result = 0;
        if (selectedsMap != null && data.getState() != ResultState.PASSED) {
            for (String category : selectedsMap.keySet()) {
                if (CHECK_MESSAGE_INCLUDE.equals(category) && data.getErrorMessage() != null) {
                    result = (result >= 0) ? checkValues(category, data.getErrorMessage(), true) : result;
                } else if (CHECK_MESSAGE_EXCLUDE.equals(category) && data.getErrorMessage() != null) {
                    result = (result >= 0) ? checkValues(category, data.getErrorMessage(), false) : result;
                } else if (CHECK_COMMENT_INCLUDE.equals(category) && data.getErrorComment() != null) {
                    result = (result >= 0) ? checkValues(category, data.getErrorComment(), true) : result;
                } else if (CHECK_COMMENT_EXCLUDE.equals(category) && data.getErrorComment() != null) {
                    result = (result >= 0) ? checkValues(category, data.getErrorComment(), false) : result;
                } else if (CHECK_TYPE_INCLUDE.equals(category) && data.getErrorType() != null) {
                    result = (result >= 0) ? checkValues(category, data.getErrorType(), true) : result;
                } else if (CHECK_TYPE_EXCLUDE.equals(category) && data.getErrorType() != null) {
                    result = (result >= 0) ? checkValues(category, data.getErrorType(), false) : result;
                }
            }
        }
        return (result > 0) ? true : false;
    }

    private int checkValues(String category, String data, boolean include) {
        int result = 0;
        String temp = data.replaceAll(" ", "");
        for (String value : selectedsMap.get(category).values()) {
            if (include) {
                if (value.replaceAll(" ", "").equals(temp)) {
                    result = (result >= 0) ? 1 : result;
                } else {
                    result = -1;
                }
            } else {
                if (!value.replaceAll(" ", "").equals(temp)) {
                    result = (result >= 0) ? 1 : result;
                } else {
                    result = -1;
                }
            }
        }
        return result;
    }*/

}
