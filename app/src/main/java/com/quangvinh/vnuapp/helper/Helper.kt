package com.quangvinh.vnuapp.helper

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import com.quangvinh.vnuapp.R
import com.quangvinh.vnuapp.activity.LoginActivity
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

fun onSessionExpired(context: Activity) {
    AlertDialog.Builder(context)
        .setTitle("Thông báo")
        .setMessage("Phiên làm việc của bạn đã hết, mời đăng nhập lại!")
        .setPositiveButton("OK") { dialog, which ->
            val intent = Intent(context, LoginActivity::class.java)
            context.startActivity(intent)
            context.finish()
        }.setIcon(R.drawable.ic_dialog_info)
        .show()
}

fun pretendToWait(context: Activity, message: String): ProgressDialog {
    val progress = ProgressDialog(context)
    progress.setMessage(message)
    progress.setCancelable(false)
    progress.show()
    return progress
}


fun sessionExpired(document: Document): Boolean {
    val title = document.head().getElementsByTag("title").text().toString().trim()
    return title == "Object moved"
}


fun getSessionExpiredDoc(context: Context): Document {
    return Jsoup.parse(
        context.assets.open("html/session_expired.html"),
        "utf-8", ""
    )
}