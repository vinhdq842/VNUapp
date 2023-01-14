package com.quangvinh.vnuapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.quangvinh.vnuapp.activity.LoginActivity
import com.quangvinh.vnuapp.helper.pretendToWait

/**
 *
 * @author SOE
 */
class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var drawerLayout: DrawerLayout

    private val viewModel: MainViewModel
        get() = ViewModelProvider(this)[MainViewModel::class.java]


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<Toolbar>(R.id.toolbar_main)
        setSupportActionBar(toolbar)
        drawerLayout = findViewById(R.id.drawer_layout)
        val navView = findViewById<NavigationView>(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)

        val headerView = navView.getHeaderView(0)

        val headerSpinner: ProgressBar = headerView.findViewById(R.id.spinner_nav_loading)
        val headerId: TextView = headerView.findViewById(R.id.txt_header_id)
        val headerName: TextView = headerView.findViewById(R.id.txt_header_name)
        val headerImage: ImageView = headerView.findViewById(R.id.img_header)
//        val headerDoB: TextView = headerView.findViewById(R.id.txt_header_bd)
        val headerMajor: TextView = headerView.findViewById(R.id.txt_header_major)
        val headerMail: TextView = headerView.findViewById(R.id.txt_header_mail)

        val viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        headerView.setOnClickListener {
            viewModel.updateNavHeader(headerSpinner)
        }

        viewModel.headerImage.observe(this) {
            if (it != null) headerImage.setImageBitmap(it)
        }

        viewModel.headerStudentId.observe(this) {
            if (it != null) headerId.text = it
        }

        viewModel.headerStudentName.observe(this) {
            if (it != null) headerName.text = it
        }

        /*
        viewModel.headerStudentBirthday.observe(this) {
            if (it != null) headerDoB.text = it
        }
         */

        viewModel.headerStudentMajor.observe(this) {
            if (it != null) headerMajor.text = it
        }

        viewModel.headerStudentMail.observe(this) {
            if (it != null) headerMail.text = it
        }

        viewModel.updateNavHeader(headerSpinner)

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_help,
                R.id.nav_home,
                R.id.nav_mail,
                R.id.nav_myinfo,
                R.id.nav_myres,
                R.id.nav_schedule,
                R.id.nav_updateinfo,
//                R.id.nav_cpass,
                R.id.nav_noti,
            ), drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.menu_about -> {
                AlertDialog.Builder(this)
                    .setMessage("Ứng dụng được phát triển bởi Đặng Quang Vinh(SOE) - 13/03/2021")
                    .setTitle("Tác giả")
                    .setIcon(R.drawable.icon)
                    .setPositiveButton("OK", null)
                    .show()
            }
            R.id.menu_feedback -> {
                val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:"))
                intent.putExtra(Intent.EXTRA_EMAIL, arrayOf("quangvinh0842@gmail.com"))
                    .putExtra(Intent.EXTRA_SUBJECT, "Góp ý về ứng dụng VNU App")
                    .putExtra(Intent.EXTRA_TEXT, "Xin chào Vinh,\n \n")

                if (intent.resolveActivity(packageManager) != null) {
                    startActivity(intent)
                }
            }
            R.id.menu_logout -> {
                viewModel.logout()
                val dialog = pretendToWait(this, "Đang đăng xuất...")
                Handler().postDelayed({
                    dialog.dismiss()
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }, 1000)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) drawerLayout.closeDrawer(GravityCompat.START)
        else super.onBackPressed()
    }

}