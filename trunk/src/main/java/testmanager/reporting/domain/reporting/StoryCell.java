package testmanager.reporting.domain.reporting;

import testmanager.reporting.util.DateUtil;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * User: Istvan Pamer
 * Date: 22/04/13
 */
public class StoryCell {
    private Set<StoryCellValue> values = new HashSet<StoryCellValue>();

    public StoryCell() {
        this.values.add(new StoryCellValue(""));
    }

    public void addValue(String value) {
        if (isEmpty()) { values.clear(); }
        values.add(new StoryCellValue(value));
    }

    public Set<StoryCellValue> getValues() {
        return values;
    }

    private boolean isEmpty() {
        boolean result = true;
        for (StoryCellValue val : values) {
            if (!"".equals(val.getValue())) { result = false; }
        }
        return result;
    }

}
