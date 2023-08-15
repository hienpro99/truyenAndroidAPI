package hienddph20890.fpoly.asg_hiendd.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;

import hienddph20890.fpoly.asg_hiendd.R;

public class introFragment extends Fragment {
    private WebView webView;
    public introFragment() {
        // Required empty public constructor
    }
    public static introFragment newInstance() {
        introFragment fragment = new introFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_intro, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        webView = view.findViewById(R.id.webView);

        // Enable JavaScript (if needed)
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // Load the YouTube video using the iframe code
        String iframeCode = "<iframe width=\"460\" height=\"550\" src=\"https://www.youtube.com/embed/gUl4FvtTy3c\" title=\"YouTube video player\" frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share\" allowfullscreen></iframe>";
        webView.loadData(iframeCode, "text/html", "utf-8");
        Button facebookButton = view.findViewById(R.id.facebookButton);
        facebookButton.findViewById(R.id.facebookButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFacebookPage();
            }
        });
        Button zaloButton = view.findViewById(R.id.zaloButton);
        zaloButton.findViewById(R.id.zaloButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openZaloPage();
            }
        });
    }
    private void openFacebookPage() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/damhien221299"));
        startActivity(intent);
    }

    private void openZaloPage() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://zalo.me/ontbum592"));
        startActivity(intent);
    }
}