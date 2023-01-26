package com.storyteller_f

import com.storyteller_f.obj.Device
import com.storyteller_f.database.DatabaseFactory
import com.storyteller_f.database.HistoryFacadeImpl
import com.storyteller_f.tables.Devices
import com.storyteller_f.tables.HistoryEntries
import com.storyteller_f.tables.Hosts
import io.ktor.http.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlin.test.*
import io.ktor.server.testing.*
import com.storyteller_f.plugins.*
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.insert
import java.io.File
import java.util.*

class ApplicationTest {
    @Test
    fun testRoot() = testApplication {
        application {
            DatabaseFactory.init()
            configureRouting()
        }
        client.get("/").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("Hello World!", bodyAsText())
        }
    }

    @Test
    fun addTestData() {
        DatabaseFactory.init()
        HistoryFacadeImpl().apply {
            insertMockData()
        }
    }


    private fun insertMockData() {
        runBlocking {
            val f = File("test.txt")
            if (f.exists()) {
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
                val safariStartTimestamp = Calendar.getInstance().apply {
                    set(2001, 1, 1)
                }.timeInMillis
                val chromeStartTimestamp = Calendar.getInstance().apply {
                    set(1601, 1, 1)
                }.timeInMillis

                val lines = f.readLines().map {
                    val split = it.split("\t")
                    if (split[2].contains(".")) {
                        split[0] to split[1] to (split[2].toDouble() * 1000).toLong() + safariStartTimestamp
                    } else {
                        val v = split[2].toLong()
                        split[0] to split[1] to (v / 1000000) + chromeStartTimestamp
                    }
                }
                DatabaseFactory.dbQuery {
                    HistoryEntries.batchInsert(lines) {
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
    }

}