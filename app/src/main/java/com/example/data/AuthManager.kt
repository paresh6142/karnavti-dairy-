package com.example.data

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await

class AuthManager(private val context: Context) {
    private val auth by lazy { FirebaseAuth.getInstance() }
    private val credentialManager = CredentialManager.create(context)

    val currentUser get() = auth.currentUser

    suspend fun signInWithGoogle(): Boolean {
        try {
            // Check if user is already signed in
            if (auth.currentUser != null) return true

            // Fallback for dev environments where string resource might be missing
            val webClientId = try {
                val resId = context.resources.getIdentifier("default_web_client_id", "string", context.packageName)
                if (resId != 0) context.getString(resId) else ""
            } catch (e: Exception) { "" }
            
            if (webClientId.isEmpty()) {
                Log.e("AuthManager", "default_web_client_id is empty. Google Services JSON might be missing.")
                return false
            }

            val googleIdOption = GetSignInWithGoogleOption.Builder(webClientId)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result = credentialManager.getCredential(
                request = request,
                context = context
            )

            handleSignIn(result)
            return auth.currentUser != null
        } catch (e: Exception) {
            Log.e("AuthManager", "Sign In failed", e)
            return false
        }
    }

    private suspend fun handleSignIn(result: GetCredentialResponse) {
        val credential = result.credential
        if (credential is CustomCredential &&
            credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) {
            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
            val authCredential = GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)
            auth.signInWithCredential(authCredential).await()
        }
    }
    
    fun signOut() {
        auth.signOut()
    }
}
