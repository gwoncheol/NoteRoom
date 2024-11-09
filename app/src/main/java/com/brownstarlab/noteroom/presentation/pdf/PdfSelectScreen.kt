package com.brownstarlab.noteroom.presentation.pdf

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.brownstarlab.noteroom.PDF_STEP_COUNT
import com.brownstarlab.noteroom.presentation.core.components.AppBarNavButton
import com.brownstarlab.noteroom.presentation.core.components.AppBarTitle
import com.brownstarlab.noteroom.presentation.core.theme.NoteRoomTheme
import com.brownstarlab.noteroom.showAsToast

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PdfSelectScreen(
    state: PdfState,
    emit: (PdfEvent) -> Unit,
    goBack: () -> Unit = {},
    goNext: () -> Unit = {},
) {
    val context = LocalContext.current
    // PDF 선택을 위한 launcher 설정
    val pdfPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            emit(PdfEvent.SetUri(context, it))
        } ?: "pdf 열기를 실패하였습니다.".showAsToast(context)
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
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "단계 1 / $PDF_STEP_COUNT",
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            if (state.uri == null && state.isConverting.not()) {
                FilledTonalButton(
                    onClick = {
                        pdfPickerLauncher.launch("application/pdf")
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(0.dp, 20.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = Color.Transparent
                    )
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "PDF 선택하기",
                                modifier = Modifier.padding(top = 4.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "PDF 선택하기",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.primary,
                            )
                        }
                        Text(
                            text = "pdf를 선택해주세요.",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
            if (state.uri == null && state.isConverting) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(0.dp, 20.dp),
                    verticalArrangement = Arrangement.Center,
                ) {
                    CircularProgressIndicator()
                }
            }
            if (state.uri != null && state.isConverting.not()) {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(top = 20.dp, bottom = 20.dp)
                ) {
                    items(state.bitmaps) { page ->
                        Image(
                            bitmap = page.asImageBitmap(),
                            contentDescription = null,
                            modifier = Modifier
                                .fillParentMaxSize()
                                .aspectRatio(page.width.toFloat() / page.height.toFloat())
                                .clip(RoundedCornerShape(10.dp))
                        )
                    }
                }
            }

            Row {
                if (state.uri != null) {
                    OutlinedButton(
                        onClick = { emit(PdfEvent.ClearUri) },
                        modifier = Modifier
                            .padding(end = 10.dp)
                            .size(54.dp),
                        shape = RoundedCornerShape(16.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Clear, contentDescription = "pdf 초기화")
                    }
                }
                Button(
                    onClick = goNext,
                    modifier = Modifier
                        .widthIn(max = 300.dp)
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(16.dp),
                    enabled = state.uri != null
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
}

@Preview(showBackground = true)
@Composable
fun PdfSelectScreenPreview() {
    NoteRoomTheme(
        darkTheme = true,
        dynamicColor = false
    ) {
        PdfSelectScreen(
            state = PdfState(
                uri = null,
                isConverting = false
            ),
            emit = {},
        )
    }
}