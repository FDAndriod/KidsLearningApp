package com.dyiz.kidslearningapp.badges


object BadgeGameThresholds {

    private const val MS_PER_MIN = 60_000L

    // Alphabet (Alpha) game
    const val ALPHA_BADGE_1_MS = 40 * MS_PER_MIN
    const val ALPHA_BADGE_5_MS = 50 * MS_PER_MIN

    // Number game
    const val NUMBER_BADGE_2_MS = 30 * MS_PER_MIN
    const val NUMBER_BADGE_6_MS = 60 * MS_PER_MIN

    // Color game
    const val COLOR_BADGE_3_MS = 25 * MS_PER_MIN
    const val COLOR_BADGE_8_MS = 45 * MS_PER_MIN

    // Shape game
    const val SHAPE_BADGE_4_MS = 55 * MS_PER_MIN
    const val SHAPE_BADGE_7_MS = 35 * MS_PER_MIN


    fun computeUnlockMask(alphaMs: Long, numberMs: Long, colorMs: Long, shapeMs: Long): Int {
        var mask = 0
        if (alphaMs >= ALPHA_BADGE_1_MS) mask = mask or (1 shl 0)
        if (numberMs >= NUMBER_BADGE_2_MS) mask = mask or (1 shl 1)
        if (colorMs >= COLOR_BADGE_3_MS) mask = mask or (1 shl 2)
        if (shapeMs >= SHAPE_BADGE_4_MS) mask = mask or (1 shl 3)
        if (alphaMs >= ALPHA_BADGE_5_MS) mask = mask or (1 shl 4)
        if (numberMs >= NUMBER_BADGE_6_MS) mask = mask or (1 shl 5)
        if (shapeMs >= SHAPE_BADGE_7_MS) mask = mask or (1 shl 6)
        if (colorMs >= COLOR_BADGE_8_MS) mask = mask or (1 shl 7)
        return mask
    }

    fun isBadgeUnlocked(mask: Int, badgeNumber1To8: Int): Boolean {
        if (badgeNumber1To8 !in 1..8) return false
        return (mask and (1 shl (badgeNumber1To8 - 1))) != 0
    }
}
