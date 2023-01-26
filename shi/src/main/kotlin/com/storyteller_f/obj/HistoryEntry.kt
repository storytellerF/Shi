package com.storyteller_f.obj

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

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
) {}

@Serializable
data class Host(val id: Long, val value: String)

object HistoryEntries : Table() {
    val id = long("id").autoIncrement()

    /**
     * 完整域名 test.baidu.com
     */
    val host = long("host")

    /**
     * 顶级域名 baidu.com
     */
    val mainHost = long("main_host")
    val deviceId = long("device_id")
    val url = varchar("url", 1000)
    val title = varchar("title", 1000)
    val accepted = bool("accepted")
    override val primaryKey: PrimaryKey
        get() = PrimaryKey(id)
}

object Devices : Table() {
    val id = long("id").autoIncrement()
    val name = varchar("name", 100)
    val factory = varchar("factory", 100)
    val identify = varchar("identify", 100)
    override val primaryKey: PrimaryKey
        get() = PrimaryKey(id)
}

object Hosts : Table() {
    val id = long("id").autoIncrement()
    val value = varchar("value", 100)
    override val primaryKey: PrimaryKey
        get() = PrimaryKey(id)
}

