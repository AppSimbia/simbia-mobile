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

    private WebView webViewPowerBI3;

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

        webViewPowerBI3 = view.findViewById(R.id.webviewPowerBI3);

        setupWebView();

        return view;
    }

    private void setupWebView() {
        WebSettings webSettings3 = webViewPowerBI3.getSettings();

        webSettings3.setJavaScriptEnabled(true);
        webSettings3.setDomStorageEnabled(true);

        String powerBiUrl3 = "https://app.powerbi.com/view?r=eyJrIjoiZjE1YTY2ZjktNmEwZi00OGQ3LTk4YjUtYjM2MjJjZWE4NzNhIiwidCI6ImIxNDhmMTRjLTIzOTctNDAyYy1hYjZhLTFiNDcxMTE3N2FjMCJ9";


        webViewPowerBI3.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                webViewPowerBI3.setVisibility(View.VISIBLE);
            }
        });

        webViewPowerBI3.loadUrl(powerBiUrl3);
    }
}