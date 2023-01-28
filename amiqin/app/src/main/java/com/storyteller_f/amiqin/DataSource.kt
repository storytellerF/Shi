package com.storyteller_f.amiqin

import androidx.paging.PagingSource
import androidx.paging.PagingState
import io.ktor.client.call.*
import io.ktor.client.request.*

class HistoryPagingSource(
    private val pageSize: Int = 30,
    private val search: String,
) : PagingSource<Int, HistoryEntry>() {
    override suspend fun load(
        params: LoadParams<Int>
    ): LoadResult<Int, HistoryEntry> {
        try {
            val nextPageNumber = params.key ?: 0
            val start = nextPageNumber * pageSize
            val response = httpClient.post("http://10.0.2.2:8080/search?start=$start&count=$pageSize") {
                setBody(search)
            }
            val body = response.body<List<HistoryEntry>>()
            return LoadResult.Page(
                data = body,
                prevKey = null, // Only paging forward.
                nextKey = if (body.isNotEmpty()) nextPageNumber + 1 else null
            )
        } catch (e: Exception) {
            // Handle errors in this block and return LoadResult.Error if it is an
            // expected error (such as a network failure).
            e.printStackTrace()
            return LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, HistoryEntry>): Int? {
        // Try to find the page key of the closest page to anchorPosition, from
        // either the prevKey or the nextKey, but you need to handle nullability
        // here:
        //  * prevKey == null -> anchorPage is the first page.
        //  * nextKey == null -> anchorPage is the last page.
        //  * both prevKey and nextKey null -> anchorPage is the initial page, so
        //    just return null.
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}