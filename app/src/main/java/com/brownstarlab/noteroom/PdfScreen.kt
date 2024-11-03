package com.brownstarlab.noteroom

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.brownstarlab.noteroom.ui.theme.NoteRoomTheme
import com.tom_roush.harmony.awt.geom.AffineTransform
import com.tom_roush.pdfbox.multipdf.LayerUtility
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.pdmodel.PDPage
import com.tom_roush.pdfbox.pdmodel.common.PDRectangle
import java.io.File

private fun isNumber(text: String): Boolean {
    return text.isEmpty() || text.matches(Regex("^\\d+$"))
}

@Composable
fun PdfScreen(pdfUri: Uri?) {
    val defaultFileNameWithExtension = if (pdfUri?.lastPathSegment == null) "알 수 없는 파일" else pdfUri
        .lastPathSegment + ".pdf"
    val defaultFileName = pdfUri?.lastPathSegment ?: "알 수 없는 파일"
    var fileName by remember { mutableStateOf(defaultFileName) }
    var topText by remember { mutableStateOf("") }
    var bottomText by remember { mutableStateOf("") }
    var leftText by remember { mutableStateOf("") }
    var rightText by remember { mutableStateOf("") }
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    Scaffold(
        modifier = Modifier.fillMaxWidth()
    ) { scaffoldPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(scaffoldPadding)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            focusManager.clearFocus()
                        }
                    )
                },
        ) {
//            PdfPreview(
//                pdfFile = File(pdfUri?.path ?: ""),
//                modifier = Modifier.width(300.dp).height(400.dp).background(color = Color.Gray),
//            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp, 20.dp, 20.dp, 0.dp)
                    .align(Alignment.BottomCenter)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "NoteRoom",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(end = 10.dp)
                    )
//                    Image(
//                        imageVector = ImageVector.vectorResource(id = R.drawable.noteroom_icon),
//                        contentDescription = "NoteRoom",
//                        modifier = Modifier.size(40.dp)
//                    )
                }
                Text(
                    text = "파일: $defaultFileNameWithExtension",
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                OutlinedTextField(
                    value = fileName,
                    onValueChange = { fileName = it },
                    label = { Text("파일 이름") },
                    suffix = { Text("pdf") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )
                OutlinedTextField(
                    value = topText,
                    onValueChange = { if (isNumber(it)) topText = it },
                    label = { Text("상단 여백") },
                    suffix = { Text("px") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    OutlinedTextField(
                        value = leftText,
                        onValueChange = { if (isNumber(it)) leftText = it },
                        label = { Text("왼쪽 여백") },
                        suffix = { Text("px") },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 4.dp),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = rightText,
                        onValueChange = { if (isNumber(it)) rightText = it },
                        label = { Text("오른쪽 여백") },
                        suffix = { Text("px") },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 4.dp),
                        singleLine = true
                    )
                }
                val execute = {
                    addMarginsToPdf(
                        context = context,
                        exportFileName = fileName,
                        inputUri = pdfUri,
                        topMargin = topText.toFloatOrNull() ?: 0f,
                        bottomMargin = bottomText.toFloatOrNull() ?: 0f,
                        leftMargin = leftText.toFloatOrNull() ?: 0f,
                        rightMargin = rightText.toFloatOrNull() ?: 0f
                    )
                }
                val isExecutable =
                    !(topText.isEmpty() && bottomText.isEmpty() && leftText.isEmpty() && rightText.isEmpty()) && fileName.isNotEmpty()

                OutlinedTextField(
                    value = bottomText,
                    onValueChange = { if (isNumber(it)) bottomText = it },
                    label = { Text("하단 여백") },
                    suffix = { Text("px") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                        }
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Button(
                    onClick = execute,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 16.dp),
                    enabled = isExecutable
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "여백 추가하기"
                    )
                    Text(
                        text = "여백 추가하기",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(6.dp)
                    )
                }
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
fun PdfScreenPreview() {
    NoteRoomTheme(
        darkTheme = true,
        dynamicColor = false
    ) {
        PdfScreen(Uri.parse(GITHUB_URI))
    }
}