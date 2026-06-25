package com.dyiz.kidslearningapp.Screens.SplashScreens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.dyiz.kidslearningapp.Database.SensorManager.SessionManager
import com.dyiz.kidslearningapp.NavGraph.NavRoutes
import com.dyiz.kidslearningapp.R
import kotlinx.coroutines.delay

@Composable
fun ThirdSplashScreen(
    navController: NavHostController,
    sessionManager: SessionManager
){
    var startAnimation by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 1000),
        label = "TextScale"
    )

    LaunchedEffect(Unit) {
        startAnimation = true
        delay(2000)

        val savedId = sessionManager.currentChildId
        if(savedId==-1){
            navController.navigate(NavRoutes.CREATE_PROFILE){
                popUpTo(NavRoutes.THIRD_SPLASH_SCREEN){inclusive = true}
            }
        }else{
            navController.navigate(NavRoutes.HOME) {
                popUpTo(NavRoutes.THIRD_SPLASH_SCREEN) { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        Image(
            painter = painterResource(id = R.drawable.secondsplashscreenimage),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Text(
            text = "Kidovo",
            color = Color.White,
            fontSize = 40.sp,
            fontFamily = FontFamily(Font(R.font.balooregular)),
            modifier = Modifier.graphicsLayer(
                scaleX = scale,
                scaleY = scale,
                alpha = scale
            )
        )
    }
}