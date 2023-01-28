package com.storyteller_f.shi;

import com.storyteller_f.filter_core.config.SimpleRegExpConfigItem;
import org.jetbrains.annotations.NotNull;

public class TitleFilterConfigItem extends SimpleRegExpConfigItem {

    public TitleFilterConfigItem(@NotNull String regexp) {
        super(regexp);
    }
}
