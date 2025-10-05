package com.example.voltreserve

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity

class LogoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logo)

        Handler().postDelayed(Runnable {
            val intent: Intent = Intent(
                this@LogoActivity,
                LoginActivity::class.java
            )
            startActivity(intent)
            finish()
        }, 4000)
    }
}