package com.germinare.simbia_mobile.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.function.Consumer;

public class BaseLoginUtils {

    private static final String TAG = "BaseLoginUtils";

    private final FirebaseAuth firebaseAuth;
    private final FirebaseFirestore firestore;
    private final Context context;

    public BaseLoginUtils(Context context) {
        this.context = context;
        this.firebaseAuth = FirebaseAuth.getInstance();
        this.firestore = FirebaseFirestore.getInstance();
    }

    /**
     * Obtém o usuário atual logado.
     */
    public FirebaseUser getCurrentUser() {
        return firebaseAuth.getCurrentUser();
    }

    /**
     * Verifica se o usuário fez o firstAccess e executa callbacks apropriados.
     * @param onFirstAccess true -> usuário ainda não fez firstAccess
     * @param onNormalAccess true -> usuário já fez firstAccess
     */
    public void checkFirstAccess(@NonNull Consumer<Boolean> onFirstAccess,
                                 @NonNull Runnable onNormalAccess) {

        FirebaseUser user = getCurrentUser();

        if (user == null) {
            Toast.makeText(context, "Usuário não autenticado.", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Usuário não autenticado.");
            return;
        }

        DocumentReference docRef = firestore.collection("employee").document(user.getUid());
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null && task.getResult().exists()) {
                DocumentSnapshot doc = task.getResult();
                Boolean firstAccess = doc.getBoolean("firstAccess");
                Log.d(TAG, "firstAccess = " + firstAccess);

                if (firstAccess != null && firstAccess) {
                    onFirstAccess.accept(true);
                } else {
                    onNormalAccess.run();
                }
            } else {
                Toast.makeText(context, "Erro ao verificar perfil.", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Erro ao obter documento user: " + task.getException());
            }
        });
    }

    /**
     * Faz logout do usuário.
     */
    public void logout() {
        firebaseAuth.signOut();
        Toast.makeText(context, "Logout realizado.", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Logout realizado.");
    }
}
