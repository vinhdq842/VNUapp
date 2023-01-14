package com.quangvinh.vnuapp.ui.noti

import android.app.Application
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.quangvinh.vnuapp.helper.getSessionExpiredDoc
import com.quangvinh.vnuapp.helper.sessionExpired
import com.quangvinh.vnuapp.util.getNetworkService
import kotlinx.coroutines.launch
import org.jsoup.nodes.Document

class NotificationViewModel(application: Application) : AndroidViewModel(application) {
    private val networkService = getNetworkService(applicationContext)

    private val applicationContext: Context
        get() = getApplication<Application>().applicationContext

    private val _sessionExpire: MutableLiveData<Boolean> = MutableLiveData()
    val sessionExpire: LiveData<Boolean>
        get() = _sessionExpire

    private val _doc: MutableLiveData<Document> = MutableLiveData()
    val doc: LiveData<Document>
        get() = _doc


    fun getSomethingAwesome() = viewModelScope.launch {
        try {
            val tmp = networkService.getNotificationPage()
            if (sessionExpired(tmp)) {
                _sessionExpire.value = true
                _doc.value = getSessionExpiredDoc(applicationContext)
            } else {
                tmp.select("span[class=Title]").remove()
                _doc.value = tmp
            }
        } catch (ex: Exception) {
            Log.d("NotificationViewModel", ex.toString())
            Toast.makeText(applicationContext, "Có lỗi xảy ra!", Toast.LENGTH_SHORT).show()
        }
    }

}