package com.example.noteapp_architecture_sample.feature_note.domain.repository

import com.example.noteapp_architecture_sample.feature_note.domain.model.Note
import kotlinx.coroutines.flow.Flow

interface NoteRepository {

    fun getAllNotes(): Flow<List<Note>>

    suspend fun getNoteById(id: Int): Note?

    suspend fun insertNote(note: Note)

    suspend fun deleteNote(note: Note)
}