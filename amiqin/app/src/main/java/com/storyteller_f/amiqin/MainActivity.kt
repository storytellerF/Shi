package com.storyteller_f.amiqin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.storyteller_f.amiqin.ui.theme.AmiqinTheme
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable

lateinit var httpClient: HttpClient

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        httpClient = HttpClient(CIO) {
            install(ContentNegotiation) {
                json()
            }
        }
        setContent {
            AmiqinTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    Amiqin("Android")
                }
            }
        }
    }
}

@Composable
fun Amiqin(name: String) {
    Column {
        Text(text = "Hello $name!")
        HistoryContent()
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    AmiqinTheme {
        Amiqin("Android")
    }
}

@Composable
fun HistoryContent() {
    val list by loadHistory()
    when (list) {
        is ResponseState.Loading -> {
            Text(text = "loading")
        }
        is ResponseState.Error -> {
            val text = (list as ResponseState.Error).throwable.localizedMessage
            Text(text = "oh~~~ $text")
        }
        is ResponseState.Success -> {
            val item = (list as ResponseState.Success<List<HistoryEntry>>).item
            HistoryList(item)
        }
    }
}

@Composable
fun HistoryList(list: List<HistoryEntry>) {
    LazyColumn(content = {
        items(list) {
            HistoryEntryView(it)
        }
    })
}

class HistoryEntryPreviewProvider : PreviewParameterProvider<HistoryEntry> {
    override val values: Sequence<HistoryEntry>
        get() = sequenceOf(HistoryEntry(0, "host", "mainHost", 0, "url", "title", false, Device(0, "name", "factory", "identity")))

}

@Preview
@Composable
fun HistoryEntryView(@PreviewParameter(HistoryEntryPreviewProvider::class) historyEntry: HistoryEntry) {
    Column(Modifier.fillMaxWidth()) {
        Text(text = historyEntry.title)
        Row {
            Icon(
                imageVector = Icons.Outlined.CheckCircle,
                contentDescription = "check",
                tint = if (historyEntry.accepted) Color.Green else LocalContentColor.current
            )
            Text(text = historyEntry.time.toString())
            Text(text = historyEntry.accepted.toString())
        }
    }
}

@Composable
fun loadHistory() = produceState<ResponseState<List<HistoryEntry>>>(initialValue = ResponseState.Loading, producer = {
    value = try {
        val response = httpClient.get("http://10.0.2.2:8080/search")
        val body = response.body<List<HistoryEntry>>()
        ResponseState.Success(body)
    } catch (e: Exception) {
        ResponseState.Error(e)
    }
})

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
)

@Serializable
data class Host(val id: Long, val value: String)
