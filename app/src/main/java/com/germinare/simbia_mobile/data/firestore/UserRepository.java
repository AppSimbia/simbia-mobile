package com.germinare.simbia_mobile.data.firestore;

import android.content.Context;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;
import java.util.function.Consumer;

public class UserRepository {
    private static final String COLLECTION_NAME = "employee";
    private final FirebaseFirestore db;
    private final Context ctx;

    public UserRepository(Context ctx){
        this.db = FirebaseFirestore.getInstance();
        this.ctx = ctx;
    }

    public void getUserByUid(
            String uid,
            Consumer<DocumentSnapshot> onSucessFunction){
        db.collection(COLLECTION_NAME).document(uid).get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) onSucessFunction.accept(document);
                    else Toast.makeText(ctx, "Dados do usuário não encontrados.", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(ctx, "Erro ao carregar dados: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    public void updateFieldByUid(
            String uid,
            Map<String, Object> map,
            Consumer<Void> onSucessFunction
    ){
        db.collection(COLLECTION_NAME).document(uid)
                .update(map)
                .addOnSuccessListener(onSucessFunction::accept)
                .addOnFailureListener(e ->
                        Toast.makeText(ctx, "Erro ao atualizar usuário: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
