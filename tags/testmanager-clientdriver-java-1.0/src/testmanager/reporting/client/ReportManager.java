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

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * The Class ReportManager manages the reporting of test and set starts or stops towards TestManager.
 *
 * @author Istvan_Pamer
 */
public class ReportManager {

    private static final String REQUEST_METHOD = "POST";
    private static final String RESPONSE_OK = "OK";
    private static final String DEFAULT_ENCODING = "UTF-8";
    private static final String URI_AVAILABILITY_MAIN = "/availability/main";
    private static final String URI_REPORTING_START = "/reporting/start";
    private static final String URI_REPORTING_STOP = "/reporting/stop";
    private static final String PARAM_TESTNAME = "testName=";
    private static final String PARAM_PARAMNAME = "paramName=";
    private static final String PARAM_SETNAME = "setName=";
    private static final String PARAM_SETSTARTDATE = "setStartDate=";
    private static final String PARAM_RESULTSTATE = "result=";
    private static final String PARAM_TESTPARAMS = "testParams=";
    private static final String PARAM_MSG = "msg=";
    private static final String PARAM_ENV = "env=";
    private static final String PARAM_CHKPOINTS = "checkPoints=";
    private static final String REPORT_DATE_FORMAT_STRING = "yyyy.MM.dd;HH:mm:ss.SSS";
    private static final int TIMEOUT_MAIN_SERVICE = 5000;
    private static final int TIMEOUT_REPORTING = 60000;

    private String uriRoot = "/TestManager/app";
    private String host;
    private String port;
    private int timeoutMainService = TIMEOUT_MAIN_SERVICE;
    private int timeoutReporting = TIMEOUT_REPORTING;

    public enum ResultState {
        PASSED, FAILED, NOT_AVAILABLE, ABORTED;
    }

    /**
     * Instantiates a new report manager.
     *
     * @param host the host
     * @param port the port
     */
    public ReportManager(String host, String port) {
        this.host = host;
        this.port = port;
    }

    /**
     * Instantiates a new report manager.
     *
     * @param host the host
     * @param port the port
     * @param uriRoot the uri of the application root
     */
    public ReportManager(String host, String port, String uriRoot) {
        this.host = host;
        this.port = port;
        this.uriRoot = uriRoot;
    }

    /**
     * Sets the timeout of checking if the main service is online. Default is 3000 ms.
     *
     * @param timeoutMainService the new timeout main service
     */
    public void setTimeoutMainService(int timeoutMainService) {
        if (timeoutMainService > 0) {
            this.timeoutMainService = timeoutMainService;
        }
    }

    /**
     * Sets the timeout for reporting. Default is 30000 ms.
     *
     * @param timeoutReporting the new timeout reporting
     */
    public void setTimeoutReporting(int timeoutReporting) {
        if (timeoutReporting > 0) {
            this.timeoutReporting = timeoutReporting;
        }
    }

    /**
     * Checks if the main service is reachable.
     *
     * @return true, if the main service is reachable
     */
    public boolean isMainServiceReachable() {
        return (RESPONSE_OK.equals(readUri(URI_AVAILABILITY_MAIN, timeoutMainService))) ? true : false;
    }

    /**
     * Report test start.
     *
     * @param testName the test name
     * @param paramName the param name
     * @param setName the set name
     * @param setStartDate the set start date
     * @param optionals the optionals
     * @return true, if successful
     */
    public boolean reportTestStart(String testName, String paramName, String setName, Date setStartDate, ReportParams optionals) {
        // EXAMPLE:
        // http://localhost:8080/TestManager/app/reporting/start?testName=test1&setName=smoke&setStartDate=2011.08.15;15:50:49.123
        boolean result = false;
        String urlParams;
        try {
            // if some of them fail, skip request
            urlParams = PARAM_TESTNAME + URLEncoder.encode(testName, DEFAULT_ENCODING);
            urlParams += "&" + PARAM_PARAMNAME + URLEncoder.encode(paramName, DEFAULT_ENCODING);
            urlParams += "&" + PARAM_SETNAME + URLEncoder.encode(setName, DEFAULT_ENCODING);
            urlParams += "&" + PARAM_SETSTARTDATE + URLEncoder.encode(new SimpleDateFormat(REPORT_DATE_FORMAT_STRING).format(setStartDate), DEFAULT_ENCODING);
            if (optionals != null) {
                if (optionals.getTestParams() != null) {
                    urlParams += "&" + PARAM_TESTPARAMS + URLEncoder.encode(optionals.getTestParams(), DEFAULT_ENCODING);
                }
                if (optionals.getEnvironment() != null) {
                    urlParams += "&" + PARAM_ENV + URLEncoder.encode(optionals.getEnvironment(), DEFAULT_ENCODING);
                }
            }
            result = (RESPONSE_OK.equals(readUri(URI_REPORTING_START, urlParams, timeoutReporting))) ? true : false;
        } catch (UnsupportedEncodingException e) {
            //e.printStackTrace();
        }
        return result;
    }

    /**
     * Report test stop.
     *
     * @param testName the test name
     * @param paramName the param name
     * @param setName the set name
     * @param setStartDate the set start date
     * @param resultState the result state
     * @param optionals the optionals
     * @return true, if successful
     */
    public boolean reportTestStop(String testName, String paramName, String setName, Date setStartDate, ResultState resultState, ReportParams optionals) {
        // EXAMPLE:
        // http://localhost:8080/TestManager/app/reporting/stop?testName=test1&setName=smoke&setStartDate=2011.08.15;15:50:49.123&result=PASSED
        boolean result = false;
        String urlParams;
        try {
            // if some of them fail, skip request
            urlParams = PARAM_TESTNAME + URLEncoder.encode(testName, DEFAULT_ENCODING);
            urlParams += "&" + PARAM_PARAMNAME + URLEncoder.encode(paramName, DEFAULT_ENCODING);
            urlParams += "&" + PARAM_SETNAME + URLEncoder.encode(setName, DEFAULT_ENCODING);
            urlParams += "&" + PARAM_SETSTARTDATE + URLEncoder.encode(new SimpleDateFormat(REPORT_DATE_FORMAT_STRING).format(setStartDate), DEFAULT_ENCODING);
            urlParams += "&" + PARAM_RESULTSTATE + URLEncoder.encode(resultState.toString(), DEFAULT_ENCODING);
            if (optionals != null) {
                if (optionals.getTestParams() != null) {
                    urlParams += "&" + PARAM_TESTPARAMS + URLEncoder.encode(optionals.getTestParams(), DEFAULT_ENCODING);
                }
                if (optionals.getErrorMessage() != null) {
                    urlParams += "&" + PARAM_MSG + URLEncoder.encode(optionals.getErrorMessage(), DEFAULT_ENCODING);
                }
                if (optionals.getEnvironment() != null) {
                    urlParams += "&" + PARAM_ENV + URLEncoder.encode(optionals.getEnvironment(), DEFAULT_ENCODING);
                }
                if (optionals.hasCheckPoints()) {
                    urlParams += "&" + PARAM_CHKPOINTS + URLEncoder.encode(optionals.getCheckPoints(), DEFAULT_ENCODING);
                }
            }
            result = (RESPONSE_OK.equals(readUri(URI_REPORTING_STOP, urlParams, timeoutReporting))) ? true : false;
        } catch (UnsupportedEncodingException e) {
            //e.printStackTrace();
        }
        return result;
    }

    private String readUri(String uri, int timeout) {
        return readUri(uri, null, timeout);
    }

    private String readUri(String uri, String urlParams, int timeout) {
        String result = null;
        HttpURLConnection conn = null;

        try {
            URL url = new URL("http://" + host + ":" + port + uriRoot + uri);
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(timeout);
            conn.setRequestMethod(REQUEST_METHOD);
            conn.setUseCaches (false);
            conn.setDoInput(true);
            conn.setDoOutput(true);

            //Send request
            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
            if (urlParams != null) {
                wr.writeBytes(urlParams);
            }
            wr.flush();
            wr.close();

            //Get Response
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream(), DEFAULT_ENCODING));
            String line;
            StringBuilder response = new StringBuilder();
            while((line = rd.readLine()) != null) {
              response.append(line);
            }
            rd.close();
            result = response.toString();
        } catch (MalformedURLException e) {
            //e.printStackTrace();
        } catch (ProtocolException e) {
            //e.printStackTrace();
        } catch (IOException e) {
            //e.printStackTrace();
        } finally {
            if(conn != null) {
                conn.disconnect();
            }
        }

        return result;
    }

}
