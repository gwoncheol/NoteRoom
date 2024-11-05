package com.brownstarlab.noteroom

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.brownstarlab.noteroom.ui.theme.NoteRoomTheme

@Composable
fun HomeScreen(
    navigateToPdf: (pdfUri: Uri) -> Unit
) {
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    // PDF 선택을 위한 launcher 설정
    val pdfPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri == null) {
            "pdf 열기를 실패하였습니다.".showAsToast(context)
        } else {
            navigateToPdf(uri)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxWidth()
    ) { scaffoldPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(scaffoldPadding)
        ) {
//            IconButton(
//                modifier = Modifier
//                    .padding(6.dp)
//                    .size(50.dp),
//                onClick = {}
//            ) {
//                Icon(
//                    imageVector = Icons.Default.KeyboardArrowLeft,
//                    contentDescription = "뒤로가기",
//                    modifier = Modifier.size(40.dp)
//                )
//            }
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
            ) {
                Text(
                    text = "NoteRoom",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                )
                Text(
                    text = "Version ${BuildConfig.VERSION_NAME}",
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(0.dp, 8.dp, 0.dp, 8.dp)
                )
                OutlinedButton(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally),
                    onClick = { uriHandler.openUri(GITHUB_URI) }
                ) {
                    Icon(
                        modifier = Modifier.size(20.dp),
                        imageVector = ImageVector.vectorResource(
                            id = R.drawable.github_icon
                        ),
                        contentDescription = "Goto Github"
                    )
                    Text(
                        text = "Github",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(6.dp, 2.dp, 0.dp, 2.dp)
                    )
                }
                OutlinedButton(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally),
                    onClick = {
                        pdfPickerLauncher.launch("application/pdf")
                    }
                ) {
                    Icon(
                        modifier = Modifier.size(20.dp),
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "시작하기"
                    )
                    Text(
                        text = "시작하기",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(2.dp, 2.dp, 0.dp, 2.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    NoteRoomTheme(
        darkTheme = true,
        dynamicColor = false
    ) {
        HomeScreen(
            navigateToPdf = {}
        )
    }
}