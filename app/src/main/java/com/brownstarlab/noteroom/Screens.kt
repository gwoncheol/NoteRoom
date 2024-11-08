package com.brownstarlab.noteroom

import kotlinx.serialization.Serializable

@Serializable
sealed class Screens {
    @Serializable
    data object Home : Screens()

    @Serializable
    data object Pdf : Screens() {
        @Serializable
        data object Select : Screens()

        @Serializable
        data object Edit : Screens()
    }
}

