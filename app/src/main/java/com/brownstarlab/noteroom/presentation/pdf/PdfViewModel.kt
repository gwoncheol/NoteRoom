package com.brownstarlab.noteroom.presentation.pdf

import android.content.Context
import android.graphics.pdf.PdfDocument
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.brownstarlab.noteroom.AUTHORITY
import com.brownstarlab.noteroom.PdfBitmapConverter
import com.brownstarlab.noteroom.getFileName
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

class PdfViewModel : ViewModel() {
    private val pdfBitmapConverter = PdfBitmapConverter()
    private val _state = MutableStateFlow(PdfState())
    val state = _state.asStateFlow()

    fun emit(event: PdfEvent) {
        when (event) {
            is PdfEvent.SetUri -> setUri(event.context, event.uri)
            is PdfEvent.SetMarginTop -> setMarginTop(event.margin)
            is PdfEvent.SetMarginBottom -> setMarginBottom(event.margin)
            is PdfEvent.SetMarginStart -> setMarginLeft(event.margin)
            is PdfEvent.SetMarginEnd -> setMarginRight(event.margin)
            is PdfEvent.SetName -> setName(event.name)
            is PdfEvent.ClearUri -> clearUri()
            is PdfEvent.ClearName -> clearName()
            is PdfEvent.Convert -> viewModelScope.launch { convert(event.context) }
        }
    }

    private suspend fun convert(
        context: Context,
        onError: () -> Unit = {},
        minimumWait: Int = 1000
    ) {
        try {
            _state.value = _state.value.copy(
                isConverting = true,
                isError = false,
                resultUri = null,
            )
            val pdfDoc = PdfDocument().apply {
                val deferredList = _state.value.bitmaps.map { bitmap ->
                    viewModelScope.async {
                        val pageInfo = PdfDocument.PageInfo.Builder(
                            (_state.value.marginStart ?: 0) + bitmap.width + (_state.value.marginEnd
                                ?: 0),
                            (_state.value.marginTop
                                ?: 0) + bitmap.height + (_state.value.marginBottom ?: 0),
                            1
                        ).create()
                        val page = startPage(pageInfo).apply {
                            canvas.drawBitmap(
                                bitmap,
                                _state.value.marginStart?.toFloat() ?: 0f,
                                _state.value.marginTop?.toFloat() ?: 0f,
                                null
                            )
                        }
                        finishPage(page)
                    }
                }
                deferredList.toMutableList().add(
                    viewModelScope.async {
                        delay(minimumWait.toLong())
                    }
                )
                deferredList.awaitAll()
            }
            val uri = File(context.cacheDir, "${_state.value.name ?: "에러"}.pdf").let {
                pdfDoc.writeTo(it.outputStream())
                pdfDoc.close()
                FileProvider.getUriForFile(context, AUTHORITY, it)
            }
            _state.value = _state.value.copy(
                isConverting = false,
                resultUri = uri
            )
        } catch (e: Exception) {
            _state.value = _state.value.copy(
                isConverting = false,
                isError = true
            )
            onError()
        }

    }

    private fun setUri(context: Context, uri: Uri) {
        if (uri == state.value.uri) return
        val newOldName = uri.getFileName(context)

        viewModelScope.launch {
            _state.value = state.value.copy(
                isConverting = true
            )
            val bitmaps = pdfBitmapConverter.convertPdf2Bitmaps(context, uri)
            _state.value = state.value.copy(
                uri = uri,
                oldName = newOldName,
                name = newOldName,
                bitmaps = bitmaps,
                isConverting = false
            )
        }
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
        if (margin == state.value.marginStart) return
        _state.value = state.value.copy(marginStart = margin)
    }

    private fun setMarginRight(margin: Int) {
        if (margin == state.value.marginEnd) return
        _state.value = state.value.copy(marginEnd = margin)
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





