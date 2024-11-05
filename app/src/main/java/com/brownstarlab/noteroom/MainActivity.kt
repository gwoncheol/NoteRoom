package com.brownstarlab.noteroom

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.brownstarlab.noteroom.ui.theme.NoteRoomTheme
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        PDFBoxResourceLoader.init(applicationContext)
        enableEdgeToEdge()
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
            NoteRoomTheme {
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
            HomeScreen(
                navigateToPdf = { uri ->
                    navController.navigate(Screens.Pdf.createRoute(uri.toString()))
                }
            )
        }
        composable(
            route = Screens.Pdf.route,
            arguments = listOf(
                navArgument("uri") {
                    type = NavType.StringType
                    nullable = true
                }
            )
        ) { backStackEntry ->
            val uri = backStackEntry.arguments?.getString("uri", pdfUri.toString())
            PdfScreen(uri)
        }
    }
}