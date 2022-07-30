package com.WinQoapp.earinmoney.Share;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.WinQoapp.earinmoney.Model.ProfileModel;
import com.example.earinmoney.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class ShareActivity extends AppCompatActivity {

    private FirebaseUser user;
    private String oppositeUID;

    private TextView referCodeTv;
    private Button shareBtn, redeemBtn;

    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_share );

        init();


        reference = FirebaseDatabase.getInstance().getReference().child("users");

        loadData();
        redeemAvailability();
        clickListener ();
    }




    private  void init(){


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        referCodeTv = findViewById(R.id.referCode_Tv);
        shareBtn = findViewById(R.id.shareBtn);
        redeemBtn = findViewById(R.id.redeemBtn);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    // referCode 1 set (4)
    private void loadData() {

        reference.child ( user.getUid () )
                .addListenerForSingleValueEvent ( new ValueEventListener () {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        String referCode = snapshot.child ( "referCode" ).getValue (String.class);
                        referCodeTv.setText ( referCode );

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                        Toast.makeText ( ShareActivity.this, "Error : "+ error.getMessage (), Toast.LENGTH_LONG ).show ();
                        finish ();
                    }
                } );

    }

    private void clickListener(){
        // referCode 2 set (4)
        shareBtn.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {


                String referCode = referCodeTv.getText ().toString ();

                String shareBody = "Hey, i'm using the best earning app. Join using my invite code to instantly get 100"
                        + " coins. My invite code is  " + referCode + "\n" +
                        "Download from Play Store\n" +
                        "https://play.google.com/store/apps/details?id=" +
                        getPackageName();


                Intent intent = new Intent (Intent.ACTION_SEND);
                intent.putExtra ( Intent.EXTRA_TEXT, shareBody );
                intent.setType ( "text/plain" );
                startActivity ( intent );

            }
        } );

        // referCode 3 set (4)

        redeemBtn.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {

                EditText editText  = new EditText ( ShareActivity.this );
                editText.setHint ( "123try" );


                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);

                editText.setLayoutParams ( layoutParams );

                AlertDialog.Builder alerDialog = new AlertDialog.Builder ( ShareActivity.this );

                alerDialog.setTitle ( "Redeem Code " );

                alerDialog.setView ( editText );


                alerDialog.setPositiveButton ( "Redeem", new DialogInterface.OnClickListener () {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //refer code share
                        String inputCode = null;
                        try {
                            inputCode = editText.getText ().toString ();
                        } catch (Exception e) {
                            e.printStackTrace ();
                        }

                        if (TextUtils.isEmpty ( inputCode )){

                            Toast.makeText ( ShareActivity.this, "Input valid code", Toast.LENGTH_LONG ).show ();
                            return;
                        }
                        if (inputCode.equals ( referCodeTv.getText ().toString () )){
                            Toast.makeText ( ShareActivity.this, "You can not input your own code", Toast.LENGTH_LONG ).show ();
                            return;
                        }

                        redeemQuery(inputCode,dialog);

                    }
                } ).setNegativeButton ( "No", new DialogInterface.OnClickListener () {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                } );
                alerDialog.show ();

            }
        } );
    }

    //share referCode 4 set (4)
    private void redeemQuery(String inputCode, final DialogInterface dialog) {

        Query query = reference.orderByChild ( "referCode" ).equalTo ( inputCode );

        query.addListenerForSingleValueEvent ( new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren ()){

                    oppositeUID = dataSnapshot.getKey ();

                    reference
                            .addListenerForSingleValueEvent ( new ValueEventListener () {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                    ProfileModel model = snapshot.getValue (ProfileModel.class);
                                    ProfileModel myModel = snapshot.child ( user.getUid () ).getValue (ProfileModel.class);
                                    int coins = model.getCoins ();
                                    int updateCoins = coins + 100;
                                    int myCoins = myModel.getCoins ();


                                    int myUpdate = myCoins + 100;


                                    HashMap<String, Object> map = new HashMap<> ();
                                    map.put ( "coins", updateCoins );


                                    HashMap<String, Object> myMap = new HashMap<> ();
                                    myMap.put ( "coins", myUpdate );
                                    myMap.put ( "redeemed", true );


                                    reference.child ( oppositeUID ).updateChildren ( map );
                                    reference.child ( user.getUid () ).updateChildren ( myMap ).addOnCompleteListener ( new OnCompleteListener<Void> () {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            dialog.dismiss ();
                                            Toast.makeText ( ShareActivity.this, "Congras!", Toast.LENGTH_LONG ).show ();
                                        }
                                    } );

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            } );
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText ( ShareActivity.this, "Error :" + error.getMessage (), Toast.LENGTH_LONG ).show ();

            }
        } );

    }
    private void redeemAvailability() {

        reference.child ( user.getUid () )
                .addValueEventListener ( new ValueEventListener () {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.exists () && snapshot.hasChild ( "redeemed" )) {
                            boolean isAvailable = snapshot.child ( "redeemed" ).getValue ( Boolean.class );

                            if (isAvailable) {
                                redeemBtn.setVisibility ( View.GONE );
                                redeemBtn.setEnabled ( false );
                            } else {
                                redeemBtn.setEnabled ( true );
                                redeemBtn.setVisibility ( View.VISIBLE );
                            }

                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                } );
    }

}