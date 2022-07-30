package com.WinQoapp.earinmoney.Share;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.WinQoapp.earinmoney.Model.ProfileModel;
import com.example.earinmoney.R;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class WatchActivity extends AppCompatActivity {

    private InterstitialAd interstitialAd;


private com.facebook.ads.RewardedVideoAd rewardedVideoAd, rewardedVideoAd2;

    private Button watchBtn1, watchBtn2;
    private TextView coinsTv;
    DatabaseReference reference;
    private RewardedAd mRewardedAd;
    private final String TAG = "--->AdMob";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_watch );

        init();

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        reference = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());

        loadData();

        //     loadInterstitialAd();
       loadRewaededAd ();

        clickListener();

    }

    private void clickListener() {

        try {
            watchBtn1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   showRewardedAd ();
                   updateDataFirebase ();
                }
            });

            watchBtn2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   showRewardedAd ();
                   updateDataFirebase ();
                }
            });
        } catch (Exception e) {
            e.printStackTrace ();
        }

    }





    private void init() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle("Watch and Earn");

        watchBtn1 = (Button) findViewById(R.id.watchBtn1);
        watchBtn2 = (Button) findViewById(R.id.watchBtn2);
        coinsTv = (TextView) findViewById(R.id.coins_Tv);

    }

    // rewrded ads
    private void loadRewaededAd() {
        AdRequest adRequest = new AdRequest.Builder().build();

        RewardedAd.load(this, "ca-app-pub-3940256099942544/5224354917",
                adRequest, new RewardedAdLoadCallback () {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error.
                        Log.d(TAG, loadAdError.getMessage());
                        mRewardedAd = null;
                        Log.d(TAG, "onAdFailedToload");
                    }

                    @Override
                    public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                        mRewardedAd = rewardedAd;
                        Log.d(TAG, "Ad was loaded.");

                        mRewardedAd.setFullScreenContentCallback(new FullScreenContentCallback () {
                            @Override
                            public void onAdShowedFullScreenContent() {
                                // Called when ad is shown.
                                Log.d(TAG, "Ad was shown.");
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(AdError adError) {
                                // Called when ad fails to show.
                                Log.d(TAG, "Ad failed to show.");
                            }

                            @Override
                            public void onAdDismissedFullScreenContent() {
                                // Called when ad is dismissed.
                                // Set the ad reference to null so you don't show the ad a second time.
                                Log.d(TAG, "Ad was dismissed.");
                                mRewardedAd = null;
                                loadRewaededAd ();
                            }
                        });
                    }
                });


    }
    private void showRewardedAd() {
        if (mRewardedAd != null) {
            Activity activityContext = this;
            mRewardedAd.show ( activityContext, new OnUserEarnedRewardListener () {
                @Override
                public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                    // Handle the reward.
                    Log.d ( TAG, "The user earned the reward." );
                    int rewardAmount = rewardItem.getAmount ();
                    String rewardType = rewardItem.getType ();

                }
            } );
        } else {
            Log.d ( TAG, "The rewarded ad wasn't ready yet." );
        }

    }



        private void loadData() {

        reference.addValueEventListener(new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                ProfileModel model = snapshot.getValue( ProfileModel.class);
                coinsTv.setText(String.valueOf(model.getCoins()));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(WatchActivity.this, "Error: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        });

    }

    private void updateDataFirebase() {

        try {
            int currentCoins = Integer.parseInt(coinsTv.getText().toString());
            int updatedCoin = currentCoins + 10;

            HashMap<String, Object> map = new HashMap<>();
            map.put("coins", updatedCoin);

            reference.updateChildren(map)
                    .addOnCompleteListener(new OnCompleteListener<Void> () {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {
                                Toast.makeText(WatchActivity.this, "Coins added successfully", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
        } catch (NumberFormatException e) {
            e.printStackTrace ();
        }

    }

}




