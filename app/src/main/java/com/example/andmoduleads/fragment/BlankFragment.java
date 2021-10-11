package com.example.andmoduleads.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.ads.control.Admod;
import com.ads.control.funtion.AdCallback;
import com.example.andmoduleads.ContentActivity;
import com.example.andmoduleads.R;
import com.google.android.gms.ads.interstitial.InterstitialAd;


public class BlankFragment extends Fragment {
    InterstitialAd mInterstitialAd;
    Button button;
    View view;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_blank, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        button =    view.findViewById(R.id.btnNextFragment);
        button.setEnabled(false);
        Admod.getInstance().getInterstitalAds(getContext(), getString(R.string.admod_interstitial_id), new AdCallback() {
            @Override
            public void onInterstitialLoad(InterstitialAd interstitialAd) {
                super.onInterstitialLoad(interstitialAd);
                mInterstitialAd = interstitialAd;
//                Toast.makeText(getContext(), "ad loaded", Toast.LENGTH_SHORT).show();
                button.setEnabled(true);
            }
        });

        button.setOnClickListener(v -> {
            Admod.getInstance().forceShowInterstitial(getActivity(), mInterstitialAd, new AdCallback() {
                @Override
                public void onAdClosed() {
                    ((ContentActivity)getActivity()).showFragment(new InlineBannerFragment(),"BlankFragment2");
                }
            });
        });

        Admod.getInstance().loadNativeFragment(getActivity(),getString(R.string.admod_native_id),view);
    }
}