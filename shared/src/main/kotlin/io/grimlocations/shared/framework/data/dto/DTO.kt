package io.grimlocations.shared.framework.data.dto

import java.time.LocalDateTime

interface DTO {
    val id: Int
    val created: LocalDateTime
    val modified: LocalDateTime
}

fun List<DTO>.containsId(id: Int) = find { it.id == id } != null