package com.storyteller_f.tables

import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Table

object HistoryEntries : LongIdTable() {

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
    val visitTime = long("visit_time")
}

class HistoryEntryEntity(id: EntityID<Long>): LongEntity(id) {

    companion object : EntityClass<Long, HistoryEntryEntity>(HistoryEntries)

    var host by HistoryEntries.host
    var mainHost by HistoryEntries.mainHost
    var deviceId by HistoryEntries.deviceId
    var url by HistoryEntries.url
    var title by HistoryEntries.title
    var accepted by HistoryEntries.accepted
    var visitTime by HistoryEntries.visitTime
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
