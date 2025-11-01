 package com.germinare.simbia_mobile.data.api.model.postgres;

import com.germinare.simbia_mobile.data.api.model.postgres.PostResponse;
import com.germinare.simbia_mobile.data.firestore.UserRepository;
import com.germinare.simbia_mobile.data.fireauth.UserAuth;
import com.germinare.simbia_mobile.data.api.repository.PostgresRepository;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import android.content.Context;
import android.util.Log;

import java.util.List;
import java.util.function.Consumer;

public class FetchUserPostsUseCase {

    private final UserRepository userRepository;
    private final UserAuth userAuth;
    private final PostgresRepository postRepository;

    public FetchUserPostsUseCase(Context context, PostgresRepository postRepository) {
        this.userRepository = new UserRepository(context);
        this.userAuth = new UserAuth();
        this.postRepository = postRepository;
    }

    public void execute(
            Consumer<List<PostResponse>> onPostsSuccess,
            Consumer<String> onFailure
    ) {
        FirebaseUser currentUser = userAuth.getCurrentUser();
        if (currentUser == null) {
            onFailure.accept("Usuário não está logado.");
            return;
        }

        String uid = currentUser.getUid();

        userRepository.getUserByUid(
                uid,
                document -> {
                    Long employeeId = document.getLong("employeeId");

                    if (employeeId != null) {
                        Log.d("FetchPosts", "Employee ID encontrado: " + employeeId);

                        postRepository.listPostsByEmployee(
                                employeeId,
                                onPostsSuccess
                        );
                    } else {
                        onFailure.accept("employeeId não encontrado no perfil do Firestore.");
                    }
                }
        );
    }
}