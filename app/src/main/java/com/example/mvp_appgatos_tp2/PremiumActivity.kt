package com.example.mvp_appgatos_tp2

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class PremiumActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_premium)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnSuscribirme = findViewById<Button>(R.id.btnSuscribirme)

        btnSuscribirme.setOnClickListener {
            val uid = FirebaseAuth.getInstance().currentUser?.uid
                ?: return@setOnClickListener

            FirebaseFirestore.getInstance()
                .collection("usuarios").document(uid)
                .set(mapOf("premium" to true))
                .addOnSuccessListener {
                    Toast.makeText(this, "Ya sos Premium", Toast.LENGTH_SHORT).show()
                    finish()
                }
        }
    }
}