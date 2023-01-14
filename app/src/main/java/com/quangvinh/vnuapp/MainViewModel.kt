package com.quangvinh.vnuapp

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.quangvinh.vnuapp.helper.URL
import com.quangvinh.vnuapp.util.getNetworkService
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val networkService = getNetworkService(applicationContext)

    private val applicationContext: Context
        get() = getApplication<Application>().applicationContext

    private val _headerImage: MutableLiveData<Bitmap> = MutableLiveData()
    val headerImage: LiveData<Bitmap>
        get() = _headerImage

    private val _headerStudentId: MutableLiveData<String> = MutableLiveData()
    val headerStudentId: LiveData<String>
        get() = _headerStudentId

    private val _headerStudentName: MutableLiveData<String> = MutableLiveData()
    val headerStudentName: LiveData<String>
        get() = _headerStudentName

    private val _headerStudentBirthday: MutableLiveData<String> = MutableLiveData()
    val headerStudentBirthday: LiveData<String>
        get() = _headerStudentBirthday

    private val _headerStudentMail: MutableLiveData<String> = MutableLiveData()
    val headerStudentMail: LiveData<String>
        get() = _headerStudentMail

    private val _headerStudentMajor: MutableLiveData<String> = MutableLiveData()
    val headerStudentMajor: LiveData<String>
        get() = _headerStudentMajor


    fun updateNavHeader(spinner: ProgressBar) = viewModelScope.launch {
        try {
            spinner.visibility = View.VISIBLE
            val info = networkService.getCustomPage("${URL}stdinfo/tabstdself.asp")
            val imgUrl = info.getElementById("piccy").attr("src").drop(6)

            _headerStudentId.value = info.getElementById("StdCode").`val`()
            _headerStudentName.value = info.getElementById("StdName").`val`()

//      _headerStudentBirthday.value = info.getElementById("StdDob").attr("value")
            _headerStudentMail.value = info.getElementById("std_off_email").attr("value")
            _headerStudentMajor.value =
                info.getElementById("ClsID").select("option[selected]").text()

            val img = networkService.getImage("$URL$imgUrl")
            _headerImage.value = img
        } catch (ex: Exception) {
            Log.d("MainViewModel", ex.toString())
            Toast.makeText(applicationContext, "Có lỗi xảy ra!", Toast.LENGTH_SHORT).show()
        } finally {
            spinner.visibility = View.INVISIBLE
        }
    }

    fun logout() = viewModelScope.launch {
        try {
            networkService.logout()
        } catch (ex: Exception) {
            Log.d("MainViewModel", ex.toString())
        }
    }

}