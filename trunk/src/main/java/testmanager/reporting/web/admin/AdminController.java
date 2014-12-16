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
package testmanager.reporting.web.admin;

import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import testmanager.reporting.domain.reporting.ErrorComment;
import testmanager.reporting.service.reporting.DataLifecycleManager;

import com.google.common.collect.Maps;

/**
 * Controller for the administration page.
 *
 * @author Istvan_Pamer
 */
@Controller
@RequestMapping("/admin/*")
public class AdminController {

    protected final Log logger = LogFactory.getLog(getClass());

    private int daysToKeepInDatabase = 30;

    @Autowired
    private DataLifecycleManager dataLifecycleManager;

    @Value("${memoryCleanJob.daysToKeepInDatabase}")
    public void setDaysToKeepInDatabase(int daysToKeepInDatabase) {
        this.daysToKeepInDatabase = daysToKeepInDatabase;
    }

    @RequestMapping(value = "index", method = RequestMethod.GET)
    public ModelAndView comments() {
        Map<String, ErrorComment> errorComments = dataLifecycleManager.getErrorCommentManager().getErrorCommentPatterns();

        Map<String, Object> data = Maps.newHashMap();
        data.put("daysToKeepInDatabase", daysToKeepInDatabase);
        data.put("errorComments", errorComments);
        data.put("errorTypes", dataLifecycleManager.getRunManager().getErrorTypes());

        return new ModelAndView("admin/index", data);
    }

    @RequestMapping(value = "deleteOldTestRuns", method = RequestMethod.GET)
    public RedirectView delete(@RequestParam int daysToKeepInDatabase) {
        logger.info("Deleting test run data older than " + daysToKeepInDatabase + " day(s).");
        dataLifecycleManager.removeOldTestRuns(daysToKeepInDatabase);
        return new RedirectView("index");
    }

    @RequestMapping(value = "deleteErrorCommentPattern", method = RequestMethod.GET)
    public RedirectView deleteErrorCommentPattern(@RequestParam String pattern) {
        logger.info("Deleting error comment pattern: " + pattern);
        dataLifecycleManager.getErrorCommentManager().deleteErrorCommentPattern(pattern);
        return new RedirectView("index");
    }

    @RequestMapping(value = "addErrorCommentPattern", method = RequestMethod.GET)
    public RedirectView addErrorCommentPattern(@RequestParam String pattern, @RequestParam String type, @RequestParam String comment) {
        if (StringUtils.isNotBlank(pattern) && isValidRegex(pattern)) {
            logger.info("Adding error comment pattern: " + pattern + " - " + type + " " + comment);
            dataLifecycleManager.getErrorCommentManager().addErrorCommentPattern(pattern, type, comment);
        } else {
            logger.info("Empty pattern not added: " + pattern + " - " + type + " " + comment);
        }
        return new RedirectView("index");
    }

    private boolean isValidRegex(String pattern) {
        boolean retval = true;
        try {
            Pattern.compile(pattern);
        } catch (PatternSyntaxException e) {
            retval = false;
        }
        return retval;
    }
}
