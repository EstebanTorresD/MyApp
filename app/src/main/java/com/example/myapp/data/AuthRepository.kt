package com.example.myapp.data

import com.example.myapp.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import android.util.Log

/**
 * Clase central para gestionar todas las interacciones con Firebase Auth y Firestore.
 * Utiliza suspend functions para operaciones asíncronas seguras con Coroutines.
 */
class AuthRepository {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val usersCollection = firestore.collection("users")
    private val TAG = "AuthRepository"

    // --- Funciones de Autenticación ---

    /**
     * 1) Pantalla de inicio de sesión: Autentica al usuario con email y contraseña.
     */
    suspend fun login(email: String, password: String): Result<User> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val uid = result.user?.uid ?: throw Exception("UID nulo.")

            // Obtener datos de Firestore para completar el objeto User
            val userDoc = usersCollection.document(uid).get().await()
            val user = userDoc.toObject(User::class.java)

            if (user != null) {
                Result.success(user)
            } else {
                auth.signOut()
                Result.failure(Exception("Error al obtener datos del usuario."))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error al iniciar sesión: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * 2) Pantalla de registro: Crea un usuario en Auth y guarda sus datos en Firestore.
     */
    suspend fun register(email: String, password: String, name: String): Result<User> {
        return try {
            // 1. Crear usuario en Firebase Authentication
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val uid = authResult.user?.uid ?: throw Exception("UID nulo después del registro")

            // 2. Guardar datos en Firestore Database (Vinculación)
            val newUser = User(uid = uid, email = email, name = name)
            usersCollection.document(uid).set(newUser).await()

            Result.success(newUser)
        } catch (e: Exception) {
            Log.e(TAG, "Error al registrar: ${e.message}")
            Result.failure(e)
        }
    }

    // --- Funciones de Gestión de Sesión ---

    fun logout() {
        auth.signOut()
    }

    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    // --- Funciones de Dispositivo IoT ---

    /**
     * 3) Registro de dispositivo IoT (Mock): Simula la vinculación del dispositivo al usuario
     * actualizando su documento en Firestore.
     */
    suspend fun registerIotDevice(uid: String, deviceId: String): Result<Unit> {
        return try {
            usersCollection.document(uid).update("iotDeviceId", deviceId).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error al registrar dispositivo: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * Obtener el ID del dispositivo IoT del usuario actual.
     */
    suspend fun getIotDeviceId(uid: String): String? {
        return try {
            val userDoc = usersCollection.document(uid).get().await()
            userDoc.getString("iotDeviceId")
        } catch (e: Exception) {
            Log.e(TAG, "Error al obtener ID del dispositivo: ${e.message}")
            null
        }
    }
}