package com.brownstarlab.noteroom.presentation.pdf

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.brownstarlab.noteroom.PdfBitmapConverter
import com.brownstarlab.noteroom.getFileName
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PdfViewModel : ViewModel() {
    private val pdfBitmapConverter = PdfBitmapConverter()
    private val _state = MutableStateFlow(PdfState())
    val state = _state.asStateFlow()

    fun emit(event: PdfEvent) {
        when (event) {
            is PdfEvent.SetUri -> setUri(event.context, event.uri)
            is PdfEvent.SetMarginTop -> setMarginTop(event.margin)
            is PdfEvent.SetMarginBottom -> setMarginBottom(event.margin)
            is PdfEvent.SetMarginLeft -> setMarginLeft(event.margin)
            is PdfEvent.SetMarginRight -> setMarginRight(event.margin)
            is PdfEvent.SetName -> setName(event.name)
            is PdfEvent.ClearUri -> clearUri()
            is PdfEvent.ClearName -> clearName()
        }
    }

    private fun setUri(context: Context, uri: Uri) {
        if (uri == state.value.uri) return
        val newOldName = uri.getFileName(context)

        viewModelScope.launch {
            _state.value = state.value.copy(
                isConverting = true
            )
            val bitmaps = pdfBitmapConverter.convertFromUri(context, uri)
            _state.value = state.value.copy(
                uri = uri,
                oldName = newOldName,
                name = newOldName,
                bitmaps = bitmaps,
                isConverting = false
            )
        }
        _state.value = state.value.copy(
            uri = uri,
            oldName = newOldName,
            name = newOldName
        )
    }

    private fun setMarginTop(margin: Int) {
        if (margin == state.value.marginTop) return
        _state.value = state.value.copy(marginTop = margin)
    }

    private fun setMarginBottom(margin: Int) {
        if (margin == state.value.marginBottom) return
        _state.value = state.value.copy(marginBottom = margin)
    }

    private fun setMarginLeft(margin: Int) {
        if (margin == state.value.marginLeft) return
        _state.value = state.value.copy(marginLeft = margin)
    }

    private fun setMarginRight(margin: Int) {
        if (margin == state.value.marginRight) return
        _state.value = state.value.copy(marginRight = margin)
    }

    private fun setName(name: String) {
        if (name == state.value.name) return
        _state.value = state.value.copy(name = name)
    }

    private fun clearUri() {
        _state.value = state.value.copy(
            uri = null,
            oldName = null
        )
    }

    private fun clearName() {
        _state.value = state.value.copy(name = null)
    }
}





