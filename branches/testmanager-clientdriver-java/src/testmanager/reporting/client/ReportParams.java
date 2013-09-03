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
package testmanager.reporting.client;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * The Class ReportParams.
 * 
 * @author Istvan_Pamer
 */
public class ReportParams {

	private static final String MESSAGE = "message";
	private static final String MAIN_TYPE = "mainType";
	private static final String SUB_TYPE = "subType";
	private static final String STATE = "state";

	private String errorMessage;
	private JSONObject environment = new JSONObject();
	private JSONObject testParams = new JSONObject();
	private List<String> checkPoints = new ArrayList<String>();
	private List<String> labels = new ArrayList<String>();

	/**
	 * Clears the parameter object in order to reuse it.
	 * 
	 * @return self
	 */
	public ReportParams clearParams() {
		errorMessage = null;
		environment = new JSONObject();
		testParams = new JSONObject();
		checkPoints = new ArrayList<String>();
		labels = new ArrayList<String>();
		return this;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public ReportParams setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
		return this;
	}

	public String getTestParams() {
		return testParams.toJSONString();
	}

	@SuppressWarnings("unchecked")
	public ReportParams setTestParam(String key, String value) {
		testParams.put(key, value);
		return this;
	}

	public String getEnvironment() {
		return environment.toJSONString();
	}

	@SuppressWarnings("unchecked")
	public ReportParams setEnvironment(String key, String value) {
		environment.put(key, value);
		return this;
	}

	public boolean hasCheckPoints() {
		return !checkPoints.isEmpty();
	}

	public String getCheckPoints() {
		return "[" + StringUtils.join(checkPoints, ", ") + "]";
	}

	@SuppressWarnings("unchecked")
	public ReportParams setCheckPoint(String message, String mainType, String subType, CheckPointState state) {
		JSONObject temp = new JSONObject();
		temp.put(MESSAGE, message);
		temp.put(MAIN_TYPE, mainType);
		temp.put(SUB_TYPE, subType);
		temp.put(STATE, state.toString());
		checkPoints.add(temp.toJSONString());
		return this;
	}

	public boolean hasLabels() {
		return !labels.isEmpty();
	}

	public String getLabels() {
		return JSONArray.toJSONString(labels);
	}

	public void addLabel(String label) {
		this.labels.add(label);
	}

	public void addLabels(List<String> labels) {
		this.labels.addAll(labels);
	}

	/**
	 * The Enum CheckPointState.
	 */
	public enum CheckPointState {
		PASSED, FAILED
	}

}
