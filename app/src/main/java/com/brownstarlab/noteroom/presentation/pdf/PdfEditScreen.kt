package com.brownstarlab.noteroom.presentation.pdf

import android.net.Uri
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.brownstarlab.noteroom.PDF_STEP_COUNT
import com.brownstarlab.noteroom.addFocusCleaner
import com.brownstarlab.noteroom.presentation.core.components.AppBarNavButton
import com.brownstarlab.noteroom.presentation.core.components.AppBarTitle
import com.brownstarlab.noteroom.presentation.core.theme.NoteRoomTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PdfEditScreen(
    state: PdfState,
    emit: (PdfEvent) -> Unit,
    goBack: () -> Unit = {},
    goNext: () -> Unit = {},
) {
    val focusManager = LocalFocusManager.current
    val canProcess = state.name != null

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
                            goNext()
                        }
                    }
                ),
                shape = RoundedCornerShape(16.dp),
            )
            Button(
                onClick = goNext,
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