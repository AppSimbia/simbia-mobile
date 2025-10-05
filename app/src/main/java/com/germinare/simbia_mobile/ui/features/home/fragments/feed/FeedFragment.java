package com.germinare.simbia_mobile.ui.features.home.fragments.feed;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.germinare.simbia_mobile.databinding.FragmentFeedBinding;
import com.germinare.simbia_mobile.ui.features.home.fragments.feed.adapter.FiltersAdapter;
import com.germinare.simbia_mobile.ui.features.home.fragments.feed.adapter.Post;
import com.germinare.simbia_mobile.ui.features.home.fragments.feed.adapter.PostAdapter;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FeedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FeedFragment extends Fragment {

    private FragmentFeedBinding binding;

    private static final String[] filters = {"Alimentício", "Cera", "Marcenaria", "Metalúrgico"};

    public FeedFragment() {
    }

    public static FeedFragment newInstance() {
        return new FeedFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFeedBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FiltersAdapter filtersAdapter = new FiltersAdapter(requireContext(), filters);
        binding.rvFilters.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvFilters.setAdapter(filtersAdapter);

        PostAdapter postAdapter = new PostAdapter(List.of(
                new Post(1L, "Orgânicos Secos", "1200.90", "12kg", "https://encurtador.com.br/e1DGN", "https://encurtador.com.br/PYjCx"),
                new Post(1L, "Orgânicos Secos", "1200.90", "12kg", "https://encurtador.com.br/e1DGN", "https://encurtador.com.br/PYjCx"),
                new Post(1L, "Orgânicos Secos", "1200.90", "12kg", "https://encurtador.com.br/e1DGN", "https://encurtador.com.br/PYjCx"),
                new Post(1L, "Orgânicos Secos", "1200.90", "12kg", "https://encurtador.com.br/e1DGN", "https://encurtador.com.br/PYjCx")
        ));
        binding.rvPost1.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvPost1.setAdapter(postAdapter);

        binding.rvPost2.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvPost2.setAdapter(postAdapter);

    }
}