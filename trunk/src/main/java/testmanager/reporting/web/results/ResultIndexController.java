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
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import testmanager.reporting.service.reporting.DataLifecycleManager;
import testmanager.reporting.service.reporting.RunManager;
import testmanager.reporting.service.reporting.SetRunManager;

/**
 * The Class ResultIndexController.
 *
 * @author Istvan_Pamer
 */
@Controller
@RequestMapping("/results/*")
public class ResultIndexController {

    protected final Log logger = LogFactory.getLog(getClass());

    @Autowired
    private RunManager runManager;
    @Autowired
    private DataLifecycleManager dataLifecycleManager;

//    private static final Comparator<SetRunManager> SET_RUN_TIME_ORDER_ASC = new Comparator<SetRunManager>() {
//    	@Override
//    	public int compare(SetRunManager o1, SetRunManager o2) {
//    		return o1.getStartDate().compareTo(o2.getStartDate());
//    	};
//    };
    private static final Comparator<SetRunManager> SET_RUN_TIME_ORDER_DESC = new Comparator<SetRunManager>() {
        @Override
        public int compare(SetRunManager o1, SetRunManager o2) {
            return o2.getStartDate().compareTo(o1.getStartDate());
        }
    };

    /**
     * Controller for handling the test set run view.
     *
     * @return the model and view
     */
    @RequestMapping(value = "index", method = RequestMethod.GET)
    public ModelAndView index() {
        logger.info("Opening results/index page.");

        List<SetRunManager> list = new ArrayList<SetRunManager>();
        list.addAll(runManager.getRunningSets());
        list.addAll(runManager.getFinishedSets());
        Collections.sort(list, SET_RUN_TIME_ORDER_DESC);

        Map<String, List<SetRunManager>> map = new TreeMap<String, List<SetRunManager>>();	// result object for gathering the sets to show
        String setName;
        Set<String> envSet = new HashSet<String>();	// environment keys for the filter
        for (SetRunManager set : list) {
            setName = set.getSetName();
            if (map.get(setName) == null) {
                map.put(setName, new ArrayList<SetRunManager>());
                // Fill environment list for summary page filter
                if (set.getEnvironment() != null && !set.getEnvironment().isEmpty()) {
                    envSet.addAll(set.getEnvironment().keySet());
                }
            }
            map.get(setName).add(set);
        }

        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("map", map);
        paramMap.put("envSet", envSet);
        return new ModelAndView("results/index", paramMap);
    }

    @RequestMapping(value = "delete", method = RequestMethod.GET)
    public RedirectView delete(@RequestParam String setId) {
        logger.info("Deleting test run.");
        dataLifecycleManager.deleteSetRun(setId, true);
        return new RedirectView("index");
    }

}
