package com.WinQoapp.earinmoney;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;


import com.WinQoapp.earinmoney.Home.LuckspinActivity;
import com.example.earinmoney.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private NavController navController;


    private FloatingActionButton spinBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_main );



            bottomNavigationView = (BottomNavigationView) findViewById ( R.id.bottnNavigationView);
            navController = Navigation.findNavController ( this, R.id.frame_layout );


            NavigationUI.setupWithNavController ( bottomNavigationView, navController );

       spinBtn = (FloatingActionButton)findViewById ( R.id.fab );


       spinBtn.setOnClickListener ( new View.OnClickListener () {
           @Override
           public void onClick(View v) {
               Intent intent = new Intent (MainActivity.this, LuckspinActivity.class );
               startActivity ( intent );

           }
       } );


    }
}