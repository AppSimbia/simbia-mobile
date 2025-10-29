package com.germinare.simbia_mobile.ui.features.home.fragments.feed;

import android.content.Intent;
import android.os.Bundle;
import android.service.autofill.FieldClassification;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.germinare.simbia_mobile.R;
import com.germinare.simbia_mobile.data.api.cache.PostgresCache;
import com.germinare.simbia_mobile.data.api.model.mongo.MatchRequest;
import com.germinare.simbia_mobile.data.api.repository.MongoRepository;
import com.germinare.simbia_mobile.databinding.FragmentProductDetailsBinding;
import com.germinare.simbia_mobile.ui.features.home.fragments.feed.activity.SolicitationSent;
import com.germinare.simbia_mobile.ui.features.home.fragments.feed.adapter.FiltersAdapter;
import com.germinare.simbia_mobile.ui.features.home.fragments.feed.adapter.Post;
import com.germinare.simbia_mobile.utils.AlertUtils;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

public class ProductDetailsFragment extends Fragment {

    private FragmentProductDetailsBinding binding;
    private PostgresCache postgresCache;
    private MongoRepository repository;
    private AlertDialog progressDialog;
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
        postgresCache = PostgresCache.getInstance();
        repository = new MongoRepository(error -> {
            AlertUtils.showDialogError(requireContext(), error);
            AlertUtils.hideLoadingDialog(progressDialog);
        });
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
        AlertUtils.DialogAlertBuilder alertBuilder = new AlertUtils.DialogAlertBuilder()
        .setTitle("Solicitar Match")
        .setDescription("Deseja solicitar um match com " + post.getIndustryName())
        .setTextAccept("ENVIAR")
        .setTextCancel("VOLTAR")
        .onAccept(V -> {
            TextInputEditText description = V.findViewById(R.id.ed_solicitation_message);
            MatchRequest request = new MatchRequest(
                    post.getIdPost(),
                    postgresCache.getEmployee().getEmployeeId(),
                    postgresCache.getIndustry().getCnpj(),
                    post.getIndustryCnpj(),
                    description.getText().toString()
            );

            progressDialog = AlertUtils.showLoadingDialog(requireContext(), "Criando Solicitação...");
            repository.createMatch(MatchRequest.createRequest(request), response -> {
                Intent intent = new Intent(requireActivity(), SolicitationSent.class);
                intent.putExtra("industryName", post.getIndustryName());
                AlertUtils.hideLoadingDialog(progressDialog);
                startActivity(intent);
                requireActivity().finish();
            });
        })
        .onCustomViewCreated((view, dialog) -> {
            TextView txName = view.findViewById(R.id.tx_name_industry_solicitation);
            txName.setText(post.getIndustryName());
        });

        AlertUtils.showDialogCustom(
                requireActivity(),
                R.layout.alert_match_solicitation,
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