package com.storyteller_f.database

import com.storyteller_f.filter_core.config.FilterConfig
import com.storyteller_f.obj.*
import com.storyteller_f.shi.TitleFilterConfigItem
import kotlinx.coroutines.*
import org.h2.util.SmallLRUCache
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.*
import org.jetbrains.exposed.sql.transactions.experimental.*

val hostCache = SmallLRUCache.newInstance<String, Long>(1000)
val rHostCache = SmallLRUCache.newInstance<Long, String>(1000)
val rDeviceCache = SmallLRUCache.newInstance<Long, Device>(1000)

fun hostString(hostId: Long): String {
    return rHostCache.getOrPut(hostId) {
        Hosts.select {
            Hosts.id eq hostId
        }.limit(1, 0).first()[Hosts.value]
    }!!
}

fun deviceName(deviceId: Long): Device {
    return rDeviceCache.getOrPut(deviceId) {
        Devices.select {
            Devices.id eq deviceId
        }.limit(1, 0).first().let {
            Device(it[Devices.id], it[Devices.name], it[Devices.factory], it[Devices.identify])
        }
    }!!
}

fun hostId(host: String): Long {
    return hostCache.getOrPut(host) {
        Hosts.select {
            Hosts.value eq host
        }.first()[Hosts.id]
    }!!
}

object DatabaseFactory {
    fun init() {
        val driverClassName = "org.h2.Driver"
        val jdbcURL = "jdbc:h2:mem:shi;DB_CLOSE_DELAY=-1"
        Database.connect(jdbcURL, driverClassName)
        transaction {
            addLogger(StdOutSqlLogger)
            SchemaUtils.create(HistoryEntries, Devices, Hosts)
        }
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}

interface HistoryFacade {
    suspend fun search(start: Long, count: Int, filter: FilterConfig? = null): List<HistoryEntry>
    suspend fun insert(entry: HistoryEntry)
}

interface DeviceFacade {
    suspend fun search(id: Long): Device
    suspend fun insert(device: Device)
}

interface HostFacade {
    suspend fun search()
    suspend fun insert()
}


class HistoryFacadeImpl : HistoryFacade {
    override suspend fun search(start: Long, count: Int, filter: FilterConfig?): List<HistoryEntry> {
        return DatabaseFactory.dbQuery {
            HistoryEntries.select {
                if (filter != null) {
                    val regexp = filter.configItems.orEmpty().filterIsInstance<TitleFilterConfigItem>().first().regexp
                    HistoryEntries.title.regexp(regexp)
                } else Op.TRUE
            }.limit(count, start).map(::convert)
        }
    }

    private fun convert(it: ResultRow): HistoryEntry {
        return HistoryEntry(
            it[HistoryEntries.id],
            hostString(it[HistoryEntries.host]),
            hostString(it[HistoryEntries.mainHost]),
            it[HistoryEntries.visitTime],
            it[HistoryEntries.url],
            it[HistoryEntries.title],
            it[HistoryEntries.accepted],
            deviceName(it[HistoryEntries.deviceId])!!
        )
    }

    override suspend fun insert(entry: HistoryEntry) {
        DatabaseFactory.dbQuery {
            HistoryEntries.insert {
                it[host] = hostId(entry.host)
                it[mainHost] = hostId(entry.mainHost)
                it[deviceId] = entry.device.deviceId
            }
        }
    }


}