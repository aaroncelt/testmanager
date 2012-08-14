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
import java.util.List;

import org.apache.commons.lang.StringUtils;

import testmanager.reporting.domain.reporting.Pair;

/**
 * The Class ChartLineData.
 *
 * @author Istvan_Pamer
 */
public class ChartPieData {

    private List<PiePair> datas = new ArrayList<PiePair>();

    public void addData(String name, String value) {
        datas.add(new PiePair(name, value));
    }

    public String getDataString() {
        return StringUtils.join(datas, ", ");
    }

    private class PiePair extends Pair<String, String> {
        public PiePair(String name, String value) {
            super(name, value);
        }

        @Override
        public String toString() {
            return "['" + getLeft() + "', " + getRight() + "]";	// ['Firefox', 45.0]
        }
    }

}
