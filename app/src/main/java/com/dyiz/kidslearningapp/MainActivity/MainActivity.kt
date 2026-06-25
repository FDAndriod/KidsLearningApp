package com.dyiz.kidslearningapp.MainActivity

import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.dyiz.kidslearningapp.Database.ViewModel.MainViewModel
import com.dyiz.kidslearningapp.NavGraph.AppNavGraph
import com.dyiz.kidslearningapp.NavGraph.NavRoutes
import com.dyiz.kidslearningapp.R
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val mainViewModel: MainViewModel by viewModels()


    private var pendingRoute by mutableStateOf<String?>(null)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pendingRoute = intent?.getStringExtra(EXTRA_TARGET_ROUTE)

        WindowCompat.setDecorFitsSystemWindows(window,true)
        setContent {
          MainScreen(
              mainViewModel = mainViewModel,
              targetRoute = pendingRoute,
              onRouteConsumed = { pendingRoute = null }
          )
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        pendingRoute = intent.getStringExtra(EXTRA_TARGET_ROUTE)
    }

    override fun onPause() {
        super.onPause()
        mainViewModel.stopTracking()
    }
    companion object {
        const val EXTRA_TARGET_ROUTE = "extra_target_route"
    }
}

@Composable
fun MainScreen(
    mainViewModel: MainViewModel,
    targetRoute: String? = null,
    onRouteConsumed: () -> Unit = {}
){
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val isMuted by mainViewModel.isGlobalMusicMuted.collectAsState()

    val globalPlayer = remember {
        MediaPlayer.create(context,R.raw.overallmusic).apply {
            isLooping = true
            setVolume(0.6f,0.6f)
        }
    }
    val isSettingEnabled by mainViewModel.isMusicSettingEnabled.collectAsState()
    val activity = context as? ComponentActivity
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val isSplashScreen = currentRoute == NavRoutes.SPLASH ||
            currentRoute == NavRoutes.SECOND_SPLASH ||
            currentRoute == NavRoutes.THIRD_SPLASH_SCREEN ||
            currentRoute==NavRoutes.HOME
            || currentRoute == NavRoutes.ALPHA_GAME_SCREEN || currentRoute == NavRoutes.SHAPE_GAME_SCREEN ||
            currentRoute == NavRoutes.COLOR_GAME_SCREEN || currentRoute == NavRoutes.NUMBER_GAME_SCREEN || currentRoute == NavRoutes.STORY_BOOKS ||
            currentRoute == NavRoutes.CREATE_PROFILE || currentRoute == NavRoutes.PARENT_AREA || currentRoute == NavRoutes.MY_BADGES

    val showTopBar = currentRoute in listOf(
        NavRoutes.DUMMY_ROUTE
    )

    LaunchedEffect(isSettingEnabled,isMuted) {
        if (isSettingEnabled && !isMuted) {
            if (!globalPlayer.isPlaying) globalPlayer.start()
        } else {
            if (globalPlayer.isPlaying) globalPlayer.pause()
        }
    }

    LaunchedEffect(currentRoute) {
        activity?.window?.let { window ->
            if (isSplashScreen) {
                WindowCompat.setDecorFitsSystemWindows(window, false)
            } else {
                WindowCompat.setDecorFitsSystemWindows(window, true)
            }
        }
    }
    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController.setSystemBarsColor(
            color = Color.Transparent,
            darkIcons = true
        )
    }
    LaunchedEffect(targetRoute) {
        if (!targetRoute.isNullOrBlank()) {
            navController.navigate(targetRoute) {
                launchSingleTop = true
            }
            onRouteConsumed()
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    if (isSettingEnabled && !isMuted && !globalPlayer.isPlaying) {
                        globalPlayer.start()
                    }
                }
                Lifecycle.Event.ON_PAUSE -> {
                    if (globalPlayer.isPlaying) globalPlayer.pause()
                }
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            globalPlayer.release()
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets.systemBars,
        topBar = {if(showTopBar)HomeTopBar()},
        containerColor = colorResource(id = R.color.white)
    ) {paddingValues ->
        val layoutDirection = LocalLayoutDirection.current
        val adjustedPadding = if (Build.VERSION.SDK_INT >= 36) {
            val statusBarPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
            PaddingValues(
                top = (paddingValues.calculateTopPadding() - statusBarPadding).coerceAtLeast(0.dp),
                bottom = paddingValues.calculateBottomPadding(),
                start = paddingValues.calculateStartPadding(layoutDirection),
                end = paddingValues.calculateEndPadding(layoutDirection)
            )
        }
        else {
            paddingValues
        }
        Box(modifier = Modifier
            .then(
                if(currentRoute  == NavRoutes.SPLASH || currentRoute == NavRoutes.SECOND_SPLASH ||
                    currentRoute == NavRoutes.THIRD_SPLASH_SCREEN ||
                    currentRoute == NavRoutes.HOME || currentRoute == NavRoutes.ALPHA_GAME_SCREEN || currentRoute == NavRoutes.SHAPE_GAME_SCREEN
                    || currentRoute == NavRoutes.COLOR_GAME_SCREEN  || currentRoute == NavRoutes.NUMBER_GAME_SCREEN || currentRoute == NavRoutes.STORY_BOOKS
                    || currentRoute == NavRoutes.CREATE_PROFILE || currentRoute == NavRoutes.PARENT_AREA || currentRoute == NavRoutes.MY_BADGES
                    )
                    Modifier
                else Modifier.padding(adjustedPadding)
            )
            .fillMaxSize()){
            AppNavGraph(navController = navController)
        }

    }

}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar() {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent
        ),
        title = {
            Text(
                text = "Kidovo",
                color = colorResource(id = R.color.hometoptextcolor),
                fontFamily = FontFamily(Font(R.font.balooregular)),
                fontSize = 22.sp
            )
        },
        actions = {
            IconButton(onClick = {
                /*ToDO*/
            }) {

            }
        }

    )
}