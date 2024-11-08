package com.brownstarlab.noteroom.presentation.core.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun AppBarTitle() {
    Text(
        text = "NoteRoom",
        fontSize = 26.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.6.sp,
    )
}