package com.brownstarlab.noteroom

import android.content.Context
import android.widget.Toast

fun String.showAsToast(context: Context) {
    Toast.makeText(context, this, Toast.LENGTH_SHORT).show()
}