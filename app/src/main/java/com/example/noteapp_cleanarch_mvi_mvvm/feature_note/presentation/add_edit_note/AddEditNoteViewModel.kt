package com.example.noteapp_cleanarch_mvi_mvvm.feature_note.presentation.add_edit_note

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noteapp_cleanarch_mvi_mvvm.feature_note.domain.model.InvalidNoteException
import com.example.noteapp_cleanarch_mvi_mvvm.feature_note.domain.model.Note
import com.example.noteapp_cleanarch_mvi_mvvm.feature_note.domain.use_case.NoteUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditNoteViewModel @Inject constructor(
    private val noteUseCases: NoteUseCases,
    savedStateHandle: SavedStateHandle
): ViewModel(){

    private val _noteTitle = mutableStateOf(NoteTextFieldState(
        hint = "Enter title here..."
    ))
    val noteTitle: State<NoteTextFieldState> = _noteTitle

    private val _noteContent = mutableStateOf(NoteTextFieldState(
        hint = "Enter Note Content Here....."
    ))
    val noteContent: State<NoteTextFieldState> = _noteContent

    private val _noteColor = mutableIntStateOf(Note.noteColors.random().toArgb())
    val noteColor: State<Int> = _noteColor

    private var currentNoteId: Int? = null

    init {
        savedStateHandle.get<Int>("noteId")?.let { noteId ->
            if(noteId != -1){
                viewModelScope.launch {
                    noteUseCases.getNoteById(noteId)?.also { note ->
                        currentNoteId = note.id
                        _noteTitle.value = noteTitle.value.copy(
                            text = note.title,
                            isHintVisible = false,
                        )
                        _noteContent.value = noteContent.value.copy(
                            text = note.content,
                            isHintVisible = false,
                        )
                        _noteColor.value = note.color
                    }
                }
            }

        }
    }

    fun enterTitle(value: String){
        _noteTitle.value = noteTitle.value.copy(
            text = value
        )
    }
    fun changeTitleFocus(focusState: FocusState){
        _noteTitle.value = noteTitle.value.copy(
            isHintVisible = !focusState.isFocused &&
                    noteTitle.value.text.isBlank()
        )
    }
    fun enterContent(value: String){
        _noteContent.value = noteContent.value.copy(
            text = value
        )
    }
    fun changeContentFocus(focusState: FocusState){
        _noteContent.value = noteContent.value.copy(
            isHintVisible = !focusState.isFocused &&
                    noteContent.value.text.isBlank()
        )
    }
    fun changeColor(color: Int){
        _noteColor.value = color
    }
    fun saveNote(){
        viewModelScope.launch {
                noteUseCases.addNote(
                    Note(
                        title = noteTitle.value.text,
                        content = noteContent.value.text,
                        color = noteColor.value,
                        timeStamp = System.currentTimeMillis(),
                        id = currentNoteId
                    )
                )
        }
    }
}
