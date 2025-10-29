package com.germinare.simbia_mobile.ui.features.home.fragments.feed;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.germinare.simbia_mobile.R;
import com.germinare.simbia_mobile.data.api.cache.PostgresCache;
import com.germinare.simbia_mobile.data.api.model.postgres.ProductCategoryResponse;
import com.germinare.simbia_mobile.data.api.repository.PostgresRepository;
import com.germinare.simbia_mobile.databinding.FragmentFeedBinding;
import com.germinare.simbia_mobile.ui.features.home.fragments.feed.adapter.FiltersAdapter;
import com.germinare.simbia_mobile.ui.features.home.fragments.feed.adapter.Post;
import com.germinare.simbia_mobile.ui.features.home.fragments.feed.adapter.PostAdapter;
import com.germinare.simbia_mobile.utils.AlertUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FeedFragment extends Fragment {

    private FragmentFeedBinding binding;
    private PostgresRepository repository;
    private PostgresCache postgresCache;
    private AlertDialog progressDialog;
    private FiltersAdapter filtersAdapter;
    private PostAdapter postAdapter1;
    private PostAdapter postAdapter2;

    private List<String> filters = new ArrayList<>();
    private List<Post> posts = new ArrayList<>();

    public FeedFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        postgresCache = PostgresCache.getInstance();
        repository = new PostgresRepository(error -> AlertUtils.showDialogError(requireContext(), error));
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFeedBinding.inflate(inflater, container, false);

        filtersAdapter = new FiltersAdapter(requireContext(), filters);
        postAdapter1 = new PostAdapter(posts, this::onClickPost);
        postAdapter2 = new PostAdapter(posts, this::onClickPost);

        progressDialog = AlertUtils.showLoadingDialog(requireContext(), "");
        postgresCache.addListener(this::updateUIFromCache);
        updateUIFromCache();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.rvFilters.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvFilters.setAdapter(filtersAdapter);

        binding.rvPost1.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvPost1.setAdapter(postAdapter1);

        binding.rvPost2.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvPost2.setAdapter(postAdapter2);

    }

    private void onClickPost(Post post){
        Bundle args = new Bundle();
        args.putParcelable("post", post);

        Navigation.findNavController(requireView()).navigate(R.id.navigation_product_details, args);
    }

    private void updateUIFromCache(){
        if (postgresCache.getProductCategory() != null && postgresCache.getPostsFiltered() != null) {
            loadDataAdapters(filters,
                    postgresCache.getProductCategory().stream().
                            map(ProductCategoryResponse::getCategoryName).collect(Collectors.toList()),
                    filtersAdapter);
            loadDataAdapters(posts, postgresCache.getPostsFiltered().stream().map(Post::new).collect(Collectors.toList()),
                    postAdapter1);
            loadDataAdapters(posts, postgresCache.getPostsFiltered().stream().map(Post::new).collect(Collectors.toList()),
                    postAdapter2);

            AlertUtils.hideLoadingDialog(progressDialog);
        }
    }

    private <T, D extends RecyclerView.Adapter> void loadDataAdapters(List<T> oldList, List<T> newList, D adapter){
        oldList.clear();
        oldList.addAll(newList);
        adapter.notifyDataSetChanged();
    }
}