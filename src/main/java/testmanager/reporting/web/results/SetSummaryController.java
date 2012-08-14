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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import testmanager.reporting.domain.filtering.SetSummaryFilterCondition;
import testmanager.reporting.domain.reporting.ResultState;
import testmanager.reporting.domain.reporting.TestRunData;
import testmanager.reporting.domain.results.ChartPieData;
import testmanager.reporting.service.reporting.RunManager;
import testmanager.reporting.service.reporting.SetRunManager;
import testmanager.reporting.util.TimeUtil;

/**
 * The Class SetSummaryController.
 *
 * @author Istvan_Pamer
 */
@Controller
@RequestMapping("/results/*")
public class SetSummaryController {

    protected final Log logger = LogFactory.getLog(getClass());

    @Autowired
    private RunManager runManager;

    private static final Comparator<SetRunManager> SET_RUN_TIME_ORDER_DESC = new Comparator<SetRunManager>() {
        @Override
        public int compare(SetRunManager o1, SetRunManager o2) {
            return o2.getStartDate().compareTo(o1.getStartDate());
        }
    };

    @RequestMapping(value = "set_summary", method = RequestMethod.GET)
    public ModelAndView set_summary(@RequestParam String setName, @RequestParam(required=false) String filterKey, @RequestParam(required=false) String filterValue,
            @RequestParam(required=false) String selecteds) {
        logger.info("Opening results/set_summary page.");

        // Prepare custom condition filter
        Map<String, Map<String, String>> selectedsMap = null;
        try {
            if (selecteds != null) {
                selectedsMap = (Map<String, Map<String, String>>) JSONValue.parse(selecteds);
            }
        } catch (Exception e) {
            logger.warn("Failed to parse JSON 'selecteds' on set_summary request: " + selecteds);
        }
        SetSummaryFilterCondition customCondition = new SetSummaryFilterCondition(selectedsMap);
        Set<String> envSet = new HashSet<String>();	// environment keys for the filter
        long sumTestTime = 0;
        int sumTestNumber = 0;
        int sumPassed = 0;
        int sumFailed = 0;
        int sumNA = 0;
        int sumCustom = 0;
        List<SetRunManager> availableSets = new ArrayList<SetRunManager>();	// currently no set is transferred to finished sets

        // Collect the list of SetRunManager which will be displayed
        for (SetRunManager set : runManager.getRunningSets()) {	// only add sets with setName
            if (setName.equals(set.getSetName())
                    && StringUtils.isNotBlank(filterValue)
                    && !set.getFinishedTests().isEmpty()
                    && set.getEnvironment().get(filterKey) != null && set.getEnvironment().get(filterKey).contains(filterValue)) {
                availableSets.add(set);
            }
        }
        if (availableSets.isEmpty()) {	// if no set matched our criteria, add all set run
            for (SetRunManager set : runManager.getRunningSets()) { // only add sets with setName
                if (setName.equals(set.getSetName())) {
                    availableSets.add(set);
                }
            }
        }
        Collections.sort(availableSets, SET_RUN_TIME_ORDER_DESC);

        // Collect detailed data to show
        Map<String, AtomicInteger> chartData = new HashMap<String, AtomicInteger>();
        Set<String> existingMessages = new TreeSet<String>();
        Set<String> existingComments = new TreeSet<String>();
        Set<String> existingTypes = runManager.getErrorTypes();
        String message;
        String comment;
        for (SetRunManager set : availableSets) {
            // fill environment params for the filter
            if (set.getEnvironment() != null && !set.getEnvironment().isEmpty()) {
                envSet.addAll(set.getEnvironment().keySet());
            }
            // get the data
            // Collect Pie Chart Data
            addChartData(chartData, ResultState.PASSED.toString(), set.getResultStatPassed().get());
            int knownIssues = 0;
            for (Entry<String, AtomicInteger> entry : set.getTypeStats().entrySet()) {
                addChartData(chartData, entry.getKey(), entry.getValue().get());
                knownIssues += entry.getValue().get();
            }
            addChartData(chartData, "UNKNOWN", set.getResultStatFailed().get() + set.getResultStatNA().get() - knownIssues);
            // Collect failed run detail data and custom statistic
            set.setCustomStatistic(0);
            for (TestRunData data : set.getAllTestRunData()) {
                message = data.getDisplayErrorMessage();
                comment = data.getErrorComment();
                if (StringUtils.isBlank(message)) {
                    message = "EMPTY MESSAGE";
                }
                if (StringUtils.isBlank(comment)) {
                    comment = "NO COMMENT";
                }
                existingMessages.add(message);
                existingComments.add(comment);
                // Collect custom filter statistics
                if (customCondition.validateCondition(data)) {
                    set.setCustomStatistic(set.getCustomStatistic() + 1);
                }
            }
            // Collect Summary Data
            sumTestTime += set.getExecutionTime();
            sumTestNumber += set.getFinishedTestNumber();
            sumPassed += set.getResultStatPassed().get();
            sumFailed += set.getResultStatFailed().get();
            sumNA += set.getResultStatNA().get();
            sumCustom += set.getCustomStatistic();
        }

        // Create the error summary table
        List<List<String>> errorTable = new ArrayList<List<String>>();
        addSetAsColumn(errorTable, existingMessages);
        addSetAsColumn(errorTable, existingComments);
        addSetAsColumn(errorTable, existingTypes);
        // Create Pie Chart Data
        ChartPieData chartPieData = new ChartPieData();
        for (Entry<String, AtomicInteger> entry : chartData.entrySet()) {
            chartPieData.addData(entry.getKey(), entry.getValue().toString());
        }

        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("setName", setName);
        paramMap.put("availableSets", availableSets);
        paramMap.put("envSet", envSet);
        paramMap.put("chartPieData", chartPieData);
        paramMap.put("sumTestTime", TimeUtil.getElapsedTimeString(sumTestTime));
        paramMap.put("sumTestNumber", sumTestNumber);
        paramMap.put("sumPassed", sumPassed);
        paramMap.put("sumFailed", sumFailed);
        paramMap.put("sumNA", sumNA);
        paramMap.put("sumCustom", sumCustom);
        paramMap.put("errorTable", errorTable);
        return new ModelAndView("results/set_summary", paramMap);
    }

    private void addChartData(Map<String, AtomicInteger> chartData, String key, int value) {
        if (!chartData.containsKey(key)) {
            chartData.put(key, new AtomicInteger(0));
        }
        chartData.get(key).addAndGet(value);
    }

    private void addSetAsColumn(List<List<String>> errorTable, Set<String> set) {
        int i = 0;
        for (String temp : set) {
            if (i >= errorTable.size()) {
                errorTable.add(new ArrayList<String>());
            }
            while (errorTable.get(i).size() < errorTable.get(0).size() - 1) {
                errorTable.get(i).add(null);
            }
            errorTable.get(i).add(temp);
            i++;
        }

        int size = (errorTable.get(0) == null) ? 0 : errorTable.get(0).size();
        for (List<String> list : errorTable) {
            while (list.size() < size) {
                list.add(null);
            }
        }
    }

}
