package com.dyiz.kidslearningapp.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext


fun shareApp(context: Context){

    val packageName = context.packageName
    val appLink =  "https://play.google.com/store/apps/details?id=$packageName"
    val send = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, "Kidovo App") // Optional subject
        putExtra(Intent.EXTRA_TEXT, "check out this amazing kids learning App: $appLink")
    }
    context.startActivity(Intent.createChooser(send, "Share"))
}
fun feedback(context: Context) {
    val packageName = context.packageName
    val marketUri = Uri.parse("market://details?id=$packageName")
    val webUri = Uri.parse("https://play.google.com/store/apps/details?id=$packageName")

    val intent = Intent(Intent.ACTION_VIEW, marketUri)
    runCatching {
        context.startActivity(intent)
    }.onFailure {
        val webIntent = Intent(Intent.ACTION_VIEW, webUri)
        context.startActivity(webIntent)
    }
}
//fun feedback(context:Context){
//    val intent = Intent(Intent.ACTION_SENDTO).apply {
//        data = Uri.parse("mailto:")
//    }
//    runCatching { context.startActivity(Intent.createChooser(intent, "Feedback")) }
//}

fun privacyPolicy(context: Context){
    val privacyUrl = "https://www.yourprivacypolicy.com" //DUMMY LINK
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(privacyUrl))
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "Unable to open link", Toast.LENGTH_SHORT).show()
    }
}