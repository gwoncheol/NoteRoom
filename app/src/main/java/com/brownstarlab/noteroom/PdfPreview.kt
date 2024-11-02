package com.brownstarlab.noteroom

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.rendering.PDFRenderer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

@Composable
fun PdfPreview(
    pdfFile: File,
    modifier: Modifier = Modifier,
    currentPage: Int = 0,
    onPageChange: (Int) -> Unit = {},
) {
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    var totalPages by remember { mutableIntStateOf(0) }
    var isLoading by remember { mutableStateOf(true) }
    var scale by remember { mutableFloatStateOf(1f) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    // PDF 초기화 및 페이지 로드
    LaunchedEffect(pdfFile, currentPage) {
        isLoading = true
        withContext(Dispatchers.IO) {
            try {
                PDFBoxResourceLoader.init(context)
                PDDocument.load(pdfFile).use { document ->
                    totalPages = document.numberOfPages
                    val renderer = PDFRenderer(document)
                    // 300 DPI로 렌더링
                    val renderedBitmap = renderer.renderImageWithDPI(
                        currentPage.coerceIn(0, totalPages - 1),
                        300f
                    )
                    bitmap = renderedBitmap
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    scale = (scale * zoom).coerceIn(0.5f, 3f)
                    offsetX += pan.x
                    offsetY += pan.y
                }
            }
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            bitmap?.let { bmp ->
                Image(
                    bitmap = bmp.asImageBitmap(),
                    contentDescription = "PDF page ${currentPage + 1}",
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer(
                            scaleX = scale,
                            scaleY = scale,
                            translationX = offsetX,
                            translationY = offsetY
                        )
                )
            }
        }
        // 페이지 네비게이션 컨트롤
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (currentPage > 0) {
                androidx.compose.material3.Button(
                    onClick = { onPageChange(currentPage - 1) }
                ) {
                    androidx.compose.material3.Text("이전")
                }
            }

            if (currentPage < totalPages - 1) {
                androidx.compose.material3.Button(
                    onClick = { onPageChange(currentPage + 1) }
                ) {
                    androidx.compose.material3.Text("다음")
                }
            }
        }
    }
}