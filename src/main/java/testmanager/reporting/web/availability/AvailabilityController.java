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
package testmanager.reporting.web.availability;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * The Class AvailabilityController. Responses to service availability requests.
 *
 * @author Istvan_Pamer
 */
@Controller
@RequestMapping("/availability/*")
public class AvailabilityController {

    //protected final Log logger = LogFactory.getLog(getClass());

    /**
     * Checks the main application.
     *
     * @return OK if TestManager is up and running
     */
    @RequestMapping(value = "main", method = RequestMethod.POST)
    public @ResponseBody String main() {
        return "OK";
    }

}
