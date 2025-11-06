package com.example.myapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapp.data.AuthRepository
import com.example.myapp.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding // <<-- CLAVE: Objeto Binding
    private val authRepository = AuthRepository()
    val user = FirebaseAuth.getInstance().currentUser
    val name = user?.displayName
    val email = user?.email


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Inicializar ViewBinding y establecer la vista
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root) // Usa la raíz del binding

        // Comprobar autenticación (Guardrail)
        if (authRepository.getCurrentUserId() == null) {
            navigateToLogin()
            return
        }

        // 4) Menú Principal - Acceso a vistas a través de 'binding'
        // Usa el operador de acceso seguro (?.) por si el usuario no ha iniciado sesión y 'user' es nulo.
        binding.tvWelcome.text = "Bienvenido!: $name"

        // Navegación (Punto 4)
        binding.btnRegisterIot.setOnClickListener {
            startActivity(Intent(this, IotRegistrationActivity::class.java))
        }

        binding.btnViewIot.setOnClickListener {
            startActivity(Intent(this, IotInteractionActivity::class.java))
        }

        // Lógica de Cerrar Sesión
        binding.btnLogout.setOnClickListener {
            authRepository.logout()
            Toast.makeText(this, "Sesión cerrada.", Toast.LENGTH_SHORT).show()
            navigateToLogin()
        }
    }

    private fun navigateToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}