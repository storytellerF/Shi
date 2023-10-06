package com.storyteller_f.shi

import com.storyteller_f.filter_core.config.SimpleRegExpConfigItem

class TitleFilterConfigItem(regexp: String, id: Long, name: String?) : SimpleRegExpConfigItem(regexp, id,
    name
) {
    override fun dup(): Any {
        return TitleFilterConfigItem(regexp, System.currentTimeMillis(), name)
    }
}

class UrlFilterConfigItem(url: String, id: Long, name: String?) : SimpleRegExpConfigItem(url, id,
    name
) {
    override fun dup(): Any {
        return UrlFilterConfigItem(regexp, System.currentTimeMillis(), name)
    }

}
