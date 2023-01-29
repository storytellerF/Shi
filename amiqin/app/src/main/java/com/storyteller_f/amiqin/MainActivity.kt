package com.storyteller_f.amiqin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.LoadState
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.storyteller_f.amiqin.filter.FilterFactory
import com.storyteller_f.amiqin.filter.TitleFilter
import com.storyteller_f.amiqin.ui.theme.AmiqinTheme
import com.storyteller_f.filter_core.Filter
import com.storyteller_f.filter_core.config.FilterConfigItem
import com.storyteller_f.filter_core.filter.Filterable
import com.storyteller_f.filter_ui.FilterDialog
import com.storyteller_f.shi.Factory
import com.storyteller_f.shi.TitleFilterConfigItem
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import java.util.*

lateinit var httpClient: HttpClient

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        httpClient = HttpClient(CIO) {
            install(ContentNegotiation) {
                json()
            }
        }
        val filterDialog = FilterDialog(this, listOf(TitleFilter(TitleFilterConfigItem("^$"))), FilterFactory())
        filterDialog.setListener(object : FilterDialog.Listener<HistoryEntry> {
            override fun onSaveState(filters: MutableList<Filter<HistoryEntry>>?): MutableList<FilterConfigItem> {
                return filters.orEmpty().map {
                    (it as TitleFilter).item
                }.toMutableList()
            }

            override fun onInitHistory(configItems: MutableList<FilterConfigItem>?) {
                filterDialog.add(configItems.orEmpty().map {
                    TitleFilter(it as TitleFilterConfigItem)
                })
            }

        })

        filterDialog.init("filter", Factory.factory)
        setContent {
            AmiqinTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    Amiqin(filterDialog)
                }
            }
        }
    }
}

@Composable
fun Amiqin(filterDialog: FilterDialog<HistoryEntry>) {
    var search by rememberSaveable { mutableStateOf("") }
    val pager by remember(search) {
        derivedStateOf {
            Pager(PagingConfig(30)) {
                HistoryPagingSource(search = search)
            }
        }
    }
    Column {
        Row() {
            Button(onClick = {
                filterDialog.show()
            }) {
                Text(text = "filter")
            }
            Button(onClick = {
                val toJson = Factory.gson.toJson(filterDialog.current())
                search = toJson
                println(toJson)
            }) {
                Text(text = "start")
            }
        }
        Text(text = search.ifEmpty { "empty" })
        HistoryContent(pager)
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
fun HistoryContent(pager: Pager<Int, HistoryEntry>) {
//    val list by loadHistory()
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
                items(items = list, key = {
                    it.entryId
                }) {
                    if (it != null)
                        HistoryEntryView(it)
                }
            })
        }
    }
}

class HistoryEntryPreviewProvider : PreviewParameterProvider<HistoryEntry> {
    override val values: Sequence<HistoryEntry>
        get() = sequenceOf(HistoryEntry(0, "host", "mainHost", 1632160889708, "url", "title", false, Device(0, "name", "factory", "identity")))

}

@Preview
@Composable
fun HistoryEntryView(@PreviewParameter(HistoryEntryPreviewProvider::class) historyEntry: HistoryEntry) {
    Column(Modifier.fillMaxWidth().padding(8.dp)) {
        Text(text = historyEntry.title, fontSize = 15.sp)
        Row(modifier = Modifier.padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically) {
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

@Serializable
data class Device(val deviceId: Long, val name: String, val factory: String, val identify: String)

@Serializable
data class HistoryEntry(
    val entryId: Long,
    val host: String,
    val mainHost: String,
    val time: Long,
    val url: String,
    val title: String,
    val accepted: Boolean,
    val device: Device
) : Filterable()

@Serializable
data class Host(val id: Long, val value: String)
