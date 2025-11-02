package com.germinare.simbia_mobile.ui.features.home.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.viewpager2.widget.ViewPager2;

import com.germinare.simbia_mobile.R;
import com.germinare.simbia_mobile.data.api.cache.Cache;
import com.germinare.simbia_mobile.data.api.model.firestore.EmployeeFirestore;
import com.germinare.simbia_mobile.data.api.model.postgres.PostResponse;
import com.germinare.simbia_mobile.data.api.repository.MongoRepository;
import com.germinare.simbia_mobile.data.api.repository.PostgresRepository;
import com.germinare.simbia_mobile.data.fireauth.UserAuth;
import com.germinare.simbia_mobile.data.firestore.UserRepository;
import com.germinare.simbia_mobile.databinding.FragmentHomeBinding;
import com.germinare.simbia_mobile.ui.features.home.fragments.challenges.Challenge;
import com.germinare.simbia_mobile.ui.features.home.fragments.challenges.ChallengePagerAdapter;
import com.germinare.simbia_mobile.ui.features.home.fragments.feed.adapter.Post;
import com.germinare.simbia_mobile.ui.features.home.fragments.feed.adapter.PostPagerAdapter;
import com.germinare.simbia_mobile.ui.features.splashScreen.SplashScreen;
import com.germinare.simbia_mobile.utils.AlertUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";

    private UserAuth userAuth;
    private PostPagerAdapter postPagerAdapter;
    private PostgresRepository repository;
    private MongoRepository mongoRepository;
    private UserRepository userRepository;
    private Cache cache;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Handler carrosselHandler = new Handler(Looper.getMainLooper());
    private FragmentHomeBinding binding;
    private Runnable navigationRunnable;
    private Runnable carrosselRunnable;
    private String lastText = "";

    private final List<Post> topThreePosts = new ArrayList<>();
    private final List<Challenge> latestTwoChallenges = new ArrayList<>();
    private ChallengePagerAdapter challengePagerAdapter;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        cache = Cache.getInstance();
        userAuth = new UserAuth();
        userRepository = new UserRepository(requireContext());
        repository = new PostgresRepository(error -> AlertUtils.showDialogError(requireContext(), error));
        mongoRepository = new MongoRepository(error -> AlertUtils.showDialogError(requireContext(), error));

        postPagerAdapter = new PostPagerAdapter(topThreePosts, this::onClickPost);
        challengePagerAdapter = new ChallengePagerAdapter(latestTwoChallenges, response -> {
            Log.d(TAG, "Desafio clicado: " + response.getId());
            Bundle bundle = new Bundle();
            bundle.putString("challengeId", response.getId());
            Navigation.findNavController(requireView())
                    .navigate(R.id.action_navigation_home_to_challengesFragment, bundle);
        });
        binding.vpChallengesHome.setAdapter(challengePagerAdapter);
        binding.vpChallengesHome.setOffscreenPageLimit(3);
        binding.vpChallengesHome.setClipToPadding(false);
        binding.vpChallengesHome.setClipChildren(false);
        binding.vpChallengesHome.getChildAt(0).setOverScrollMode(View.OVER_SCROLL_NEVER);

        binding.ivLegalGuide.setOnClickListener(v -> Navigation.findNavController(requireView())
                .navigate(R.id.action_homeFragment_to_legalGuideFragment));
        binding.ivPainelImpacto.setOnClickListener(v -> Navigation.findNavController(requireView())
                .navigate(R.id.action_navigation_home_to_navigation_impacts));

        return binding.getRoot();
    }

    private void onClickPost(Post post) {
        Bundle args = new Bundle();
        args.putParcelable("post", post);
        Navigation.findNavController(requireView()).navigate(R.id.navigation_product_details, args);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadData();
        setupInputHandling();
        setupCarrossel();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (binding != null && binding.etPerguntaEva != null) {
            binding.etPerguntaEva.setText("");
            lastText = "";
            handler.removeCallbacksAndMessages(null);
        }
        startCarrosselRotation();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopCarrosselRotation();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacksAndMessages(null);
        carrosselHandler.removeCallbacksAndMessages(null);
        binding = null;
    }

    private void loadData() {
        FirebaseUser user = userAuth.getCurrentUser();
        if (user == null) {
            startActivity(new Intent(requireActivity(), SplashScreen.class));
            requireActivity().finish();
            return;
        }

        userRepository.getUserByUid(user.getUid(), document -> {
            EmployeeFirestore employee = new EmployeeFirestore(document);
            cache.setEmployee(employee);
            mongoRepository.findAllChatByEmployeeId(employee.getUid(), cache::setChats);
            repository.findIndustryById(document.getLong("industryId"), industry -> {
                cache.setIndustry(industry);
                repository.listPostsByCnpj(industry.getCnpj(), list -> {
                    cache.setPosts(list);
                    cache.setPostsFiltered(list);
                    updateCarrossel(list);
                });
            });
            repository.listProductCategories(cache::setProductCategory);
        });

        mongoRepository.findAllChallenges(response -> {
            latestTwoChallenges.clear();

            latestTwoChallenges.addAll((response.size() > 3 ? response.subList(0, 3) : response).stream()
                    .map(Challenge::new).collect(Collectors.toList()));

            latestTwoChallenges.forEach(challenge -> {
                repository.findIndustryByIdEmployee(challenge.getIdEmployeeQuestion(), industryResponse -> {
                    challenge.setIndustryImage(industryResponse.getImage());
                    challenge.setIndustryName(industryResponse.getIndustryName());
                    challengePagerAdapter.notifyDataSetChanged();
                });
            });
        });
    }

    private void updateCarrossel(List<PostResponse> postResponses) {
        if (postResponses == null) return;

        List<Post> allPosts = postResponses.stream()
                .map(Post::new)
                .collect(Collectors.toList());

        Collections.shuffle(allPosts);
        List<Post> topThree = allPosts.size() > 3 ? allPosts.subList(0, 3) : allPosts;

        topThreePosts.clear();
        topThreePosts.addAll(topThree);
        postPagerAdapter.notifyDataSetChanged();

        startCarrosselRotation();
    }

    private void setupCarrossel() {
        if (binding.vpCarroselPostsHome != null) {
            binding.vpCarroselPostsHome.setAdapter(postPagerAdapter);
            binding.vpCarroselPostsHome.setOffscreenPageLimit(3);
            binding.vpCarroselPostsHome.setClipToPadding(false);
            binding.vpCarroselPostsHome.setClipChildren(false);
            binding.vpCarroselPostsHome.getChildAt(0).setOverScrollMode(View.OVER_SCROLL_NEVER);

            final int delay = 4000;
            carrosselRunnable = new Runnable() {
                @Override
                public void run() {
                    if (binding != null && binding.vpCarroselPostsHome != null && postPagerAdapter.getItemCount() > 0) {
                        int nextItem = (binding.vpCarroselPostsHome.getCurrentItem() + 1) % topThreePosts.size();
                        binding.vpCarroselPostsHome.setCurrentItem(nextItem, true);
                        carrosselHandler.postDelayed(this, delay);
                    }
                }
            };

            binding.vpCarroselPostsHome.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                private boolean userScroll = false;

                @Override
                public void onPageScrollStateChanged(int state) {
                    super.onPageScrollStateChanged(state);
                    if (state == ViewPager2.SCROLL_STATE_DRAGGING) {
                        userScroll = true;
                        carrosselHandler.removeCallbacks(carrosselRunnable);
                    } else if (state == ViewPager2.SCROLL_STATE_IDLE && userScroll) {
                        userScroll = false;
                        carrosselHandler.postDelayed(carrosselRunnable, delay);
                    }
                }
            });
        }
    }

    private void startCarrosselRotation() {
        if (carrosselRunnable != null && binding != null && postPagerAdapter.getItemCount() > 0) {
            carrosselHandler.removeCallbacks(carrosselRunnable);
            carrosselHandler.postDelayed(carrosselRunnable, 4000);
        }
    }

    private void stopCarrosselRotation() {
        if (carrosselRunnable != null) {
            carrosselHandler.removeCallbacks(carrosselRunnable);
        }
    }

    private void setupInputHandling() {
        if (binding == null) return;

        binding.etPerguntaEva.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                    actionId == EditorInfo.IME_ACTION_SEND ||
                    actionId == EditorInfo.IME_ACTION_GO ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {

                handleUserInput();
                return true;
            }
            return false;
        });

        binding.etPerguntaEva.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) scheduleNavigation();
        });

        binding.etPerguntaEva.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_UP) scheduleNavigation();
            return false;
        });
    }

    private void scheduleNavigation() {
        if (navigationRunnable != null) handler.removeCallbacks(navigationRunnable);

        navigationRunnable = () -> {
            if (binding == null) return;

            String currentText = binding.etPerguntaEva.getText().toString().trim();
            if (!currentText.equals(lastText) && !currentText.isEmpty()) {
                lastText = currentText;
                handleUserInput();
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
                bundle.putString("message", userInput);

                try {
                    BottomNavigationView bottomNav = requireActivity().findViewById(R.id.nav_view);
                    if (bottomNav != null) bottomNav.setSelectedItemId(R.id.navigation_eva);

                    Navigation.findNavController(requireView()).navigate(R.id.navigation_eva, bundle);
                } catch (Exception e) {
                    Log.e(TAG, "Erro ao navegar para Eva", e);
                }
            }
        }
    }
}