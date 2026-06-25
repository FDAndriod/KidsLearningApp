package com.dyiz.kidslearningapp.Database.ViewModel

import android.util.Log
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dyiz.kidslearningapp.Database.Model.ChildProfile
import com.dyiz.kidslearningapp.Database.Repos.KidsRepository
import com.dyiz.kidslearningapp.Database.SensorManager.SessionManager
import com.dyiz.kidslearningapp.badges.BadgeGameProgressStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    val repository: KidsRepository,
    val sessionManager: SessionManager,
    val badgeGameProgress: BadgeGameProgressStore,
) : ViewModel() {

    companion object {
        private const val TAG = "ScreenTimeTrack"
    }

    //-- background music
    private val _isGlobalMusicMuted = MutableStateFlow(false)
    val isGlobalMusicMuted = _isGlobalMusicMuted.asStateFlow()

    //-- music toggle setting

    private val _isMusicSettingEnabled = MutableStateFlow(sessionManager.isMusicSettingEnabled)
    val isMusicSettingEnabled = _isMusicSettingEnabled.asStateFlow()


    private val _activeChild = MutableStateFlow<ChildProfile?>(null)
    val activeChild = _activeChild.asStateFlow()

    private val _isLocked = MutableStateFlow(false)
    val isLocked = _isLocked.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<String>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    private var timerJob: Job? = null
    private var isTracking = false

    init {
        val savedId = sessionManager.currentChildId
        if (savedId != -1) {
            refreshChildData(savedId)
        }
    }

    fun markParentVisited() {
        sessionManager.isFirstTimeParentVisit = false
    }

    fun toggleMusicSetting(enabled: Boolean) {
        viewModelScope.launch {
            sessionManager.isMusicSettingEnabled = enabled
            _isMusicSettingEnabled.value = enabled
        }
    }
    fun setGlobalMusicMuted(muted: Boolean) {
        _isGlobalMusicMuted.value = muted
    }
    fun refreshChildData(id: Int) {
        viewModelScope.launch {
            val child = repository.getChildById(id)
            child?.let {
                val today = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())
                if (it.lastActiveDate != today) {
                    repository.resetTime(it.id, today)
                    _activeChild.value = it.copy(usedTimeSecondsToday = 0, lastActiveDate = today)
                } else {
                    _activeChild.value = it
                }
                checkLockStatus()
            }
        }
    }

    fun startScreenTimeTracking() {
        val current = _activeChild.value ?: run {
            Log.d(TAG, "startScreenTimeTracking: skip (no active child)")
            return
        }
        val limitSec = (current.limitHours * 3600) + (current.limitMinutes * 60)
        if (limitSec <= 0) {
            Log.d(TAG, "startScreenTimeTracking: skip (limit is 0)")
            return
        }
        if (_isLocked.value) {
            Log.d(TAG, "startScreenTimeTracking: skip (already locked)")
            return
        }
        if (isTracking) {
            Log.d(TAG, "startScreenTimeTracking: skip (already tracking)")
            return
        }
        isTracking = true
        timerJob?.cancel()
        Log.d(
            TAG,
            "startScreenTimeTracking: START childId=${current.id} limit=${limitSec}s used=${current.usedTimeSecondsToday}s"
        )
        timerJob = viewModelScope.launch {
            while (isActive && isTracking) {
                delay(1000)
                val child = _activeChild.value
                if (child == null) {
                    Log.d(TAG, "tick: stop (child null)")
                    stopTracking()
                    break
                }
                val allowed = (child.limitHours * 3600) + (child.limitMinutes * 60)
                if (allowed <= 0) {
                    Log.d(TAG, "tick: stop (limit removed)")
                    stopTracking()
                    break
                }
                if (!_isLocked.value) {
                    repository.tickSecond(child.id)
                    val updatedChild = repository.getChildById(child.id)
                    _activeChild.value = updatedChild
                    val used = updatedChild?.usedTimeSecondsToday ?: 0L
                    Log.d(
                        TAG,
                        "tick: +1s childId=${child.id} used=${used}s / limit=${allowed}s remaining=${allowed - used}s locked=${_isLocked.value}"
                    )
                    checkLockStatus()
                } else {
                    Log.d(TAG, "tick: stop (locked mid-tick)")
                    stopTracking()
                }
            }
            Log.d(TAG, "tracking loop ended isTracking=$isTracking")
        }
    }

    fun stopTracking() {
        if (isTracking) {
            Log.d(TAG, "stopTracking()")
        }
        isTracking = false
        timerJob?.cancel()
    }
    private fun checkLockStatus() {
        val child = _activeChild.value ?: return
        val totalAllowedSeconds = (child.limitHours * 3600) + (child.limitMinutes * 60)

        if (totalAllowedSeconds <= 0) {
            _isLocked.value = false
            stopTracking()
            return
        }
        if (child.usedTimeSecondsToday >= totalAllowedSeconds) {
            _isLocked.value = true
            stopTracking()

            viewModelScope.launch {
                _navigationEvent.emit("FORCE_HOME")
            }

        } else {
            _isLocked.value = false
        }
    }
    fun updateChildTimeLimit(hours: Int, mins: Int) {
        viewModelScope.launch {
            _activeChild.value?.let {
                repository.updateTimeLimit(it.id, hours, mins)
                if (hours > 0 || mins > 0) {
                    repository.resetUsedTime(it.id)
                }
                refreshChildData(it.id)
                checkLockStatus()
            }
        }
    }

    fun selectChild(childId: Int) {
        viewModelScope.launch {
            val child = repository.getChildById(childId)
            child?.let {
                sessionManager.currentChildId = childId
                _activeChild.value = it
                checkLockStatus()
            }
        }
    }

    fun createProfile(name: String, age: String, avatar: Int, bgColor: Int, onComplete: () -> Unit) {
        viewModelScope.launch {
            val today = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())

            val newProfile = ChildProfile(
                name = name,
                ageRange = age,
                avatarRes = avatar,
                bgColor = bgColor,
                lastActiveDate = today,
                limitHours = 0,
                limitMinutes = 0,
                usedTimeSecondsToday = 0
            )
            val newId = repository.saveNewProfile(newProfile).toInt()

            sessionManager.currentChildId = newId

            refreshChildData(newId)
            onComplete()
        }
    }
    fun deleteSelectedChildren(ids: List<Int>, allChildren: List<ChildProfile>) {
        viewModelScope.launch {
            repository.deleteChildren(ids)

            val currentId = sessionManager.currentChildId
            if (ids.contains(currentId)) {
                val remainingChildren = allChildren.filter { !ids.contains(it.id) }

                if (remainingChildren.isNotEmpty()) {
                    selectChild(remainingChildren[0].id)
                } else {
                    sessionManager.currentChildId = -1
                    _activeChild.value = null
                }
            }
        }
    }
    fun updateChildProfile(child: ChildProfile) {
        viewModelScope.launch {
            repository.updateChild(child)
            if (sessionManager.currentChildId == child.id) {
                _activeChild.value = child
            }

        }
    }

}