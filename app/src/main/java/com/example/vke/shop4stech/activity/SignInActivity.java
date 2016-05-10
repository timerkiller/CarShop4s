package com.example.vke.shop4stech.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;

import com.example.vke.shop4stech.R;
import com.example.vke.shop4stech.fragment.SignInFragment;
import com.example.vke.shop4stech.helper.PreferencesHelper;

public class SignInActivity extends FragmentActivity
        implements SignInFragment.OnFragmentInteractionListener{

    public static void start(Activity activity, Boolean edit) {
        Intent starter = new Intent(activity, SignInActivity.class);
        //noinspection unchecked
        ActivityCompat.startActivity(activity,
                starter,
                ActivityOptionsCompat.makeSceneTransitionAnimation(activity).toBundle());
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        String username = "vic";
        String password = "123456";
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.sign_in_container, SignInFragment.newInstance(false)).commit();
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (PreferencesHelper.isSignedIn(this)) {
            finish();
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}