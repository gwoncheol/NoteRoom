package com.brownstarlab.noteroom.presentation.core.components

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun AppBarNavButton(
    onClick: () -> Unit
) {
    FilledTonalIconButton(
        onClick = onClick,
        colors = IconButtonDefaults.filledTonalIconButtonColors(
            containerColor = Color.Transparent,
        ),
        shape = RoundedCornerShape(12.dp),
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
            contentDescription = "이전",
            modifier = Modifier.size(40.dp)
        )
    }
}