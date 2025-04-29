package com.example.demo

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("MESSAGES")
data class Message(
    @Id
    val id: Long? = null,
    val text: String,
    val createdAt: LocalDateTime = LocalDateTime.now()
)
