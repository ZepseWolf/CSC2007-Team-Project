package edu.singaporetech.csc2007team06.repositories

import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import edu.singaporetech.csc2007team06.models.User
import edu.singaporetech.csc2007team06.utils.Resource
import edu.singaporetech.csc2007team06.utils.safeCall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

/**
 * Define business logic for register and login users.
 */
class AuthRepository {
    private val auth = Firebase.auth
    private val db = Firebase.firestore
    val COLLECTION: String = "users"

    /**
     * Register a new user.
     */
    suspend fun registerUser(email: String, password: String, name: String): Resource<AuthResult> {
        return withContext(Dispatchers.IO) {
            safeCall {
                val registrationResult =
                    auth.createUserWithEmailAndPassword(email, password).await()

                // Update user profile with name
                val user = registrationResult.user!!
                val userId = user.uid
                val profileUpdates = userProfileChangeRequest {
                    displayName = name
                }
                user.updateProfile(profileUpdates).await()

                // Add user to Firestore "users" collection
                db.collection(COLLECTION).document(userId).set(User(userId, name, email)).await()

                // Send email to verify email address
                user.sendEmailVerification().await()

                // Logout current user
                auth.signOut()

                // Return registration result
                Resource.Success(registrationResult)

            }
        }
    }

    /**
     * Login a user.
     */
    suspend fun login(email: String, password: String): Resource<AuthResult> {
        return withContext(Dispatchers.IO) {
            safeCall {
                val result = auth.signInWithEmailAndPassword(email, password).await()
                // check if result is successful and email is verified
                if (auth.currentUser?.isEmailVerified == true) {
                    Resource.Success(result)
                } else {
                    auth.currentUser?.sendEmailVerification()
                    Resource.Error("Email not verify")
                }
            }
        }
    }

    /**
     * Get the current user.
     * By Firebase Auth.
     */
    suspend fun user(): Resource<DocumentSnapshot> {
        return withContext(Dispatchers.IO) {
            safeCall {
                if (auth.currentUser == null) {
                    Resource.Error("User not logged in")
                } else {
                    val result =
                        db.collection(COLLECTION).document(auth.currentUser!!.uid).get().await()
                    Resource.Success(result)
                }
            }
        }
    }

    /**
     * Add FCM token to logged in user.
     */
    suspend fun addToken(token: String) {
        return withContext(Dispatchers.IO) {
            safeCall {
                if (auth.currentUser == null) {
                    Resource.Error("User not logged in")
                } else {
                    val db = FirebaseFirestore.getInstance()
                    val userRef = db.collection("users").document(auth.currentUser!!.uid)
                    val result = db.runTransaction { transaction ->

                        // Get the user object
                        val userSnapshot = transaction.get(userRef)

                        // If the user exists, add the token to the list of tokens
                        if (userSnapshot.exists()) {
                            val user = userSnapshot.toObject(User::class.java)
                            val updatedFcmTokens = user?.fcmToken?.toMutableList()
                            if (updatedFcmTokens?.contains(token) == false) {
                                updatedFcmTokens.add(token)
                                transaction.update(userRef, "fcmToken", updatedFcmTokens)
                            }
                        }
                    }.await()
                    Resource.Success(result)
                }
            }
        }
    }

    /**
     * Remove FCM token from logged in user.
     */
    suspend fun removeToken(userId: String, token: String) {
        return withContext(Dispatchers.IO) {
            safeCall {
                val db = FirebaseFirestore.getInstance()
                val userRef = db.collection("users").document(userId)
                val result = db.runTransaction { transaction ->
                    val userSnapshot = transaction.get(userRef)

                    // If the user exists, remove the token from the list of tokens
                    if (userSnapshot.exists()) {
                        val user = userSnapshot.toObject(User::class.java)
                        val updatedFcmTokens = user?.fcmToken?.toMutableList()
                        if (updatedFcmTokens?.contains(token) == true) {
                            updatedFcmTokens.remove(token)
                            transaction.update(userRef, "fcmToken", updatedFcmTokens)
                        }
                    }
                }.await()
                Resource.Success(result)
            }
        }
    }

    /**
     * Get FCM token from Firebase Messaging.
     */
    suspend fun getFcmToken(): Resource<String> {
        return withContext(Dispatchers.IO) {
            safeCall {
                val result = FirebaseMessaging.getInstance().token.await()
                Resource.Success(result)
            }
        }
    }

    /**
     * Update user email
     */
    suspend fun updateEmail(email: String) {
        //update firestore user email
        return withContext(Dispatchers.IO) {
            safeCall {
                val data = db.collection(COLLECTION).document(Firebase.auth.uid.toString())
                    .update("email", email).await()
                Resource.Success(data)
            }
        }
    }

    /**
     * Update user name
     */
    suspend fun updateName(name: String) {
        //update firestore user name
        return withContext(Dispatchers.IO) {
            safeCall {
                val data = db.collection(COLLECTION).document(Firebase.auth.uid.toString())
                    .update("name", name).await()
                Resource.Success(data)
            }
        }
    }
}