package com.brownstarlab.noteroom

sealed class Screens(val route: String) {
    data object Home : Screens("home")
    data object Pdf : Screens("pdf") {
        data object Select : Screens("pdf/select")
        data object Edit : Screens("pdf/edit")

        fun createRoute(uriString: String) = "pdf/$uriString"
    }
}

