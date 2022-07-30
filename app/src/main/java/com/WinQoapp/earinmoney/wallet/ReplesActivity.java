package com.WinQoapp.earinmoney.wallet;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.widget.FrameLayout;

import com.WinQoapp.earinmoney.About.VersionFragment;
import com.example.earinmoney.R;
import com.WinQoapp.earinmoney.quiz.QuizFragment;


public class ReplesActivity extends AppCompatActivity {

    private FrameLayout frameLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_reples );





        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        frameLayout = findViewById(R.id.framelayout);

        int position = getIntent().getIntExtra("position", 0);

        if (position == 1) {
            if (getSupportActionBar() != null)
                getSupportActionBar().setTitle("Withdraw the Phone Recharge");

            fragmentReplacer(new RechargFragment ());
        }

        if (position == 2) {
            if (getSupportActionBar() != null)
                getSupportActionBar().setTitle("Withdraw the Bikash Payment");

            fragmentReplacer(new BikashFragment ());
        }
//
        if (position == 3) {
            if (getSupportActionBar() != null)
                getSupportActionBar().setTitle("Withdraw the Nagad Payment  ");

            fragmentReplacer(new NogadFragment ());
        }

        if (position == 4) {
            if (getSupportActionBar() != null)
                getSupportActionBar().setTitle("Withdraw the Rocket Payment ");

            fragmentReplacer(new RocketFragment ());
        }

        if (position == 5) {
            if (getSupportActionBar() != null)
                getSupportActionBar().setTitle("Version Code ");

            fragmentReplacer(new VersionFragment () );
        }


        if (position == 6) {
            if (getSupportActionBar() != null)
                getSupportActionBar().setTitle("Play the Quiz Now");

            fragmentReplacer(new QuizFragment () );
        }

    }

    public void fragmentReplacer(Fragment fragment) {

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

        fragmentTransaction.replace(frameLayout.getId(), fragment);

        fragmentTransaction.commit();


    }








}