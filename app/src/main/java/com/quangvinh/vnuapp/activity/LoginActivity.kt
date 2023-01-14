package com.quangvinh.vnuapp.activity

import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import com.quangvinh.vnuapp.MainActivity
import com.quangvinh.vnuapp.R
import com.quangvinh.vnuapp.helper.*


/**
 *
 * @author SOE
 */

class LoginActivity : AppCompatActivity() {
    private lateinit var btnLogin: AppCompatButton
    private lateinit var txtStudentId: AppCompatEditText
    private lateinit var txtPassword: AppCompatEditText
    private lateinit var checkBoxRemember: AppCompatCheckBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        btnLogin = findViewById(R.id.buttonLogin)
        txtStudentId = findViewById(R.id.editTextId)
        txtPassword = findViewById(R.id.editTextPassword)
        checkBoxRemember = findViewById(R.id.checkBoxRemember)
        val spinner = findViewById<ProgressBar>(R.id.spinner_login)

        val viewModel = ViewModelProvider(this)[LoginViewModel::class.java]

        checkBoxRemember.setOnCheckedChangeListener { _, checked ->
            viewModel.rememberInfo = checked
            if (checked) {
                if (!(viewModel.studentId.isBlank() || viewModel.password.isBlank())) saveData(
                    viewModel.studentId,
                    viewModel.password,
                    viewModel.rememberInfo
                )
            } else clearData()
        }

        txtStudentId.doOnTextChanged { text, _, _, _ ->
            viewModel.studentId = text.toString()
        }

        txtPassword.doOnTextChanged { text, _, _, _ ->
            viewModel.password = text.toString()
        }

        viewModel.loginResult.observe(this) {
            if (it) {
                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(
                    this@LoginActivity,
                    "Sai truy cập hoặc mật khẩu. Vui lòng nhập lại!",
                    Toast.LENGTH_SHORT
                ).show()
            }
            btnLogin.isEnabled = true
            spinner.visibility = View.INVISIBLE
        }

        loadOldData()

        btnLogin.setOnClickListener {
            if (viewModel.studentId.isBlank() || viewModel.password.isBlank()) {
                Toast.makeText(this, "Thông tin đăng nhập không hợp lệ!", Toast.LENGTH_SHORT)
                    .show()
            } else {
                val connectivityManager =
                    getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager

                if (connectivityManager.activeNetworkInfo == null || connectivityManager.activeNetworkInfo?.isConnected == false) {
                    Toast.makeText(this, "Hãy bật kết nối internet trước!", Toast.LENGTH_SHORT)
                        .show()
                } else {

                    try {
                        it.isEnabled = false
                        spinner.visibility = View.VISIBLE
                        viewModel.login()
                    } catch (ex: Exception) {
                        Toast.makeText(this, "Có lỗi xảy ra!", Toast.LENGTH_SHORT).show()
                    }

                }
            }
        }

    }


    private fun loadOldData() {
        val sharedPreferences = getSharedPreferences(PREF_LOGIN_INFO, MODE_PRIVATE)
        val id = sharedPreferences.getString(PREF_ID_KEY, "")
        txtStudentId.append(id)
        txtPassword.append(
            decodePassword(
                sharedPreferences.getString(PREF_PASS_KEY, "")!!.toByteArray(), id!!.toByteArray()
            )
        )
        checkBoxRemember.isChecked = sharedPreferences.getBoolean(PREF_REMEMBER_KEY, false)
    }


    private fun saveData(studentId: String, password: String, rememberInfo: Boolean) {
        val editor: SharedPreferences.Editor = getSharedPreferences(
            PREF_LOGIN_INFO,
            MODE_PRIVATE
        ).edit()

        if (rememberInfo) {
            editor
                .putString(PREF_ID_KEY, studentId)
                .putString(
                    PREF_PASS_KEY,
                    encodePassword(password.toByteArray(), studentId.toByteArray())
                )
                .putBoolean(PREF_REMEMBER_KEY, rememberInfo)
                .apply()
        } else {
            editor.clear().commit()
        }

    }

    private fun clearData() =
        getSharedPreferences(PREF_LOGIN_INFO, MODE_PRIVATE).edit().clear().apply()


    private fun encodePassword(src: ByteArray, key: ByteArray): String {
        for (index in src.indices) {
            src[index] = (src[index].toInt() xor MAGIC_NUM).toByte()
            src[index] = ((src[index] - key[index % key.size] % 5).toByte())
        }
        return String(src)
    }

    private fun decodePassword(src: ByteArray, key: ByteArray): String {
        for (index in src.indices) {
            src[index] = ((src[index] + key[index % key.size] % 5).toByte())
            src[index] = (src[index].toInt() xor MAGIC_NUM).toByte()
        }
        return String(src)
    }


}