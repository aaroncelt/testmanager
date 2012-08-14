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

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Utility class for handling dates.
 *
 * @author Istvan_Pamer
 */
public final class DateUtil {

    protected static final Log logger = LogFactory.getLog(DateUtil.class);

    private static final String REPORT_DATE_FORMAT_STRING = "yyyy.MM.dd;HH:mm:ss.SSS";
    private static final String REPORT_DATE_FORMAT_HTML_STRING = "yyyy/MM/dd HH:mm";
    private static final long ONE_DAY_LONG = 86400000L;

    private DateUtil() {
    }

    /**
     * Parses the date.
     *
     * @param dateString the date string
     * @return the date
     */
    public static Date parse(String dateString) {
        Date parsed = null;
        try {
            parsed = new SimpleDateFormat(REPORT_DATE_FORMAT_STRING).parse(dateString); // SimpleDateFormat is not Thread safe
        } catch(Exception e) {
            logger.info("ERROR: Cannot parse \"" + dateString + "\"");
        }
        return parsed;
    }

    /**
     * Format the given date.
     *
     * @param date the date
     * @return the string
     */
    public static String format(Date date) {
        return new SimpleDateFormat(REPORT_DATE_FORMAT_STRING).format(date);
    }

    /**
     * Format html style.
     *
     * @param date the date
     * @return the string
     */
    public static String formatHTMLStyle(Date date) {
        return new SimpleDateFormat(REPORT_DATE_FORMAT_HTML_STRING).format(date);
    }

    /**
     * Gets the date x days before today.
     *
     * @param days the days
     * @return the date before days
     */
    public static Date getDateBeforeDays(int days) {
        return new Date(new Date().getTime() - days * ONE_DAY_LONG);
    }

}
