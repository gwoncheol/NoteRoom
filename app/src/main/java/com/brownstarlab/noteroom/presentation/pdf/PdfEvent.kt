package com.brownstarlab.noteroom.presentation.pdf

import android.content.Context
import android.net.Uri

sealed class PdfEvent {
    data class SetUri(val context: Context, val uri: Uri) : PdfEvent()
    data class SetMarginTop(val margin: Int) : PdfEvent()
    data class SetMarginBottom(val margin: Int) : PdfEvent()
    data class SetMarginStart(val margin: Int) : PdfEvent()
    data class SetMarginEnd(val margin: Int) : PdfEvent()
    data class SetName(val name: String) : PdfEvent()
    data object ClearUri : PdfEvent()
    data object ClearName : PdfEvent()
    data class Convert(val context: Context) : PdfEvent()
}