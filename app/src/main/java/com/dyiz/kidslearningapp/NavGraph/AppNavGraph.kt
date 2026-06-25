package com.dyiz.kidslearningapp.NavGraph

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.dyiz.kidslearningapp.Database.ViewModel.MainViewModel
import com.dyiz.kidslearningapp.Screens.GamesScreen.AlphaGameScreen
import com.dyiz.kidslearningapp.Screens.GamesScreen.ColorGameScreen
import com.dyiz.kidslearningapp.Screens.Profile.CreateProfile
import com.dyiz.kidslearningapp.Screens.HomeScreen.HomeScreen
import com.dyiz.kidslearningapp.Screens.GamesScreen.NumberGameScreen
import com.dyiz.kidslearningapp.Screens.ParentScreen.ParentsAreaScreen
import com.dyiz.kidslearningapp.Screens.SplashScreens.SecondSplash
import com.dyiz.kidslearningapp.Screens.GamesScreen.ShapeGameScreen
import com.dyiz.kidslearningapp.Screens.SplashScreens.SplashScreen
import com.dyiz.kidslearningapp.Screens.StoryBooksScreen.StoryBooksScreen
import com.dyiz.kidslearningapp.Screens.SplashScreens.ThirdSplashScreen
import com.dyiz.kidslearningapp.Screens.badgesScreen.MyBadgesScreen

@Composable
fun AppNavGraph(
    navController: NavHostController
){
    val mainViewModel: MainViewModel = hiltViewModel()
    NavHost(
        navController = navController,
        startDestination = NavRoutes.SPLASH,
        enterTransition = { fadeIn(animationSpec = tween(300)) },
        exitTransition = { fadeOut(animationSpec = tween(300)) }
    ) {
        composable(NavRoutes.SPLASH) {
            SplashScreen(navController = navController)
        }
        composable(NavRoutes.SECOND_SPLASH){
            SecondSplash(navController=navController)
        }
        composable(NavRoutes.THIRD_SPLASH_SCREEN){
            val sessionManager = hiltViewModel<MainViewModel>().sessionManager
            ThirdSplashScreen(navController = navController,sessionManager)
        }
        composable(
            NavRoutes.HOME,
            enterTransition = { fadeIn(animationSpec = tween(200)) },
            exitTransition = { fadeOut(animationSpec = tween(200)) }
        ) {
            HomeScreen(navController,
                mainViewModel
            )
        }

        /*Alphabet Game Screens*/
        composable(
            NavRoutes.ALPHA_GAME_SCREEN,
            enterTransition = { fadeIn(animationSpec = tween(200)) },
            exitTransition = { fadeOut(animationSpec = tween(200)) }
        ){
            AlphaGameScreen(navController = navController,mainViewModel)
        }

        /*Numbers Game Screen*/
        composable(
            NavRoutes.NUMBER_GAME_SCREEN,
            enterTransition = { fadeIn(animationSpec = tween(200)) },
            exitTransition = { fadeOut(animationSpec = tween(200)) }
        ){
            NumberGameScreen(navController = navController,mainViewModel = mainViewModel)
        }

        /*Shape Game Screen*/
        composable(
            NavRoutes.SHAPE_GAME_SCREEN,
            enterTransition = { fadeIn(animationSpec = tween(200)) },
            exitTransition = { fadeOut(animationSpec = tween(200)) }
        ){
            ShapeGameScreen(navController = navController,mainViewModel=mainViewModel)
        }
        /*Color Game Screen*/
        composable(
            NavRoutes.COLOR_GAME_SCREEN,
            enterTransition = { fadeIn(animationSpec = tween(200)) },
            exitTransition = { fadeOut(animationSpec = tween(200)) }
        ){
            ColorGameScreen(navController = navController,mainViewModel = mainViewModel)
        }

        // story books
        composable(
            NavRoutes.STORY_BOOKS,
            enterTransition = { fadeIn(animationSpec = tween(200)) },
            exitTransition = { fadeOut(animationSpec = tween(200)) }
        ){
            StoryBooksScreen(navController = navController,mainViewModel = mainViewModel)
        }

        // Kids Profile

        composable(
            NavRoutes.CREATE_PROFILE,
            enterTransition = { fadeIn(animationSpec = tween(200)) },
            exitTransition = { fadeOut(animationSpec = tween(200)) }
        ) {
            CreateProfile(navController = navController)
        }

        composable(
            NavRoutes.PARENT_AREA
        ) {
            ParentsAreaScreen(navController = navController,mainViewModel)
        }

        composable(
            NavRoutes.MY_BADGES,
        ) {
            MyBadgesScreen(navController = navController,mainViewModel)
        }

    }
}