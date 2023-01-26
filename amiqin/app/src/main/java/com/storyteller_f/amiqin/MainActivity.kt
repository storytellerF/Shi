package com.storyteller_f.amiqin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.paging.LoadState
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.storytellerF.compose_ui.FilterDialog
import com.storytellerF.compose_ui.SimpleFilterView
import com.storyteller_f.amiqin.api.Device
import com.storyteller_f.amiqin.api.HistoryEntry
import com.storyteller_f.amiqin.filter.TitleFilter
import com.storyteller_f.amiqin.filter.UrlFilter
import com.storyteller_f.amiqin.ui.theme.AmiqinTheme
import com.storyteller_f.config_core.Config
import com.storyteller_f.config_core.DefaultDialog
import com.storyteller_f.config_core.EditorKey
import com.storyteller_f.config_core.editor
import com.storyteller_f.filter_core.Filter
import com.storyteller_f.filter_core.config.FilterConfigItem
import com.storyteller_f.filter_core.filter.simple.SimpleRegExpFilter
import com.storyteller_f.shi.*
import com.storyteller_f.sort_core.config.SortConfig
import com.storyteller_f.sort_core.config.sortConfigAdapterFactory
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import java.util.Calendar

val httpClient: HttpClient by lazy {
    HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }
    }
}

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            AmiqinTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Amiqin(::current)
                }
            }
        }
    }

    private fun current(): Config? {
        val createEditorKey = EditorKey.createEditorKey(filesDir.absolutePath, "sort")
        return createEditorKey.editor(
            SortConfig.emptySortListener,
            sortConfigAdapterFactory,
            Factory.factory
        ).lastConfig
    }
}

@Composable
fun Amiqin(current: () -> Config? = { null }) {
    var search by rememberSaveable { mutableStateOf("") }

    var showFilterDialog by remember {
        mutableStateOf(false)
    }
    Column {
        Row {
            Button(onClick = {
                showFilterDialog = true
            }) {
                Text(text = "filter")
            }
            Button(onClick = {
                val toJson = Factory.gson.toJson(current())
                search = toJson
                println(toJson)
            }) {
                Text(text = "start")
            }
        }
        if (BuildConfig.DEBUG)
            Text(text = search.ifEmpty { "null" })
        HistoryContent(search)
    }
    if (showFilterDialog)
        FilterDialog(
            suffix = "filter",
            close = { showFilterDialog = false },
            listener = object : DefaultDialog.Listener<Filter<HistoryEntry>, FilterConfigItem> {
                override fun onSaveState(oList: List<Filter<HistoryEntry>>): List<FilterConfigItem> =
                    oList.mapNotNull {
                        when (it) {
                            is SimpleRegExpFilter -> it.item
                            else -> null
                        }
                    }

                override fun onRestoreState(configItems: List<FilterConfigItem>) =
                    configItems.mapNotNull {
                        when (it) {
                            is TitleFilterConfigItem -> TitleFilter(it)
                            is UrlFilterConfigItem -> UrlFilter(it)
                            else -> null
                        }
                    }

                override fun onActiveChanged(activeList: List<Filter<HistoryEntry>>) = Unit
                override fun onEditingChanged(editing: List<Filter<HistoryEntry>>) {
                }

            },
            filters = listOf(TitleFilter(TitleFilterConfigItem("^$", 0, null))),
            factory = Factory.factory
        ) { filter, itemChange ->
            if (filter is TitleFilter) {
                SimpleFilterView(filter = filter, updateName = {
                    itemChange.change(
                        TitleFilter(
                            TitleFilterConfigItem(
                                filter.regexp,
                                filter.id,
                                filter.item.name
                            )
                        )
                    )
                }, updateRegExp = {
                    itemChange.change(
                        TitleFilter(
                            TitleFilterConfigItem(
                                it,
                                filter.id,
                                filter.item.name
                            )
                        )
                    )

                })
            }
        }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    AmiqinTheme {
//        Amiqin("Android", pager)
    }
}

@Composable
fun HistoryContent(search: String) {
    val pager by remember(search) {
        derivedStateOf {
            Pager(PagingConfig(30)) {
                HistoryPagingSource(search = search)
            }
        }
    }
    val list = pager.flow.collectAsLazyPagingItems()
    when (val loadState = list.loadState.refresh) {
        is LoadState.Loading -> {
            Text(text = "loading")
        }

        is LoadState.Error -> {
            val text = loadState.error.localizedMessage
            Text(text = "oh~~~ $text")
        }

        is LoadState.NotLoading -> {
            LazyColumn(content = {
                items(
                    count = list.itemCount,
                    key = list.itemKey(key = {
                        it.entryId
                    }),
                    contentType = list.itemContentType()
                ) { index ->
                    val item = list[index]
                    if (item != null)
                        HistoryEntryView(item)
                }
            })
        }
    }
}

class HistoryEntryPreviewProvider : PreviewParameterProvider<HistoryEntry> {
    override val values: Sequence<HistoryEntry>
        get() = sequenceOf(
            HistoryEntry(
                0,
                "host",
                "mainHost",
                1632160889708,
                "url",
                "title",
                false,
                Device(0, "name", "factory", "identity")
            )
        )

}

@Preview
@Composable
fun HistoryEntryView(@PreviewParameter(HistoryEntryPreviewProvider::class) historyEntry: HistoryEntry) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Text(text = historyEntry.title, fontSize = 15.sp)
        Row(
            modifier = Modifier.padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.CheckCircle,
                contentDescription = "check",
                modifier = Modifier.padding(end = 8.dp),
                tint = if (historyEntry.accepted) Color.Green else LocalContentColor.current
            )
            Text(text = Calendar.getInstance().apply {
                timeInMillis = historyEntry.time
            }.time.toString(), modifier = Modifier.background(Color.LightGray), fontSize = 12.sp)
        }
    }
}

sealed class ResponseState<out T> {
    object Loading : ResponseState<Nothing>()
    data class Error(val throwable: Throwable) : ResponseState<Nothing>()
    data class Success<T>(val item: T) : ResponseState<T>()
}
