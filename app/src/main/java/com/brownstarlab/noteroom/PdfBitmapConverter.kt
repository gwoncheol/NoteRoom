package com.brownstarlab.noteroom

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfDocument.PageInfo
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.internal.wait
import java.io.File

class PdfBitmapConverter {
    var pdfRenderer: PdfRenderer? = null

    suspend fun convertPdf2Bitmaps(context: Context, contentUri: Uri): List<Bitmap> =
        withContext(Dispatchers.IO) {
            pdfRenderer?.close()

            context.contentResolver.openFileDescriptor(contentUri, "r")?.use { descriptor ->
                with(PdfRenderer(descriptor)) {
                    pdfRenderer = this
                    (0 until pageCount).map { index ->
                        async {
                            openPage(index).use { page ->
                                val bitmap =
                                    Bitmap.createBitmap(
                                        page.width,
                                        page.height,
                                        Bitmap.Config.ARGB_8888
                                    )
                                val canvas = Canvas(bitmap).apply {
                                    drawColor(Color.WHITE)
                                    drawBitmap(bitmap, 0f, 0f, null)
                                }

                                page.render(
                                    bitmap,
                                    null,
                                    null,
                                    PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY
                                )

                                bitmap
                            }
                        }
                    }.awaitAll()
                }
            } ?: emptyList()
        }

    suspend fun convertBitmaps2Pdf(
        context: Context,
        bitmaps: List<Bitmap>,
        destination: Uri,
        marginTop: Int,
        marginBottom: Int,
        marginStart: Int,
        marginEnd: Int
    ) {
        withContext(Dispatchers.IO) {
            PdfDocument().let {
                bitmaps.map { bitmap ->
                    async {
                        val pageInfo = PageInfo.Builder(
                            marginStart + bitmap.width + marginEnd,
                            marginTop + bitmap.height + marginBottom,
                            1
                        ).create()
                        val page = it.startPage(pageInfo)
                        page.canvas.drawBitmap(
                            bitmap,
                            marginStart.toFloat(),
                            marginEnd.toFloat(),
                            null
                        )
                        it.finishPage(page)
                    }
                }.awaitAll()

                it.writeTo(context.contentResolver.openOutputStream(destination))
                it.close()
            }
        }
    }

}