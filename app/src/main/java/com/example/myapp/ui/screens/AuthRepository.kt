package com.example.myapp.ui.screens

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import android.util.Log

data class User(
    val uid: String,
    val email: String,
    val name: String? = null,
    val iotDeviceId: String? = null
)

class AuthRepository {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val usersCollection = firestore.collection("users")
    private val TAG = "AuthRepository"

    suspend fun login(email: String, password: String): Result<User> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val uid = result.user?.uid ?: throw Exception("UID nulo después de iniciar sesión")

            val userDoc = usersCollection.document(uid).get().await()
            val user = userDoc.toObject(User::class.java)

            if (user != null) {
                Result.success(user)
            } else {
                auth.signOut()
                Result.failure(Exception("Usuario no encontrado en la base de datos."))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error al iniciar sesión: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun register(email: String, password: String, name: String): Result<User> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val uid = authResult.user?.uid ?: throw Exception("UID nulo después del registro")

            val newUser = User(uid = uid, email = email, name = name)
            usersCollection.document(uid).set(newUser).await()

            Result.success(newUser)
        } catch (e: Exception) {
            Log.e(TAG, "Error al registrar: ${e.message}")
            Result.failure(e)
        }
    }

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

    suspend fun registerIotDevice(uid: String, deviceId: String): Result<Unit> {
        return try {
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