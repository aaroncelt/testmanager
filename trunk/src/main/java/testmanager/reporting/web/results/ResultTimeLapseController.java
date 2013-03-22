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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import testmanager.reporting.domain.reporting.Pair;
import testmanager.reporting.domain.reporting.ResultState;
import testmanager.reporting.domain.reporting.TestRunData;
import testmanager.reporting.domain.results.ChartLineData;
import testmanager.reporting.service.naming.TestRunDisplayNameGenerator;
import testmanager.reporting.service.reporting.RunManager;
import testmanager.reporting.service.reporting.SetRunManager;
import testmanager.reporting.util.DateUtil;

/**
 * The Class ResultTimeLapseController.
 * 
 * @author Istvan_Pamer
 */
@Controller
@RequestMapping("/results/*")
public class ResultTimeLapseController {

	protected final Log logger = LogFactory.getLog(getClass());

	private int timeLapseColNum = 7; // overridden from timeLapseColNum property

	@Autowired
	private RunManager runManager;
	@Autowired
	private TestRunDisplayNameGenerator testRunDisplayNameGenerator;

	private Comparator<SetRunManager> timeLapseSortOrder;

	private static final Comparator<SetRunManager> SET_RUN_TIME_ORDER_DESC = new Comparator<SetRunManager>() {
		@Override
		public int compare(SetRunManager o1, SetRunManager o2) {
			return o2.getStartDate().compareTo(o1.getStartDate());
		}
	};

	private static final Comparator<SetRunManager> SET_RUN_TIME_ORDER_ASC = new Comparator<SetRunManager>() {
		@Override
		public int compare(SetRunManager o1, SetRunManager o2) {
			return o1.getStartDate().compareTo(o2.getStartDate());
		}
	};

	@Value("${timelapse.timeLapseColNum}")
	public void setTimeLapseColNum(int timeLapseColNum) {
		this.timeLapseColNum = timeLapseColNum;
	}

	/**
	 * Controller for handling the test set run view.
	 * 
	 * @return the model and view
	 */
	@RequestMapping(value = "time_lapse", method = RequestMethod.GET)
	public ModelAndView time_lapse(@RequestParam String setName, @RequestParam(required = false) Integer maxColNum,
			@RequestParam(required = false) String filterKey, @RequestParam(required = false) String filterValue,
			@CookieValue(required = false, defaultValue = "asc") String timeLapseOrder) {
		logger.info("Opening results/time_lapse page.");

		// Set the column number
		int timeLapseColToShow = timeLapseColNum;
		if (maxColNum != null && maxColNum > 0) {
			timeLapseColToShow = maxColNum;
		}
		setSetSorterMethod(timeLapseOrder);

		// Collect the list of SetRunManager which will be displayed
		List<SetRunManager> list = getResultsList(setName, filterKey, filterValue, timeLapseColToShow);

		// gathering time lapse table - key is the test name (name + parameter),
		// the value is a list of sequential runs: statistics + runData
		Map<String, Pair<AtomicInteger, List<TestRunData>>> map = new TreeMap<String, Pair<AtomicInteger, List<TestRunData>>>(); // table
																																	// data
		List<Pair<String, Map<String, String>>> header = new ArrayList<Pair<String, Map<String, String>>>(); // header
																												// info:
																												// time
																												// +
																												// environment
		ChartLineData chartLineData = new ChartLineData(); // data for the pass
															// rate chart
		ChartLineData chartFailData = new ChartLineData(); // data for the fail
															// diagnosis chart -
															// number of bugs,
															// env. issues, etc.
															// per run
		if (!list.isEmpty()) {
			Pair<AtomicInteger, List<TestRunData>> row;
			String key;
			for (int i = 0; i < list.size(); i++) {
				key = null;
				// Fill charts
				fillCharts(setName, list.get(i), chartLineData, chartFailData);
				for (TestRunData data : list.get(i).getFinishedTests().values()) {
					// add header info
					if (key == null) { // fill out the header data, only once
						header.add(new Pair<String, Map<String, String>>(DateUtil.formatHTMLStyle(list.get(i).getStartDate()), data.getEnvironment())); // add
																																						// header
																																						// info
					}
					// fill out the actual column of a test row
					key = testRunDisplayNameGenerator.generateUniqueTestID(data);
					row = map.get(key);
					if (row == null) {
						row = new Pair<AtomicInteger, List<TestRunData>>(new AtomicInteger(0), new ArrayList<TestRunData>());
						map.put(key, row);
						if (i > 0) { // if adding a new test (new row) fill out
										// the missing columns, if there is any
							for (int j = 0; j < i; j++) {
								row.getRight().add(null);
							}
						}
					}
					if (row.getRight().size() < list.size()) {
						row.getRight().add(data);
						// add row statistics
						if (ResultState.PASSED != data.getState()) {
							row.getLeft().incrementAndGet();
						}
					} else {
						map.put("INCONSISTENT TEST NAMING!</br>PLEASE CHANGE IT:</br>" + key, null);
					}
				}
				// fill out the row's columns which were unfilled in this round
				for (Pair<AtomicInteger, List<TestRunData>> checkRow : map.values()) {
					while (checkRow != null && checkRow.getRight().size() < i + 1) {
						checkRow.getRight().add(null);
						checkRow.getLeft().incrementAndGet();
					}
				}
			}
		}

		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("map", map);
		paramMap.put("tableHeader", header);
		paramMap.put("setName", setName);
		paramMap.put("colNum", list.size());
		paramMap.put("maxColNum", timeLapseColToShow);
		paramMap.put("chartLineData", chartLineData);
		paramMap.put("chartFailData", chartFailData);
		return new ModelAndView("results/time_lapse", paramMap);
	}

	private List<SetRunManager> getResultsList(String setName, String filterKey, String filterValue, int timeLapseColToShow) {
		List<SetRunManager> retval = new ArrayList<SetRunManager>();
		// currently no set is transferred to finished sets
		List<SetRunManager> availableSets = new ArrayList<SetRunManager>(runManager.getRunningSets());
		Collections.sort(availableSets, SET_RUN_TIME_ORDER_DESC);
		for (SetRunManager set : availableSets) {
			if (!set.getFinishedTests().isEmpty() && set.getSetName().equals(setName)) {
				if (StringUtils.isNotBlank(filterValue)) {
					String temp = new ArrayList<TestRunData>(set.getFinishedTests().values()).get(0).getEnvironment().get(filterKey);
					if (temp != null && temp.contains(filterValue)) {
						retval.add(set);
					}
				} else {
					retval.add(set);
				}
				if (retval.size() >= timeLapseColToShow) {
					break;
				}
			}
		}

		Collections.sort(retval, timeLapseSortOrder);
		return retval;
	}

	private void setSetSorterMethod(String timeLapseOrder) {
		if ("desc".equals(timeLapseOrder)) {
			timeLapseSortOrder = SET_RUN_TIME_ORDER_DESC;
		} else {
			timeLapseSortOrder = SET_RUN_TIME_ORDER_ASC;
		}
	}

	private void fillCharts(String setName, SetRunManager setRunManager, ChartLineData chartLineData, ChartLineData chartFailData) {
		// Fill chart: pass rate
		chartLineData.addCategory(DateUtil.formatHTMLStyle(setRunManager.getStartDate()));
		chartLineData.addData(setName,
				Float.toString(Math.round((float) setRunManager.getResultStatPassed().get() / (float) setRunManager.getFinishedTestNumber() * 100)));
		// Fill chart: fail diagnosis
		chartFailData.addCategory(DateUtil.formatHTMLStyle(setRunManager.getStartDate()));
		int knownIssues = 0;
		for (Entry<String, AtomicInteger> entry : setRunManager.getTypeStats().entrySet()) {
			chartFailData.addData(entry.getKey(), entry.getValue().toString());
			knownIssues += entry.getValue().get();
		}
		chartFailData.addData("UNKNOWN",
				Integer.toString(setRunManager.getResultStatFailed().get() + setRunManager.getResultStatNA().get() - knownIssues));
	}

}
