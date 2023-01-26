package com.storyteller_f.obj

import kotlinx.serialization.Serializable


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
)

@Serializable
data class Host(val id: Long, val value: String)
