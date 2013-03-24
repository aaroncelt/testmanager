'''
Created on 2013.03.23.

@author: Laszlo Tarcsanyi
@contact: tarcsanyi.laszlo@gmail.com

@license:
This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

@copyright: Copyright (C) 2012  Istvan Pamer
'''
from urllib2 import URLError
import urllib
import urllib2


class ReportManager(object):
    '''
    The Class ReportManager manages the reporting of test 
    and set starts or stops towards TestManager.
    '''
    RESPONSE_OK = "OK"
    DEFAULT_ENCODING = "UTF-8"
    URI_AVAILABILITY_MAIN = "/availability/main"
    URI_REPORTING_START = "/reporting/start"
    URI_REPORTING_STOP = "/reporting/stop"
    PARAM_CHKPOINTS = "checkPoints="
    REPORT_DATE_FORMAT_STRING = "%Y.%m.%d;%H:%M:%S.%f"

    def __init__(self, host, port, uri_root="/testmanager/app"):
        '''
        Instantiates a new report manager.
        :param __host: the TM __host name
        :type __host: str
        :param __port: the TM __port number
        :type __port: int
        :param __uri_root: the app root path, default is /testmanager/app
        :type __uri_root: str
        '''
        self.__host = host
        self.__port = port
        self.__uri_root = uri_root
        
        self.__timeout_main_service = 5000
        self.__timeout_reporting = 60000

    def set_timeout_main_service(self, timeout):
        '''
        Sets the timeout of checking if the main service is online. Default is 3000 ms.
        :param timeout: timeout in millisec
        :type timeout: int
        '''
        self.__timeout_main_service = timeout
        
    def set_timeout_reporting(self, timeout):
        '''
        Sets the timeout for reporting. Default is 60000 ms.
        :param timeout: timeout in millisec
        :type timeout: int
        '''
        self.__timeout_reporting = timeout

    def is_main_service_reachable(self):
        '''
        Checks if the main service is reachable.
        :returns: true if reachable, false otherwise
        '''
        return self.RESPONSE_OK == self.__get_url_response(self.URI_AVAILABILITY_MAIN)
    
    def report_test_start(self, test_name, param_name, set_name, set_start_date, optionals=None):
        '''
        Report test start.
        :param test_name: the test name
        :type test_name: str
        :param param_name: the param name
        :type param_name: str
        :param set_name: name of the test set
        :type set_name: str
        :param set_start_date: when the set started
        :type set_start_date: datetime.datetime
        :param optionals: optional parameters
        :type optionals: dict
        :returns: true if the report success, false otherwise
        '''
        params = self.__get_params(test_name, param_name, set_name, set_start_date, optionals)
        return self.RESPONSE_OK == self.__get_url_response(self.URI_REPORTING_START, params)
        
    def report_test_stop(self, test_name, param_name, set_name, set_start_date, result_state, optionals=None):
        '''
        Report test stop.
        :param test_name: the test name
        :type test_name: str
        :param param_name: the param name
        :type param_name: str
        :param set_name: name of the test set
        :type set_name: str
        :param set_start_date: when the set started
        :type set_start_date: datetime.datetime
        :param result_state: the test result state
        :type result_state: ResultState
        :param optionals: optional parameters
        :type optionals: dict
        :returns: true if the report success, false otherwise
        '''
        params = self.__get_params(test_name, param_name, set_name, set_start_date, optionals)
        params["result"] = result_state
        if optionals:
            if optionals.get_error_message():
                params["msg"]=optionals.get_error_message()
            if optionals.has_checkpoints():
                params["checkPoints"] = optionals.get_checkpoints()
        print params
        return self.RESPONSE_OK == self.__get_url_response(self.URI_REPORTING_STOP, params)
    
    def __get_params(self, test_name, param_name, set_name, set_start_date, optionals):
        retval = {"testName":test_name,
                  "paramName":param_name,
                  "setName":set_name,
                  "setStartDate":set_start_date.strftime(self.REPORT_DATE_FORMAT_STRING)[:-3],
                  }
        if optionals:
            if optionals.get_test_params():
                retval["testParams"] = optionals.get_test_params()
            if optionals.get_environment():
                retval["env"] = optionals.get_environment()
        return retval
        
    def __get_url_response(self, uri, params=None):
        url = self.__get_target_url(uri)
        resp = None
        try:
            resp = urllib2.urlopen(url, urllib.urlencode(params),self.__timeout_reporting).read()
        except URLError:
            pass
        return resp
        
    def __get_target_url(self, uri):
        return "http://" + self.__host + ":" + self.__port + self.__uri_root + uri
