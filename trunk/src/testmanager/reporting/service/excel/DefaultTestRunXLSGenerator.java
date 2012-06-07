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
package testmanager.reporting.service.excel;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import jxl.CellView;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.Colour;
import jxl.format.UnderlineStyle;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.apache.commons.lang.StringUtils;

import testmanager.reporting.domain.reporting.ResultState;
import testmanager.reporting.domain.reporting.TestRunData;

/**
 * The Class TestRunXLSGenerator.
 *
 * @author Istvan_Pamer
 */
public class DefaultTestRunXLSGenerator implements TestRunXLSGenerator {

    private static final int FONT_SIZE = 10;

    private static final int COL_0 = 0;
    private static final int COL_1 = 1;
    private static final int COL_2 = 2;
    private static final int COL_3 = 3;
    private static final int COL_4 = 4;
    private static final int COL_5 = 5;
    private static final int COL_6 = 6;
    private static final int COL_7 = 7;
    private static final int COL_0_SIZE = 120;
    private static final int COL_1_SIZE = 20;
    private static final int COL_2_SIZE = 5;
    private static final int COL_3_SIZE = 5;
    private static final int COL_4_SIZE = 5;
    private static final int COL_5_SIZE = 40;
    private static final int COL_6_SIZE = 8;

    private static final Comparator<TestRunData> TEST_RUN_NAME_ORDER_ASC = new Comparator<TestRunData>() {
        @Override
        public int compare(TestRunData o1, TestRunData o2) {
            return (o1.getTestName().equals(o2.getTestName())) ? o1.getParamName().compareTo(o2.getParamName()) : o1.getTestName().compareTo(o2.getTestName());
        }
    };

    private File temp;

    public DefaultTestRunXLSGenerator() {
    }

    @Override
    public File createTestRunXLS(List<TestRunData> runDataList) throws Exception {
        if (runDataList == null) {
            throw new IllegalArgumentException("The list of test result data is null!");
        }

        Collections.sort(runDataList, TEST_RUN_NAME_ORDER_ASC);
        temp = File.createTempFile("testRunXLS", ".xls");

        // Initialize XLS ============================================
        WorkbookSettings ws = new WorkbookSettings();
        ws.setLocale(new Locale("en", "EN"));
        WritableWorkbook workbook = Workbook.createWorkbook(temp, ws);
        WritableSheet s = workbook.createSheet("Sheet1", 0);

        /* Format the Font */
        WritableFont wf = new WritableFont(WritableFont.ARIAL, FONT_SIZE, WritableFont.BOLD);
        WritableCellFormat cf = new WritableCellFormat(wf);
        cf.setWrap(true);
        WritableFont linkFormat = new WritableFont(WritableFont.ARIAL, FONT_SIZE, WritableFont.BOLD, false, UnderlineStyle.SINGLE, Colour.BLUE);
        WritableCellFormat clf = new WritableCellFormat(linkFormat);
        clf.setWrap(true);
        // pass / fail / na
        WritableFont wf1 = new WritableFont(WritableFont.ARIAL, FONT_SIZE);
        WritableCellFormat cfPass = new WritableCellFormat(wf1);
        WritableCellFormat cfFail = new WritableCellFormat(wf1);
        WritableCellFormat cfNota = new WritableCellFormat(wf1);
        cfPass.setBackground(Colour.LIGHT_GREEN);
        cfFail.setBackground(Colour.RED);
        cfNota.setBackground(Colour.AQUA);

        // Set size of columns
        CellView cv = new CellView();
        cv.setAutosize(true);
        s.setColumnView(COL_0, COL_0_SIZE); // 1. - colnum, 2. param - [number - colsize] or cellview
        s.setColumnView(COL_1, COL_1_SIZE);
        s.setColumnView(COL_2, COL_2_SIZE);
        s.setColumnView(COL_3, COL_3_SIZE);
        s.setColumnView(COL_4, COL_4_SIZE);
        s.setColumnView(COL_5, COL_5_SIZE);
        s.setColumnView(COL_6, COL_6_SIZE);
        s.setColumnView(COL_7, cv);

        // Set header row
        if (!runDataList.isEmpty()) {
            s.addCell(new Label(COL_0, 0, runDataList.get(0).getSetName() + " - " + runDataList.get(0).getSetStartDate().toString(), cf));
        }
        s.addCell(new Label(COL_0, 1, "Test Name", cf)); // column, row, Label text, cell format
        s.addCell(new Label(COL_1, 1, "Param Name", cf));
        s.addCell(new Label(COL_2, 1, "Pass", cf));
        s.addCell(new Label(COL_3, 1, "Fail", cf));
        s.addCell(new Label(COL_4, 1, "N/A", cf));
        s.addCell(new Label(COL_5, 1, "Error Message", cf));
        s.addCell(new Label(COL_6, 1, "Type", cf));
        s.addCell(new Label(COL_7, 1, "Comment", cf));

        // Write data
        Map<String, Map<String, AtomicInteger>> map = new HashMap<String, Map<String,AtomicInteger>>();

        int i = 2;
        for (TestRunData runData : runDataList) {
            s.addCell(new Label(COL_0, i, runData.getTestName()));
            s.addCell(new Label(COL_1, i, runData.getParamName()));

            if (ResultState.PASSED.equals(runData.getState())) {
                s.addCell(new Number(COL_2, i, 1, cfPass));
                s.addCell(new Number(COL_3, i, 0, cfPass));
                s.addCell(new Number(COL_4, i, 0, cfPass));
            } else if (ResultState.NOT_AVAILABLE.equals(runData.getState())) {
                s.addCell(new Number(COL_2, i, 0, cfNota));
                s.addCell(new Number(COL_3, i, 0, cfNota));
                s.addCell(new Number(COL_4, i, 1, cfNota));
            } else {
                s.addCell(new Number(COL_2, i, 0, cfFail));
                s.addCell(new Number(COL_3, i, 1, cfFail));
                s.addCell(new Number(COL_4, i, 0, cfFail));
            }

            s.addCell(new Label(COL_5, i, runData.getErrorMessage()));
            s.addCell(new Label(COL_6, i, runData.getErrorType()));
            s.addCell(new Label(COL_7, i, runData.getErrorComment()));
            i++;

            // Gather statistics
            if (!ResultState.PASSED.equals(runData.getState())) {
                gatherStats(map, runData);
            }
        }

        // write statistics
        i++;
        for (String type : map.keySet()) {
            s.addCell(new Label(COL_4, i, type));
            for (String errorMessage : map.get(type).keySet()) {
                s.addCell(new Label(COL_5, i, errorMessage));
                s.addCell(new Number(COL_6, i, map.get(type).get(errorMessage).intValue()));
                i++;
            }
            i++;
        }

        workbook.write();
        workbook.close();

        return temp;
    }

    private void gatherStats(Map<String, Map<String, AtomicInteger>> map, TestRunData runData) {
        Map<String, AtomicInteger> m;
        String type = runData.getErrorType();
        String message = runData.getErrorMessage();
        if (StringUtils.isBlank(type)) {
            type = "UNFILLED";
        }
        if (StringUtils.isBlank(message)) {
            message = "EMPTY MESSAGE";
        }
        if (map.get(type) != null) {
            m = map.get(type);
            if (m.get(message) != null) {
                m.get(message).incrementAndGet();
            } else {
                m.put(message, new AtomicInteger(1));
            }
        } else {
            m = new HashMap<String, AtomicInteger>();
            m.put(message, new AtomicInteger(1));
            map.put(type, m);
        }
    }

}
