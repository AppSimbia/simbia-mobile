package com.germinare.simbia_mobile.ui.features.home.fragments.eva;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.germinare.simbia_mobile.R;
import com.germinare.simbia_mobile.databinding.FragmentEvaBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EvaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EvaFragment extends Fragment {

    private FragmentEvaBinding binding;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public EvaFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EvaFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EvaFragment newInstance(String param1, String param2) {
        EvaFragment fragment = new EvaFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentEvaBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.imageView6.setOnClickListener(V -> {
            final String messageContent = binding.etChatMessage.getText().toString();

            if (!messageContent.isEmpty()) {
                Bundle envelope = new Bundle();
                envelope.putString("message", messageContent);

                Navigation.findNavController(view).navigate(R.id.navigation_eva_messages, envelope);
            }
        });

    }
}