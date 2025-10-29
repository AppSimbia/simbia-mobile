package com.germinare.simbia_mobile.ui.features.home.fragments.feed;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.germinare.simbia_mobile.R;
import com.germinare.simbia_mobile.databinding.FragmentProductDetailsBinding;
import com.germinare.simbia_mobile.ui.features.home.fragments.feed.adapter.FiltersAdapter;
import com.germinare.simbia_mobile.ui.features.home.fragments.feed.adapter.Post;
import com.germinare.simbia_mobile.utils.AlertUtils;

import java.util.List;

public class ProductDetailsFragment extends Fragment {

    private FragmentProductDetailsBinding binding;
    private static final List<String> classficationsLabels = List.of(
            "Perigoso", "Não Perigoso Não Inerte", "Não Perigoso Inerte"
    );

    public ProductDetailsFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProductDetailsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle args = getArguments();

        if (args != null){
            Post post = args.getParcelable("post");

            binding.txNameIndustry.setText(post.getIndustryName());
            binding.txProductTittle.setText(post.getTitle());
            binding.txDescriptionProduct.setText(post.getDescription());
            binding.txPriceProduct.setText(formatterPrice(String.valueOf(post.getPrice())));
            binding.txQuantityProduct.setText(formatterQuantity(String.valueOf(post.getQuantity())));

            FiltersAdapter filtersAdapter = new FiltersAdapter(requireContext(),
                    List.of(post.getCategory(), classficationsLabels.get(Integer.parseInt(post.getClassification())-1)), false);
            binding.rvFiltersProduct.setLayoutManager(new LinearLayoutManager(requireContext(),
                    LinearLayoutManager.HORIZONTAL, false));
            binding.rvFiltersProduct.setAdapter(filtersAdapter);

            Glide.with(binding.imageIndustry.getContext())
                    .load(post.getUrlIndustry())
                    .into(binding.imageIndustry);

            Glide.with(binding.imageProduct.getContext())
                    .load(post.getUrlImage())
                    .into(binding.imageProduct);

            binding.btnSolicitarMatch.setOnClickListener(V -> showDialog(post));
        }
    }

    private void showDialog(Post post){
        AlertUtils.DialogAlertBuilder alertBuilder = new AlertUtils.DialogAlertBuilder();
        alertBuilder.setTitle("Solicitar Match");
        alertBuilder.setDescription("Deseja solicitar um match com " + "Indústria de Lebuddha");
        alertBuilder.setTextAccept("Solicitar");
        alertBuilder.setTextCancel("Cancelar");
        alertBuilder.onAccept(V -> {
            Bundle args = new Bundle();
            args.putParcelable("post", post);

            Navigation.findNavController(requireView()).navigate(R.id.navigation_solicitation_match, args);
            V.dismiss();
        });

        AlertUtils.showDialogDefault(
                requireActivity(),
                alertBuilder
        );
    }

    private String formatterPrice(String price){
        return "R$ " + price + "/Kg";
    }

    private String formatterQuantity(String quantity){
        return "Quantidade Disponível: " + quantity + "Kg";
    }
}