package com.example.mvp_appgatos_tp2

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class GastosActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_gastos)

        val categorias = arrayOf("Comida", "Transporte", "Ocio", "Otros")
        val spinnerCategoria = findViewById<Spinner>(R.id.spinnerCategoria)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, categorias)
        spinnerCategoria.adapter = adapter

        val monedas = arrayOf("ARS", "USD", "EUR")
        val spinnerMoneda = findViewById<Spinner>(R.id.spinnerMoneda)
        spinnerMoneda.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, monedas)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val db = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        val uid = auth.currentUser?.uid

        val editTextMonto = findViewById<EditText>(R.id.editTextMonto)
        val editTextDescripcion = findViewById<EditText>(R.id.editTextDescripcion)
        val btnAgregarGasto = findViewById<Button>(R.id.btnAgregarGasto)

        btnAgregarGasto.setOnClickListener {
            val descripcion = editTextDescripcion.text.toString().trim()
            val monto = editTextMonto.text.toString().toDoubleOrNull()

            if (monto == null) {
                Toast.makeText(this, "Ingresá un monto válido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val categoria = spinnerCategoria.selectedItem.toString()
            val moneda = spinnerMoneda.selectedItem.toString()

            lifecycleScope.launch {
                var montoEnPesos = monto

                if (moneda != "ARS") {
                    try {
                        val respuesta = ApiClient.exchangeRate.convertCurrency(
                            com.example.mvp_appgatos_tp2.BuildConfig.EXCHANGE_API_KEY, moneda, "ARS", monto
                        )
                        val convertido = respuesta.body()?.conversion_result

                        if (!respuesta.isSuccessful || convertido == null) {
                            Toast.makeText(this@GastosActivity, "No se pudo convertir la moneda", Toast.LENGTH_SHORT).show()
                            return@launch
                        }
                        montoEnPesos = convertido
                    } catch (e: Exception) {
                        Toast.makeText(this@GastosActivity, "Error de conexión: ${e.message}", Toast.LENGTH_SHORT).show()
                        return@launch
                    }
                }

                val gasto = hashMapOf(
                    "descripcion" to descripcion,
                    "monto" to montoEnPesos,
                    "montoOriginal" to monto,
                    "moneda" to moneda,
                    "categoria" to categoria,
                    "fecha" to System.currentTimeMillis(),
                    "uid" to uid
                )

                db.collection("gastos")
                    .add(gasto)
                    .addOnSuccessListener {
                        Toast.makeText(this@GastosActivity, "Gasto agregado", Toast.LENGTH_SHORT).show()
                        editTextDescripcion.text.clear()
                        editTextMonto.text.clear()
                    }
                    .addOnFailureListener { error ->
                        Toast.makeText(this@GastosActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }

        val listViewGastos = findViewById<ListView>(R.id.listViewGastos)
        val textViewTotal = findViewById<TextView>(R.id.textViewTotal)

        db.collection("gastos")
            .whereEqualTo("uid", uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Toast.makeText(this, "Error al leer gastos: ${error.message}", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                if (snapshot == null) return@addSnapshotListener

                val listaTextos = mutableListOf<String>()
                var total = 0.0

                for (documento in snapshot.documents) {
                    val descripcion = documento.getString("descripcion") ?: ""
                    val monto = documento.getDouble("monto") ?: 0.0
                    val categoria = documento.getString("categoria") ?: ""

                    total += monto
                    listaTextos.add("$categoria - $$monto - $descripcion")
                }

                textViewTotal.text = "Total: $$total"

                val adapterLista = ArrayAdapter(this, android.R.layout.simple_list_item_1, listaTextos)
                listViewGastos.adapter = adapterLista
            }
    }
}