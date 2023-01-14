package com.quangvinh.vnuapp.activity

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.quangvinh.vnuapp.util.getNetworkService
import kotlinx.coroutines.launch


class LoginViewModel(application: Application) : AndroidViewModel(application) {
    var studentId = ""
    var password = ""
    var rememberInfo = false

    private val networkService = getNetworkService(applicationContext)

    private val applicationContext: Context
        get() = getApplication<Application>().applicationContext

    private val _loginResult = MutableLiveData<Boolean>()

    val loginResult: LiveData<Boolean>
        get() = _loginResult

    fun login() {
        viewModelScope.launch {
            networkService.archiveNewSession()

            val document = networkService.login(studentId, password)
            val title = document.head().getElementsByTag("title")

            _loginResult.value =
                !title.text().isNullOrEmpty() && title.text()
                    .toString() == "Cổng thông tin đào tạo"

        }
    }

}