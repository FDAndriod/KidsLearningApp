package com.dyiz.kidslearningapp.utils

import com.dyiz.kidslearningapp.R

data class AlphabetItem(
    val letter: Char,
    val word: String,
    val imageRes: Int
)

object AlphabetData {
    val list = listOf(
        AlphabetItem('A', "Apple", R.drawable.appleimage),
        AlphabetItem('B', "Ball", R.drawable.ballimage),
        AlphabetItem('C', "Cat", R.drawable.catimage),
        AlphabetItem('D', "Dog", R.drawable.dogimage),
        AlphabetItem('E', "Egg", R.drawable.eggimage),
        AlphabetItem('F', "Fish", R.drawable.fishimage),
        AlphabetItem('G', "Goat", R.drawable.goatimage),
        AlphabetItem('H', "Hat", R.drawable.hatimage),
        AlphabetItem('I', "IceCream", R.drawable.icecreamimage),
        AlphabetItem('J', "Jump", R.drawable.jumpimage),
        AlphabetItem('K', "Kite", R.drawable.kiteimage),
        AlphabetItem('L', "Lamp", R.drawable.lampimage),
        AlphabetItem('M', "Moon", R.drawable.moonimage),
        AlphabetItem('N', "Net", R.drawable.netimage),
        AlphabetItem('O', "Owl", R.drawable.owlimage),
        AlphabetItem('P', "Pet", R.drawable.parrotimage),
        AlphabetItem('Q', "Queen", R.drawable.queenimage),
        AlphabetItem('R', "Rain", R.drawable.rainalpha),
        AlphabetItem('S', "Sun", R.drawable.sunimage),
        AlphabetItem('T', "Train", R.drawable.trainimage),
        AlphabetItem('U', "Umbrella", R.drawable.umberallaimage),
        AlphabetItem('V', "Van", R.drawable.vanimage),
        AlphabetItem('W', "Whale", R.drawable.whaleimage),
        AlphabetItem('X', "Xylophone", R.drawable.xylophoneimage),
        AlphabetItem('Y', "Yoyo", R.drawable.yoyoimage),
        AlphabetItem('Z', "Zebra", R.drawable.zebraimage)
    )
}