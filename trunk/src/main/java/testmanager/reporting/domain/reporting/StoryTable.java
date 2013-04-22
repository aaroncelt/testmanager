package testmanager.reporting.domain.reporting;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * User: Istvan Pamer
 * Date: 16/04/13
 */
public class StoryTable {

    private Map<String, List<StoryCell>> storyTable = new ConcurrentSkipListMap<String, List<StoryCell>>();
    private Map<String, Integer> columns = new ConcurrentHashMap<String, Integer>();

    public boolean save(ReportDTO dto) {
        List<StoryCell> row;
        if (storyTable.get(dto.getStory()) == null) {
            storyTable.put(dto.getStory(), new ArrayList<StoryCell>());
        }
        row = storyTable.get(dto.getStory());

        Integer col;
        if (!columns.containsKey(dto.getLayer())) {
            int colNum = columns.keySet().size();
            // add new column to columns
            columns.put(dto.getLayer(), new Integer(colNum));
            // expand existing lists
            for (List<StoryCell> expandingRow : storyTable.values()) {
                expandRow(expandingRow);
            }
        }
        col = columns.get(dto.getLayer());

        addElement(row, col, dto.getTestName());

        return true;
    }

    private void addElement(List<StoryCell> list, int index, String value) {
        expandRow(list);
        list.get(index).addValue(value);
    }

    private void expandRow(List<StoryCell> list) {
        while (list.size() < columns.keySet().size()) {
            list.add(new StoryCell());
        }
    }

    public Map<String, List<StoryCell>> getTable() {
        return storyTable;
    }

    public List<String> getColumns() {
        List<String> result = new ArrayList<String>();
        for (Map.Entry<String, Integer> e : columns.entrySet()) {
            if (e.getValue() >= result.size()) {
                result.add(e.getKey());
            } else {
                result.add(e.getValue(), e.getKey());
            }
        }
        return result;
    }

    public void cleanStoryTable() {
        synchronized (storyTable) {
            // remove expired values
            for (List<StoryCell> list : storyTable.values()) {
                for (StoryCell cell : list) {
                    Iterator<StoryCellValue> iterator = cell.getValues().iterator();
                    while (iterator.hasNext()) {
                        if (iterator.next().isExpired()) {
                            iterator.remove();
                        }
                    }
                }
            }
            // remove empty stories
            Iterator<Map.Entry<String, List<StoryCell>>> iterator = storyTable.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, List<StoryCell>> e = iterator.next();
                boolean rowEmpty = true;
                for (StoryCell cell : e.getValue()) {
                    if (!cell.getValues().isEmpty()) {
                        rowEmpty = false;
                    }
                }
                if (rowEmpty) {
                    storyTable.remove(e.getKey());
                }
            }
        }
    }

}
