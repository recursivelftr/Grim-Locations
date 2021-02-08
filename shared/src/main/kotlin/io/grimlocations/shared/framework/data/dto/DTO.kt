package io.grimlocations.shared.framework.data.dto

import java.time.LocalDateTime

interface DTO {
    val id: Int
    val created: LocalDateTime
    val modified: LocalDateTime
}