package com.brownstarlab.noteroom.presentation.pdf

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.brownstarlab.noteroom.PDF_STEP_COUNT
import com.brownstarlab.noteroom.addFocusCleaner
import com.brownstarlab.noteroom.presentation.core.components.AppBarNavButton
import com.brownstarlab.noteroom.presentation.core.components.AppBarTitle
import com.brownstarlab.noteroom.presentation.core.theme.NoteRoomTheme
import com.brownstarlab.noteroom.showAsToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PdfEditScreen(
    state: PdfState,
    emit: (PdfEvent) -> Unit,
    goBack: () -> Unit = {},
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()
    val canProcess = state.name != null
    val doProcess = {
        scope.launch {
            addMarginToPdf2(
                context,
                state.name ?: "",
                state.bitmaps,
                state.marginTop ?: 0,
                state.marginBottom ?: 0,
                state.marginStart ?: 0,
                state.marginEnd ?: 0
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { AppBarTitle() },
                navigationIcon = {
                    AppBarNavButton(
                        onClick = goBack
                    )
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(20.dp)
                .addFocusCleaner(focusManager),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "단계 2 / $PDF_STEP_COUNT",
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = state.oldName ?: "선택된 pdf가 없습니다.",
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp, 20.dp)
            )
            Box(
                modifier = Modifier.weight(1f)
            ) {}
            OutlinedTextField(
                value = state.name ?: "",
                onValueChange = { emit(PdfEvent.SetName(it)) },
                placeholder = { Text("새로운 이름") },
                label = { Text("새로운 이름") },
                suffix = { Text("pdf") },
                singleLine = true,
                modifier = Modifier
                    .widthIn(max = 300.dp)
                    .fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                shape = RoundedCornerShape(16.dp),
            )
            OutlinedTextField(
                value = state.marginTop?.toString() ?: "",
                onValueChange = { emit(PdfEvent.SetMarginTop((it.toIntOrNull() ?: 0))) },
                placeholder = { Text("여백 크기") },
                label = { Text("위쪽 여백") },
                suffix = { Text("mm") },
                leadingIcon = { Icon(Icons.Default.KeyboardArrowUp, "위쪽 여백") },
                singleLine = true,
                modifier = Modifier
                    .widthIn(max = 300.dp)
                    .fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
                shape = RoundedCornerShape(16.dp),
            )
            OutlinedTextField(
                value = state.marginStart?.toString() ?: "",
                onValueChange = { emit(PdfEvent.SetMarginStart((it.toIntOrNull() ?: 0))) },
                placeholder = { Text("여백 크기") },
                label = { Text("왼쪽 여백") },
                suffix = { Text("mm") },
                leadingIcon = { Icon(Icons.AutoMirrored.Default.KeyboardArrowLeft, "위쪽 여백") },
                singleLine = true,
                modifier = Modifier
                    .widthIn(max = 300.dp)
                    .fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
                shape = RoundedCornerShape(16.dp),
            )
            OutlinedTextField(
                value = state.marginEnd?.toString() ?: "",
                onValueChange = { emit(PdfEvent.SetMarginEnd((it.toIntOrNull() ?: 0))) },
                placeholder = { Text("여백 크기") },
                label = { Text("오른쪽 여백") },
                suffix = { Text("mm") },
                leadingIcon = { Icon(Icons.AutoMirrored.Default.KeyboardArrowRight, "위쪽 여백") },
                singleLine = true,
                modifier = Modifier
                    .widthIn(max = 300.dp)
                    .fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
                shape = RoundedCornerShape(16.dp),
            )
            OutlinedTextField(
                value = state.marginBottom?.toString() ?: "",
                onValueChange = { emit(PdfEvent.SetMarginBottom((it.toIntOrNull() ?: 0))) },
                placeholder = { Text("여백 크기") },
                label = { Text("아래쪽 여백") },
                suffix = { Text("mm") },
                leadingIcon = { Icon(Icons.Default.KeyboardArrowDown, "위쪽 여백") },
                singleLine = true,
                modifier = Modifier
                    .widthIn(max = 300.dp)
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        if (canProcess) {
                            doProcess()
                        }
                    }
                ),
                shape = RoundedCornerShape(16.dp),
            )
            Button(
                onClick = { doProcess() },
                modifier = Modifier
                    .widthIn(max = 300.dp)
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(16.dp),
                enabled = canProcess
            ) {
                Text(
                    text = "다음",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }


}

private suspend fun addMarginToPdf2(
    context: Context,
    exportFileName: String,
    originalBitmaps: List<Bitmap>,
    marginTop: Int,
    marginBottom: Int,
    marginStart: Int,
    marginEnd: Int
) {
    withContext(Dispatchers.IO) {
        try {
            val outputFile = PdfDocument().let {
                originalBitmaps.map { bitmap ->
                    async {
                        val pageInfo = PdfDocument.PageInfo.Builder(
                            marginStart + bitmap.width + marginEnd,
                            marginTop + bitmap.height + marginBottom,
                            1
                        ).create()
                        val page = it.startPage(pageInfo)
                        page.canvas.drawBitmap(
                            bitmap,
                            marginStart.toFloat(),
                            marginTop.toFloat(),
                            null
                        )
                        it.finishPage(page)
                    }
                }.awaitAll()
                val outputFile = File(context.cacheDir, "${exportFileName}.pdf")
                it.writeTo(outputFile.outputStream())
                it.close()
                outputFile
            }
            // 파일 공유
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                outputFile
            )
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            withContext(Dispatchers.Main) {
                "PDF 여백 추가 완료!".showAsToast(context)
            }
            context.startActivity(Intent.createChooser(shareIntent, "공유하기"))
            (context as Activity).finish()
        } catch (e: Exception) {
            Log.e("PdfScreen", "PDF 처리 중 에러 발생", e)
            withContext(Dispatchers.Main) {
                "PDF 처리 중 오류 발생".showAsToast(context)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PdfEditScreenPreview() {
    NoteRoomTheme(
        darkTheme = true,
        dynamicColor = false
    ) {
        PdfEditScreen(
            state = PdfState(
                uri = Uri.EMPTY,
                oldName = "test.pdf",
            ),
            emit = {},
        )
    }
}