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
package testmanager.reporting.web.reporting;

import com.google.gson.Gson;
import com.google.gson.internal.StringMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import testmanager.reporting.domain.reporting.ReportDTO;
import testmanager.reporting.service.reporting.RunManager;
import testmanager.reporting.util.DateUtil;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The Class ReportingController handles the reporting requests. Clients can start and stop a test run here.
 *
 * @author Istvan_Pamer
 */
@Controller
@RequestMapping("/reporting/*")
public class ReportingController {

    private static final String RESPONSE_OK = "OK";
    private static final String RESPONSE_BAD = "BAD";

    protected final Log logger = LogFactory.getLog(getClass());

    @Autowired
    private RunManager runManager;

    /**
     * Start.
     *
     * @param testName the name of the test - class + method name or unique ID - MUST BE UNIQUE among all test names
     * @param paramName the name or number of the test parameter "row" - unique for a test
     * @param setName the name of the set which the test belongs to - one test can belong to more sets
     * @param setStartDate the actual start date of the test set run. This will separate multiple runs of a single set.
     * @param testParams the test parameters - Map<String, String> type JSON string
     * @param env the environment variables - Map<String, String> type JSON string
     * @param story Story / Issue / Ticket number for which the test was written
     * @param layer Implementation layer where the test comes from. Eg. web, unit, ...
     * @return string OK on success.
     * @throws ServletException the servlet exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @RequestMapping(value = "start", method = RequestMethod.POST)
    public @ResponseBody String start(@RequestParam String testName, @RequestParam String paramName, @RequestParam String setName, @RequestParam String setStartDate,
            @RequestParam(required=false) String testParams, @RequestParam(required=false) String env,
            @RequestParam(required=false) String story, @RequestParam(required=false) String layer) {

        logger.info("Reporting test START: " + testName + " @ " + paramName);
        String response = RESPONSE_BAD;

        // validate request params
        Date setStart = DateUtil.parse(setStartDate);

        Map<String, String> testParamsMap = null;
        try {
            if (testParams != null) {
                testParamsMap = (Map<String, String>) JSONValue.parse(testParams);
            }
        } catch (Exception e) {
            logger.warn("Failed to parse testParams on start request: " + testParams);
        }

        Map<String, String> envMap = null;
        try {
            if (env != null) {
                envMap = (Map<String, String>) JSONValue.parse(env);
            }
        } catch (Exception e) {
            logger.warn("Failed to parse env on start request: " + env);
        }

        // process request
        if (setStart != null) {
            ReportDTO dto = new ReportDTO();
            dto.setTestName(testName);
            dto.setParamName(paramName);
            dto.setSetName(setName);
            dto.setSetStartDate(setStart);
            dto.setTestparams(testParamsMap);
            dto.setEnv(envMap);
            dto.setLayer((layer == null || layer.isEmpty()) ? setName : layer);

            if (runManager.startTest(dto)) {
                response = RESPONSE_OK;
            } else {
                logger.error("Reporting test START was: " + RESPONSE_BAD + " " + dto.toString());
            }

            for (String str : story.split(" ")) {
                ReportDTO storyDto = new ReportDTO(dto);
                storyDto.setStory(str);
                storyDto.setLayer(dto.getLayer());
                runManager.saveStory(storyDto);
            }
        }

        return response;
    }

    /**
     * Stop.
     *
     * @param testName the name of the test - class + method name or unique ID - MUST BE UNIQUE among all test names
     * @param paramName the name or number of the test parameter "row" - unique for a test
     * @param setName the name of the set which the test belongs to - one test can belong to more sets
     * @param setStartDate the set start date
     * @param result the result
     * @param testParams the testparams - Map<String, String> type JSON string
     * @param msg the detailed error message
     * @param env the environment variables - Map<String, String> type JSON string
     * @return string OK on success.
     * @throws ServletException the servlet exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @RequestMapping(value = "stop", method = RequestMethod.POST)
    public @ResponseBody String stop(@RequestParam String testName, @RequestParam String paramName, @RequestParam String setName, @RequestParam String setStartDate, @RequestParam String result,
            @RequestParam(required=false) String testParams, @RequestParam(required=false) String msg, @RequestParam(required=false) String env,
            @RequestParam(required=false) String checkPoints) {

        logger.info("Reporting test STOP: " + testName + " @ " + paramName);
        String response = RESPONSE_BAD;

        // validate request params
        Date setStart = DateUtil.parse(setStartDate);

        // TODO: refactor into a util class into a generic function call
        Map<String, String> testParamsMap = null;
        try {
            if (testParams != null) {
                testParamsMap = (Map<String, String>) JSONValue.parse(testParams);
            }
        } catch (Exception e) {
            logger.warn("Failed to parse testParams on start request: " + testParams);
        }
        Map<String, String> envMap = null;
        try {
            if (env != null) {
                envMap = (Map<String, String>) JSONValue.parse(env);
            }
        } catch (Exception e) {
            logger.warn("Failed to parse env on start request: " + env);
        }
        List<Map<String, String>> checkPointsMap = null;
        try {
            if (checkPoints != null) {
                checkPointsMap = (List<Map<String, String>>) JSONValue.parse(checkPoints);
            }
        } catch (Exception e) {
            logger.warn("Failed to parse checkPoints on start request: " + checkPoints);
        }

        // set error message correctly
        String message;
        if ("PASSED".equals(result)) {
            message = null;
        } else if (msg != null) {
            message = msg.trim();
        } else {
            message = "NO ERROR MESSAGE";
        }

        // process request
        if (setStart != null) {
            ReportDTO dto = new ReportDTO();
            dto.setTestName(testName);
            dto.setParamName(paramName);
            dto.setSetName(setName);
            dto.setSetStartDate(setStart);
            dto.setResult(result);
            dto.setTestparams(testParamsMap);
            dto.setMsg(message);
            dto.setEnv(envMap);
            dto.setCheckPoints(checkPointsMap);

            if (runManager.stopTest(dto)) {
                response = RESPONSE_OK;
            } else {
                logger.error("Reporting test STOP was: " + RESPONSE_BAD + " " + dto.toString());
            }
        }

        return response;
    }

    @RequestMapping(value = "story-bulk", method = RequestMethod.POST)
    public @ResponseBody String storyBulk(@RequestBody String body) {
        logger.info("Reporting story bulk upload.");
        String response = RESPONSE_OK;

        Gson gson = new Gson();
        Map<String, List<StringMap<String>>> map = gson.fromJson(body, new HashMap<String, List<StringMap<String>>>().getClass());

        ReportDTO dto;
        for (StringMap<String> element : map.get("story")) {
            dto = new ReportDTO();
            dto.setStory(element.get("tag"));
            dto.setTestName(element.get("testName"));
            dto.setLayer(element.get("layer"));

            if (!runManager.saveStory(dto)) {
                logger.error("Reporting test for story: " + RESPONSE_BAD + " " + dto.toString());
            }
        }

        return response;
    }

}
