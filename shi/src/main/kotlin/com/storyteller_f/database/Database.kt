package com.storyteller_f.database

import com.storyteller_f.config_core.ConfigItem
import com.storyteller_f.filter_core.config.FilterConfig
import com.storyteller_f.filter_core.config.SimpleRegExpConfigItem
import com.storyteller_f.obj.*
import com.storyteller_f.tables.Devices
import com.storyteller_f.tables.HistoryEntries
import com.storyteller_f.tables.Hosts
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
        val driverClassName = "org.sqlite.JDBC"
        val jdbcURL = "jdbc:sqlite:databases/shi.db"
        Database.connect(jdbcURL, driverClassName)
        transaction {
//            addLogger(StdOutSqlLogger)
            SchemaUtils.create(HistoryEntries, Devices, Hosts)
        }
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}

interface HistoryFacade {
    suspend fun search(start: Long, count: Int, filter: FilterConfig? = null): Response<HistoryEntry>
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
    override suspend fun search(start: Long, count: Int, filter: FilterConfig?): Response<HistoryEntry> {
        return DatabaseFactory.dbQuery {
            Response(query(filter).limit(count, start).map(::convert), query(filter).count())
        }
    }

    private fun query(filter: FilterConfig?) = HistoryEntries.select {
        filter?.configItems.orEmpty().fold(Op.TRUE) { last: Op<Boolean>, item: ConfigItem ->
            val regexpOp = when (item) {
                is SimpleRegExpConfigItem -> HistoryEntries.title.regexp(item.regexp)
                else -> null
            }
            last andIfNotNull regexpOp
        }
    }

    private fun convert(it: ResultRow): HistoryEntry {
        return HistoryEntry(
            it[HistoryEntries.id].value,
            hostString(it[HistoryEntries.host]),
            hostString(it[HistoryEntries.mainHost]),
            it[HistoryEntries.visitTime],
            it[HistoryEntries.url],
            it[HistoryEntries.title],
            it[HistoryEntries.accepted],
            deviceName(it[HistoryEntries.deviceId])
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