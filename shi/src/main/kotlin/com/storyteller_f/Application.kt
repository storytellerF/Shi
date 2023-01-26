package com.storyteller_f

import com.storyteller_f.database.DatabaseFactory
import com.storyteller_f.obj.HistoryEntry
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import com.storyteller_f.plugins.*

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    DatabaseFactory.init()
    configureHTTP()
    configureSerialization()
    configureSecurity()
    configureRouting()
}
