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

import testmanager.reporting.service.reporting.DataLifecycleManager;
import testmanager.reporting.service.reporting.ErrorCommentManager;

@Controller
@RequestMapping("/results/*")
public class CommentController {

    protected final Log logger = LogFactory.getLog(getClass());

    @Autowired
    private ErrorCommentManager errorCommentManager;
    @Autowired
    private DataLifecycleManager dataLifecycleManager;

    /**
     * Start.
     *
     * @param testName the name of the test - class + method name or unique ID - MUST BE UNIQUE among all test names
     * @param paramName the name or number of the test parameter "row" - unique for a test
     * @param setName the name of the set which the test belongs to - one test can belong to more sets
     * @param setStartDate the actual start date of the test set run. This will separate multiple runs of a single set.
     * @param testParams the test parameters - Map<String, String> type JSON string
     * @param env the environment variables - Map<String, String> type JSON string
     * @return string OK on success.
     */
    @RequestMapping(value = "comments", method = RequestMethod.GET)
    public ModelAndView comments() {
        return new ModelAndView("results/comments", "comments", errorCommentManager.getErrorComments());
    }

    @RequestMapping(value = "deleteComment", method = RequestMethod.GET)
    public RedirectView delete(@RequestParam String errorMessage, @RequestParam String id) {
        logger.info("Deleting test comment.");
        dataLifecycleManager.removeComment(errorMessage, Integer.parseInt(id));
        return new RedirectView("comments");
    }

}
