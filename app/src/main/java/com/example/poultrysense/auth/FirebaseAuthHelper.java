package com.example.poultrysense.auth;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class FirebaseAuthHelper {

    private FirebaseAuth mAuth;

    public FirebaseAuthHelper() {
        mAuth = FirebaseAuth.getInstance();
    }

    public FirebaseAuth getAuth() {
        return mAuth;
    }

    public FirebaseUser getCurrentUser() {
        return mAuth.getCurrentUser();
    }

    // Mengecek apakah user sudah terverifikasi (jika butuh verifikasi email)
    public boolean isUserLoggedIn() {
        return mAuth.getCurrentUser() != null;
    }

    public void logout() {
        mAuth.signOut();
    }

    public String getUserEmail() {
        return mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getEmail() : null;
    }
}