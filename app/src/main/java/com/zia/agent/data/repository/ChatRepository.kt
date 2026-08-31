package com.zia.agent.data.repository

import com.zia.agent.data.local.ChatDao
import com.zia.agent.data.local.entities.ChatEntity
import com.zia.agent.data.local.entities.MessageEntity
import kotlinx.coroutines.flow.Flow

class ChatRepository(private val dao: ChatDao) {

    fun getAllChats(): Flow<List<ChatEntity>> = dao.getAllChats()

    suspend fun createChat(title: String, providerId: String, modelId: String): Long {
        return dao.insertChat(ChatEntity(title = title, providerId = providerId, modelId = modelId))
    }

    suspend fun updateChatTitle(id: Long, title: String) {
        dao.getChatById(id)?.let {
            dao.updateChat(it.copy(title = title, updatedAt = System.currentTimeMillis()))
        }
    }

    suspend fun deleteChat(id: Long) = dao.deleteChat(id)

    fun getMessages(chatId: Long): Flow<List<MessageEntity>> = dao.getMessagesForChat(chatId)

    suspend fun addMessage(chatId: Long, role: String, content: String, isError: Boolean = false): Long {
        return dao.insertMessage(MessageEntity(chatId = chatId, role = role, content = content, isError = isError))
    }

    suspend fun getMessagesSync(chatId: Long): List<MessageEntity> = dao.getMessagesForChatSync(chatId)

    suspend fun clearMessages(chatId: Long) = dao.deleteMessagesForChat(chatId)
}
