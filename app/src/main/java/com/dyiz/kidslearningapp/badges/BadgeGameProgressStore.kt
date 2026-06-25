package com.dyiz.kidslearningapp.badges

import android.content.Context
import androidx.activity.result.launch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class BadgeGameProgressStore(context: Context) {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _refreshTick = MutableStateFlow(0)
    val refreshTick: StateFlow<Int> = _refreshTick.asStateFlow()

    private fun bumpRefresh() {
        _refreshTick.value = _refreshTick.value + 1
    }

    private fun timeKey(childId: Int, game: BadgeGameType): String =
        "badge_play_ms_${childId}_${game.name}"

    private fun dotPendingKey(childId: Int): String = "badge_dot_pending_$childId"


    private val _newlyUnlockedBadge = MutableSharedFlow<Int>(replay = 0)
    val newlyUnlockedBadge: SharedFlow<Int> = _newlyUnlockedBadge.asSharedFlow()

    fun getPlayTimeMs(childId: Int, game: BadgeGameType): Long {
        if (childId <= 0) return 0L
        return prefs.getLong(timeKey(childId, game), 0L)
    }

    fun readUnlockMask(childId: Int): Int {
        if (childId <= 0) return 0
        val a = getPlayTimeMs(childId, BadgeGameType.Alpha)
        val n = getPlayTimeMs(childId, BadgeGameType.Number)
        val c = getPlayTimeMs(childId, BadgeGameType.Color)
        val s = getPlayTimeMs(childId, BadgeGameType.Shape)
        return BadgeGameThresholds.computeUnlockMask(a, n, c, s)
    }

    fun hasBadgeDotPending(childId: Int): Boolean {
        if (childId <= 0) return false
        return prefs.getBoolean(dotPendingKey(childId), false)
    }

    fun clearBadgeDotPending(childId: Int) {
        if (childId <= 0) return
        prefs.edit().putBoolean(dotPendingKey(childId), false).apply()
        bumpRefresh()
    }
    @Synchronized
    fun addPlayTimeMs(childId: Int, game: BadgeGameType, deltaMs: Long) {
        if (childId <= 0 || deltaMs <= 0L) return
        val maskBefore = readUnlockMask(childId)
        val key = timeKey(childId, game)
        val next = prefs.getLong(key, 0L) + deltaMs
        prefs.edit().putLong(key, next).apply()
        val maskAfter = readUnlockMask(childId)
        val diff = maskAfter and maskBefore.inv()
        if (diff != 0) {
            prefs.edit().putBoolean(dotPendingKey(childId), true).apply()
            // Find which bit was turned on (1-8)
            for (i in 0..7) {
                if ((diff and (1 shl i)) != 0) {
                    // Emit badge index (1-based)
                    CoroutineScope(Dispatchers.IO).launch {
                        _newlyUnlockedBadge.emit(i + 1)
                    }
                }
            }
        }
        bumpRefresh()
    }
}

private const val PREFS_NAME = "kids_badge_game_prefs"
