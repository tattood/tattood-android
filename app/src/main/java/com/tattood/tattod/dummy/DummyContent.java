package com.tattood.tattod.dummy;

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
public class DummyContent {

    /**
     * An array of sample (dummy) items.
     */
    public static final List<DummyItem> RECENT_ITEMS = new ArrayList<DummyItem>();
    public static final List<DummyItem> POPULAR_ITEMS = new ArrayList<DummyItem>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, DummyItem> RECENT_ITEM_MAP = new HashMap<String, DummyItem>();
    public static final Map<String, DummyItem> POPULAR_ITEM_MAP = new HashMap<>();

    private static final int COUNT = 25;

    static {
        // Add some sample items.
        for (int i = 1; i <= COUNT; i++) {
            addItem(RECENT_ITEMS, RECENT_ITEM_MAP,  createDummyItem(i, "recent "));
            addItem(POPULAR_ITEMS, POPULAR_ITEM_MAP, createDummyItem(i, "popular "));
        }
    }

    static void addItem(List<DummyItem> list, Map<String, DummyItem> hash, DummyItem item) {
        list.add(item);
        hash.put(item.id, item);
    }

    static DummyItem createDummyItem(int position, String label) {
        return new DummyItem(String.valueOf(position), label + position, makeDetails(position));
    }

    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
    }

    /**
     * A dummy item representing a piece of content.
     */
    public static class DummyItem {
        public final String id;
        public final String content;
        public final String details;

        public DummyItem(String id, String content, String details) {
            this.id = id;
            this.content = content;
            this.details = details;
        }

        @Override
        public String toString() {
            return content;
        }
    }
}
