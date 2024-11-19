package uni.agustiniana.ui.navigation

import kotlinx.serialization.Serializable

sealed class RouteScreen {

    @Serializable
    data object Home: RouteScreen()

    @Serializable
    data object Demo: RouteScreen()
}