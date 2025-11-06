package com.example.myapp.data

import com.example.myapp.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import android.util.Log

class AuthRepository {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val usersCollection = firestore.collection("users")
    private val TAG = "AuthRepository"


    suspend fun login(email: String, password: String): Result<User> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val uid = result.user?.uid ?: throw Exception("UID nulo.")

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

    fun logout() {
        auth.signOut()
    }

    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
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