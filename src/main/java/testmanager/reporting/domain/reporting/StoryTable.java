package testmanager.reporting.domain.reporting;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * User: Istvan Pamer
 * Date: 16/04/13
 */
public class StoryTable {

    // Story, Layer, Values
    private Map<String, Map<String, Set<StoryCellValue>>> storyTable = new ConcurrentSkipListMap<String, Map<String, Set<StoryCellValue>>>();

    public boolean save(ReportDTO dto) {
        Map<String, Set<StoryCellValue>> row;
        if (storyTable.get(dto.getStory()) == null) {
            storyTable.put(dto.getStory(), new TreeMap<String, Set<StoryCellValue>>());
        }
        row = storyTable.get(dto.getStory());

        Set<StoryCellValue> cells;
        if (row.get(dto.getLayer()) == null) {
            row.put(dto.getLayer(), new TreeSet<StoryCellValue>());
        }
        cells = row.get(dto.getLayer());

        cells.add(new StoryCellValue(dto.getTestName()));
        return true;
    }

    public Map<String, Map<String, Set<StoryCellValue>>> getTable() {
        return storyTable;
    }

    public void cleanStoryTable() {
        synchronized (storyTable) {
            for (Map<String, Set<StoryCellValue>> map : storyTable.values()) {
                Iterator<String> iterator = map.keySet().iterator();
                while (iterator.hasNext()) {
                    String key = iterator.next();
                    if (isCellSetEmpty(map.get(key))) {
                        iterator.remove();
                    }
                }
            }

            Iterator<String> iterator = storyTable.keySet().iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                if (storyTable.get(key).isEmpty()) {
                    iterator.remove();
                }
            }
        }
    }

    private boolean isCellSetEmpty(Set<StoryCellValue> set) {
        boolean result = true;
        Iterator<StoryCellValue> iterator = set.iterator();
        while (iterator.hasNext()) {
            StoryCellValue cell = iterator.next();
            if (!cell.isExpired()) {
                result = false;
            } else {
                iterator.remove();
            }
        }
        return result;
    }

}
