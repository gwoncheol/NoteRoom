package com.brownstarlab.noteroom

import PdfScreen
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.brownstarlab.noteroom.ui.theme.NoteroomTheme
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        PDFBoxResourceLoader.init(applicationContext)

        // Intent로 전달받은 PDF Uri를 저장할 변수
        var pdfUri: Uri? = null

        // Intent 처리
        when (intent?.action) {
            Intent.ACTION_SEND -> {
                if (intent.type?.startsWith("application/pdf") == true) {
                    pdfUri = intent.getParcelableExtra(Intent.EXTRA_STREAM)
                }
            }
            Intent.ACTION_VIEW -> {
                // VIEW 인텐트로 전달된 PDF URI 처리
                pdfUri = intent.data
            }
        }

        setContent {
            NoteroomTheme {
                AppNavigation(
                    startDestination = if (pdfUri == null) Screens.Home.route else Screens.Pdf.route,
                    pdfUri = pdfUri
                )
            }
        }
    }
}

@Composable
fun AppNavigation(
    startDestination: String,
    pdfUri: Uri?
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = startDestination) {
        composable(Screens.Home.route) {
            HomeScreen()
        }
        composable(Screens.Pdf.route) {
            PdfScreen(pdfUri)
        }
    }
}