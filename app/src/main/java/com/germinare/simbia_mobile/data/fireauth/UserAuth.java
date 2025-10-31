package com.germinare.simbia_mobile.data.fireauth;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.function.Consumer;

public class UserAuth {

    private final FirebaseAuth firebaseAuth;

    public UserAuth(){
        this.firebaseAuth = FirebaseAuth.getInstance();
    }

    public void signIn(String email, String password, Consumer<Task<AuthResult>> onComplete){
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(onComplete::accept);
    }

    public FirebaseUser getCurrentUser(){
        return firebaseAuth.getCurrentUser();
    }

}
