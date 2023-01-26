package com.storyteller_f.shi;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory;
import com.storyteller_f.config_core.ConfigItem;

public class Factory {
    public static RuntimeTypeAdapterFactory<ConfigItem> factory = RuntimeTypeAdapterFactory.of(ConfigItem.class, "config-item-key").registerSubtype(TitleFilterConfigItem.class, "title");

    public static Gson gson = new GsonBuilder().registerTypeAdapterFactory(factory).create();
}
