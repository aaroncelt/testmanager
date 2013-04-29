package testmanager.reporting.domain.reporting;

import testmanager.reporting.util.DateUtil;

import java.util.Date;

/**
 * User: Istvan Pamer
 * Date: 22/04/13
 */
public class StoryCellValue implements Comparable<StoryCellValue> {
    public static final int DAYS_TO_EXPIRE = 1;

    private String value;
    private Date creationDate = new Date();
    private boolean expired = false;

    public StoryCellValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public boolean isExpired() {
        if (!expired && creationDate.before(DateUtil.getDateBeforeDays(DAYS_TO_EXPIRE))) {
            expired = true;
        }
        return expired;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StoryCellValue cellValue = (StoryCellValue) o;

        if (value != null ? !value.equals(cellValue.value) : cellValue.value != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return value != null ? value.hashCode() : 0;
    }

    @Override
    public int compareTo(StoryCellValue o) {
        return value.compareTo(o.getValue());
    }
}
