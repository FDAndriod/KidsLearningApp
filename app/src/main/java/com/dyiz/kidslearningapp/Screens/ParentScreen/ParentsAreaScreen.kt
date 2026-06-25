package com.dyiz.kidslearningapp.Screens.ParentScreen

import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavHostController
import com.dyiz.kidslearningapp.Database.Model.ChildProfile
import com.dyiz.kidslearningapp.Database.ViewModel.MainViewModel
import com.dyiz.kidslearningapp.NavGraph.NavRoutes
import com.dyiz.kidslearningapp.R
import com.dyiz.kidslearningapp.Screens.Profile.AvatarData
import com.dyiz.kidslearningapp.Screens.Profile.AvatarItem
import com.dyiz.kidslearningapp.Screens.Profile.LabelText
import com.dyiz.kidslearningapp.Screens.Profile.threeDShadow
import com.dyiz.kidslearningapp.util.validAvatarDrawableRes
import com.dyiz.kidslearningapp.utils.feedback
import com.dyiz.kidslearningapp.utils.privacyPolicy
import com.dyiz.kidslearningapp.utils.shareApp
import java.util.Calendar

@Composable
fun ParentsAreaScreen(
    navController: NavHostController,
    viewModel: MainViewModel
) {
    val allChildren by viewModel.repository.getAllChildren().collectAsState(initial = emptyList())
    var isVerified by remember { mutableStateOf(false) }
    var yearInput by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    // Logic for Verification
    val currentYear = Calendar.getInstance().get(Calendar.YEAR)

    LaunchedEffect(Unit) {
        viewModel.markParentVisited()
    }


    LaunchedEffect(yearInput) {
        if (yearInput.length == 4) {
            val birthYear = yearInput.toIntOrNull() ?: 0
            val age = currentYear - birthYear
            if (age in 18..80) {
                isVerified = true
                errorMessage = null
            } else if(age<18){
                errorMessage = "You are under the age limit!"
                yearInput = "" // Reset
            } else{
                errorMessage = "Invalid year! Age limit is 80."
                yearInput = "" // Reset
            }
        } else if (yearInput.isNotEmpty()) {
            errorMessage = null
        }
    }
    if (!isVerified) {
        ParentGateOverlay(
            input = yearInput,
            errorMessage = errorMessage,
            onNumberClick = { if (yearInput.length < 4) yearInput += it },
            onDelete = { if (yearInput.isNotEmpty()) yearInput = yearInput.dropLast(1) },
            onClose = { navController.navigateUp() }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        CompositionLocalProvider(
            LocalDensity provides Density(
                LocalDensity.current.density,
                fontScale = 1f
            )
        ) {
            Image(
                painter = painterResource(id = R.drawable.parentscreenbg),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .navigationBarsPadding(),
                contentScale = ContentScale.FillBounds
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .verticalScroll(rememberScrollState())
            ) {

                ParentsAreaContent(
                    viewModel = viewModel,
                    children = allChildren,
                    navController = navController,
                    onAddChild = { navController.navigate(NavRoutes.CREATE_PROFILE) }
                )
            }
        }
    }
}
@Composable
fun ParentGateOverlay(
    input: String,    onNumberClick: (String) -> Unit,
    errorMessage: String?=null,
    onDelete: () -> Unit,
    onClose: () -> Unit
) {

    Dialog(
        onDismissRequest = {onClose()},
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        CompositionLocalProvider(LocalDensity provides Density(LocalDensity.current.density, fontScale = 1f)) {

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                // --- Main White Card ---
                Card(
                    shape = RoundedCornerShape(30.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(4.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                ) {
                    Box(modifier = Modifier.padding(vertical = 23.dp, horizontal = 20.dp)) {

                        // --- Blue Star (Top Left) ---
                        Image(
                            painter = painterResource(id = R.drawable.starblue),
                            contentDescription = null,
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .offset(x = (10).dp, y = (140).dp)
                                .alpha(0.8f)
                        )

                        // --- Red Star (Bottom Right) ---
                        Image(
                            painter = painterResource(id = R.drawable.starblue),
                            contentDescription = null,
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .offset(x = 5.dp, y = (-20).dp)
                                .alpha(0.8f)
                        )
                        // --- Blue Star (Top Right) ---
                        Image(
                            painter = painterResource(id = R.drawable.starred),
                            contentDescription = null,
                            modifier = Modifier
//                            .size(30.dp)
                                .align(Alignment.TopEnd)
                                .offset(x = (-5).dp, y = (50).dp)
                                .alpha(0.8f)
                        )

                        // --- Red Star (Bottom Right) ---
                        Image(
                            painter = painterResource(id = R.drawable.starred),
                            contentDescription = null,
                            modifier = Modifier
//                            .size(25.dp)
                                .align(Alignment.BottomStart)
                                .offset(x = 5.dp, y = (-70).dp)
                                .alpha(0.8f)
                        )
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Title
                            Text(
                                text = "Enter Your Year of Birth",
                                color = Color(0xFFF56C12), // Exact Orange
                                fontSize = 20.sp,
                                fontFamily = FontFamily(Font(R.font.balooregular)),
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "(For Parents area access)",
                                color = Color(0xFF7E7C7C), // Exact Orange
                                fontSize = 12.sp,
                                fontFamily = FontFamily(Font(R.font.inter_regular)),
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            // --- PIN Display (Horizontal Lines) ---
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(15.dp),
                                verticalAlignment = Alignment.Bottom
                            ) {
                                repeat(4) { index ->
                                    val digit =
                                        if (index < input.length) input[index].toString() else ""
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.width(50.dp)
                                    ) {
                                        Text(
                                            text = digit,
                                            fontSize = 22.sp,
                                            color = Color.Black,
                                            fontFamily = FontFamily(Font(R.font.balootwomediam))
                                        )
                                        // Horizontal Line
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(2.dp)
                                                .background(
                                                    Color(0xFFB5B5B5),
                                                    RoundedCornerShape(2.dp)
                                                )
                                        )
                                    }
                                }
                            }
                            // --- Error Message
                            Box(
                                modifier = Modifier
                                    .height(25.dp)
                                    .padding(top = 8.dp)
                            ) {
                                if (errorMessage != null) {
                                    Text(
                                        text = errorMessage,
                                        color = Color(0xFFED1C2A),
                                        fontSize = 14.sp,
                                        fontFamily = FontFamily(Font(R.font.balootworegular)),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(30.dp))

                            // --- Custom Keypad ---
                            val keypadNumbers = listOf(
                                listOf("1", "2", "3"),
                                listOf("4", "5", "6"),
                                listOf("7", "8", "9"),
                                listOf("", "0", "DEL")
                            )

                            Column(
                                verticalArrangement = Arrangement.spacedBy(15.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                keypadNumbers.forEach { row ->
                                    Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                                        row.forEach { item ->
                                            if (item.isEmpty()) {
                                                Spacer(modifier = Modifier.size(65.dp))
                                            } else {
                                                KeypadButton(
                                                    text = item,
                                                    isDelete = item == "DEL",
                                                    onClick = {
                                                        if (item == "DEL") onDelete() else onNumberClick(
                                                            item
                                                        )
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                }
                // --- Close Button (Red Square at Top Right) ---
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = (-10).dp, y = (50).dp)
                        .size(32.dp)
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }) { onClose() },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.crossimagestory),
                        contentDescription = "Close",
                        modifier = Modifier.size(50.dp)
                    )
                }

            }
        }
    }
}

@Composable
fun KeypadButton(text: String, isDelete: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(65.dp)
            .clip(CircleShape)
            .background(Color(0xFFFFEDDA)) // Light Peach/Beige background
            .clickable(
                indication = null, interactionSource = remember { MutableInteractionSource() }
            ) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (isDelete) {
            Box(
                modifier = Modifier
                    .size(65.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF0EDE9)), // Light Peach/Beige background
                contentAlignment = Alignment.Center
            ) {
            // Use your Backspace/Delete Icon
            Image(
                painter = painterResource(id = R.drawable.paddel), // Ensure this exists
                contentDescription = "Delete",
                modifier = Modifier.size(28.dp)
            )
                }
        }
        else {
            Text(
                text = text,
                fontSize = 22.sp,
                color = Color.Black,
                fontFamily = FontFamily(Font(R.font.balootworegular))
            )
        }
    }
}

@Composable
fun ParentsAreaContent(
    viewModel: MainViewModel,
    children: List<ChildProfile>,
    navController: NavHostController,
    onAddChild: () -> Unit
) {
    val context = LocalContext.current
    val appVersion = remember {
        try {
            @Suppress("DEPRECATION")
            context.packageManager.getPackageInfo(context.packageName, 0).versionName ?: "1.0"
        } catch (_: Exception) {
            "1.0"
        }
    }
    val activeChild by viewModel.activeChild.collectAsState()
    // --- States for Delete Feature ---
    var isDeleteMode by remember { mutableStateOf(false) }
    val selectedChildren = remember { mutableStateListOf<Int>() }
    // --- States for Child Edit ---
    var showEditSheet by remember { mutableStateOf(false) }
    var childToEdit by remember { mutableStateOf<ChildProfile?>(null) }


    var showTimePicker by remember { mutableStateOf(false) }
    var showRemoveLimitDialog by remember { mutableStateOf(false) }
// --- Screen Time Section inside ParentsAreaContent ---
    val limitHours = activeChild?.limitHours ?: 0
    val limitMinutes = activeChild?.limitMinutes ?: 0
    val isTimeSet = limitHours > 0 || limitMinutes > 0

    val timeDisplayValue = if (isTimeSet) {
        "${limitHours.toString().padStart(2, '0')}h ${limitMinutes.toString().padStart(2, '0')}m"
    } else {
        "Off"
    }

    val isMusicEnabled by viewModel.isMusicSettingEnabled.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // --- Custom Top Bar ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.backarrowgame),
                contentDescription = "Back",
                modifier = Modifier
                    .size(16.dp)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) { navController.popBackStack() },
            )
            Text(
                text = "Parents Area",
                fontSize = 22.sp,
                fontFamily = FontFamily(Font(R.font.balootwomediam)),
                modifier = Modifier.padding(start = 16.dp)
            )
        }
        // --- Your Children Section ---
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize(),
            shape = RoundedCornerShape(30.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF2E4)) // Light Peach
        ) {
            Column(
                modifier = Modifier.padding(15.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        "Your Children",
                        modifier = Modifier.align(Alignment.Center),
                        color = Color(0xFFF56C12),
                        fontFamily = FontFamily(Font(R.font.balootwobold)),
                        fontSize = 18.sp
                    )
                    if (children.isNotEmpty()) {
                        Image(
                            painter = painterResource(id = R.drawable.childrendelicon),
                            contentDescription = "Toggle Delete",
                            modifier = Modifier
//                            .size(24.dp)
                                .align(Alignment.CenterEnd)
                                .clickable(
                                    indication = null,
                                    interactionSource = remember { MutableInteractionSource() }
                                ) {
                                    isDeleteMode = !isDeleteMode
                                    selectedChildren.clear()
                                },
                            colorFilter = if (isDeleteMode) ColorFilter.tint(Color.Gray) else null
                        )
                    }
                }
                Spacer(modifier = Modifier.height(13.dp))

                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    // List of Children from Database
                    items(children, key = { it.id }) { child ->
                        val context = LocalContext.current
                        val safeAvatarRes = remember(child.id, child.avatarRes) {
                            context.validAvatarDrawableRes(child.avatarRes)
                        }
                        val isSelectedForDelete = selectedChildren.contains(child.id)
                        val isSelected = activeChild?.id == child.id
                        val borderCol = if (isSelected) Color(0xFFFF5596) else Color.Transparent

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .padding(horizontal = 10.dp)
                                .clickable(
                                    indication = null,
                                    interactionSource = remember { MutableInteractionSource() }
                                ) {
                                    if (isDeleteMode) {
                                        if (isSelectedForDelete) selectedChildren.remove(child.id)
                                        else selectedChildren.add(child.id)
                                    } else {
                                        viewModel.selectChild(child.id)
                                        Toast.makeText(
                                            context,
                                            "${child.name} selected",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                        ) {
                            Box(contentAlignment = Alignment.TopEnd) {
                                // Avatar Circle
                                Box(
                                    modifier = Modifier
                                        .size(70.dp)
                                        .background(
                                            Color(child.bgColor).copy(alpha = 0.4f),
                                            CircleShape
                                        )
                                        .border(2.dp, borderCol, CircleShape)
                                        .padding(10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Image(
                                        painter = painterResource(id = safeAvatarRes),
                                        contentDescription = null,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                                // Edit (Pencil) Icon
                                if (isDeleteMode) {
                                    // Red Selection Circle
                                    Box(
                                        modifier = Modifier
                                            .size(22.dp)
                                            .offset(x = 4.dp, y = (-2).dp)
                                            .background(Color.White, CircleShape)
                                            .border(2.dp, Color.Red, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (isSelectedForDelete) {
                                            Box(
                                                modifier = Modifier
                                                    .size(12.dp)
                                                    .background(Color.Red, CircleShape)
                                            )
                                        }
                                    }
                                }
                                else {
                                    Image(
                                        painter = painterResource(id = R.drawable.penciliconchild),
                                        contentDescription = "Edit",
                                        modifier = Modifier
                                            .size(27.dp)
                                            .offset(x = (2).dp, y = (-2).dp)
                                            .clickable(
                                                indication = null,
                                                interactionSource = remember { MutableInteractionSource() }
                                            ) {
                                                childToEdit = child
                                                showEditSheet = true
                                            }
                                    )
                                }

                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = child.name,
                                fontSize = 14.sp,
                                fontFamily = FontFamily(Font(R.font.balootworegular)),
                                color = Color.Black
                            )
                        }
                    }

                    // Add Child Button (Dashed Orange)
                    if (!isDeleteMode) {
                        item {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .padding(horizontal = 10.dp)
                                    .clickable(
                                        indication = null,
                                        interactionSource = remember { MutableInteractionSource() }
                                    ) { onAddChild() }
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(70.dp)
                                        .drawBehind {
                                            drawCircle(
                                                color = Color(0xFFFF9D5C),
                                                style = Stroke(
                                                    width = 4f,
                                                    pathEffect = PathEffect.dashPathEffect(
                                                        floatArrayOf(
                                                            15f,
                                                            15f
                                                        )
                                                    )
                                                )
                                            )
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.Add,
                                        contentDescription = null,
                                        tint = Color(0xFFFB923C),
                                        modifier = Modifier.size(30.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Add a Child",
                                    fontSize = 14.sp,
                                    fontFamily = FontFamily(Font(R.font.balootworegular)),
                                    color = Color.Black
                                )
                            }
                        }
                    }

                }
            }
            // --- Bottom Delete Button Section (Animated) ---
            AnimatedVisibility(
                visible = isDeleteMode,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(8.dp))


                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.65f)
                            .height(50.dp)
                            .clickable(
                                enabled = selectedChildren.isNotEmpty(),
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ) {
                                val nameToDelete =
                                    children.filter { selectedChildren.contains(it.id) }
                                        .joinToString(", ") { it.name }
                                viewModel.deleteSelectedChildren(
                                    selectedChildren.toList(),
                                    children
                                )
                                Toast.makeText(context, "Deleted $nameToDelete", Toast.LENGTH_SHORT)
                                    .show()
                                isDeleteMode = false
                                selectedChildren.clear()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    if (selectedChildren.isNotEmpty()) Color(0xFFED1C2A) else Color(
                                        0xFFD9D9D9
                                    ),
                                    RoundedCornerShape(15.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Delete (${selectedChildren.size} Selected)",
                                color = Color.White,
                                fontFamily = FontFamily(Font(R.font.balooregular)),
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(15.dp))

        SettingsRow(
            painter = painterResource(id = R.drawable.timer),
            title = "Screen Time",
            value = timeDisplayValue,
            isTimeSet = isTimeSet,
            onReset = {
                showRemoveLimitDialog = true
            },
            onClick = {
                showTimePicker = true
            }
        )
        Spacer(modifier = Modifier.height(15.dp))


        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(30.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFFE6)) // Light Yellow
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(26.dp)
            ) {
                // Settings Header
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.settingiconkidgame),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "Settings",
                        modifier = Modifier.padding(start = 12.dp),
                        fontSize = 18.sp, fontFamily = FontFamily(Font(R.font.balootwomediam))
                    )
                }
                SettingToggleRow(
                    icon = painterResource(id = R.drawable.gamemusicicon),
                    title = "Music",
                    checked = isMusicEnabled,
                    onToggle = { newValue ->
                        viewModel.toggleMusicSetting(newValue)
                        val message = if(newValue) "Music ON" else "Music OFF"
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    }
                )
                // Rate Us
                SettingActionRow(painterResource(id = R.drawable.rateusiconparent), "Rate us"){
                    feedback(context)
                }
                // Share & Privacy Policy in one Row
                SettingActionRow(painterResource(id = R.drawable.gameshareicon), "Share App"){
                    shareApp(context)
                }
                SettingActionRow(
                    painterResource(id = R.drawable.privacyiconparent),
                    "Privacy Policy"
                ){
                    //here link is not present
//                    privacyPolicy(context)
                    Toast.makeText(context, "Privacy Link is missing", Toast.LENGTH_SHORT).show()
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Version: $appVersion",
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, bottom = 8.dp),
            textAlign = TextAlign.Center,
            color = Color(0xFF7E7C7C),
            fontSize = 16.sp,
            fontStyle = FontStyle.Italic,
            fontFamily = FontFamily(Font(R.font.balooregular))
        )
    }

    if (showEditSheet && childToEdit != null) {
        EditProfileBottomSheet(
            child = childToEdit!!,
            onDismiss = { showEditSheet = false },
            onSave = { updatedChild ->
                viewModel.updateChildProfile(updatedChild)
                showEditSheet = false
            }
        )
    }
    // ... baki code
    if (showRemoveLimitDialog) {
        RemoveLimitConfirmDialog(
            onDismiss = { showRemoveLimitDialog = false },
            onConfirm = {
                viewModel.updateChildTimeLimit(0, 0)
                showRemoveLimitDialog = false
            }
        )
    }

    if (showTimePicker) {
        ScreenTimeWheelDialog(
            initialHours = activeChild?.limitHours ?: 0,
            initialMins = activeChild?.limitMinutes ?: 0,
            onDismiss = { showTimePicker = false },
            onConfirm = { h, m ->
                viewModel.updateChildTimeLimit(h, m)
                showTimePicker = false
                val timeText = if (h == 0 && m == 0) "Time limit removed" else "Time set to ${h}h ${m}m"
                Toast.makeText(context, timeText, Toast.LENGTH_SHORT).show()
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileBottomSheet(
    child: ChildProfile, // Data coming from DB to edit
    onDismiss: () -> Unit,
    onSave: (ChildProfile) -> Unit
) {
    val context = LocalContext.current
    // Local states initialized with existing child data
    var name by remember(child.id) { mutableStateOf(child.name) }
    var selectedAge by remember(child.id) { mutableStateOf(child.ageRange) }
    var selectedAvatar by remember(child.id, child.avatarRes) {
        mutableIntStateOf(context.validAvatarDrawableRes(child.avatarRes))
    }

    // Using your existing Avatar list logic for consistency
    val avatars = remember {
        listOf(
            AvatarData(R.drawable.profilewhale, Color(0xFFD1C4E9), Color(0xFF67419F)),
            AvatarData(R.drawable.profilepenguin, Color(0xFFB3E5FC), Color(0xFF06A7D2)),
            AvatarData(R.drawable.profilebutterfly, Color(0xFFFFCDD2), Color(0xFFFF5596)),
            AvatarData(R.drawable.profiletiger, Color(0xFFFFE0B2), Color(0xFFFB923C))
        )
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFFFBFAFA),
        shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
        dragHandle = { BottomSheetDefaults.DragHandle(color = Color(0xFFE0E0E0)) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 30.dp) // Bottom spacing
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Edit Info",
                fontSize = 24.sp,
                fontFamily = FontFamily(Font(R.font.balootwosemibold)),
                color = Color(0xFFF56C12)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // --- Name Input (3D Style like your CreateProfile) ---
            LabelText("Kid's First Name")
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .threeDShadow(cornerRadius = 20.dp)
                    .background(Color.White, RoundedCornerShape(20.dp))
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            ) {
                TextField(
                    value = name,
                    onValueChange = { name = it },
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

            Spacer(modifier = Modifier.height(20.dp))

            // --- Age Selection (Using your AgeOptionCard) ---
            LabelText("Kid's Age")
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val ages = listOf("2-4 yrs", "4-6 yrs", "6-8 yrs")
                ages.forEach { age ->
                    AgeOptionCards(
                        text = age,
                        isSelected = selectedAge == age,
                        modifier = Modifier.weight(1f),
                        onClick = { selectedAge = age }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // --- Avatar Selection (Using your AvatarItem) ---
            LabelText("Choose Avatar")
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
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

            Spacer(modifier = Modifier.height(30.dp))

            // --- Save Button (Tick Icon with 3D shadow) ---
            val isEnabled = name.trim().isNotEmpty()
            val currentAvatarData = avatars.find { it.resId == selectedAvatar } ?: avatars[0]

            Box(
                modifier = Modifier
                    .align(Alignment.End)
                    .size(60.dp)
                    .threeDShadow(
                        color = if (isEnabled) Color(0xFFDC8035).copy(alpha = 0.4f) else Color.Gray.copy(
                            alpha = 0.2f
                        ),
                        cornerRadius = 30.dp
                    )
                    .background(
                        if (isEnabled) Color(0xFFFB923C) else Color(0xFFD9D9D9),
                        CircleShape
                    )
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                        enabled = isEnabled
                    ) {
                        onSave(
                            child.copy(
                                name = name,
                                ageRange = selectedAge,
                                avatarRes = selectedAvatar,
                                bgColor = currentAvatarData.bgColor.toArgb()
                            )
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Save",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}
@Composable
private fun AgeOptionCards(text: String, isSelected: Boolean, modifier: Modifier, onClick: () -> Unit) {
    val backgroundColor = Color.White
    val borderColor = if (isSelected) Color(0xFFFB923C) else Color.Transparent

    Box(
        modifier = modifier
            .height(55.dp)
            .threeDShadow(cornerRadius = 15.dp) // 3D Shade
            .background(backgroundColor, RoundedCornerShape(15.dp))
            .border(if (isSelected) 1.3.dp else 0.dp, borderColor, RoundedCornerShape(15.dp))
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }) { onClick() },
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
fun RemoveLimitConfirmDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 30.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                shape = RoundedCornerShape(35.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
                Column(
                    modifier = Modifier.padding(25.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Title
                    Text(
                        "Remove Limit?",
                        color = Color(0xFFF56C12), // Orange
                        fontSize = 20.sp,
                        fontFamily = FontFamily(Font(R.font.balooregular))
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Message Box (Light Peach)
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF2E4)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = buildAnnotatedString {
                                append("This action will ")
                                withStyle(style = SpanStyle(fontFamily = FontFamily(Font(R.font.balootwobold)))) {
                                    append("remove")
                                }
                                append(" the screen time limit.")
                            },
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(20.dp),
                            fontSize = 16.sp,
                            fontFamily = FontFamily(Font(R.font.balootworegular)),
                            color = Color.Black
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Buttons Row - Right Aligned with 3D Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End, // Buttons at the end (Right)
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // --- Cancel Button (3D) ---
                        Box(
                            modifier = Modifier
                                .width(100.dp)
                                .height(45.dp)
                                .clickable(
                                    indication = null,
                                    interactionSource = remember { MutableInteractionSource() }
                                ) { onDismiss() },
                            contentAlignment = Alignment.Center
                        ) {
                            // Bottom Shadow Layer
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .offset(y = 3.dp)
                                    .background(Color(0xFFE0E0E0), RoundedCornerShape(25.dp))
                            )
                            // Top White Layer
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.White, RoundedCornerShape(25.dp))
                                    .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(25.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "Cancel",
                                    color = Color(0xFFED1C2A),
                                    fontSize = 17.sp,
                                    fontFamily = FontFamily(Font(R.font.balooregular))
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        // --- Yes, Remove Button (3D) ---
                        Box(
                            modifier = Modifier
                                .width(140.dp)
                                .height(45.dp)
                                .clickable(
                                    indication = null,
                                    interactionSource = remember { MutableInteractionSource() }
                                ) { onConfirm() },
                            contentAlignment = Alignment.Center
                        ) {
                            // Bottom Shadow Layer (Darker Orange)
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .offset(y = 4.dp)
                                    .background(Color(0xFFD37424), RoundedCornerShape(25.dp))
                            )
                            // Top Orange Layer
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color(0xFFF58F3C), RoundedCornerShape(25.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "Yes, Remove",
                                    color = Color.White,
                                    fontSize = 17.sp,
                                    fontFamily = FontFamily(Font(R.font.balooregular))
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
@Composable
fun ScreenTimeWheelDialog(
    initialHours: Int = 0,
    initialMins: Int = 0,
    onDismiss: () -> Unit,
    onConfirm: (Int, Int) -> Unit
) {
    var selectedHours by remember { mutableIntStateOf(initialHours) }
    var selectedMins by remember { mutableIntStateOf(initialMins) }

    val hoursList = (0..12).map { it.toString().padStart(2, '0') }
    val minsList = (0..59).map { it.toString().padStart(2, '0') }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 25.dp),
            contentAlignment = Alignment.Center
        ) {
            // Main Card
            Card(
                shape = RoundedCornerShape(35.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
                Column(
                    modifier = Modifier.padding(25.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Title and Decorative Stars
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        // Pink Star Top Left
                        Image(
                            painter = painterResource(R.drawable.starred),
                            contentDescription = null,
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .offset(x = (-5).dp, y = (-5).dp)
                        )

                        Text(
                            "Set Screen Time",
                            color = Color(0xFFF56C12), // Orange
                            fontSize = 20.sp,
                            fontFamily = FontFamily(Font(R.font.balooregular))
                        )

                        // Blue Star Top Right
                        Image(
                            painter = painterResource(R.drawable.starblue),
                            contentDescription = null,
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .offset(x = 5.dp, y = 5.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Wheel Picker Area (Peach Background)
                    Card(
                        shape = RoundedCornerShape(25.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF2E4)), // Very Light Peach
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Headers
                            Row(
                                modifier = Modifier.fillMaxWidth(0.8f),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                Text("Hours", fontSize = 16.sp,fontFamily = FontFamily(Font(R.font.balootwomediam)), color = Color.Black)
                                Text("Mins", fontSize = 16.sp, fontFamily = FontFamily(Font(R.font.balootwomediam)), color = Color.Black)
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Pickers
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                WheelTextPicker(items = hoursList, startIndex = initialHours) { selectedHours = it }
                                Spacer(modifier = Modifier.width(40.dp))
                                WheelTextPicker(items = minsList, startIndex = initialMins) { selectedMins = it }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(30.dp))

                    // Buttons Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(15.dp)
                    ) {
                        // Cancel Button (White with Shadow)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(55.dp)
                                .threeDShadow(
                                    cornerRadius = 28.dp,
                                    offsetY = 4.dp,
                                    color = Color(0xFFE0E0E0)
                                )
                                .background(Color.White, RoundedCornerShape(28.dp))
                                .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(28.dp))
                                .clickable(
                                    indication = null,
                                    interactionSource = remember { MutableInteractionSource() }
                                ) { onDismiss() },
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Cancel", color = Color(0xFFED1C24), fontSize = 18.sp,fontFamily = FontFamily(Font(R.font.balooregular)) )
                        }

                        // Set Button (Orange with Shadow)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(55.dp)
                                .threeDShadow(
                                    cornerRadius = 28.dp,
                                    offsetY = 4.dp,
                                    color = Color(0xFFCD7731)
                                )
                                .background(Color(0xFFFB923C), RoundedCornerShape(28.dp))
                                .clickable(
                                    indication = null,
                                    interactionSource = remember { MutableInteractionSource() }) {
                                    onConfirm(
                                        selectedHours,
                                        selectedMins
                                    )
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Set", color = Color.White, fontSize = 18.sp, fontFamily = FontFamily(Font(R.font.balooregular)))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WheelTextPicker(
    items: List<String>,
    startIndex: Int =0,
    onItemSelected: (Int) -> Unit
) {
    val itemHeight = 45.dp
    val initialIndex = (items.size * 50) + startIndex
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = initialIndex - 1)
    val flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)

    LaunchedEffect(listState.isScrollInProgress) {
        if (!listState.isScrollInProgress) {
            val centerIndex = listState.firstVisibleItemIndex + 1
            onItemSelected(centerIndex % items.size)
        }
    }

    Box(
        modifier = Modifier.height(itemHeight * 3),
        contentAlignment = Alignment.Center
    ) {
        LazyColumn(
            state = listState,
            flingBehavior = flingBehavior,
            modifier = Modifier.width(70.dp),
            contentPadding = PaddingValues(vertical = 0.dp)
        ) {
            items(items.size * 100) { index ->
                val actualIndex = index % items.size
                val isSelected = listState.firstVisibleItemIndex + 1 == index

                Box(
                    modifier = Modifier
                        .height(itemHeight)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = items[actualIndex],
                        fontSize = if (isSelected) 26.sp else 18.sp,
                        fontFamily = if (isSelected) FontFamily(Font(R.font.balootwosemibold)) else FontFamily(Font(R.font.balootworegular)),
                        color = if (isSelected) Color(0xFF5701C2) else Color(0xFF7E7C7C)
                    )
                }
            }
        }
    }
}
@Composable
fun SettingActionRow(painter: Painter, title: String, modifier: Modifier = Modifier,onClick: () -> Unit) {
    Row(
        modifier = modifier.clickable (indication = null, interactionSource = remember { MutableInteractionSource() }){
            onClick()
        },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(painter = painter, contentDescription = null, modifier = Modifier.size(22.dp))
        Text(text = title, modifier = Modifier.padding(start = 10.dp), fontSize = 15.sp, color = Color.Black)
    }
}

@Composable
fun SettingToggleRow(
    icon: Painter,
    title: String,
    checked:Boolean,
    onToggle:(Boolean)->Unit
) {
//    var checked by remember { mutableStateOf(true) }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(icon, contentDescription = null, modifier = Modifier.size(24.dp))
            Text(title, modifier = Modifier.padding(start = 6.dp), fontSize = 16.sp, fontFamily = FontFamily(Font(R.font.balootworegular)))
        }
        CustomCompactSwitch(checked = checked, onCheckedChange={onToggle(it)})
    }
}

@Composable
fun SettingsRow(
    painter: Painter,
    title: String,
    value: String,
    isTimeSet: Boolean = false,
    onReset:()->Unit = {},
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF2E4)) // Same as children box
    ) {
        Row(
            modifier = Modifier
                .padding(18.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(painter = painter, contentDescription = null, modifier = Modifier.size(22.dp))
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontFamily = FontFamily(Font(R.font.balootwomediam)),
                    modifier = Modifier.padding(start = 12.dp)
                )
            }
            if(!isTimeSet){
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = value,
                        fontSize = 17.sp, fontFamily = FontFamily(Font(R.font.balootwomediam)),
                        color =Color(0xFF7E7C7C)
                    )
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = Color(0xFF7E7C7C))
                }
            }else{
                Box(contentAlignment = Alignment.TopEnd) {
                    Box(
                        modifier = Modifier
                            .padding(end = 4.dp) // Space for the small X icon
                            .threeDShadow(
                                color = Color.Black.copy(alpha = 0.1f),
                                offsetY = 3.dp,
                                cornerRadius = 25.dp
                            )
                            .background(Color.White, RoundedCornerShape(25.dp))
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = value,
                            color = Color(0xFF5701C2), // Purple text
                            fontSize = 18.sp,
                            fontFamily = FontFamily(Font(R.font.balootwobold))
                        )
                    }

                    // Small Red Close/Delete Button
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ) { onReset() }
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.crossimagestory), // Use your small red X asset
                            contentDescription = "Reset",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CustomCompactSwitch(checked: Boolean, onCheckedChange: (Boolean) -> Unit) {    val thumbOffset by animateDpAsState(targetValue = if (checked) 16.dp else 0.dp)
    val bgColor = if (checked) Color(0xFFFB923C) else Color(0xFFD3D3D3)

    Box(
        modifier = Modifier
            .width(40.dp)
            .height(22.dp)
            .clip(RoundedCornerShape(15.dp))
            .background(bgColor)
            .clickable { onCheckedChange(!checked) }
            .padding(3.dp)
    ) {
        Box(
            modifier = Modifier
                .offset(x = thumbOffset)
                .size(16.dp)
                .clip(CircleShape)
                .background(Color.White)
        )
    }
}