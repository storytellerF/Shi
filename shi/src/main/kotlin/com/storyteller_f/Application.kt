package com.storyteller_f

import com.storyteller_f.database.DatabaseFactory
import com.storyteller_f.plugins.configureHTTP
import com.storyteller_f.plugins.configureRouting
import com.storyteller_f.plugins.configureSecurity
import com.storyteller_f.plugins.configureSerialization
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main(args: Array<String>) = EngineMain.main(args)


fun Application.module() {
    DatabaseFactory.init()
    configureHTTP()
    configureSerialization()
    configureSecurity()
    configureRouting()
}
