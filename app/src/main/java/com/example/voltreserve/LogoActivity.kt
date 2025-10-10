package com.example.voltreserve

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.voltreserve.helpers.SessionDbHelper

class LogoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logo)

        val dbHelper = SessionDbHelper(this)

        Handler(Looper.getMainLooper()).postDelayed({
            val token = dbHelper.getSession()

            if (!token.isNullOrEmpty()) {
                // Token exists → Check role and redirect
                val role = dbHelper.getRole()?.lowercase()

                when (role) {
                    "owner" -> {
                        val intent = Intent(this, DashboardActivity::class.java)
                        startActivity(intent)
                        finish()
                    }

                    "stationoperator" -> {
                        val intent = Intent(this, OperatorDashboardActivity::class.java)
                        startActivity(intent)
                        finish()
                    }

                    else -> {
                        // Unknown or invalid token → go to login
                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
            } else {
                // No token saved → go to Login
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }, 2500) // 2.5s splash delay
    }
}
