package com.brownstarlab.noteroom

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.EaseInSine
import androidx.compose.animation.core.EaseOutSine
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.util.fastJoinToString
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import java.io.File

fun String.showAsToast(context: Context) {
    Toast.makeText(context, this, Toast.LENGTH_SHORT).show()
}

@Composable
inline fun <reified T : ViewModel> NavBackStackEntry.sharedViewModel(
    navController: NavHostController
): T {
    val navGraphRoute = destination.parent?.route ?: return viewModel()
    val parentEntry = remember(this) {
        navController.getBackStackEntry(navGraphRoute)
    }
    return viewModel(parentEntry)
}

fun Modifier.addFocusCleaner(focusManager: FocusManager, doOnClear: () -> Unit = {}): Modifier {
    return this.pointerInput(Unit) {
        detectTapGestures(
            onTap = {
                doOnClear()
                focusManager.clearFocus()
            }
        )
    }
}

fun isNumber(str: String): Boolean {
    return str.matches(Regex("[0-9]*"))
}

fun Uri.getFileName(context: Context): String {
    val cursor = context.contentResolver.query(this, null, null, null, null)

    return cursor?.use {
        it.moveToFirst()
        val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        val list = it.getString(nameIndex).split(".")
        list.subList(0, list.size - 1).fastJoinToString(".")
    } ?: File(path!!).name
}

fun enterTransition(): EnterTransition {
    return slideInHorizontally(
        animationSpec = tween(300, 0, EaseInSine),
        initialOffsetX = { it / 10 },
    ) + fadeIn(
        animationSpec = tween(300, 0, EaseInSine),
        initialAlpha = 0.0f
    )
}

fun popExitTransition(): ExitTransition {
    return slideOutHorizontally(
        targetOffsetX = { it / 10 },
        animationSpec = tween(300, 0, EaseOutSine)
    ) + fadeOut(
        animationSpec = tween(300, 0, EaseOutSine),
        targetAlpha = 0.0f
    )
}