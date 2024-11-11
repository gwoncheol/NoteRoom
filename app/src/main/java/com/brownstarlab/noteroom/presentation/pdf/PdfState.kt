package com.brownstarlab.noteroom.presentation.pdf

import android.graphics.Bitmap
import android.net.Uri

data class PdfState(
    val uri: Uri? = null,
    val oldName: String? = null,
    val bitmaps: List<Bitmap> = emptyList(),
    val isConverting: Boolean = false,
    //
    val marginTop: Int? = null,
    val marginBottom: Int? = null,
    val marginStart: Int? = null,
    val marginEnd: Int? = null,
    val name: String? = null,
    //
    val resultUri: Uri? = null,
    val isError: Boolean = false
)