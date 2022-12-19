package de.diegruender49.smokinghabit.placeholder;


import java.text.DateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class PlaceholderContent {

    /**
     * An array of sample (placeholder) items.
     */
    public static final List<PlaceholderItem> ITEMS = new ArrayList<PlaceholderItem>();

    /**
     * A map of sample (placeholder) items, by ID.
     */
    public static final Map<String, PlaceholderItem> ITEM_MAP = new HashMap<String, PlaceholderItem>();

    public static void addItem(PlaceholderItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    public static PlaceholderItem createPlaceholderItem(int position, long aDateTime, String desc) {
        return new PlaceholderItem(String.valueOf(position), "Item ", aDateTime,desc);
    }


    /**
     * A placeholder item representing a piece of content.
     */
    public static class PlaceholderItem {
        public final String id;
        public final String content;
        public final String logDate;
        public final String logTime;
        public final String logDesc;
        private final long logDateTime;

        public PlaceholderItem(String id, String content, long pDateTime, String Desc) {
            this.id = id;
            this.content = content;
            this.logDateTime = pDateTime;
            this.logDate = DateFormat.getDateInstance().format(pDateTime);
            this.logTime = DateFormat.getTimeInstance().format(pDateTime);
            this.logDesc = Desc;
        }

        @Override
        public String toString() {
            return id + logDate + logTime + logDesc;
        }
    }
}