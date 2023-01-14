package com.quangvinh.vnuapp.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.webkit.CookieManager
import androidx.appcompat.app.AppCompatActivity
import com.quangvinh.vnuapp.helper.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.net.HttpURLConnection
import java.net.URL

private var service: MainNetwork? = null

fun getNetworkService(context: Context): MainNetwork = when (service) {
    null -> {
        service = MainNetworkImpl1(context)
        service!!
    }
    else -> service!!
}

class MainNetworkImpl1(private val context: Context) : MainNetwork {

    override suspend fun archiveNewSession() = withContext(Dispatchers.IO) {
        val connection = createNewRequest("${URL}dkmh/login.asp")
        clearOldSession()
        storeCookies(connection)
        connection.disconnect()
    }

    override suspend fun login(studentId: String, password: String): Document =
        withContext(Dispatchers.IO) {
            val connection = createNewRequest("${URL}dkmh/login.asp")

            with(connection) {
                requestMethod = "POST"
                doInput = true
                doOutput = true
                setCookies(this)
                connect()
            }

            val params = encodeRequestParams(
                listOf(
                    "chkSubmit" to "ok",
                    "txtLoginId" to studentId,
                    "txtPassword" to password,
                    "txtSel" to "1",
                )
            )

            val outputStream = connection.outputStream
            outputStream.write(params.toByteArray())
            outputStream.flush()
            val doc = Jsoup.parse(connection.inputStream, null, URL)
            connection.disconnect()
            doc
        }

    override suspend fun getHomePage(): Document = requestPage("Help/default.asp")

    override suspend fun getStudentInfoPage(): Document = requestPage("StdInfo/default.asp")

    override suspend fun getUpdateStudentInfoPage(): Document =
        requestPage("StdInfo/createstudentTab.asp")

    override suspend fun getStudentResultPage(): Document = requestPage("ListPoint/listpoint.asp")

    override suspend fun getExamSchedulePage(): Document =
        requestPage("StdExamination/StdExamination.asp")

    override suspend fun getHelpPage(): Document = requestPage("Messages/feedback.asp")

    override suspend fun getMailPage(): Document = requestPage("BMS/InitializePassword.asp")

    override suspend fun getNotificationPage(): Document = requestPage("Messages/Receive.asp")

    private suspend fun requestPage(url: String): Document = withContext(Dispatchers.IO) {
        val connection = createNewRequest("$URL$url")
        connection.requestMethod = RequestMethod.GET.name
        connection.doInput = true
        setCookies(connection)
        connection.connect()

        val doc = Jsoup.parse(connection.inputStream, null, URL)
        connection.disconnect()
        doc
    }

    override suspend fun logout() = withContext(Dispatchers.IO) {
        val connection = createNewRequest("${URL}dkmh/login.asp")
        connection.requestMethod = RequestMethod.GET.name
        connection.doOutput = true
        connection.doInput = true
        setCookies(connection)
        val params = encodeRequestParams(listOf("logout" to "logout"))
        connection.connect()
        connection.outputStream.write(params.toByteArray())
        connection.outputStream.flush()
        val doc = Jsoup.parse(connection.inputStream, null, URL)
        connection.disconnect()
        println(doc.head().getElementsByTag("title").html())
    }

    override suspend fun getImage(url: String): Bitmap = withContext(Dispatchers.IO) {
        val connection = URL(url).openConnection() as HttpURLConnection
        connection.requestMethod = RequestMethod.GET.name
        connection.doInput = true
        setCookies(connection)

        val res = BitmapFactory.decodeStream(connection.inputStream)
        connection.disconnect()
        res
    }

    override suspend fun getCustomPage(url: String): Document = withContext(Dispatchers.IO) {
        val connection = createNewRequest(url)
        connection.requestMethod = RequestMethod.GET.name
        connection.doInput = true
        setCookies(connection)
        connection.connect()

        val doc = Jsoup.parse(connection.inputStream, null, URL)
        connection.disconnect()
        doc
    }


    private fun storeCookies(connection: HttpURLConnection) {
        val cookies = connection.headerFields["Set-Cookie"]
        val cookieMg: CookieManager = CookieManager.getInstance()
        cookieMg.setAcceptCookie(true)
        if (cookies != null) {
            val sharedPreferences = context.getSharedPreferences(
                PREF_COOKIES_INFO,
                AppCompatActivity.MODE_PRIVATE
            )
            val setCookies: HashSet<String> =
                sharedPreferences.getStringSet(
                    PREF_SET_COOKIES,
                    HashSet<String>()
                ) as HashSet<String>
            for (cookie in cookies) {
                if (cookie != null) {
                    setCookies.add(cookie)
                    cookieMg.setCookie("daotao.vnu.edu.vn", cookie)
                }
//                Log.d("COOKIES", cookie)
            }

            sharedPreferences
                .edit()
                .putStringSet(PREF_SET_COOKIES, setCookies)
                .apply()
        }
    }

    private fun setCookies(connection: HttpURLConnection) {
        val sharedPreferences = context.getSharedPreferences(
            PREF_COOKIES_INFO,
            AppCompatActivity.MODE_PRIVATE
        )
        val setCookies =
            sharedPreferences.getStringSet(PREF_SET_COOKIES, HashSet<String>())

        if (setCookies != null) {
            for (cookie in setCookies) {
                connection.setRequestProperty("Cookie", cookie)
            }
        }
    }

    private fun clearOldSession() =
        context.getSharedPreferences(PREF_COOKIES_INFO, AppCompatActivity.MODE_PRIVATE)
            .edit()
            .clear()
            .apply()

}

fun createNewRequest(url: String) = (URL(url).openConnection() as HttpURLConnection).apply {
    setRequestProperty("Connection", "keep-alive")
    setRequestProperty("Accept-Language", "ru,en-GB;q=0.8,en;q=0.6")
    setRequestProperty("Accept-Charset", "utf-8")
    setRequestProperty(
        "Accept",
        "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8"
    )
}

interface MainNetwork {
    // dkmh/default.asp
    suspend fun archiveNewSession()

    // dkmh/login.asp
    suspend fun login(studentId: String, password: String): Document

    // Help/default.asp
    suspend fun getHomePage(): Document

    // StdInfo/default.asp
    suspend fun getStudentInfoPage(): Document

    // StdInfo/createstudentTab.asp
    suspend fun getUpdateStudentInfoPage(): Document

    // ListPoint/listpoint.asp
    suspend fun getStudentResultPage(): Document

    // StdExamination/StdExamination.asp
    suspend fun getExamSchedulePage(): Document

    // Messages/feedback.asp
    suspend fun getHelpPage(): Document

    // BMS/InitializePassword.asp
    suspend fun getMailPage(): Document

    // Messages/Receive.asp
    suspend fun getNotificationPage(): Document

    // dkmh/login.asp?Logout=logout
    suspend fun logout()

    suspend fun getImage(url: String): Bitmap

    suspend fun getCustomPage(url: String): Document
}