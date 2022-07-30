package com.WinQoapp.earinmoney.quiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.WinQoapp.earinmoney.Model.ProfileModel;
import com.example.earinmoney.R;
import com.example.earinmoney.databinding.ActivityResultBinding;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ResultActivity extends AppCompatActivity {

    ActivityResultBinding binding;

    private DatabaseReference reference;
    private FirebaseUser user;

    private TextView coinsTv;
    private Button restartBtn;

    private AdView adView;

    int POINTS = 10;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        binding = ActivityResultBinding.inflate ( getLayoutInflater () );
        setContentView ( binding.getRoot ());


        MobileAds.initialize(this, new OnInitializationCompleteListener () {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {



            }
        });

        //banner Ads

        adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);


        coinsTv = (TextView) findViewById ( R.id.coins_Tv );
        restartBtn = (Button) findViewById ( R.id.restartBtn );

        // result set 1
        int correctAnswers = getIntent ().getIntExtra ( "correct", 0 );
        int totalQuestions = getIntent ().getIntExtra ( "total", 0 );

        long points = correctAnswers * POINTS;




        //rank 1 set
         user = FirebaseAuth.getInstance ().getCurrentUser ();
         reference = FirebaseDatabase.getInstance ().getReference ();


        binding.score.setText ( String.format ( "%d/%d", correctAnswers, totalQuestions ) );
        binding.earnCoins.setText ( String.valueOf ( points ));

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        reference = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());


        reference.addValueEventListener ( new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ProfileModel model = snapshot.getValue (ProfileModel.class);

                coinsTv.setText(String.valueOf(model.getCoins()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText ( ResultActivity.this, "Error"+ error.getMessage (), Toast.LENGTH_LONG ).show ();
                finish ();

            }
        } );

        //rang 2 set


        restartBtn.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent (ResultActivity.this, QuizFragment.class);
                startActivity ( intent);
            }
        } );

    }


}