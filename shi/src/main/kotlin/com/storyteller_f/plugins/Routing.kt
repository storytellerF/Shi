package com.storyteller_f.plugins

import com.storyteller_f.database.DatabaseFactory
import com.storyteller_f.database.HistoryFacadeImpl
import com.storyteller_f.obj.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.insert
import java.io.File
import java.util.Calendar

fun Application.configureRouting() {
    val dao = HistoryFacadeImpl().apply {
        runBlocking {
            val device = DatabaseFactory.dbQuery {
                Hosts.insert {
                    it[value] = "baidu.com"
                }
                val insertStatement = Devices.insert {
                    it[name] = "MI"
                    it[factory] = "MI"
                    it[identify] = "MI"
                }
                Device(
                    insertStatement[Devices.id],
                    insertStatement[Devices.name],
                    insertStatement[Devices.factory],
                    insertStatement[Devices.identify]
                )
            }
            val apply = Calendar.getInstance().apply {
                set(2001, 1, 1)
            }
            val timeInMillis = apply.timeInMillis
            val readLines = File("test.txt").readLines().map {
                val split = it.split("\t")
                val toDouble = split[2].toDouble()
                val toInt = (toDouble * 1000).toLong()
                split[0] to split[1] to toInt + timeInMillis
            }
            DatabaseFactory.dbQuery {
                HistoryEntries.batchInsert(readLines) {
                    this[HistoryEntries.deviceId] = device.deviceId
                    this[HistoryEntries.host] = 1
                    this[HistoryEntries.mainHost] = 1
                    this[HistoryEntries.url] = it.first.second
                    this[HistoryEntries.title] = it.first.first
                    this[HistoryEntries.accepted] = false
                    this[HistoryEntries.visitTime] = it.second
                }
            }

        }
    }
    routing {
        get("/") {
            call.respondText("Hello World!")
        }
        get("/search") {
            val start = call.parameters["start"]?.toLong() ?: 0
            val count = call.parameters["count"]?.toInt() ?: return@get
            println("start $start count $count")
            call.respond(dao.search(start, count))
        }
    }
}
