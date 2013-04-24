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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import testmanager.reporting.domain.reporting.Pair;
import testmanager.reporting.service.reporting.DataLifecycleManager;
import testmanager.reporting.service.reporting.RunManager;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * The Class ResultIndexController.
 *
 * @author Istvan_Pamer
 */
@Controller
@RequestMapping("/results/*")
public class StoryTableController {

    protected final Log logger = LogFactory.getLog(getClass());

    @Autowired
    private RunManager runManager;
    @Autowired
    private DataLifecycleManager dataLifecycleManager;

    /**
     * Controller for handling the test set run view.
     *
     * @return the model and view
     */
    @RequestMapping(value = "story", method = RequestMethod.GET)
    public ModelAndView story() {
        logger.info("Opening results/story page.");

        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("storyTable", runManager.getStoryTable().getTable());
        return new ModelAndView("results/story", paramMap);
    }

    @RequestMapping(value = "clean-stories", method = RequestMethod.GET)
    public RedirectView cleanStories() {
        logger.info("Opening results/clean-stories page.");

        runManager.getStoryTable().cleanStoryTable();
        return new RedirectView("story");
    }

}
