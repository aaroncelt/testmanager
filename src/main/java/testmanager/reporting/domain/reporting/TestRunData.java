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
package testmanager.reporting.domain.reporting;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.util.HtmlUtils;

import testmanager.reporting.service.linkgeneration.LinkGeneratorStrategy;
import testmanager.reporting.service.reporting.ErrorCommentManager;
import testmanager.reporting.service.reporting.SetRunManager;
import testmanager.reporting.util.TimeUtil;

/**
 * The Class TestRunData.
 * 
 * @author Istvan_Pamer
 */
/**
 * @author Laszlo_Tarcsanyi
 * 
 */
public class TestRunData {

	// sent by the user
	private String testName;
	private String paramName; // name or number of the test parameter "row"
	private String setName;
	private Date setStartDate;
	private Map<String, String> params = new HashMap<String, String>(); // parameter
																		// names
																		// and
																		// values
	private Map<String, String> environment = new HashMap<String, String>();
	private List<CheckPoint> checkPoints = new ArrayList<CheckPoint>();
	private List<String> labels = new ArrayList<String>();
	private ResultState state = ResultState.STARTED;
	private String errorMessage;
	private Integer errorCommentId = null; // which key from the map belongs to
											// this run
	private ErrorCommentManager errorCommentManager; // errorCommentId,
														// ErrorComment -
														// reference to the main
														// error map value
	private LinkGeneratorStrategy linkGeneratorStrategy;
	// calculated
	private SetRunManager setRunManager;
	private String id;
	private String idFull;
	private Date startDate;
	private Date stopDate;
	private long executionTime;
	private String linkToResult = "#";
	private String displayTestName; // this will be displayed on test run data
									// table page
	private String displayParamName;
	private String displayErrorMessage;
	private String displayExecutionTime;

	private TestRunData(String testName, String paramName, SetRunManager setRunManager) {
		this.setRunManager = setRunManager;
		this.testName = testName;
		this.paramName = paramName;
		this.setName = setRunManager.getSetName();
		this.setStartDate = setRunManager.getStartDate();
		this.displayTestName = formatTextForHTML(testName);
		this.displayParamName = paramName;
	}

	/**
	 * Creates the test run data.
	 * 
	 * @param testName
	 *            the test name
	 * @param params
	 *            the params
	 * @param setName
	 *            the set name
	 * @param setStartDate
	 *            the set start date
	 * @return the test run data
	 */
	public static TestRunData createTestRunData(String testName, String paramName, SetRunManager setRunManager,
			LinkGeneratorStrategy linkGeneratorStrategy, ErrorCommentManager errorCommentManager) {
		TestRunData result = new TestRunData(testName, paramName, setRunManager);
		result.id = TestRunData.generateID(testName, paramName);
		result.idFull = generateFullID(setRunManager.getId(), result.id);
		result.startDate = new Date();
		result.linkGeneratorStrategy = linkGeneratorStrategy;
		result.errorCommentManager = errorCommentManager;
		return result;
	}

	/**
	 * Generate id.
	 * 
	 * @param testName
	 *            the test name
	 * @param params
	 *            the params
	 * @return the string
	 */
	public static String generateID(String testName, String paramName) {
		return testName + ";;" + paramName;
	}

	/**
	 * Generate full id. Contains set plus test data.
	 * 
	 * @param setId
	 *            the set id
	 * @param testId
	 *            the test id
	 * @return the string
	 */
	public static String generateFullID(String setId, String testId) {
		return setId + "<>" + testId;
	}

	/**
	 * Generate link to the test result static report file.
	 * 
	 * @return the string
	 */
	public String generateResultLink() {
		if (linkGeneratorStrategy != null) {
			linkToResult = linkGeneratorStrategy.generateLink(this);
		}
		return linkToResult;
	}

	/**
	 * Sets the error comment for the test run. Users should come here via the
	 * RunManager.
	 * 
	 * @param comment
	 *            the comment
	 * @param type
	 *            the type
	 */
	public void setErrorComment(String comment, String type) {
		if (comment != null && type != null) {
			String oldType = getErrorType();
			errorCommentId = errorCommentManager.setComment(errorMessage, errorCommentId, new ErrorComment(comment, type).addLinkedId(idFull));
			// Increment set type stats
			if (oldType != type) {
				// Only new type or type change - comment can only be removed
				// from DLM
				// increase new type stat
				if (!setRunManager.getTypeStats().containsKey(type)) {
					setRunManager.getTypeStats().put(type, new AtomicInteger(0));
				}
				setRunManager.getTypeStats().get(type).incrementAndGet();
				if (oldType != null && setRunManager.getTypeStats().containsKey(oldType)) { // type
																							// change
					// decrease old type stat
					if (setRunManager.getTypeStats().get(oldType).decrementAndGet() == 0) {
						setRunManager.getTypeStats().remove(oldType);
					}
				}
			}
		}
	}

	public String getId() {
		return id;
	}

	public String getIdFull() {
		return idFull;
	}

	public String getTestName() {
		return testName;
	}

	public String getParamName() {
		return paramName;
	}

	public String getSetName() {
		return setName;
	}

	public Date getSetStartDate() {
		return setStartDate;
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

	public long getExecutionTime() {
		return executionTime;
	}

	public void setExecutionTime(long executionTime) {
		this.executionTime = executionTime;
		this.displayExecutionTime = TimeUtil.getElapsedTimeString(executionTime);
	}

	public String getDisplayExecutionTime() {
		return displayExecutionTime;
	}

	public ResultState getState() {
		return state;
	}

	public void setState(ResultState state) {
		this.state = state;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
		this.displayErrorMessage = formatTextForHTML(errorMessage);
	}

	public Map<String, String> getParams() {
		return params;
	}

	public void setParams(Map<String, String> params) {
		if (params != null) {
			this.params.putAll(params);
		}
	}

	public Map<String, String> getEnvironment() {
		return environment;
	}

	public void setEnvironment(Map<String, String> environment) {
		if (environment != null) {
			this.environment.putAll(environment);
		}
	}

	public List<CheckPoint> getCheckPoints() {
		return checkPoints;
	}

	public void addCheckPoint(CheckPoint checkPoint) {
		if (checkPoint != null) {
			checkPoints.add(checkPoint);
		}
	}

	public List<String> getLabels() {
		return labels;
	}

	public void addLabel(String label) {
		if (label != null) {
			labels.add(label);
		}
	}

	public void addLabels(List<String> inputLabels) {
		if (inputLabels != null) {
			for (String label : inputLabels) {
				labels.add(label);
			}
		}
	}

	public String getLinkToResult() {
		return linkToResult;
	}

	public String getErrorType() {
		return errorCommentManager.getType(errorMessage, errorCommentId);
	}

	public String getErrorComment() {
		return errorCommentManager.getComment(errorMessage, errorCommentId);
	}

	public Integer getErrorCommentId() {
		return errorCommentId;
	}

	public void setErrorCommentId(Integer errorCommentId) {
		this.errorCommentId = errorCommentId;
	}

	public String getErrorCommentSuggestion() {
		return (errorCommentId == null) ? errorCommentManager.getCommentSuggestion(errorMessage) : errorCommentManager.getComment(errorMessage,
				errorCommentId);
	}

	public String getErrorTypeSuggestion() {
		return (errorCommentId == null) ? errorCommentManager.getTypeSuggestion(errorMessage) : errorCommentManager.getType(errorMessage,
				errorCommentId);
	}

	public String getDisplayTestName() {
		return displayTestName;
	}

	public void setDisplayTestName(String displayTestName) {
		this.displayTestName = formatTextForHTML(displayTestName);
	}

	public String getDisplayParamName() {
		return displayParamName;
	}

	public void setDisplayParamName(String displayParamName) {
		this.displayParamName = formatTextForHTML(displayParamName);
	}

	public String getDisplayErrorMessage() {
		return displayErrorMessage;
	}

	public void setDisplayErrorMessage(String displayErrorMessage) {
		this.displayErrorMessage = formatTextForHTML(displayErrorMessage);
	}

	public SetRunManager getSetRunManager() {
		return setRunManager;
	}

	@Override
	public String toString() {
		return "TestRunData [testName=" + testName + ", paramName=" + paramName + ", setName=" + setName + ", setStartDate=" + setStartDate
				+ ", params=" + params + ", environment=" + environment + ", checkPoints=" + checkPoints + ", state=" + state + ", errorMessage="
				+ errorMessage + ", errorCommentId=" + errorCommentId + ", errorCommentManager=" + errorCommentManager + ", linkGeneratorStrategy="
				+ linkGeneratorStrategy + ", setRunManager=" + setRunManager + ", id=" + id + ", idFull=" + idFull + ", startDate=" + startDate
				+ ", stopDate=" + stopDate + ", executionTime=" + executionTime + ", linkToResult=" + linkToResult + ", displayTestName="
				+ displayTestName + ", displayParamName=" + displayParamName + ", displayErrorMessage=" + displayErrorMessage
				+ ", displayExecutionTime=" + displayExecutionTime + "]";
	}

	private String formatTextForHTML(String text) {
		return StringUtils.isNotBlank(text) ? HtmlUtils.htmlEscape(text) : "";
	}

}
