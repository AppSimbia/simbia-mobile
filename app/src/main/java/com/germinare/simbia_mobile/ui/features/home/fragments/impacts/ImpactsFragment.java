package com.germinare.simbia_mobile.ui.features.home.fragments.impacts;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.germinare.simbia_mobile.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ImpactsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ImpactsFragment extends Fragment {

    private WebView webViewPowerBI1;
    private WebView webViewPowerBI2;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ImpactsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ImpactsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ImpactsFragment newInstance(String param1, String param2) {
        ImpactsFragment fragment = new ImpactsFragment();
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

        View view = inflater.inflate(R.layout.fragment_impacts, container, false);

        webViewPowerBI1 = view.findViewById(R.id.webviewPowerBI1);
        webViewPowerBI2 = view.findViewById(R.id.webviewPowerBI2);

        setupWebView();

        return view;
    }

    private void setupWebView() {
        WebSettings webSettings1 = webViewPowerBI1.getSettings();
        WebSettings webSettings2 = webViewPowerBI2.getSettings();

        webSettings1.setJavaScriptEnabled(true);
        webSettings2.setJavaScriptEnabled(true);
        webSettings1.setDomStorageEnabled(true);
        webSettings2.setDomStorageEnabled(true);

        String powerBiUrl1 = "https://app.powerbi.com/view?r=eyJrIjoiMzk2ZTg2ODUtNjdjYy00OGJlLWI2ODAtNTUxYzZhNGFkNWRmIiwidCI6ImIxNDhmMTRjLTIzOTctNDAyYy1hYjZhLTFiNDcxMTE3N2FjMCJ9";
        String powerBiUrl2 = "https://app.powerbi.com/view?r=eyJrIjoiZGZkYzVlNTItYmQzYy00ZDZlLWI5YTEtMjFkODZhY2FkZTRlIiwidCI6ImIxNDhmMTRjLTIzOTctNDAyYy1hYjZhLTFiNDcxMTE3N2FjMCJ9";


        webViewPowerBI1.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                webViewPowerBI1.setVisibility(View.VISIBLE);
            }
        });

        webViewPowerBI2.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                webViewPowerBI1.setVisibility(View.VISIBLE);
            }
        });

        webViewPowerBI1.loadUrl(powerBiUrl1);
        webViewPowerBI2.loadUrl(powerBiUrl2);
    }
}