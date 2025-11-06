package com.example.myapp

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.myapp.data.AuthRepository
import kotlinx.coroutines.launch

class IotInteractionActivity : AppCompatActivity() {

    // Simulación de Views (reemplazar con ViewBinding real)
    private lateinit var deviceIdTextView: TextView
    private lateinit var plazasTextView: TextView
    private lateinit var plazasdisponiblesTextView: TextView

    private val authRepository = AuthRepository()
    private var isLightOn = false // Estado simulado
    private var currentDeviceId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_iot_interaction) // 5) Pantalla de interacción

        // Inicializar Views (ejemplo, debes usar tu ViewBinding)
        deviceIdTextView = findViewById(R.id.tv_device_id)
        plazasTextView = findViewById(R.id.tv_plazas)
        plazasdisponiblesTextView = findViewById(R.id.tv_plazasdisponibles)

        loadDeviceData()

    }

    private fun loadDeviceData() {
        val uid = authRepository.getCurrentUserId()
        if (uid == null) {
            Toast.makeText(this, "Usuario no autenticado.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        lifecycleScope.launch {
            // 1. Obtener el ID del dispositivo del usuario desde Firestore
            currentDeviceId = authRepository.getIotDeviceId(uid)

            if (currentDeviceId == null) {
                deviceIdTextView.text = "Dispositivo: No vinculado"
                plazasTextView.text = "Datos no disponibles."
                plazasdisponiblesTextView.text = "Datos no disponibles."
                Toast.makeText(this@IotInteractionActivity, "Por favor, registre su dispositivo primero.", Toast.LENGTH_LONG).show()
            } else {
                // 2. Mostrar información relevante (simulada para la evaluación)
                deviceIdTextView.text = "Dispositivo: $currentDeviceId"

                // --- Datos Simulados ---
                val plazas = 25
                val plazasdisponibles = 15
                plazasTextView.text = "Plazas Totales: ${plazas}"
                plazasdisponiblesTextView.text = "Plazas Disponibles: ${plazasdisponibles}"

            }
        }
    }
}