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

import java.util.Date;
import java.util.List;
import java.util.Map;

public class ReportDTO {

	// Required
	// start
	private String testName; // testName the name of the test - class + method
								// name or unique ID - MUST BE UNIQUE among all
								// test names
	private String paramName; // paramName the name or number of the test
								// parameter "row" - unique for a test
	private String setName; // setName the name of the set which the test
							// belongs to - one test can belong to more sets
	private Date setStartDate; // setStartDate the actual start date of the test
								// set run. This will separate multiple runs of
								// a single set.
	// stop
	private String result;

	// Optional
	private Map<String, String> env;
	private Map<String, String> testparams;
	private List<Map<String, String>> checkPoints;
	private List<String> labels;
	private String msg;
	private String story;
	private String layer;

	public ReportDTO() {
	}

	public ReportDTO(ReportDTO copy) {
		this.testName = copy.getTestName();
		this.paramName = copy.getParamName();
		this.setName = copy.getSetName();
		this.setStartDate = copy.getSetStartDate();
		this.result = copy.getResult();
	}

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

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public Map<String, String> getEnv() {
		return env;
	}

	public void setEnv(Map<String, String> env) {
		this.env = env;
	}

	public Map<String, String> getTestparams() {
		return testparams;
	}

	public void setTestparams(Map<String, String> testparams) {
		this.testparams = testparams;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public List<Map<String, String>> getCheckPoints() {
		return checkPoints;
	}

	public void setCheckPoints(List<Map<String, String>> checkPoints) {
		this.checkPoints = checkPoints;
	}

	public String getStory() {
		return story;
	}

	public void setStory(String story) {
		this.story = story;
	}

	public String getLayer() {
		return layer;
	}

	public void setLayer(String layer) {
		this.layer = layer;
	}

	public List<String> getLabels() {
		return labels;
	}

	public void setLabels(List<String> labels) {
		this.labels = labels;
	}

	@Override
	public String toString() {
		return "ReportDTO [testName=" + testName + ", paramName=" + paramName + ", setName=" + setName + ", setStartDate=" + setStartDate
				+ ", result=" + result + ", env=" + env + ", testparams=" + testparams + ", checkPoints=" + checkPoints + ", msg=" + msg + ", story="
				+ story + ", layer=" + layer + "]";
	}

}
