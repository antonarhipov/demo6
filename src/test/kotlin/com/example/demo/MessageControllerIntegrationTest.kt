package com.example.demo

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.context.annotation.Import
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.test.annotation.DirtiesContext
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration::class)
@DirtiesContext
class MessageControllerIntegrationTest {

    @LocalServerPort
    private var port: Int = 0

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @Autowired
    private lateinit var repository: MessageRepository

    private fun url(path: String): String = "http://localhost:$port/api/messages$path"

    @Test
    fun `should create, read, update and delete a message`() {
        // Clean up any existing data
        repository.deleteAll()

        // Create a message
        val message = Message(text = "Hello, World!")
        val createResponse = restTemplate.postForEntity(url(""), message, Message::class.java)
        
        assertEquals(HttpStatus.CREATED, createResponse.statusCode)
        val createdMessage = createResponse.body!!
        assertNotNull(createdMessage.id)
        assertEquals("Hello, World!", createdMessage.text)
        
        // Get the message by ID
        val getResponse = restTemplate.getForEntity(url("/${createdMessage.id}"), Message::class.java)
        
        assertEquals(HttpStatus.OK, getResponse.statusCode)
        val retrievedMessage = getResponse.body!!
        assertEquals(createdMessage.id, retrievedMessage.id)
        assertEquals(createdMessage.text, retrievedMessage.text)
        
        // Update the message
        val updatedMessage = createdMessage.copy(text = "Updated message")
        val updateResponse = restTemplate.exchange(
            url("/${createdMessage.id}"),
            HttpMethod.PUT,
            HttpEntity(updatedMessage),
            Message::class.java
        )
        
        assertEquals(HttpStatus.OK, updateResponse.statusCode)
        val returnedUpdatedMessage = updateResponse.body!!
        assertEquals(createdMessage.id, returnedUpdatedMessage.id)
        assertEquals("Updated message", returnedUpdatedMessage.text)
        
        // Get all messages
        val getAllResponse = restTemplate.getForEntity(url(""), Array<Message>::class.java)
        
        assertEquals(HttpStatus.OK, getAllResponse.statusCode)
        val allMessages = getAllResponse.body!!
        assertEquals(1, allMessages.size)
        
        // Search for messages
        val searchResponse = restTemplate.getForEntity(url("/search?text=Updated"), Array<Message>::class.java)
        
        assertEquals(HttpStatus.OK, searchResponse.statusCode)
        val searchResults = searchResponse.body!!
        assertEquals(1, searchResults.size)
        assertEquals("Updated message", searchResults[0].text)
        
        // Delete the message
        val deleteResponse = restTemplate.exchange(
            url("/${createdMessage.id}"),
            HttpMethod.DELETE,
            null,
            Void::class.java
        )
        
        assertEquals(HttpStatus.NO_CONTENT, deleteResponse.statusCode)
        
        // Verify the message is deleted
        val getAfterDeleteResponse = restTemplate.getForEntity(url("/${createdMessage.id}"), Message::class.java)
        assertEquals(HttpStatus.NOT_FOUND, getAfterDeleteResponse.statusCode)
    }
}