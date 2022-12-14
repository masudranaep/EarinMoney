package com.WinQoapp.earinmoney.Home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.WinQoapp.earinmoney.Model.ProfileModel;
import com.WinQoapp.earinmoney.Sping.SpinItem;
import com.example.earinmoney.R;
import com.WinQoapp.earinmoney.Sping.WheelView;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.RewardedVideoAd;
import com.facebook.ads.RewardedVideoAdListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;


public class LuckySpinFragment extends Fragment {

    private Button playBtn, watchBtn;
    private TextView coinsTv;

    List<SpinItem> spinItemList = new ArrayList<> ();

    WheelView wheelView;
    private FirebaseUser user;

    DatabaseReference reference;

    int currentSpins;

    private RewardedVideoAd rewardedVideoAd;
    private InterstitialAd interstitialAd;


    public LuckySpinFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate ( R.layout.fragment_lucky_spin, container, false );
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //  loadAd();

        init(view);
        loadData();
        spinList();
        clickListener();

    }

    private void init(View view) {

        playBtn = (Button) view.findViewById(R.id.playBtn);
        wheelView = (WheelView) view.findViewById(R.id.wheelView);
        coinsTv = (TextView) view.findViewById(R.id.coins_Tv);
        watchBtn = (Button) view.findViewById(R.id.watchBtn);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference().child("users");


    }

    private void spinList() {

        SpinItem item1 = new SpinItem();
        item1.text = "0";  //You can change according to your need
        item1.color = 0xffFFF3E0; //Change background color
        spinItemList.add(item1);

        SpinItem item2 = new SpinItem();
        item2.text = "5";
        item2.color = 0xffFFE0B2;
        spinItemList.add(item2);

        SpinItem item3 = new SpinItem();
        item3.text = "3";
        item3.color = 0xffFFCC80;
        spinItemList.add(item3);

        SpinItem item4 = new SpinItem();
        item4.text = "8";
        item4.color = 0xffFFF3E0;
        spinItemList.add(item4);

        SpinItem item5 = new SpinItem();
        item5.text = "7";
        item5.color = 0xffFFE0B2;
        spinItemList.add(item5);

        SpinItem item6 = new SpinItem();
        item6.text = "15";
        item6.color = 0xffFFCC80;
        spinItemList.add(item6);

        SpinItem item7 = new SpinItem();
        item7.text = "16";
        item7.color = 0xffFFF3E0;
        spinItemList.add(item7);

        SpinItem item8 = new SpinItem();
        item8.text = "10";
        item8.color = 0xffFFE0B2;
        spinItemList.add(item8);


        SpinItem item9 = new SpinItem();
        item9.text = "12";
        item9.color = 0xffFFCC80;
        spinItemList.add(item9);

        SpinItem item10 = new SpinItem();
        item10.text = "0";
        item10.color = 0xffFFF3E0;
        spinItemList.add(item10);

        SpinItem item11 = new SpinItem();
        item11.text = "18";
        item11.color = 0xffFFE0B2;
        spinItemList.add(item11);

        SpinItem item12 = new SpinItem();
        item12.text = "20";
        item12.color = 0xffFFCC80;
        spinItemList.add(item12);


        wheelView.setData(spinItemList);
        wheelView.setRound(getRandCircleRound());


        wheelView.setLuckyRoundItemSelectedListener(new WheelView.LuckyRoundItemSelectedListener() {
            @Override
            public void LuckyRoundItemSelected(int index) {

                playBtn.setEnabled(true);
                playBtn.setAlpha(1f);

                //wheel stop rotating:: here to show ad
                showAd();


                String value = spinItemList.get(index - 1).text;
                updateDataFirebase(Integer.parseInt(value));


            }
        });

    }

    private void clickListener() {

        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = getRandomIndex();

                if (currentSpins >= 1 && currentSpins < 3) {
                    wheelView.startWheelWithTargetIndex(index);
                    Toast.makeText(getActivity(), "Watch video to get more spins", Toast.LENGTH_SHORT).show();
                    watchBtn.setVisibility(View.VISIBLE);

                }

                if (currentSpins < 1) {
                    playBtn.setEnabled(false);
                    playBtn.setAlpha(.6f);
                    Toast.makeText(getActivity(), "Watch video to get more spins", Toast.LENGTH_SHORT).show();
                    watchBtn.setVisibility(View.VISIBLE);
                } else {
                    playBtn.setEnabled(false);
                    playBtn.setAlpha(.6f);

                    wheelView.startWheelWithTargetIndex(index);

                    watchBtn.setVisibility(View.INVISIBLE);

                }

            }
        });


        watchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showRewardedAds();

            }
        });

    }

    //reawrd view ads
    private void showRewardedAds() {

        if (rewardedVideoAd.isAdLoaded () && rewardedVideoAd.isAdInvalidated ()) {

            rewardedVideoAd.show ();

            rewardedVideoAd.setAdListener ( new RewardedVideoAdListener () {
                @Override
                public void onRewardedVideoCompleted() {

                    int up = currentSpins + 1;
                    HashMap<String, Object> map = new HashMap<> ();

                    map.put ( "spins", up );
                    reference.child ( user.getUid () )
                            .updateChildren ( map );
                }

                @Override
                public void onLoggingImpression(Ad ad) {

                }

                @Override
                public void onRewardedVideoClosed() {

                }

                @Override
                public void onError(Ad ad, AdError adError) {

                }

                @Override
                public void onAdLoaded(Ad ad) {

                }

                @Override
                public void onAdClicked(Ad ad) {

                }
            } );

            rewardedVideoAd.show ();

        }
    }

    private int getRandomIndex() {

        int[] index = new int[]{1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 3, 3, 3, 3, 3, 3, 4, 4, 4, 4, 5, 5, 5, 6, 6, 7, 7, 9, 9, 10, 11, 12};

        int random = new Random ().nextInt(index.length);

        return index[random];
    }

    private int getRandCircleRound() {

        Random random = new Random();

        return random.nextInt(10) + 15;
    }

    private void loadData() {

        reference.child(user.getUid())
                .addValueEventListener(new ValueEventListener () {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        ProfileModel model = snapshot.getValue(ProfileModel.class);
                        coinsTv.setText(String.valueOf(model.getCoins()));

                        currentSpins = model.getSpins();

                        String currentSpin = "Spin The Wheel" + String.valueOf(currentSpins);
                        playBtn.setText(currentSpin);


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();

                        if (getActivity() != null)
                            getActivity().finish();
                    }
                });

    }

    private void updateDataFirebase(int reward) {

        int currentCoins = Integer.parseInt(coinsTv.getText().toString());
        int updatedCoins = currentCoins + reward;

        int updatedSpins = currentSpins - 1;

        HashMap<String, Object> map = new HashMap<>();
        map.put("coins", updatedCoins);
        map.put("spins", updatedSpins);

        reference.child(user.getUid())
                .updateChildren(map)
                .addOnCompleteListener(new OnCompleteListener<Void> () {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Coins added", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Error: " + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });

    }

    private void loadAd() {

        if (getContext() == null)
            return;

        rewardedVideoAd = new RewardedVideoAd (getContext(), getString(R.string.abmod_RewaedVideAd_id));
        rewardedVideoAd.loadAd();


        interstitialAd = new InterstitialAd (getContext(), getString(R.string.abmod_interstital_id));
        interstitialAd.loadAd();


    }

    private void showAd() {

        if (interstitialAd.isAdLoaded()) {
            interstitialAd.show();
        }

    }


}