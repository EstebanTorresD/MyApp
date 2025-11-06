package com.example.myapp.ui.screens

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import android.util.Log

// Modelo de datos para el usuario que se guardará en Firestore
data class User(
    val uid: String,
    val email: String,
    val name: String? = null,
    val iotDeviceId: String? = null // Para el punto 3
)

// Repositorio para manejar toda la lógica de Firebase Auth y Firestore
class AuthRepository {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val usersCollection = firestore.collection("users")
    private val TAG = "AuthRepository"

    // 1) Inicio de Sesión
    suspend fun login(email: String, password: String): Result<User> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val uid = result.user?.uid ?: throw Exception("UID nulo después de iniciar sesión")

            // Obtener datos del usuario desde Firestore
            val userDoc = usersCollection.document(uid).get().await()
            val user = userDoc.toObject(User::class.java)

            if (user != null) {
                Result.success(user)
            } else {
                auth.signOut() // Si el usuario existe en Auth pero no en Firestore (anomalía)
                Result.failure(Exception("Usuario no encontrado en la base de datos."))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error al iniciar sesión: ${e.message}")
            Result.failure(e)
        }
    }

    // 2) Registro de Usuario (Auth + Firestore)
    suspend fun register(email: String, password: String, name: String): Result<User> {
        return try {
            // 1. Crear usuario en Firebase Authentication
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val uid = authResult.user?.uid ?: throw Exception("UID nulo después del registro")

            // 2. Guardar datos adicionales en Firestore Database
            val newUser = User(uid = uid, email = email, name = name)
            usersCollection.document(uid).set(newUser).await()

            Result.success(newUser)
        } catch (e: Exception) {
            Log.e(TAG, "Error al registrar: ${e.message}")
            Result.failure(e)
        }
    }

    // Obtener información del usuario actual (útil para el punto 5)
    suspend fun getCurrentUser(uid: String): Result<User> {
        return try {
            val userDoc = usersCollection.document(uid).get().await()
            val user = userDoc.toObject(User::class.java)
            if (user != null) {
                Result.success(user)
            } else {
                Result.failure(Exception("Datos de usuario no disponibles."))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error al obtener usuario: ${e.message}")
            Result.failure(e)
        }
    }

    // 3) Lógica de "Registro" del Dispositivo IoT (Mock para la evaluación)
    suspend fun registerIotDevice(uid: String, deviceId: String): Result<Unit> {
        return try {
            // Simplemente actualizamos el documento del usuario con el ID del dispositivo
            usersCollection.document(uid).update("iotDeviceId", deviceId).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error al registrar dispositivo: ${e.message}")
            Result.failure(e)
        }
    }

    fun logout() {
        auth.signOut()
    }

    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    fun getUserId(): String? {
        return auth.currentUser?.uid
    }
}