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
import json

class ReportParams(object):

    

    def __init__(self):
        '''
        Constructor
        '''
        self.__environment = {}
        self.__test_params = {}
        self.__checkpoints = []
        self.__error_message = None
        
    def clear_params(self):
        self.__init__()
        return self
    
    def get_error_message(self):
        return self.__error_message
    
    def set_error_message(self, msg):
        self.__error_message = msg
        return self
    
    def get_test_params(self):
        return json.dumps(self.__test_params)
    
    def set_test_params(self, key, value):
        self.__test_params[key] = value
        return self
    
    def get_environment(self):
        return json.dumps(self.__environment)

    def set_environment(self, key, value):
        self.__environment[key] = value
        return self
    
    def has_checkpoints(self):
        return self.__checkpoints
    
    def get_checkpoints(self):
        return json.dumps(self.__checkpoints)
    
    def set_checkpoint(self, message, main_type, sub_type, state):
        self.__checkpoints.append({"message":message,
                                             "mainType":main_type,
                                             "subType":sub_type,
                                             "state":state})
        return self
