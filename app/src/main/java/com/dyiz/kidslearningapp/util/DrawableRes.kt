package com.dyiz.kidslearningapp.util

import android.content.Context
import android.content.res.Resources
import com.dyiz.kidslearningapp.R

fun Context.validAvatarDrawableRes(storedId: Int?): Int {
    val fallback = R.drawable.profilebutterfly
    if (storedId == null || storedId == 0) return fallback
    return try {
        if (resources.getResourceTypeName(storedId) == "drawable") storedId else fallback
    } catch (_: Resources.NotFoundException) {
        fallback
    }
}
