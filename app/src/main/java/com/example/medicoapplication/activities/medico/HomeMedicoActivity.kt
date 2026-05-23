package com.example.medicoapplication.activities.medico

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.medicoapplication.R
import com.example.medicoapplication.activities.auth_e_cadastro.LoginActivity

class HomeMedicoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_medico)

        val btnSair = findViewById<Button>(R.id.btnLogoutMedico)
        btnSair.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}