package com.storyteller_f.plugins

import com.storyteller_f.database.HistoryFacadeImpl
import com.storyteller_f.filter_core.config.FilterConfig
import com.storyteller_f.shi.Factory
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    val dao = HistoryFacadeImpl()
    routing {
        get("/") {
            call.respondText("Hello World!")
        }
        post("/search") {
            val start = call.parameters["start"]?.toLong() ?: 0
            val count = call.parameters["count"]?.toInt() ?: return@post
            val receive = call.receiveText()
            println("start $start count $count receive $receive")
            val filterConfig = if (receive.isNotEmpty()) {
                Factory.gson.fromJson(receive, FilterConfig::class.java)
            } else null
            call.respond(dao.search(start, count, filterConfig))
        }
    }
}
