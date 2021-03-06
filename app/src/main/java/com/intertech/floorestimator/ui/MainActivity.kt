package com.intertech.floorestimator.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.example.flatdialoglibrary.dialog.FlatDialog
import com.intertech.floorestimator.MainViewModel
import com.intertech.floorestimator.R
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber

class MainActivity : BaseActivity() {
    var mainActivityVm: MainViewModel? = null
    private val mainVMFactory: MainViewModel.MainViewModelFactory by lazy {
        MainViewModel.MainViewModelFactory(MainRepository.getInstance())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mainActivityVm = ViewModelProvider(this, mainVMFactory)
            .get(MainViewModel::class.java)
        mainActivityVm?.getUserTypeText()?.observe(this, Observer {
            text_main_userType.text = it
        })
        mainActivityVm?.getIsAdminUser()?.observe(this, Observer {
            text_main_logout.visibility = if (it) View.VISIBLE else View.GONE
        })
        mainActivityVm?.getToastMessage()?.observe(this, Observer { msg ->
            if (msg.isNotBlank()) {
                showToast(msg, Toast.LENGTH_SHORT)
                mainActivityVm?.clearToastMessage()
            }
        })

        bttn_main_newEstimate.setOnClickListener { startNewEstimate() }
        text_main_userType.setOnClickListener { adminLogin() }
        text_main_logout.setOnClickListener { mainActivityVm?.adminLogout() }
    }

    private fun startNewEstimate() {
        TODO("Not yet implemented")
    }

    private fun adminLogin() {
        val flatDialog = FlatDialog(this)
        flatDialog.setTitle("Admin Login")
            .setFirstTextFieldHint("Email")
            .setFirstTextField(DEFAULT_ADMIN_EMAIL)
            .setSecondTextFieldHint("Password")
            .setSecondTextField(DEFAULT_ADMIN_PASSWORD)
            .setFirstButtonText("Login")
            .setSecondButtonText("CANCEL")
            .isCancelable(true)
            .withFirstButtonListner {
                mainActivityVm?.attemptAdminLogin(flatDialog.firstTextField, flatDialog.secondTextField)
            }
            .withSecondButtonListner {
                flatDialog.dismiss()
            }
            .show()
    }

    override fun onStart() {
        super.onStart()

        checkAdminLogin()
    }

    private fun checkAdminLogin() {
        mainActivityVm?.updateAdminAuthState()
    }

    companion object {
        val DEFAULT_ADMIN_EMAIL = "admin@admin.com"
        val DEFAULT_ADMIN_PASSWORD = "123456"
    }
}
