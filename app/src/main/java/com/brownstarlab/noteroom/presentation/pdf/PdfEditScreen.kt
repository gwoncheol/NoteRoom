package com.brownstarlab.noteroom.presentation.pdf

import android.app.Activity
import android.content.Context
import android.content.Intent
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
import com.tom_roush.harmony.awt.geom.AffineTransform
import com.tom_roush.pdfbox.multipdf.LayerUtility
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.pdmodel.PDPage
import com.tom_roush.pdfbox.pdmodel.common.PDRectangle
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
    val canProcess = state.name != null
    val doProcess = {
        addMarginsToPdf(
            context,
            state.name ?: "",
            state.uri,
            state.marginTop?.toFloat() ?: 0f,
            state.marginBottom?.toFloat() ?: 0f,
            state.marginLeft?.toFloat() ?: 0f,
            state.marginRight?.toFloat() ?: 0f
        )
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
                value = state.marginLeft?.toString() ?: "",
                onValueChange = { emit(PdfEvent.SetMarginLeft((it.toIntOrNull() ?: 0))) },
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
                value = state.marginRight?.toString() ?: "",
                onValueChange = { emit(PdfEvent.SetMarginRight((it.toIntOrNull() ?: 0))) },
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

private fun addMarginsToPdf(
    context: Context,
    exportFileName: String,
    inputUri: Uri?,
    topMargin: Float,
    bottomMargin: Float,
    leftMargin: Float,
    rightMargin: Float,
) {
    if (inputUri == null) {
        Log.e("PdfScreen", "PDF 파일이 없습니다.")
        "PDF 파일이 없습니다.".showAsToast(context)
        return
    }

    try {
        val inputStream = context.contentResolver.openInputStream(inputUri)
        val outputFile = File(context.cacheDir, "${exportFileName}.pdf")
        // PDF 로드
        val oldDoc = PDDocument.load(inputStream)
        val newDoc = PDDocument()

        oldDoc.use {
            for (page in it.pages) {
                // 새로운 크기 계산
                val newBox = PDRectangle(
                    page.cropBox.width + leftMargin + rightMargin,
                    page.cropBox.height + topMargin + bottomMargin
                )
                // 새 페이지 생성
                val newPage = PDPage(newBox)
                newDoc.addPage(newPage)
                val layerUtility = LayerUtility(newDoc)
                val formXObject = layerUtility.importPageAsForm(it, page)
                val afTransform = AffineTransform.getTranslateInstance(
                    leftMargin.toDouble(),
                    topMargin.toDouble()
                )
                layerUtility.appendFormAsLayer(
                    newPage,
                    formXObject,
                    afTransform,
                    "page_${page.hashCode()}"
                )
            }
        }
        // 저장
        newDoc.save(outputFile)
        newDoc.close()
        // 파일 공유
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            outputFile
        )
//        val uri = outputFile.toUri()
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        "PDF 여백 추가 완료!".showAsToast(context)
        context.startActivity(Intent.createChooser(shareIntent, "공유하기"))
        (context as Activity).finish()

    } catch (e: Exception) {
        Log.e("PdfScreen", "PDF 처리 중 에러 발생", e)
        "PDF 처리 중 오류 발생".showAsToast(context)
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