package com.trulloy.pseudocontactapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class AboutUs : AppCompatActivity() {

    private lateinit var btncontact: Button
    private lateinit var btnwebsite: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_us)

        btncontact = findViewById(R.id.btn_contact)
        btnwebsite = findViewById(R.id.btn_website)


        // Open dial pad with phone number
        btncontact.setOnClickListener {
            val phoneNumber = getString(R.string.contact_phone)
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:$phoneNumber")
            startActivity(intent)
        }

        // Open website in browser
        btnwebsite.setOnClickListener {
            val websiteUrl = getString(R.string.website_url)
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(websiteUrl)
            startActivity(intent)
        }


    }
}
