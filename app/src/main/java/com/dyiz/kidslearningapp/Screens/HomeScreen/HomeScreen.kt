package com.dyiz.kidslearningapp.Screens.HomeScreen

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.currentStateAsState
import androidx.navigation.NavHostController
import com.dyiz.kidslearningapp.Database.ViewModel.MainViewModel
import com.dyiz.kidslearningapp.NavGraph.NavRoutes
import com.dyiz.kidslearningapp.R
import com.dyiz.kidslearningapp.util.validAvatarDrawableRes
import com.dyiz.kidslearningapp.utils.AnimatedGameCardWrapper
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(
    navController: NavHostController,
    viewModel: MainViewModel
) {
    val child by viewModel.activeChild.collectAsState()
    val isLocked by viewModel.isLocked.collectAsState()
    val badgeRefreshTick by viewModel.badgeGameProgress.refreshTick.collectAsState()
    val showBadgeUnlockDot = remember(child?.id, badgeRefreshTick) {
        val id = child?.id ?: -1
        id > 0 && viewModel.badgeGameProgress.hasBadgeDotPending(id)
    }
    val context = LocalContext.current
    val avatarPainterRes = remember(child?.id, child?.avatarRes) {
        context.validAvatarDrawableRes(child?.avatarRes)
    }
    val backgroundFromDb = child?.bgColor?.let { Color(it) } ?: Color.Transparent

    val limitSeconds =
        remember(child?.id, child?.limitHours, child?.limitMinutes) {
            ((child?.limitHours ?: 0) * 3600) + ((child?.limitMinutes ?: 0) * 60)
        }

    LaunchedEffect(child?.id, limitSeconds, isLocked) {
        if (child != null && limitSeconds > 0 && !isLocked) {
            viewModel.startScreenTimeTracking()
        } else {
            viewModel.stopTracking()
        }
    }
    val lifecycleOwner = LocalLifecycleOwner.current
    val state by lifecycleOwner.lifecycle.currentStateAsState()

    LaunchedEffect(state) {
        if (state == Lifecycle.State.RESUMED) {
            val savedId = viewModel.sessionManager.currentChildId
            if (savedId != -1) {
                viewModel.refreshChildData(savedId)
            }
        }
    }
    // home cards animation
    var startAnimation by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(300)
        startAnimation = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F7F7))
            .statusBarsPadding()
    ) {
        CompositionLocalProvider(
            LocalDensity provides Density(
                LocalDensity.current.density,
                fontScale = 1f
            )
        ) {
            Image(
                painter = painterResource(id = R.drawable.kidhomeillustrationimage),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter),
                contentScale = ContentScale.FillWidth
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .navigationBarsPadding()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp)
                ) {
                    // Top Bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        /*AppName*/
                        Box(
                            modifier = Modifier
                                .size(45.dp)
                                .background(backgroundFromDb, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(
                                    id = avatarPainterRes
                                ),
                                contentScale = ContentScale.Crop, modifier = Modifier.size(26.dp),
                                contentDescription = null,
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = child?.name ?: "Guest",
                            fontSize = 18.sp,
                            color = Color.Black,
                            fontFamily = FontFamily(Font(R.font.balootwomediam)),
                            modifier = Modifier.weight(1f)
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box {
                                TopBarCardIcon(iconRes = R.drawable.medalicon) {
                                    navController.navigate(NavRoutes.MY_BADGES)
                                }
                                if (showBadgeUnlockDot) {
                                    Surface(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .align(Alignment.TopEnd)
                                            .offset(x = (2).dp, y = (-6).dp),
                                        shape = CircleShape,
                                        color = Color.Red
                                    ) {}
                                }
                            }

                            // ---(Parent/User) with Notification Dot ---
                            Box {
                                TopBarCardIcon(iconRes = R.drawable.parenticon) {
                                    navController.navigate(NavRoutes.PARENT_AREA)
                                }

                                if (viewModel.sessionManager.isFirstTimeParentVisit) {
                                    Surface(
                                        modifier = Modifier
                                            .size(18.dp)
                                            .align(Alignment.TopEnd)
                                            .offset(x = (2).dp, y = (-6).dp),
                                    ) {
                                        Image(
                                            painter = painterResource(
                                                id = R.drawable.warningsign
                                            ),
                                            contentScale = ContentScale.Crop,
                                            contentDescription = null,
                                        )
                                    }
                                }

                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    /*Four cards section*/
                    key(child?.id,isLocked) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(15.dp)
                            ) {
                                Box(modifier = Modifier.weight(1f)) {
                                    AnimatedGameCardWrapper(visible = startAnimation, delay = 0) {
                                        GameCard(
                                            title = "Alphabets",
                                            imageRes = R.drawable.alphabetsgamebox2,
                                            backgroundColor = Color(0xFFFEE69C),
                                            isLocked = isLocked,
                                            onClick = {
                                                if (isLocked) {
                                                    Toast.makeText(
                                                        context,
                                                        "Your limit has exceeded! Please update limit from parent settings.",
                                                        android.widget.Toast.LENGTH_LONG
                                                    ).show()
                                                } else {
                                                    navController.navigate(NavRoutes.ALPHA_GAME_SCREEN)
                                                }
                                            }
                                        )
                                    }
                                }
                                Box(modifier = Modifier.weight(1f)) {
                                    AnimatedGameCardWrapper(visible = startAnimation, delay = 150) {
                                        GameCard(
                                            title = "Numbers",
                                            imageRes = R.drawable.numbergamebox2,
                                            backgroundColor = Color(0xFFE1CFF6),
                                            isLocked = isLocked,
//                                        modifier = Modifier.weight(1f),
                                            onClick = {
                                                if (isLocked) {
                                                    Toast.makeText(
                                                        context,
                                                        "Your limit has exceeded! Please update limit from parent settings.",
                                                        android.widget.Toast.LENGTH_LONG
                                                    ).show()
                                                } else {
                                                    navController.navigate(NavRoutes.NUMBER_GAME_SCREEN)
                                                }
                                            }
                                        )
                                    }
                                }
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(15.dp)
                            ) {
                                Box(modifier = Modifier.weight(1f)) {
                                    AnimatedGameCardWrapper(visible = startAnimation, delay = 300) {
                                        GameCard(
                                            title = "Shapes",
                                            imageRes = R.drawable.shapegamebox2,
                                            backgroundColor = Color(0xFFB2E5F7),
                                            isLocked = isLocked,
//                                        modifier = Modifier.weight(1f),
                                            onClick = {
                                                if (isLocked) {
                                                    Toast.makeText(
                                                        context,
                                                        "Your limit has exceeded! Please update limit from parent settings.",
                                                        android.widget.Toast.LENGTH_LONG
                                                    ).show()
                                                } else {
                                                    navController.navigate(NavRoutes.SHAPE_GAME_SCREEN)
                                                }
                                            }
                                        )
                                    }
                                }
                                Box(modifier = Modifier.weight(1f)) {
                                    AnimatedGameCardWrapper(visible = startAnimation, delay = 450) {
                                        GameCard(
                                            title = "Colors",
                                            imageRes = R.drawable.colorgamebox2,
                                            backgroundColor = Color(0xFFB4EFDD),
                                            isLocked = isLocked,
//                                        modifier = Modifier.weight(1f),
                                            onClick = {
                                                if (isLocked) {
                                                    Toast.makeText(
                                                        context,
                                                        "Your limit has exceeded! Please update limit from parent settings.",
                                                        android.widget.Toast.LENGTH_LONG
                                                    ).show()
                                                } else {
                                                    navController.navigate(NavRoutes.COLOR_GAME_SCREEN)
                                                }
                                            }
                                        )
                                    }
                                }
                            }
                            // Story Book row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(15.dp)
                            ) {
                                Box(modifier = Modifier.weight(1f)) {

                                    AnimatedGameCardWrapper(visible = startAnimation, delay = 450) {
                                        GameCard(
                                            title = "Story Books",
                                            imageRes = R.drawable.storybookimage,
                                            backgroundColor = Color(0xFFF6C0B9),
//                                        modifier = Modifier.weight(1f),
                                            onClick = { navController.navigate(NavRoutes.STORY_BOOKS) }
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TopBarCardIcon(iconRes: Int, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFEFEFEF).copy(alpha = 0.8f)),
        modifier = Modifier
            .size(42.dp)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onClick() }
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}


@Composable
fun GameCard(
    title: String,
    imageRes: Int,
    backgroundColor: Color,
    isLocked: Boolean = false,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .aspectRatio(0.95f)
            .fillMaxWidth()
            .padding(2.dp)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxSize(),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(4.dp, Color.White),
            colors = CardDefaults.cardColors(containerColor = if (isLocked) Color(0xFFD5DBDA) else backgroundColor),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = title,
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp),
                    contentScale = ContentScale.Crop,
                    colorFilter = if (isLocked) ColorFilter.colorMatrix(ColorMatrix().apply {
                        setToSaturation(
                            0f
                        )
                    }) else null
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .background(Color.White, RoundedCornerShape(12.dp))
                        .padding(vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = title,
                        fontSize = 15.sp,
                        fontFamily = FontFamily(Font(R.font.roboto_medium)),
                        color = Color(0xFF000000)
                    )
                }
            }
        }
        if (isLocked) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.15f), RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.lockoverly),
                    modifier = Modifier.size(50.dp),
                    contentDescription = "Locked"
                )
            }
        }
    }
}