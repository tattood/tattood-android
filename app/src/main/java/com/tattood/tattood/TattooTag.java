package com.tattood.tattood;

import android.graphics.Color;

import com.cunoraz.tagview.Tag;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by eksi on 26/04/17.
 */

public class TattooTag extends Tag {

    public TattooTag(String t) {
        this(t, false);
    }

    public TattooTag(String t, boolean delete) {
        super(t);
        isDeletable = delete;
        layoutColor = getRandomColor();
    }

    private int getRandomColor() {
        ArrayList<String> colors = new ArrayList<>();
//        RED
        colors.add("#70B71C1C");
        colors.add("#70FF5252");
        colors.add("#70EF5353");
        colors.add("#70EF9A9A");
//        PINK
        colors.add("#70AD1457");
        colors.add("#70EC407A");
        colors.add("#70C51162");
//        PURPLE
        colors.add("#70AB47BC");
        colors.add("#70CE93D8");
//        DEEP PURPLE
        colors.add("#705E35B1");
        colors.add("#709775CD");
        colors.add("#706200EA");
//        INDIGO
        colors.add("#70536DFE");
//        BLUE
        colors.add("#701E88E5");
        colors.add("#7090CAF9");
        colors.add("#702979FF");
//        LIGHT BLUE
        colors.add("#7081D4FA");
//        CYAN
        colors.add("#7000BCD4");
//        EMBER
        colors.add("#70FFAB00");
        colors.add("#70FFC107");
        return Color.parseColor(colors.get(new Random().nextInt(colors.size())));
    }
}
