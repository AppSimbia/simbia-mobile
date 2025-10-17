package com.germinare.simbia_mobile.ui.features.home.fragments.feed;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.germinare.simbia_mobile.R;
import com.germinare.simbia_mobile.databinding.FragmentSolicitationMatchBinding;
import com.germinare.simbia_mobile.ui.features.home.fragments.feed.activity.SolicitationSent;
import com.germinare.simbia_mobile.ui.features.home.fragments.feed.adapter.Post;

public class SolicitationMatchFragment extends Fragment {

    private FragmentSolicitationMatchBinding binding;

    public SolicitationMatchFragment() {
    }

    public static SolicitationMatchFragment newInstance() {
        return new SolicitationMatchFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSolicitationMatchBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle args = getArguments();

        if (args != null) {
            Post post = args.getParcelable("post");

            binding.txNameIndustrySolicitation.setText("Indústria de Lebuddha");
            binding.txSolicitationDescription.setText(formartterDescription(post.getTitle()));

            binding.btnCancelSolicitation.setOnClickListener(V -> Navigation.findNavController(requireView()).navigate(R.id.navigation_product_details, args));
            binding.btnSendSolicitation.setOnClickListener(V -> {
                Bundle argsSolicitation = new Bundle();
                argsSolicitation.putString("nameIndustry", "Indústria de Lebuddha");

                Intent intent = new Intent(requireContext(), SolicitationSent.class);
                intent.putExtras(argsSolicitation);
                startActivity(intent);
            });
        }
    }

    private String formartterDescription(String title){
        final String description = "Porque você gostou de \"";
        return description + title + "\"?";
    }
}