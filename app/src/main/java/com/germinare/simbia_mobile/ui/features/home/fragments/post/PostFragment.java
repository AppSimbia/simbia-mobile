package com.germinare.simbia_mobile.ui.features.home.fragments.post;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.germinare.simbia_mobile.R;

import java.util.ArrayList;
import java.util.List;


public class PostFragment extends Fragment {

    private Spinner spinnerCategoria;
    private Spinner spinnerUnidade;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public PostFragment() {
        // Required empty public constructor
    }

    public static PostFragment newInstance(String param1, String param2) {
        PostFragment fragment = new PostFragment();
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
        View view = inflater.inflate(R.layout.fragment_post, container, false);

        spinnerCategoria = view.findViewById(R.id.spinner_categoria);
        spinnerUnidade = view.findViewById(R.id.spinner_unidade);

        List<String> categorias = new ArrayList<>();
        categorias.add("Papelão");
        categorias.add("Plástico");
        categorias.add("Vidro");
        categorias.add("Metal");
        categorias.add("Eletrônicos");
        categorias.add("Orgânico");

        List<String> unidades = new ArrayList<>();
        unidades.add("Kg");
        unidades.add("g");
        unidades.add("Tonelada");
        unidades.add("Unidade");
        unidades.add("Litro");
        unidades.add("m³");

        ArrayAdapter<String> categoriaAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                categorias
        );
        categoriaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategoria.setAdapter(categoriaAdapter);

        ArrayAdapter<String> unidadeAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                unidades
        );
        unidadeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUnidade.setAdapter(unidadeAdapter);

        return view;
    }
}
