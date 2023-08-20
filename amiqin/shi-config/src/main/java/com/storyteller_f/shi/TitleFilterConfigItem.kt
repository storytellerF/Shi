package com.storyteller_f.shi

import com.storyteller_f.filter_core.config.SimpleRegExpConfigItem

class TitleFilterConfigItem(regexp: String) : SimpleRegExpConfigItem(regexp) {
    override fun dup(): Any {
        return TitleFilterConfigItem(regexp)
    }
}

class UrlFilterConfigItem(url: String) : SimpleRegExpConfigItem(url) {
    override fun dup(): Any {
        return UrlFilterConfigItem(regexp)
    }

}
