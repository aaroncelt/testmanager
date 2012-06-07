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
package testmanager.reporting.domain.results;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

/**
 * The Class ChartLineData.
 *
 * @author Istvan_Pamer
 */
public class ChartLineData {

    private List<String> categories = new ArrayList<String>();
    Map<String, List<String>> map = new HashMap<String, List<String>>();

    public void addCategory(String category) {
        categories.add("'" + category + "'");
    }

    public String getCategoryString() {
        return "[" + StringUtils.join(categories, ", ") + "]";
    }

    public void addData(String name, String data) {
        if (!map.containsKey(name)) {
            map.put(name, new ArrayList<String>());
        }
        map.get(name).add(data);
    }

    public String getDataString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        List<String> list = new ArrayList<String>();
        for (String key : map.keySet()) {
            list.add("{ name: '" + key + "', data: [" + StringUtils.join(map.get(key), ", ") + "] }");
        }
        builder.append(StringUtils.join(list, ", "));
        builder.append("]");
        return builder.toString();
    }

}
