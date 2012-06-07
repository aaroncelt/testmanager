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
package testmanager.reporting.util;

/**
 * The Class TimeUtil.
 *
 * @author Istvan_Pamer
 */
public final class TimeUtil {

    private TimeUtil() {
    }

    /**
     * Gets the elapsed time string.
     *
     * @param elapsedTime the elapsed time
     * @return the elapsed time string
     */
    public static String getElapsedTimeString(long elapsedTime) {
        String format = String.format("%%0%dd", 2);
        long elapsedSec = elapsedTime / 1000;
        String seconds = String.format(format, elapsedSec % 60);
        String minutes = String.format(format, (elapsedSec % 3600) / 60);
        String hours = String.format(format, elapsedSec / 3600);
        return hours + ":" + minutes + ":" + seconds;
    }

}
