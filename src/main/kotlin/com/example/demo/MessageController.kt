package com.example.demo

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/messages")
class MessageController(private val repository: MessageRepository) {

    @GetMapping
    fun getAllMessages(): ResponseEntity<List<Message>> {
        return ResponseEntity.ok(repository.findAll().toList())
    }

    @GetMapping("/{id}")
    fun getMessageById(@PathVariable id: Long): ResponseEntity<Message> {
        val message = repository.findById(id)
        return if (message.isPresent) {
            ResponseEntity.ok(message.get())
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping("/search")
    fun searchMessages(@RequestParam text: String): ResponseEntity<List<Message>> {
        return ResponseEntity.ok(repository.findByTextContaining(text))
    }

    @PostMapping
    fun createMessage(@RequestBody message: Message): ResponseEntity<Message> {
        val savedMessage = repository.save(message)
        return ResponseEntity.status(HttpStatus.CREATED).body(savedMessage)
    }

    @PutMapping("/{id}")
    fun updateMessage(@PathVariable id: Long, @RequestBody message: Message): ResponseEntity<Message> {
        return if (repository.existsById(id)) {
            val updatedMessage = repository.save(message.copy(id = id))
            ResponseEntity.ok(updatedMessage)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @DeleteMapping("/{id}")
    fun deleteMessage(@PathVariable id: Long): ResponseEntity<Void> {
        return if (repository.existsById(id)) {
            repository.deleteById(id)
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.notFound().build()
        }
    }
}