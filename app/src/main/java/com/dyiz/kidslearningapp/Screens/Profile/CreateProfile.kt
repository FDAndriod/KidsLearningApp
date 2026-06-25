package com.dyiz.kidslearningapp.Screens.Profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController

import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.dyiz.kidslearningapp.Database.ViewModel.MainViewModel
import com.dyiz.kidslearningapp.NavGraph.NavRoutes
import com.dyiz.kidslearningapp.R


data class AvatarData(
    val resId: Int,
    val bgColor: Color,
    val borderColor: Color
)
@Composable
fun CreateProfile(
    navController: NavHostController,
    viewModel: MainViewModel = hiltViewModel()
) {
    var name by remember { mutableStateOf("") }
    var selectedAge by remember { mutableStateOf("2-4 yrs") }
    var selectedAvatar by remember { mutableIntStateOf(R.drawable.profilewhale) }


    val avatars = remember {
        listOf(
            AvatarData(R.drawable.profilewhale, Color(0xFFD1C4E9),Color(0xFF67419F)),      // Light Purple
            AvatarData(R.drawable.profilepenguin, Color(0xFFB3E5FC),Color(0xFF06A7D2)),    // Light Blue
            AvatarData(R.drawable.profilebutterfly, Color(0xFFFFCDD2),Color(0xFFFF5596)),  // Light Pink
            AvatarData(R.drawable.profiletiger, Color(0xFFFFE0B2),Color(0xFFFB923C))
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        CompositionLocalProvider(LocalDensity provides Density(LocalDensity.current.density, fontScale = 1f)) {

            // Background Image
            Image(
                painter = painterResource(id = R.drawable.profilebgimage),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Lottie Animation
                val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.gameprofilelottie))
                LottieAnimation(
                    composition = composition,
                    iterations = LottieConstants.IterateForever,
                    modifier = Modifier
                        .size(200.dp)
                        .padding(top = 20.dp)
                )

                Text(
                    "Let's create the profile",
                    color = Color(0xFF7E7C7C),
                    fontSize = 18.sp,
                    fontFamily = FontFamily(Font(R.font.poppin_regular))
                )

                Spacer(modifier = Modifier.height(16.dp))

                // --- Name Input ---
                LabelText("Kid's First Name")
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
//                        .padding(vertical = 8.dp)
                        .threeDShadow(cornerRadius = 20.dp) // 3D Shade
                        .background(Color.White, RoundedCornerShape(20.dp))
                        .padding(start = 16.dp, end = 16.dp,top = 2.dp)
                ) {
                    TextField(
                        value = name,
                        onValueChange = { name = it },
                        placeholder = {
                            Text(
                                text = "Enter First Name",
                                color = Color(0xFF7E7C7C),
                                fontSize = 15.sp,
                                fontFamily = FontFamily(Font(R.font.balootworegular))
                            )
                        },
                        textStyle = TextStyle(
                            color = Color(0xFF040505),
                            fontSize = 15.sp,
                            fontFamily = FontFamily(Font(R.font.balootworegular))
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // --- Age Selection ---
                LabelText("Kid's Age")
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 2.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val ages = listOf("2-4 yrs", "4-6 yrs", "6-8 yrs")
                    ages.forEach { age ->
                        AgeOptionCard(
                            text = age,
                            isSelected = selectedAge == age,
                            modifier = Modifier.weight(1f),
                            onClick = { selectedAge = age }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // --- Avatar Selection ---
                LabelText("Choose Avatar")
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 2.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    avatars.forEach { avatar ->
                        AvatarItem(
                            avatar = avatar,
                            isSelected = selectedAvatar == avatar.resId,
                            onClick = { selectedAvatar = avatar.resId }
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))
                // --- Let's Go Button ---
                val isEnabled = name.trim().isNotEmpty()

                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .height(65.dp)
                        .threeDShadow(
                            color = if (isEnabled) Color(0xFF8B4513).copy(alpha = 0.5f) else Color.Black.copy(
                                alpha = 0.2f
                            ),
                            offsetY = 6.dp,
                            cornerRadius = 35.dp
                        )
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() },
                            enabled = isEnabled
                        ) {
                            val avatarData =
                                avatars.find { it.resId == selectedAvatar } ?: avatars[0]


                            viewModel.createProfile(
                                name,
                                selectedAge,
                                selectedAvatar,
                                avatarData.bgColor.toArgb()
                            ) {
                                val route = NavRoutes.createHomeRoute(
                                    avatarData.resId,
                                    avatarData.bgColor,
                                    avatarData.borderColor
                                )
                                navController.navigate(route) {
                                    popUpTo(NavRoutes.CREATE_PROFILE) { inclusive = true }
                                }
                            }
                        }
                        .background(
                            brush = if (isEnabled) {
                                Brush.verticalGradient(
                                    listOf(
                                        Color(0xFFDC8035),
                                        Color(0xFFFB923C),
                                        Color(0xFFFB923C)

                                    )
                                ) // Orange 3D
                            } else {
                                Brush.verticalGradient(
                                    listOf(
                                        Color(0xFFA7A7A7),
                                        Color(0xFFD9D9D9),
                                        Color(0xFFD9D9D9)

                                    )
                                )
                            },
                            shape = RoundedCornerShape(35.dp)
                        )
                        .border(2.dp, Color.White.copy(alpha = 0.5f), RoundedCornerShape(35.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Let's Go",
                        fontSize = 21.sp,
                        color = Color.White,
                        fontFamily = FontFamily(Font(R.font.balooregular)),
                        style = TextStyle(
                            shadow = Shadow(
                                color = Color.Black.copy(alpha = 0.3f),
                                offset = Offset(0f, 4f),
                                blurRadius = 2f
                            )
                        )
                    )
                }

                Spacer(modifier = Modifier.height(40.dp)) // Bottom padding for all devices
            }
        }
    }
}
@Composable
fun LabelText(text: String) {
    Text(
        text = text,
        modifier = Modifier.fillMaxWidth(),
        fontFamily = FontFamily(Font(R.font.balootwosemibold)),
        fontSize = 17.sp,
        color = Color(0xFF404040)
    )
}
@Composable
fun AgeOptionCard(text: String, isSelected: Boolean, modifier: Modifier, onClick: () -> Unit) {
    val backgroundColor = if (isSelected) Color.White else Color.White.copy(alpha = 0.4f)
    val borderColor = if (isSelected) Color(0xFFFB923C) else Color.Transparent

    Box(
        modifier = modifier
            .height(55.dp)
            .threeDShadow(cornerRadius = 15.dp) // 3D Shade
            .background(backgroundColor, RoundedCornerShape(15.dp))
            .border(if (isSelected) 2.dp else 0.dp, borderColor, RoundedCornerShape(15.dp))
            .clickable (indication = null, interactionSource = remember { MutableInteractionSource() }){ onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontFamily = FontFamily(Font(R.font.poppin_regular)),
            color = if (isSelected) Color.Black else Color(0xFF7E7C7C)
        )
    }
}
@Composable
fun AvatarItem(avatar: AvatarData, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(65.dp)
//            .scale(if (isSelected) 1.1f else 1f)
            .background(
                color = avatar.bgColor,
                shape = CircleShape
            )
            .border(
                width = if (isSelected) 1.5.dp else 0.dp,
                color = if (isSelected) avatar.borderColor else Color.Transparent,
                shape = CircleShape
            )
            .clickable(
                indication = null, interactionSource = remember { MutableInteractionSource()}
            ) { onClick() }
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Image(painter = painterResource(id = avatar.resId), contentDescription = null)
    }
}
@Composable
fun Modifier.threeDShadow(
    color: Color = Color.Black.copy(alpha = 0.2f),
    offsetY: Dp = 4.dp,
    cornerRadius: Dp = 15.dp
) = this.drawBehind {
    val shadowColor = color
    val spread = offsetY.toPx()

    // Bottom shadow layer
    drawRoundRect(
        color = shadowColor,
        topLeft = Offset(0f, spread),
        size = size,
        cornerRadius = CornerRadius(cornerRadius.toPx())
    )
}