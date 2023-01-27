package com.storyteller_f.amiqin.filter

import android.view.View
import android.view.ViewGroup
import com.storyteller_f.amiqin.HistoryEntry
import com.storyteller_f.filter_core.config.SimpleRegExpConfigItem
import com.storyteller_f.filter_ui.adapter.FilterItemContainer
import com.storyteller_f.filter_ui.adapter.FilterItemViewHolder
import com.storyteller_f.filter_ui.adapter.FilterViewHolderFactory
import com.storyteller_f.filter_ui.filter.SimpleRegexpFilter

class TitleFilter(item: SimpleRegExpConfigItem) : SimpleRegexpFilter<HistoryEntry>("标题", item) {
    override fun getMatchString(t: HistoryEntry?) = t?.title.orEmpty()
    override fun getItemViewType(): Int {
        return 1
    }

    class ViewHolder(itemView: View) : SimpleRegexpFilter.ViewHolder(itemView)
}

class FilterFactory : FilterViewHolderFactory() {
    override fun create(parent: ViewGroup, viewType: Int, container: FilterItemContainer): FilterItemViewHolder {
        SimpleRegexpFilter.ViewHolder.create(parent.context, container.frameLayout)
        return TitleFilter.ViewHolder(container.view)
    }

}