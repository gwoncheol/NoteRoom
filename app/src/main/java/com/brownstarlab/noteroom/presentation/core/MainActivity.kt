package com.brownstarlab.noteroom.presentation.core

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.brownstarlab.noteroom.Screens
import com.brownstarlab.noteroom.presentation.core.theme.NoteRoomTheme
import com.brownstarlab.noteroom.presentation.pdf.PdfEditScreen
import com.brownstarlab.noteroom.presentation.pdf.PdfEvent
import com.brownstarlab.noteroom.presentation.pdf.PdfSelectScreen
import com.brownstarlab.noteroom.presentation.pdf.PdfViewModel
import com.brownstarlab.noteroom.sharedViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        val pdfUri: Uri? = when (intent?.action) {
            Intent.ACTION_SEND -> {
                if (intent.type?.startsWith("application/pdf") == true) {
                    intent.getParcelableExtra(Intent.EXTRA_STREAM)
                } else null
            }

            Intent.ACTION_VIEW -> intent.data
            else -> null
        }

        setContent {
            NoteRoomTheme {
                AppNavigation(
                    pdfUri = pdfUri
                )
            }
        }
    }
}

@Composable
fun AppNavigation(
    pdfUri: Uri?
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = if (pdfUri == null) Screens.Home else Screens.Pdf
    ) {
        composable<Screens.Home> {
            HomeScreen(
                gotoPdfStep = {
                    navController.navigate(Screens.Pdf)
                }
            )
        }
        navigation<Screens.Pdf>(
            startDestination = Screens.Pdf.Select
        ) {
            composable<Screens.Pdf.Select> {
                val viewModel = it.sharedViewModel<PdfViewModel>(navController)
                val state = viewModel.state.collectAsStateWithLifecycle()
                val context = LocalContext.current
                val goNext = { navController.navigate(Screens.Pdf.Edit) }

                pdfUri?.let { uri ->
                    LaunchedEffect(key1 = uri) {
                        viewModel.emit(PdfEvent.SetUri(context, uri))
                    }
                }

                PdfSelectScreen(
                    state = state.value,
                    emit = { event -> viewModel.emit(event) },
                    goBack = { navController.popBackStack() },
                    goNext = goNext,
                )
            }
            composable<Screens.Pdf.Edit> {
                val viewModel = it.sharedViewModel<PdfViewModel>(navController)
                val state = viewModel.state.collectAsStateWithLifecycle()

                PdfEditScreen(
                    state = state.value,
                    emit = { event -> viewModel.emit(event) },
                    goBack = { navController.popBackStack() }
                )
            }
        }
    }
}