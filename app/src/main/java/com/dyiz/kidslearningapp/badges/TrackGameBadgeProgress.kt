package com.dyiz.kidslearningapp.badges

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.currentStateAsState
import com.dyiz.kidslearningapp.Database.ViewModel.MainViewModel
import kotlinx.coroutines.delay


@Composable
fun TrackGameBadgeProgress(mainViewModel: MainViewModel, game: BadgeGameType) {
    val isLocked by mainViewModel.isLocked.collectAsState()
    val child by mainViewModel.activeChild.collectAsState()

    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycleState by lifecycleOwner.lifecycle.currentStateAsState()

    LaunchedEffect(isLocked, lifecycleState, game, child?.id) {
        while (true) {
            delay(1000)
            val id = mainViewModel.activeChild.value?.id?.takeIf { it > 0 }
                ?: mainViewModel.sessionManager.currentChildId
            if (id <= 0) continue
            if (isLocked) continue
            if (lifecycleState != Lifecycle.State.RESUMED) continue
            mainViewModel.badgeGameProgress.addPlayTimeMs(id, game, 1000L)
        }
    }
}
