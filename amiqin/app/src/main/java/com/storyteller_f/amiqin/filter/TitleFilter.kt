package com.storyteller_f.amiqin.filter

import android.view.View
import com.storyteller_f.amiqin.HistoryEntry
import com.storyteller_f.filter_core.config.SimpleRegExpConfigItem
import com.storyteller_f.filter_ui.adapter.FilterItemContainer
import com.storyteller_f.filter_ui.adapter.FilterItemViewHolder
import com.storyteller_f.filter_ui.adapter.FilterViewHolderFactory
import com.storyteller_f.filter_ui.filter.SimpleRegExpFilter

class TitleFilter(item: SimpleRegExpConfigItem) : SimpleRegExpFilter<HistoryEntry>("标题", item) {
    override fun getMatchString(t: HistoryEntry): CharSequence = t.title
    override val itemViewType: Int
        get() {
            return 1
        }

    override fun dup(): Any {
        return TitleFilter(item.dup() as SimpleRegExpConfigItem)
    }

    class ViewHolder(itemView: View) : SimpleRegExpFilter.ViewHolder<HistoryEntry>(itemView)
}

class FilterFactory : FilterViewHolderFactory<HistoryEntry>() {
    override fun create(
        viewType: Int,
        container: FilterItemContainer
    ): FilterItemViewHolder<HistoryEntry> {
        SimpleRegExpFilter.ViewHolder.create(container)
        return TitleFilter.ViewHolder(container.view)
    }

}