package com.tattood.tattod.dummy;

import com.tattood.tattod.dummy.DummyContent.DummyItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.tattood.tattod.dummy.DummyContent.addItem;
import static com.tattood.tattod.dummy.DummyContent.createDummyItem;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class DummyContent2 {
    public static final List<DummyItem> RECENT_ITEMS = new ArrayList<>();
    public static final List<DummyItem> POPULAR_ITEMS = new ArrayList<DummyItem>();
    public static final Map<String, DummyContent.DummyItem> RECENT_ITEM_MAP = new HashMap<String, DummyItem>();
    public static final Map<String, DummyItem> POPULAR_ITEM_MAP = new HashMap<>();

    private static final int COUNT = 100;

    static {
        // Add some sample items.
        for (int i = 1; i <= COUNT; i++) {
            addItem(RECENT_ITEMS, RECENT_ITEM_MAP,  createDummyItem(i, "recent "));
            addItem(POPULAR_ITEMS, POPULAR_ITEM_MAP, createDummyItem(i, "popular "));
        }
    }
}
