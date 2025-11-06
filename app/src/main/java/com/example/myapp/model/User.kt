package com.example.myapp.model

/**
 * Modelo de datos para almacenar la información del usuario en Firestore.
 * El campo 'iotDeviceId' se usa para el punto 3 y 5 de la evaluación.
 */
data class User(
    val uid: String = "",
    val email: String = "",
    val name: String = "",
    val iotDeviceId: String? = null // ID del dispositivo Arduino registrado
)