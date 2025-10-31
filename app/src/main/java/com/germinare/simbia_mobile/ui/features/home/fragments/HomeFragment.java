package com.germinare.simbia_mobile.ui.features.home.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.germinare.simbia_mobile.R;
import com.germinare.simbia_mobile.data.api.cache.PostgresCache;
import com.germinare.simbia_mobile.data.api.repository.PostgresRepository;
import com.germinare.simbia_mobile.data.fireauth.UserAuth;
import com.germinare.simbia_mobile.data.firestore.UserRepository;
import com.germinare.simbia_mobile.databinding.FragmentHomeBinding;
import com.germinare.simbia_mobile.ui.features.splashScreen.SplashScreen;
import com.germinare.simbia_mobile.utils.AlertUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseUser;

public class HomeFragment extends Fragment {

    private UserAuth userAuth;
    private PostgresRepository repository;
    private UserRepository userRepository;
    private PostgresCache postgresCache;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private FragmentHomeBinding binding;
    private Runnable navigationRunnable;
    private String lastText = "";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        postgresCache = PostgresCache.getInstance();
        userAuth = new UserAuth();
        userRepository = new UserRepository(requireContext());
        repository = new PostgresRepository(error -> AlertUtils.showDialogError(requireContext(), error));

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadData();
        setupInputHandling();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (binding != null && binding.etPerguntaEva != null) {
            binding.etPerguntaEva.setText("");
            lastText = "";
            handler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacksAndMessages(null);
        binding = null;
    }

    private void loadData(){
        FirebaseUser user = userAuth.getCurrentUser();
        if (user == null) {
            startActivity(new Intent(requireActivity(), SplashScreen.class));
            requireActivity().finish();
        }

        userRepository.getUserByUid(user.getUid(), document -> {
            postgresCache.setEmployee(document);
            repository.findIndustryById(document.getLong("industryId"), industry -> {
                postgresCache.setIndustry(industry);
                repository.listPostsByCnpj(industry.getCnpj(), list -> {
                    postgresCache.setPosts(list);
                    postgresCache.setPostsFiltered(list);
                });
            });
            repository.listProductCategories(postgresCache::setProductCategory);
        });
    }

    private void setupInputHandling() {
        binding.etPerguntaEva.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE ||
                        actionId == EditorInfo.IME_ACTION_SEND ||
                        actionId == EditorInfo.IME_ACTION_GO ||
                        (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {

                    handleUserInput();
                    return true;
                }
                return false;
            }
        });

        binding.etPerguntaEva.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    scheduleNavigation();
                }
            }
        });

        binding.etPerguntaEva.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP) {
                    scheduleNavigation();
                }
                return false;
            }
        });
    }

    private void scheduleNavigation() {
        if (navigationRunnable != null) {
            handler.removeCallbacks(navigationRunnable);
        }

        navigationRunnable = new Runnable() {
            @Override
            public void run() {
                String currentText = binding.etPerguntaEva.getText().toString().trim();

                if (!currentText.equals(lastText) && !currentText.isEmpty()) {
                    lastText = currentText;
                    handleUserInput();
                }
            }
        };
        handler.postDelayed(navigationRunnable, 1000);
    }

    private void handleUserInput() {
        if (isAdded() && binding != null) {
            String userInput = binding.etPerguntaEva.getText().toString().trim();
            if (!userInput.isEmpty()) {
                handler.removeCallbacksAndMessages(null);

                binding.etPerguntaEva.setText("");
                binding.etPerguntaEva.clearFocus();
                lastText = "";

                Bundle bundle = new Bundle();
                bundle.putString("user_input", userInput);

                try {
                    BottomNavigationView bottomNav = requireActivity().findViewById(R.id.nav_view);
                    if (bottomNav != null) {
                        bottomNav.setSelectedItemId(R.id.navigation_eva);
                    }
                    Navigation.findNavController(requireView()).navigate(R.id.navigation_eva, bundle);
                } catch (Exception e) {
                    if (binding != null) {
                        binding.etPerguntaEva.setText(userInput);
                    }
                }
            }
        }
    }
}