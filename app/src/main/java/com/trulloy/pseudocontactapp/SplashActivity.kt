package com.trulloy.pseudocontactapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.os.Handler
import android.os.Looper
import java.util.Calendar
import android.widget.TextView

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val year = Calendar.getInstance().get(Calendar.YEAR)
        val copyright = findViewById<TextView>(R.id.copyrightText)
        copyright.text = "© $year Trulloy. All rights reserved."


        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 2000)
    }
}