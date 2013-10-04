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
package testmanager.reporting.web.results;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import testmanager.reporting.domain.reporting.CheckPoint;
import testmanager.reporting.domain.reporting.ResultState;
import testmanager.reporting.domain.reporting.TestRunData;
import testmanager.reporting.domain.results.ChartPieData;
import testmanager.reporting.service.excel.TestRunXLSGenerator;
import testmanager.reporting.service.reporting.RunManager;
import testmanager.reporting.service.reporting.SetRunManager;

/**
 * The Class ResultTableController.
 * 
 * @author Istvan_Pamer
 */
@Controller
@RequestMapping("/results/*")
public class ResultTableController {

	protected final Log logger = LogFactory.getLog(getClass());

	private static final String RESPONSE_OK = "OK";
	private static final String RESPONSE_BAD = "BAD";
	private Pattern scenarioGroupPattern;
	@Autowired
	private RunManager runManager;
	@Autowired
	private TestRunXLSGenerator generator;

	private static final Comparator<TestRunData> TEST_RUN_NAME_ORDER_ASC = new Comparator<TestRunData>() {
		@Override
		public int compare(TestRunData o1, TestRunData o2) {
			String s1 = o1.getTestName() + o1.getParamName();
			String s2 = o2.getTestName() + o2.getParamName();
			return s1.compareTo(s2);
		}
	};
	// private static final Comparator<TestRunData> TEST_RUN_NAME_ORDER_DESC =
	// new Comparator<TestRunData>() {
	// @Override
	// public int compare(TestRunData o1, TestRunData o2) {
	// return o2.getTestName().compareTo(o1.getTestName());
	// };
	// };
	private static final Comparator<TestRunData> TEST_RUN_ERROR_ORDER_ASC = new Comparator<TestRunData>() {
		@Override
		public int compare(TestRunData o1, TestRunData o2) {
			int result;
			if (ResultState.FAILED.equals(o1.getState()) || ResultState.FAILED.equals(o2.getState())) {
				result = compareByMessage(o1, o2);
			} else if (ResultState.NOT_AVAILABLE.equals(o1.getState()) || ResultState.NOT_AVAILABLE.equals(o2.getState())) {
				result = compareByMessage(o1, o2);
			} else if (!ResultState.PASSED.equals(o1.getState())) {
				result = -1;
			} else if (!ResultState.PASSED.equals(o2.getState())) {
				result = 1;
			} else {
				result = 0;
			}
			return result;
		}

		private int compareByMessage(TestRunData o1, TestRunData o2) {
			int result;
			if (o1.getErrorMessage() == null && o2.getErrorMessage() == null) {
				result = TEST_RUN_NAME_ORDER_ASC.compare(o1, o2);
			} else if (o1.getErrorMessage() != null && o2.getErrorMessage() == null) {
				result = -1;
			} else if (o1.getErrorMessage() == null && o2.getErrorMessage() != null) {
				result = 1;
			} else {
				result = o1.getErrorMessage().compareTo(o2.getErrorMessage());
			}
			return result;
		}
	};

	/**
	 * Controller for handling the test run data view.
	 * 
	 * @param setId the set id
	 * @return the model and view
	 */
	@RequestMapping(value = "table", method = RequestMethod.GET)
	public ModelAndView table(@RequestParam String setId) {
		logger.info("OPEN results/table {setId=" + setId + "}");

		List<TestRunData> list = runManager.getAllTestRunData(setId);
		// Collections.sort(list, TEST_RUN_NAME_ORDER_ASC);
		Collections.sort(list, TEST_RUN_ERROR_ORDER_ASC);

		SetRunManager setRunManager = runManager.getSetRunManager(setId);
		// Collect Pie Chart Data
		ChartPieData chartPieData = new ChartPieData(); // Pie Chart Data
		chartPieData.addData(ResultState.PASSED.toString(), setRunManager.getResultStatPassed().toString());
		int knownIssues = 0;
		for (Entry<String, AtomicInteger> entry : setRunManager.getTypeStats().entrySet()) {
			chartPieData.addData(entry.getKey(), entry.getValue().toString());
			knownIssues += entry.getValue().get();
		}
		chartPieData.addData("UNKNOWN",
				Integer.toString(setRunManager.getResultStatFailed().get() + setRunManager.getResultStatNA().get() - knownIssues));

		Integer passedCP = 0, failedCP = 0;
		for (TestRunData testRunData : list) {
			for (CheckPoint cp : testRunData.getCheckPoints()) {
				switch (cp.getState()) {
				case PASSED:
					passedCP++;
					break;
				case FAILED:
					failedCP++;
					break;
				default:
					break;
				}
			}
		}
		ChartPieData cpPieChartData = new ChartPieData(); // Pie Chart Data
		cpPieChartData.addData("PASSED", passedCP.toString());
		cpPieChartData.addData("FAILED", failedCP.toString());
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("testRunData", list);
		map.put("errorTypes", runManager.getErrorTypes());
		map.put("setId", setId);
		map.put("setRunManager", setRunManager);
		map.put("chartPieData", chartPieData);
		map.put("cpPieChartData", cpPieChartData);
		map.put("passedCP", passedCP);
		map.put("failedCP", failedCP);

		return new ModelAndView("results/table", map);
	}

	@RequestMapping(value = "table_cpb", method = RequestMethod.GET)
	public ModelAndView table_cpb(@RequestParam String setId) {
		logger.info("OPEN results/table_cpb {setId=" + setId + "}");

		List<TestRunData> list = runManager.getAllTestRunData(setId);
		SetRunManager setRunManager = runManager.getSetRunManager(setId);
		// Collect Pie Chart Data
		Integer passedCP = 0, failedCP = 0;
		for (TestRunData testRunData : list) {
			for (CheckPoint cp : testRunData.getCheckPoints()) {
				switch (cp.getState()) {
				case PASSED:
					passedCP++;
					break;
				case FAILED:
					failedCP++;
					break;
				default:
					break;
				}
			}
		}

		Map<String, Map<String, TestRunData>> scenarioResultMap = new TreeMap<String, Map<String, TestRunData>>();
		Set<String> allPhases = new HashSet<String>();
		for (TestRunData testRunData : list) {
			Matcher matcher = scenarioGroupPattern.matcher(testRunData.getDisplayTestName());
			if (matcher.matches()) {
				String scenarioKey = matcher.group(1);
				String phaseKey = matcher.group(2);
				if (!scenarioResultMap.containsKey(scenarioKey)) {
					scenarioResultMap.put(scenarioKey, new TreeMap<String, TestRunData>());
				}

				scenarioResultMap.get(scenarioKey).put(phaseKey, testRunData);
				allPhases.add(phaseKey);
			}
		}
		Map<String, ResultState> scenarioResultStateMap = new TreeMap<String, ResultState>();

		for (Entry<String, Map<String, TestRunData>> scenario : scenarioResultMap.entrySet()) {
			ResultState result = ResultState.PASSED;
			for (Entry<String, TestRunData> phase : scenario.getValue().entrySet()) {
				if (result == ResultState.PASSED) {
					result = phase.getValue().getState();
				}
			}
			scenarioResultStateMap.put(scenario.getKey(), result);
		}

		List<String> asList = new ArrayList<String>(allPhases);
		Collections.sort(asList, new AlphanumComparator());

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("scenarioResultMap", scenarioResultMap);
		map.put("scenarioResultStateMap", scenarioResultStateMap);
		map.put("phases", asList);

		map.put("setId", setId);
		map.put("setRunManager", setRunManager);

		return new ModelAndView("results/table_cpb", map);
	}

	/**
	 * Controller for saving the comments on test runs.
	 * 
	 * @param setId the set id
	 * @param testName the test name
	 * @param paramName the param name
	 * @param type the type
	 * @param comment the comment
	 * @return the model and view
	 */
	@RequestMapping(value = "save", method = RequestMethod.GET)
	public @ResponseBody
	String save(@RequestParam String setId, @RequestParam String testName, @RequestParam String paramName, @RequestParam String type,
			@RequestParam String comment) {
		logger.info("SAVE {setId=" + setId + ", testName=" + testName + ", paramName=" + paramName + ", type=" + type + ", comment=" + comment + "}");

		String result = RESPONSE_BAD;
		if (runManager.saveTestRunComment(setId, TestRunData.generateID(testName, paramName), type, comment)) {
			result = RESPONSE_OK;
		}
		return result;
	}

	/**
	 * Save all suggested comments for a set run.
	 * 
	 * @param setId the set id
	 * @return the string
	 */
	@RequestMapping(value = "save_all", method = RequestMethod.GET)
	public @ResponseBody
	String saveAll(@RequestParam String setId) {
		logger.info("Saving all suggested comments for the set: " + setId);

		String result = RESPONSE_BAD;
		if (runManager.saveAllTestRunComment(setId)) {
			result = RESPONSE_OK;
		}
		return result;
	}

	@RequestMapping(value = "generate_xls", method = RequestMethod.GET)
	public void generateXls(@RequestParam String setId, HttpServletResponse response) {
		logger.info("Generating test run XLS. SetID: " + setId);

		File file = null;
		try {
			file = generator.createTestRunXLS(runManager.getAllTestRunData(setId));
			file.deleteOnExit();

			response.setContentType("application/ms-excel");
			response.setHeader("Content-Disposition", "attachment; filename=" + file.getName());

			InputStream is = new FileInputStream(file);
			IOUtils.copy(is, response.getOutputStream());
			response.flushBuffer();
		} catch (Exception ex) {
			logger.info("Error writing file to output stream. setId: '" + setId + "'");
		} finally {
			if (file != null) {
				file.delete();
			}
		}
	}

	@RequestMapping(value = "checkpoints", method = RequestMethod.GET)
	public @ResponseBody
	String checkPoints(@RequestParam String setId, @RequestParam String testName, @RequestParam String paramName) {
		logger.info("Requested checkpoint for: \n" + setId + "\n" + testName + "\n" + paramName);
		String generateID = TestRunData.generateID(testName, paramName);
		TestRunData runData = runManager.getTestRunData(setId, generateID);

		List<CheckPoint> checkPoints = new ArrayList<CheckPoint>(runData.getCheckPoints());
		Collections.reverse(checkPoints);
		return listToHtmlTable(checkPoints);
	}

	private String listToHtmlTable(List<CheckPoint> list) {
		StringBuilder builder = new StringBuilder();
		builder.append("<table id=\"checkPointTable\">");
		for (CheckPoint checkPoint : list) {
			builder.append("<tr class=\"" + getCheckPointClass(checkPoint.getState()) + "\">");
			builder.append("<td>" + checkPoint.getMessage() + "</td>");
			builder.append("<td>" + checkPoint.getMainType() + "</td>");
			builder.append("<td>" + checkPoint.getSubType() + "</td>");
			builder.append("<td style=\"text-align: center\">" + checkPoint.getState() + "</td>");
			builder.append("</tr>");
		}
		builder.append("</table>");
		return builder.toString();

	}

	private String getCheckPointClass(ResultState resultState) {
		String retval;
		switch (resultState) {
		case PASSED:
			retval = "passed";
			break;
		case FAILED:
			retval = "failed";
			break;
		default:
			retval = "";
			break;
		}
		return retval;
	}

	@Value("${screnario.group.pattern}")
	public void setScenarioGroupPattern(String scenarioGroupPattern) {
		this.scenarioGroupPattern = Pattern.compile(scenarioGroupPattern);
	}

	public class AlphanumComparator implements Comparator<String> {
		private final boolean isDigit(char ch) {
			return ch >= 48 && ch <= 57;
		}

		/**
		 * Length of string is passed in for improved efficiency (only need to
		 * calculate it once)
		 **/
		private final String getChunk(String s, int slength, int marker) {
			StringBuilder chunk = new StringBuilder();
			char c = s.charAt(marker);
			chunk.append(c);
			marker++;
			if (isDigit(c)) {
				while (marker < slength) {
					c = s.charAt(marker);
					if (!isDigit(c))
						break;
					chunk.append(c);
					marker++;
				}
			} else {
				while (marker < slength) {
					c = s.charAt(marker);
					if (isDigit(c))
						break;
					chunk.append(c);
					marker++;
				}
			}
			return chunk.toString();
		}

		public int compare(String o1, String o2) {
			int thisMarker = 0;
			int thatMarker = 0;
			int s1Length = o1.length();
			int s2Length = o2.length();

			while (thisMarker < s1Length && thatMarker < s2Length) {
				String thisChunk = getChunk(o1, s1Length, thisMarker);
				thisMarker += thisChunk.length();

				String thatChunk = getChunk(o2, s2Length, thatMarker);
				thatMarker += thatChunk.length();

				// If both chunks contain numeric characters, sort them
				// numerically
				int result = 0;
				if (isDigit(thisChunk.charAt(0)) && isDigit(thatChunk.charAt(0))) {
					// Simple chunk comparison by length.
					int thisChunkLength = thisChunk.length();
					result = thisChunkLength - thatChunk.length();
					// If equal, the first different number counts
					if (result == 0) {
						for (int i = 0; i < thisChunkLength; i++) {
							result = thisChunk.charAt(i) - thatChunk.charAt(i);
							if (result != 0) {
								return result;
							}
						}
					}
				} else {
					result = thisChunk.compareTo(thatChunk);
				}

				if (result != 0)
					return result;
			}

			return s1Length - s2Length;
		}
	}
}
