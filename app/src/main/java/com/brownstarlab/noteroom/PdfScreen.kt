import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.tom_roush.harmony.awt.geom.AffineTransform
import com.tom_roush.pdfbox.multipdf.LayerUtility
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.pdmodel.PDPage
import com.tom_roush.pdfbox.pdmodel.common.PDRectangle
import java.io.File

@Composable
fun PdfScreen(pdfUri: Uri?) {
    var topText by remember { mutableStateOf("") }
    var bottomText by remember { mutableStateOf("") }
    var leftText by remember { mutableStateOf("") }
    var rightText by remember { mutableStateOf("") }

    val context = LocalContext.current
    val numberFilter: (String) -> Boolean = { text ->
        text.isEmpty() || text.matches(Regex("^\\d+$"))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 파일 이름 표시
        pdfUri?.let { uri ->
            Text(
                text = "파일: ${uri.lastPathSegment ?: "알 수 없는 파일"}",
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        // 입력 필드들 (이전과 동일)
        OutlinedTextField(
            value = topText,
            onValueChange = { if (numberFilter(it)) topText = it },
            label = { Text("상단 여백") },
            suffix = { Text("px") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedTextField(
                value = leftText,
                onValueChange = { if (numberFilter(it)) leftText = it },
                label = { Text("왼쪽 여백") },
                suffix = { Text("px") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                singleLine = true
            )

            OutlinedTextField(
                value = rightText,
                onValueChange = { if (numberFilter(it)) rightText = it },
                label = { Text("오른쪽 여백") },
                suffix = { Text("px") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp),
                singleLine = true
            )
        }

        OutlinedTextField(
            value = bottomText,
            onValueChange = { if (numberFilter(it)) bottomText = it },
            label = { Text("하단 여백") },
            suffix = { Text("px") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Button(
            onClick = {
                pdfUri?.let { uri ->
                    try {
                        addMarginsToPdf(
                            context = context,
                            inputUri = uri,
                            topMargin = topText.toFloatOrNull() ?: 0f,
                            bottomMargin = bottomText.toFloatOrNull() ?: 0f,
                            leftMargin = leftText.toFloatOrNull() ?: 0f,
                            rightMargin = rightText.toFloatOrNull() ?: 0f
                        )
                        Toast.makeText(context, "PDF 여백 추가 완료!", Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        // 에러 로그 출력
                        Log.e("PdfScreen", "PDF 처리 중 에러 발생", e)
                        Toast.makeText(context, "PDF 처리 중 오류 발생: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            enabled = pdfUri != null && topText.isNotEmpty() && bottomText.isNotEmpty() &&
                     leftText.isNotEmpty() && rightText.isNotEmpty()
        ) {
            Text("여백 추가하기")
        }
    }
}

private fun addMarginsToPdf(
    context: Context,
    inputUri: Uri,
    topMargin: Float,
    bottomMargin: Float,
    leftMargin: Float,
    rightMargin: Float
) {
    val inputStream = context.contentResolver.openInputStream(inputUri)
    val outputFile = File(context.cacheDir, "output_${System.currentTimeMillis()}.pdf")

    try {
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

                val afTransform = AffineTransform.getTranslateInstance(leftMargin.toDouble(), topMargin.toDouble())
                layerUtility.appendFormAsLayer(newPage, formXObject, afTransform, "page_${page.hashCode()}")
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

        context.startActivity(Intent.createChooser(shareIntent, "공유하기"))

    } catch (e: Exception) {
        Log.e("PdfScreen", "PDF 처리 중 상세 에러", e)
        throw e  // 상위로 에러를 전파하여 Toast 메시지 표시
    }
}

@Composable
@Preview
fun PdfScreenPreview() {
    PdfScreen(null)
}