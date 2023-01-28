package com.storyteller_f.shi;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory;
import com.storyteller_f.filter_core.config.FilterConfigItem;

public class Factory {
    public static RuntimeTypeAdapterFactory<FilterConfigItem> factory = RuntimeTypeAdapterFactory.of(FilterConfigItem.class, "config-item-key").registerSubtype(TitleFilterConfigItem.class, "title");

    public static Gson gson = new GsonBuilder().registerTypeAdapterFactory(factory).create();
}
