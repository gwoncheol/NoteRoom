package com.brownstarlab.noteroom.presentation.pdf

import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.brownstarlab.noteroom.addFocusCleaner
import com.brownstarlab.noteroom.presentation.core.components.AppBarNavButton
import com.brownstarlab.noteroom.presentation.core.components.AppBarTitle
import com.brownstarlab.noteroom.presentation.core.theme.NoteRoomTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PdfResultScreen(
    state: PdfState,
    emit: (PdfEvent) -> Unit,
    goBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val canProcess = state.isConverting.not() and state.isError.not()
    val open = {
        state.resultUri?.let { uri ->
            val intent = Intent(Intent.ACTION_VIEW).apply {
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                data = uri
            }
            context.startActivity(intent)
        }
    }
    val share = {
        state.resultUri?.let { uri ->
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(shareIntent, "공유하기"))
        }
    }

    LaunchedEffect(true) {
        emit(PdfEvent.Convert(context))
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
            Box(
                modifier = Modifier.weight(1f)
            ) {
                if (state.isConverting) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(100.dp)
                    )
                } else {
                    Text(
                        text = "변환 완료",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
            OutlinedButton(
                onClick = { open() },
                modifier = Modifier
                    .widthIn(max = 300.dp)
                    .fillMaxWidth()
                    .padding(bottom = 10.dp)
                    .height(54.dp),
                shape = RoundedCornerShape(16.dp),
                enabled = canProcess
            ) {
                Text(
                    text = "열기",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
            OutlinedButton(
                onClick = { share() },
                modifier = Modifier
                    .widthIn(max = 300.dp)
                    .fillMaxWidth()
                    .height(54.dp)
                    .padding(bottom = 8.dp),
                shape = RoundedCornerShape(16.dp),
                enabled = canProcess
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "공유하기",
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = "공유하기",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
            Button(
                onClick = goBack,
                modifier = Modifier
                    .widthIn(max = 300.dp)
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(16.dp),
                enabled = state.isConverting.not()
            ) {
                Text(
                    text = "닫기",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PdfResultScreenPreview() {
    NoteRoomTheme(
        darkTheme = true,
        dynamicColor = false
    ) {
        PdfResultScreen(
            state = PdfState(
                isConverting = false
            ),
            emit = {},
            goBack = {}
        )
    }
}
