package com.example.myapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.myapp.data.AuthRepository
import kotlinx.coroutines.launch

class IotRegistrationActivity : AppCompatActivity() {

    private lateinit var deviceIdEditText: EditText
    private lateinit var registerButton: Button

    private val authRepository = AuthRepository()

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(R.layout.activity_iot_registration)

        deviceIdEditText = findViewById(R.id.et_device_id)
        registerButton = findViewById(R.id.btn_register_device)

        val mockDeviceId = "ARDUINO_${authRepository.getCurrentUserId()?.takeLast(4)}"
        deviceIdEditText.setText(mockDeviceId)

        registerButton.setOnClickListener {
            val deviceId = deviceIdEditText.text.toString()
            val uid = authRepository.getCurrentUserId()

            if (uid == null) {
                Toast.makeText(this, "Error: Usuario no autenticado.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val result = authRepository.registerIotDevice(uid, deviceId)
                result.onSuccess {
                    Toast.makeText(this@IotRegistrationActivity, "Dispositivo '$deviceId' vinculado con éxito.", Toast.LENGTH_LONG).show()
                    finish()
                }.onFailure {
                    Toast.makeText(this@IotRegistrationActivity, "Error al vincular: ${it.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}