package com.germinare.simbia_mobile.ui.features.home.fragments.feed;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.germinare.simbia_mobile.R;
import com.germinare.simbia_mobile.data.api.cache.Cache;
import com.germinare.simbia_mobile.data.api.model.integration.PostSuggestRequest;
import com.germinare.simbia_mobile.data.api.model.postgres.ProductCategoryResponse;
import com.germinare.simbia_mobile.data.api.repository.IntegrationRepository;
import com.germinare.simbia_mobile.data.api.repository.PostgresRepository;
import com.germinare.simbia_mobile.databinding.FragmentFeedBinding;
import com.germinare.simbia_mobile.ui.features.home.fragments.feed.adapter.FiltersAdapter;
import com.germinare.simbia_mobile.ui.features.home.fragments.feed.adapter.Post;
import com.germinare.simbia_mobile.ui.features.home.fragments.feed.adapter.PostAdapter;
import com.germinare.simbia_mobile.ui.features.home.fragments.feed.adapter.PostPagerAdapter;
import com.germinare.simbia_mobile.utils.AlertUtils;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class FeedFragment extends Fragment {

    private FragmentFeedBinding binding;
    private Cache cache;
    private IntegrationRepository integrationRepository;
    private PostgresRepository postgresRepository;
    private AlertDialog progressDialog;
    private PostPagerAdapter postPagerAdapter;
    private FiltersAdapter filtersAdapter;
    private PostAdapter postAdapter1;
    private PostAdapter postAdapter2;
    private PostAdapter postAdapterSuggest;

    private final List<String> filters = new ArrayList<>();
    private final List<Post> posts = new ArrayList<>();
    private final List<Post> postsSuggest = new ArrayList<>();
    private final List<Post> topThreePosts = new ArrayList<>();

    private static final List<String> measuresUnits = Arrays.asList(
            "Kg", "Litro", "Metro", "Unidade"
    );

    public FeedFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Consumer<String> onError = error -> {
            AlertUtils.showDialogError(requireContext(), error);
            AlertUtils.hideDialog(progressDialog);
        };

        cache = Cache.getInstance();
        integrationRepository = new IntegrationRepository(onError);
        postgresRepository = new PostgresRepository(onError);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFeedBinding.inflate(inflater, container, false);

        postPagerAdapter = new PostPagerAdapter(topThreePosts, this::onClickPost);
        filtersAdapter = new FiltersAdapter(requireContext(), filters);
        postAdapter1 = new PostAdapter(posts, this::onClickPost);
        postAdapter2 = new PostAdapter(posts, this::onClickPost);
        postAdapterSuggest = new PostAdapter(postsSuggest, this::onClickPost, false);

        progressDialog = AlertUtils.showLoadingDialog(requireContext(), "");
        cache.addListener(this::updateUIFromCache);
        updateUIFromCache();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.vpCarroselPosts.setAdapter(postPagerAdapter);
        binding.vpCarroselPosts.setOffscreenPageLimit(3);
        binding.vpCarroselPosts.setClipToPadding(false);
        binding.vpCarroselPosts.setClipChildren(false);
        binding.vpCarroselPosts.getChildAt(0).setOverScrollMode(View.OVER_SCROLL_NEVER);
        setupHandler();

        binding.rvFilters.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvFilters.setAdapter(filtersAdapter);

        binding.rvPost1.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvPost1.setAdapter(postAdapter1);

        binding.rvPost2.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvPost2.setAdapter(postAdapter2);

        binding.btnSugerirMatch.setOnClickListener(V -> {
            AlertUtils.DialogAlertBuilder builder = new AlertUtils.DialogAlertBuilder()
                    .onCustomViewCreated((v, dialog) -> {
                        ArrayAdapter<String> categoriaAdapter = new ArrayAdapter<>(
                                requireContext(),
                                android.R.layout.simple_dropdown_item_1line,
                                filters
                        );
                        ((AutoCompleteTextView) dialog.findViewById(R.id.alert_actv_categoria)).setAdapter(categoriaAdapter);

                        ArrayAdapter<String> unidadeAdapter = new ArrayAdapter<>(
                                requireContext(),
                                android.R.layout.simple_dropdown_item_1line,
                                measuresUnits
                        );
                        ((AutoCompleteTextView) dialog.findViewById(R.id.alert_actv_unidade)).setAdapter(unidadeAdapter);
                    })
                    .onAccept(this::suggestPost);

            AlertUtils.showDialogCustom(
                    requireContext(),
                    R.layout.alert_sugerir_match,
                    builder
            );
        });
    }

    private void onClickPost(Post post){
        Bundle args = new Bundle();
        args.putParcelable("post", post);
        Navigation.findNavController(requireView()).navigate(R.id.navigation_product_details, args);
    }

    private void updateUIFromCache(){
        if (cache.getProductCategory() != null && cache.getPostsFiltered() != null) {
            List<Post> allPosts = cache.getPostsFiltered().stream()
                    .map(Post::new)
                    .collect(Collectors.toList());

            loadDataAdapters(filters,
                    cache.getProductCategory().stream()
                            .map(ProductCategoryResponse::getCategoryName)
                            .collect(Collectors.toList()),
                    filtersAdapter);
            loadDataAdapters(posts, allPosts, postAdapter1);
            loadDataAdapters(posts, allPosts, postAdapter2);
            Collections.shuffle(allPosts);
            List<Post> topThree = allPosts.size() > 3 ? allPosts.subList(0, 3) : allPosts;
            loadDataAdapters(topThreePosts, topThree, postPagerAdapter);

            AlertUtils.hideDialog(progressDialog);
        }
    }

    private <T, D extends RecyclerView.Adapter<?>> void loadDataAdapters(List<T> oldList, List<T> newList, D adapter){
        oldList.clear();
        oldList.addAll(newList);
        adapter.notifyDataSetChanged();
    }

    private void suggestPost(Dialog dialog){
        PostSuggestRequest request = new PostSuggestRequest(
                cache.getProductCategory().stream()
                        .filter(category -> category.getCategoryName().equals(((AutoCompleteTextView) dialog.findViewById(R.id.alert_actv_categoria)).getText().toString()))
                        .map(ProductCategoryResponse::getId)
                        .findFirst().orElse(null),
                ((TextInputEditText) dialog.findViewById(R.id.alert_et_quantidade)).getText().toString(),
                String.valueOf(measuresUnits.indexOf(
                        ((AutoCompleteTextView) dialog.findViewById(R.id.alert_actv_unidade)).getText().toString())+1),
                String.valueOf(cache.getEmployee().getIndustryId())
        );
        AlertUtils.hideDialog(dialog);
        progressDialog = AlertUtils.showLoadingDialog(requireContext(), "Pensando...");

        integrationRepository.suggestPost(request, response -> {
            postsSuggest.clear();
            for (Long idPost : response.getData()) {
                postgresRepository.findPostById(idPost, post -> {
                    List<Post> newList = new ArrayList<>(postsSuggest);
                    newList.add(new Post(post));
                    Log.d("teste", newList.toString());
                    loadDataAdapters(postsSuggest, newList, postAdapterSuggest);
                });
            }

            AlertUtils.DialogAlertBuilder builder = new AlertUtils.DialogAlertBuilder()
                    .onCustomViewCreated((view, dialogPosts) -> {
                        ((RecyclerView) dialogPosts.findViewById(R.id.rv_posts_suggests)).setLayoutManager(
                                new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                        );
                        ((RecyclerView) dialogPosts.findViewById(R.id.rv_posts_suggests)).setAdapter(postAdapterSuggest);
                    });

            AlertUtils.showDialogCustom(
                    requireContext(),
                    R.layout.card_posts_suggests,
                    builder
            );
            AlertUtils.hideDialog(progressDialog);
        });
    }

    private void setupHandler(){
        final int delay = 4000;
        final Handler handler = new Handler(Looper.getMainLooper());
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (postPagerAdapter.getItemCount() > 0) {
                    int nextItem = (binding.vpCarroselPosts.getCurrentItem() + 1) % topThreePosts.size();
                    binding.vpCarroselPosts.setCurrentItem(nextItem, true);
                    handler.postDelayed(this, delay);
                }
            }
        };
        Runnable restartRunnable = () -> {
            handler.removeCallbacks(runnable);
            handler.postDelayed(runnable, delay);
        };

        binding.vpCarroselPosts.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            private boolean userScroll = false;

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
                if (state == ViewPager2.SCROLL_STATE_DRAGGING) {
                    userScroll = true;
                    handler.removeCallbacks(runnable);
                } else if (state == ViewPager2.SCROLL_STATE_IDLE && userScroll) {
                    userScroll = false;
                    handler.postDelayed(runnable, delay);
                }
            }
        });
        handler.postDelayed(runnable, delay);
        getViewLifecycleOwner().getLifecycle().addObserver(new androidx.lifecycle.DefaultLifecycleObserver() {
            @Override
            public void onDestroy(@NonNull androidx.lifecycle.LifecycleOwner owner) {
                handler.removeCallbacks(runnable);
            }
        });
    }
}