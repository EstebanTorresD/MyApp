package com.example.myapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapp.data.AuthRepository
import com.example.myapp.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val authRepository = AuthRepository()
    val user = FirebaseAuth.getInstance().currentUser
    val name = user?.displayName
    val email = user?.email


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (authRepository.getCurrentUserId() == null) {
            navigateToLogin()
            return
        }


        binding.tvWelcome.text = "Bienvenido!: $name"


        binding.btnRegisterIot.setOnClickListener {
            startActivity(Intent(this, IotRegistrationActivity::class.java))
        }

        binding.btnViewIot.setOnClickListener {
            startActivity(Intent(this, IotInteractionActivity::class.java))
        }


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