package com.storyteller_f.amiqin

import com.google.gson.typeadapters.RuntimeTypeAdapterFactory
import com.storyteller_f.amiqin.filter.FilterFactory
import com.storyteller_f.amiqin.filter.TitleFilter
import com.storyteller_f.config_core.ConfigItem
import com.storyteller_f.config_edit.DefaultDialog
import com.storyteller_f.filter_core.Filter
import com.storyteller_f.filter_core.config.FilterConfigItem
import com.storyteller_f.filter_ui.FilterDialogFragment
import com.storyteller_f.filter_ui.adapter.FilterViewHolderFactory
import com.storyteller_f.shi.Factory
import com.storyteller_f.shi.TitleFilterConfigItem


class AmiqinFilterDialogFragment: FilterDialogFragment<HistoryEntry>() {
    override val dialogListener: DefaultDialog.Listener<Filter<HistoryEntry>, FilterConfigItem>
        get() = object : DefaultDialog.Listener<Filter<HistoryEntry>, FilterConfigItem> {
            override fun onSaveState(oList: List<Filter<HistoryEntry>>) =
                oList.map {
                    (it as TitleFilter).item
                }.toMutableList()

            override fun onActiveListSelected(configItems: List<FilterConfigItem>) =
                configItems.map {
                    TitleFilter(it as TitleFilterConfigItem)
                }

            override fun onActiveChanged(activeList: List<Filter<HistoryEntry>>) = Unit

        }
    override val filters: List<Filter<HistoryEntry>>
        get() = listOf(TitleFilter(TitleFilterConfigItem("^$")))
    override val runtimeTypeAdapterFactory: RuntimeTypeAdapterFactory<ConfigItem>
        get() = Factory.factory
    override val viewHolderFactory: FilterViewHolderFactory<HistoryEntry>
        get() = FilterFactory()

}