package com.example.myapp.model


data class User(
    val uid: String = "",
    val email: String = "",
    val name: String = "",
    val iotDeviceId: String? = null
)