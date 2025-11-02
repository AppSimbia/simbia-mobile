package com.germinare.simbia_mobile.ui.features.home.fragments.eva;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.germinare.simbia_mobile.R;
import com.germinare.simbia_mobile.databinding.FragmentEvaBinding;

public class EvaFragment extends Fragment {

    private FragmentEvaBinding binding;
    private EvaViewModel evaViewModel;

    public EvaFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentEvaBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 🔹 Obtém o mesmo ViewModel compartilhado com EvaMessagesFragment
        evaViewModel = new ViewModelProvider(requireActivity()).get(EvaViewModel.class);

        // 🔹 Limpa o histórico de mensagens quando o usuário volta para a tela inicial da Eva
        evaViewModel.clearMessages();

        // --- ENVIO MANUAL PELO INPUT ---
        binding.imageView6.setOnClickListener(v -> {
            final String messageContent = binding.etChatMessage.getText().toString().trim();

            if (!messageContent.isEmpty()) {
                Bundle envelope = new Bundle();
                envelope.putString("message", messageContent);

                // limpa o campo de input
                binding.etChatMessage.setText("");

                Navigation.findNavController(view)
                        .navigate(R.id.navigation_eva_messages, envelope);
            }
        });

        // --- BOTÕES DE ATALHO ---

        // 1. Guia Legal
        binding.btnGuia.setOnClickListener(v -> {
            Bundle envelope = new Bundle();
            envelope.putString("message", "O que é o Guia Legal?");
            Navigation.findNavController(view).navigate(R.id.navigation_eva_messages, envelope);
        });

        // 2. Recomendar Melhorias
        binding.btnPergunta.setOnClickListener(v -> {
            Bundle envelope = new Bundle();
            envelope.putString("message", "Mande melhorias para minha indústria.");
            Navigation.findNavController(view).navigate(R.id.navigation_eva_messages, envelope);
        });

        // 3. Painel de Impacto
        binding.btnPainel.setOnClickListener(v -> {
            Bundle envelope = new Bundle();
            envelope.putString("message", "O que é o Painel de Impacto do Simbia?");
            Navigation.findNavController(view).navigate(R.id.navigation_eva_messages, envelope);
        });
    }
}
