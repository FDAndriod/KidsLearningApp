package com.dyiz.kidslearningapp.NavGraph

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

object NavRoutes {
    const val SPLASH = "splash"
    const val HOME = "home/{resId}/{bgColor}/{borderColor}"
    const val SECOND_SPLASH = "second_splash"
    const val THIRD_SPLASH_SCREEN = "third_splash_screen"
    const val DUMMY_ROUTE = "dummy_route"
    /*Game Screens*/
    const val ALPHA_GAME_SCREEN = "alpha_game_screen"
    const val NUMBER_GAME_SCREEN = "number_game_screen"
    const val SHAPE_GAME_SCREEN  = "shape_game_screen"
    const val COLOR_GAME_SCREEN = "color_game_screen"

    const val STORY_BOOKS = "story_books"
    const val CREATE_PROFILE = "create_profile"
    const val PARENT_AREA = "parent_area"
    const val MY_BADGES = "my_badges"

    fun createHomeRoute(resId: Int, bgColor: Color, borderColor: Color): String {
        return "home/$resId/${bgColor.toArgb()}/${borderColor.toArgb()}"
    }
}