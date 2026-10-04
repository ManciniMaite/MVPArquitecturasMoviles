package com.example.mvp_appgatos_tp2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()

        val editTextEmail = findViewById<EditText>(R.id.editTextEmail)
        val editTextPassword = findViewById<EditText>(R.id.editTextPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnRegistro = findViewById<Button>(R.id.btnRegistro)
        val textViewError = findViewById<TextView>(R.id.textViewError)

        btnLogin.setOnClickListener {
            val email = editTextEmail.text.toString().trim()
            val password = editTextPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                textViewError.text = "Completá email y contraseña"
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    irAPantallaDeGastos()
                }
                .addOnFailureListener { error ->
                    textViewError.text = "Error: ${error.message}"
                }
        }

        btnRegistro.setOnClickListener {
            val email = editTextEmail.text.toString().trim()
            val password = editTextPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                textViewError.text = "Completá email y contraseña"
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    irAPantallaDeGastos()
                }
                .addOnFailureListener { error ->
                    textViewError.text = "Error: ${error.message}"
                }
        }
    }

    private fun irAPantallaDeGastos() {
        val intent = Intent(this, GastosActivity::class.java)
        startActivity(intent)
        finish()
    }
}