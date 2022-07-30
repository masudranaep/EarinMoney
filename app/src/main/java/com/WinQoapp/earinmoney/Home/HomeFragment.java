package com.WinQoapp.earinmoney.Home;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.icu.text.SimpleDateFormat;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.WinQoapp.earinmoney.Model.Internet;
import com.WinQoapp.earinmoney.Model.ProfileModel;
import com.WinQoapp.earinmoney.wallet.ReplesActivity;
import com.bumptech.glide.Glide;
import com.example.earinmoney.R;
import com.WinQoapp.earinmoney.Share.ShareActivity;


import com.WinQoapp.earinmoney.Share.WatchActivity;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;


public class HomeFragment extends Fragment {


    private AdLoader adLoader;
    private AdView adView;
    private RewardedAd mRewardedAd;
    private final String TAG = "--->AdMob";
    private InterstitialAd mInterstitialAd;

    private CardView shareCard, dailyCheck, watchCard, inviteCard, aboutCard, quizCardView;

    private FloatingActionButton spinBtn;

    private CircleImageView profileImage;
    private TextView nameTv, emailTv, coinsTv;

    Toolbar toolbar;

    private DatabaseReference reference;
    private FirebaseUser user;

    private Dialog dialog;

    Internet internet;


    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate ( R.layout.fragment_home, container, false );

        MobileAds.initialize ( getContext (), new OnInitializationCompleteListener () {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                loadRewaededAd ();
            }
        } );


        shareCard = (CardView) view.findViewById ( R.id.cardshare_Tv );
        dailyCheck = (CardView) view.findViewById ( R.id.dailyCard );
        watchCard = (CardView) view.findViewById ( R.id.watchCard );
        inviteCard = (CardView) view.findViewById ( R.id.inviteCard );
        aboutCard = (CardView) view.findViewById ( R.id.aboutCard );
        quizCardView = (CardView) view.findViewById ( R.id.quizCardView );


        coinsTv = (TextView) view.findViewById ( R.id.coins_Tv );
        nameTv = (TextView) view.findViewById ( R.id.name_Tv );
        emailTv = (TextView) view.findViewById ( R.id.email_Tv );

        profileImage = (CircleImageView) view.findViewById ( R.id.profileImage );


        internet = new Internet ( getContext () );
        checkInternetConnection ();

        // getDataFromdatabse();


        FirebaseAuth auth = FirebaseAuth.getInstance ();
        user = auth.getCurrentUser ();

        reference = FirebaseDatabase.getInstance ().getReference ().child ( "users" );

        dialog = new Dialog ( getContext () );
        dialog.setContentView ( R.layout.loading_dialog );

        if (dialog.getWindow () != null)

            dialog.getWindow ().setBackgroundDrawable ( new ColorDrawable ( Color.TRANSPARENT ) );

        dialog.setCancelable ( false );


        shareCard.setOnClickListener ( new View.OnClickListener () {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                startActivity ( new Intent ( getContext (), ShareActivity.class ) );

            }
        } );


        dailyCheck.setOnClickListener ( new View.OnClickListener () {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {

                dailyCheck ();

                showRewardedAd ();

            }
        } );


        watchCard.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent ( getContext (), WatchActivity.class );
                startActivity ( intent );
            }
        } );

        aboutCard.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent ( getContext (), ReplesActivity.class );
                intent.putExtra ( "position", 5 );
                startActivity ( intent );

            }
        } );

        inviteCard.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent ( getContext (), ShareActivity.class );
                startActivity ( intent );
            }
        } );


        quizCardView.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent ( getContext (), ReplesActivity.class );
                intent.putExtra ( "position", 6 );
                startActivity ( intent );

            }
        } );


        reference.child ( user.getUid () )
                .addValueEventListener ( new ValueEventListener () {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        ProfileModel model = snapshot.getValue ( ProfileModel.class );

                        try {
                            nameTv.setText ( model.getName () );
                            emailTv.setText ( model.getEmail () );
                            coinsTv.setText ( String.valueOf ( model.getCoins () ) );
                        } catch (Exception e) {
                            e.printStackTrace ();
                        }


                        try {
                            Glide.with ( getContext () )
                                    .load ( model.getImage () )
                                    .timeout ( 6000 )
                                    .placeholder ( R.drawable.quizphoto )
                                    .into ( profileImage );
                        } catch (Exception e) {
                            e.printStackTrace ();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                        Toast.makeText ( getContext (), "Error" + error.getMessage (), Toast.LENGTH_LONG ).show ();

                    }
                } );


        return view;


    }




    // rewrded ads
    private void loadRewaededAd() {
        AdRequest adRequest = new AdRequest.Builder ().build ();

        RewardedAd.load ( getContext (), "ca-app-pub-3940256099942544/5224354917",
                adRequest, new RewardedAdLoadCallback () {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error.
                        Log.d ( TAG, loadAdError.getMessage () );
                        mRewardedAd = null;
                        Log.d ( TAG, "onAdFailedToload" );
                    }

                    @Override
                    public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                        mRewardedAd = rewardedAd;
                        Log.d ( TAG, "Ad was loaded." );

                        mRewardedAd.setFullScreenContentCallback ( new FullScreenContentCallback () {
                            @Override
                            public void onAdShowedFullScreenContent() {
                                // Called when ad is shown.
                                Log.d ( TAG, "Ad was shown." );
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(AdError adError) {
                                // Called when ad fails to show.
                                Log.d ( TAG, "Ad failed to show." );
                            }

                            @Override
                            public void onAdDismissedFullScreenContent() {
                                // Called when ad is dismissed.
                                // Set the ad reference to null so you don't show the ad a second time.
                                Log.d ( TAG, "Ad was dismissed." );
                                mRewardedAd = null;
                                loadRewaededAd ();
                            }
                        } );
                    }
                } );


    }
    private void showRewardedAd() {
        if (mRewardedAd != null) {
            Activity activityContext = getActivity ();
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
        // internet check  2 set (20) <- internent
    private void checkInternetConnection(){

        if(internet.isConnected ()){
            new isInternetActive ().execute (  );
        }else {
            Toast.makeText ( getContext (), "Please check your internet", Toast.LENGTH_LONG ).show ();

        }
    }



//daily chack (1)
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void dailyCheck() {

        if (internet.isConnected ()) {


            final SweetAlertDialog sweetAlertDialog = new SweetAlertDialog ( getContext (), SweetAlertDialog.PROGRESS_TYPE );
            sweetAlertDialog.setTitleText ( "Please Wait..." );
            sweetAlertDialog.setCancelable ( false );
            sweetAlertDialog.show ();


            final Date currentDate = Calendar.getInstance ().getTime ();

            final SimpleDateFormat dateFormat = new SimpleDateFormat ( "dd/MM/yyyy", Locale.ENGLISH );

            final String date = dateFormat.format ( currentDate );

            DatabaseReference reference = FirebaseDatabase.getInstance ().getReference ();

            reference.child ( "Daily Check" ).child ( user.getUid () )
                    .addListenerForSingleValueEvent ( new ValueEventListener () {
                        @RequiresApi(api = Build.VERSION_CODES.N)
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            if (snapshot.exists ()) {

                                String dbDateString = snapshot.child ( "date" ).getValue ( String.class );

                                try {
                                    assert dbDateString != null;
                                    Date dbDate = dateFormat.parse ( dbDateString );

                                    String xDate = dateFormat.format ( currentDate );
                                    Date date = dateFormat.parse ( xDate );

                                    if (date.after ( dbDate ) && date.compareTo ( dbDate ) != 0) {

                                        //reward ads
                                        reference.child ( "users" ).child ( user.getUid () )

                                                .addListenerForSingleValueEvent ( new ValueEventListener () {
                                                    @RequiresApi(api = Build.VERSION_CODES.N)
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                        ProfileModel model = snapshot.getValue ( ProfileModel.class );

                                                        int currentCoins = model.getCoins ();

                                                        int update = currentCoins + 1000;
                                                        int spinC = model.getSpins ();
                                                        int updatedSpins = spinC + 3;

                                                        HashMap<String, Object> map = new HashMap<> ();
                                                        map.put ( "coins", update );
                                                        map.put ( "spin", updatedSpins );

                                                        reference.child ( "users" ).child ( user.getUid () )
                                                                .updateChildren ( map );

                                                        //dailycheck firebase 2 set (14)

                                                        reference.child ( "users" ).child ( user.getUid () )
                                                                .updateChildren ( map );


                                                        Date newDate = Calendar.getInstance ().getTime ();
                                                        String newDateString = dateFormat.format ( newDate );

                                                        HashMap<String, String> dateMap = new HashMap<> ();
                                                        dateMap.put ( "date", newDateString );

                                                        reference.child ( "Daily Check" ).child ( user.getUid () )
                                                                .setValue ( dateMap )
                                                                .addOnCompleteListener ( new OnCompleteListener<Void> () {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {


                                                                        sweetAlertDialog.changeAlertType ( SweetAlertDialog.SUCCESS_TYPE );
                                                                        sweetAlertDialog.setTitleText ( "Success" );
                                                                        sweetAlertDialog.setContentText ( "Coins added to your acceount successfully" );
                                                                        sweetAlertDialog.setConfirmButton ( "Dismiss", new SweetAlertDialog.OnSweetClickListener () {
                                                                            @Override
                                                                            public void onClick(SweetAlertDialog sweetAlertDialog) {

                                                                                sweetAlertDialog.dismissWithAnimation ();

                                                                            }
                                                                        } ).show ();
                                                                    }
                                                                } );

                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {
                                                        Toast.makeText ( getContext (), "Error" + error.getMessage (),
                                                                Toast.LENGTH_LONG ).show ();

                                                    }
                                                } );

                                    } else {
                                        sweetAlertDialog.changeAlertType ( SweetAlertDialog.ERROR_TYPE );
                                        sweetAlertDialog.setTitleText ( "Failed" );
                                        sweetAlertDialog.setContentText ( "You have already rewarded, come back tomorrow" );
                                        sweetAlertDialog.setConfirmButton ( "Dismiss", null );
                                        sweetAlertDialog.show ();
                                    }

                                } catch (ParseException e) {
                                    e.printStackTrace ();
                                    sweetAlertDialog.dismissWithAnimation ();
                                }
                            } else {
                                sweetAlertDialog.changeAlertType ( SweetAlertDialog.WARNING_TYPE );
                                sweetAlertDialog.setTitleText ( "System Busy" );
                                sweetAlertDialog.setContentText ( "System is busy, please try again later" );
                                sweetAlertDialog.setConfirmButton ( "Dismiss", new SweetAlertDialog.OnSweetClickListener () {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {

                                        sweetAlertDialog.dismissWithAnimation ();
                                    }
                                } );
                                sweetAlertDialog.show ();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText ( getContext (), "Error" + error.getMessage (),
                                    Toast.LENGTH_LONG ).show ();
                            sweetAlertDialog.dismissWithAnimation ();

                        }
                    } );
        }else {
            Toast.makeText ( getContext (), "Please check your internet", Toast.LENGTH_LONG ).show ();
        }


    }
    // internet check  1 set (20) <- internent
    class isInternetActive extends AsyncTask<Void, Void, String> {


        @Override
        protected String doInBackground(Void... voids) {

            InputStream inputStream = null;
            String json = "";

            try {
                //icons/martz90/circle/256/android-icon.png
                //url contain small android icon to check internet

                String strURL = "https://icons.iconarchive.com/";
                URL url = new URL ( strURL );

                URLConnection urlConnection  = url.openConnection ();
                urlConnection.setDoOutput ( true );
                inputStream = urlConnection.getInputStream ();
                json = "success";


            }catch (Exception e){

                e.printStackTrace ();
                json = "failed";
            }
            return json;
        }

        @Override
        protected void onPostExecute(String s) {
            //super.onPostExecute ( s );

            if (s != null){

                if(s.equals ( "success" )){

                    Toast.makeText ( getContext (), "Internet Connecet", Toast.LENGTH_LONG ).show ();


                }else {
                    Toast.makeText ( getContext (), "No Internet ", Toast.LENGTH_LONG ).show ();


                }


            }else {

                Toast.makeText (getContext (), "No Internet", Toast.LENGTH_LONG ).show ();
            }
        }

        @Override
        protected void onPreExecute() {
            Toast.makeText ( getContext (), "Validating Internet", Toast.LENGTH_LONG ).show ();


            super.onPreExecute ();
        }
    }

    }


