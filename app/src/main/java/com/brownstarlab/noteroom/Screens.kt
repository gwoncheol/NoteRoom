package com.brownstarlab.noteroom

sealed class Screens(val route: String) {
    data object Home : Screens("home")
    data object Pdf : Screens("pdf")
    data object Preview : Screens("preview")
}