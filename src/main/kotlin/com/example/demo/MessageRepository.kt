package com.example.demo

import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface MessageRepository : CrudRepository<Message, Long> {
    
    fun findByTextContaining(text: String): List<Message>
    
    @Query("SELECT * FROM messages WHERE created_at > :date")
    fun findMessagesCreatedAfter(@Param("date") date: String): List<Message>
}