package com.storyteller_f.amiqin.filter

import com.storyteller_f.amiqin.api.HistoryEntry
import com.storyteller_f.filter_core.config.SimpleRegExpConfigItem
import com.storyteller_f.filter_core.filter.simple.SimpleRegExpFilter

class TitleFilter(item: SimpleRegExpConfigItem) : SimpleRegExpFilter<HistoryEntry>("标题", item) {
    override fun getMatchString(t: HistoryEntry): CharSequence = t.title
    override val itemViewType: Int
        get() = 1

    override fun dup(): Any {
        return TitleFilter(item.dup() as SimpleRegExpConfigItem)
    }

}

class UrlFilter(item: SimpleRegExpConfigItem) : SimpleRegExpFilter<HistoryEntry>("host", item) {
    override fun getMatchString(t: HistoryEntry): CharSequence {
        return t.host
    }

    override val itemViewType: Int
        get() = 1

    override fun dup(): Any {
        return UrlFilter(item.dup() as SimpleRegExpConfigItem)
    }

}