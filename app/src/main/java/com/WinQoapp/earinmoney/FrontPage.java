package com.WinQoapp.earinmoney;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.WinQoapp.earinmoney.Email.LoginActivity;
import com.example.earinmoney.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class FrontPage extends AppCompatActivity {


    FirebaseAuth auth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_front_page );




        auth = FirebaseAuth.getInstance ();
        user = auth.getCurrentUser ();

        new Handler ().postDelayed ( new Runnable () {
            @Override
            public void run() {

                if (user != null) {

                    startActivity ( new Intent ( FrontPage.this, MainActivity.class ) );
//                    if(user.isEmailVerified ()){
//                        startActivity ( new Intent ( FrontPage.this, MainActivity.class ) );
//                    }else {
//                        startActivity ( new Intent(FrontPage.this, LoginActivity.class ) );
//                    }

                }else {
                    startActivity ( new Intent (FrontPage.this, LoginActivity.class ) );
                }
                finish ();
            }
        }, 1500 );




    }
}