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
package testmanager.reporting.dao.dto;

import java.util.Date;
import java.util.List;

/**
 * The Class TestRunDto.
 * 
 * @author Istvan_Pamer
 */
public class TestRunDto {

	// sent by the user
	private String testName;
	private String paramName;
	private String setName;
	private Date setStartDate;
	private List<PairDto> params; // parameter names and values
	private List<PairDto> environment;
	private List<CheckPointDto> checkPoints;
	private List<LabelDto> labels;
	private Integer errorMessageId; // used in TestRunData table
	private String errorMessage; // used in TestRunMessage
	private String errorType; // used in TestRunMessage
	private String errorComment; // used in TestRunMessage
	// calculated
	private Date startDate;
	private Date stopDate;
	private String state; // ResultState.STARTED;
	// DB only
	private Integer testRunId; // ID from the DB - Not the same as the one in
								// TestRunData

	public String getTestName() {
		return testName;
	}

	public void setTestName(String testName) {
		this.testName = testName;
	}

	public String getParamName() {
		return paramName;
	}

	public void setParamName(String paramName) {
		this.paramName = paramName;
	}

	public String getSetName() {
		return setName;
	}

	public void setSetName(String setName) {
		this.setName = setName;
	}

	public Date getSetStartDate() {
		return setStartDate;
	}

	public void setSetStartDate(Date setStartDate) {
		this.setStartDate = setStartDate;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getStopDate() {
		return stopDate;
	}

	public void setStopDate(Date stopDate) {
		this.stopDate = stopDate;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public List<PairDto> getParams() {
		return params;
	}

	public void setParams(List<PairDto> params) {
		this.params = params;
	}

	public List<PairDto> getEnvironment() {
		return environment;
	}

	public void setEnvironment(List<PairDto> environment) {
		this.environment = environment;
	}

	public List<CheckPointDto> getCheckPoints() {
		return checkPoints;
	}

	public void setCheckPoints(List<CheckPointDto> checkPoints) {
		this.checkPoints = checkPoints;
	}

	public List<LabelDto> getLabels() {
		return labels;
	}

	public void setLabels(List<LabelDto> labels) {
		this.labels = labels;
	}

	public Integer getTestRunId() {
		return testRunId;
	}

	public void setTestRunId(Integer testRunId) {
		this.testRunId = testRunId;
	}

	public Integer getErrorMessageId() {
		return errorMessageId;
	}

	public void setErrorMessageId(Integer errorMessageId) {
		this.errorMessageId = errorMessageId;
	}

	public String getErrorType() {
		return errorType;
	}

	public void setErrorType(String errorType) {
		this.errorType = errorType;
	}

	public String getErrorComment() {
		return errorComment;
	}

	public void setErrorComment(String errorComment) {
		this.errorComment = errorComment;
	}

	@Override
	public String toString() {
		return "TestRunDto [testName=" + testName + ", paramName=" + paramName + ", setName=" + setName + ", setStartDate=" + setStartDate
				+ ", params=" + params + ", environment=" + environment + ", checkPoints=" + checkPoints + ", errorMessageId=" + errorMessageId
				+ ", errorMessage=" + errorMessage + ", errorType=" + errorType + ", errorComment=" + errorComment + ", startDate=" + startDate
				+ ", stopDate=" + stopDate + ", state=" + state + ", testRunId=" + testRunId + "]";
	}

}
