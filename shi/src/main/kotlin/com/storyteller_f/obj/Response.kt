package com.storyteller_f.obj

import kotlinx.serialization.Serializable

@Serializable
class Response<T>(val data: List<T>, val total: Long) {
}