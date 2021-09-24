package com.example.andmoduleads;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.ads.control.Admod;
import com.ads.control.funtion.AdCallback;
import com.example.andmoduleads.fragment.BlankFragment;
import com.example.andmoduleads.fragment.BlankFragment2;
import com.google.android.gms.ads.interstitial.InterstitialAd;

import java.lang.reflect.Constructor;
import java.util.List;

public class ContentActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);


        showFragment(new BlankFragment(),"BlankFragment");
    }


    public void showFragment(Fragment fragment,String tag) {
        try {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            Fragment fragment1 = getSupportFragmentManager().findFragmentByTag(tag);
            if (fragment1 != null && fragment1.isAdded()) { // if the fragment is already in container
                ft.show(fragment1);
            } else { // fragment needs to be added to frame container
                ft.add(R.id.flMain, fragment, tag);
//                ft.addToBackStack(tag);
            }
            List<Fragment> fragments = getSupportFragmentManager().getFragments();
            if (fragments.size() > 0) {
                for (Fragment frag : fragments) {
                    if (frag != fragment1) {
                        if (frag.isAdded())
                            ft.hide(frag);
                    }
                }
            }
            ft.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}