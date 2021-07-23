package io.grimlocations.framework.data.dto

import java.time.LocalDateTime

interface OrderedNameDTO: OrderedDTO, NameDTO

interface OrderedDTO: DTO {
    val order: Int
}

interface NameDTO: DTO {
    val name: String
}

interface DTO {
    val id: Int
    val created: LocalDateTime
    val modified: LocalDateTime
}

fun Collection<DTO>.containsId(id: Int) = find { it.id == id } != null

fun <T: DTO> Set<T>.replaceDTO(dto: T): Set<T> {
    val newSet = mutableSetOf<T>()
    for (item in this) {
        if (dto.id == item.id) {
            newSet.add(dto)
        } else {
            newSet.add(item)
        }
    }
    return newSet
}

fun <T: DTO> Set<T>.replaceDTO(dto: T, newDTO: T): Set<T> {
    val newSet = mutableSetOf<T>()
    for (item in this) {
        if (dto.id == item.id) {
            newSet.add(newDTO)
        } else {
            newSet.add(item)
        }
    }
    return newSet
}

fun <T: DTO> List<T>.replaceDTO(dto: T): List<T> {
    val newList = mutableListOf<T>()
    for (item in this) {
        if (dto.id == item.id) {
            newList.add(dto)
        } else {
            newList.add(item)
        }
    }
    return newList
}