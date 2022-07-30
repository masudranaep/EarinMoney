package com.WinQoapp.earinmoney.wallet;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.WinQoapp.earinmoney.Model.ProfileModel;
import com.example.earinmoney.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class WalletFragment extends Fragment {


    private LinearLayout phoneRecharge, bikash, nagat, rocket;


    private TextView coinsTv;
    Toolbar toolbar;

    private DatabaseReference reference;
    private FirebaseUser user;


    public WalletFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate ( R.layout.fragment_wallet, container, false );


        phoneRecharge = (LinearLayout) view.findViewById ( R.id.phoneRecharge);
        bikash = (LinearLayout) view.findViewById ( R.id.Bikash );
        nagat = (LinearLayout) view.findViewById ( R.id.NagadNumber );
        rocket = (LinearLayout) view.findViewById ( R.id.rocket );

        coinsTv = (TextView) view.findViewById ( R.id.coins_Tv );

        FirebaseAuth auth = FirebaseAuth.getInstance ();
        user = auth.getCurrentUser ();

        reference = FirebaseDatabase.getInstance ().getReference ().child ( "users" );




        reference.child ( user.getUid () )
                .addValueEventListener ( new ValueEventListener () {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        ProfileModel model = snapshot.getValue (ProfileModel.class);

                        try {
                            coinsTv.setText ( String.valueOf ( model.getCoins () ) );
                        } catch (Exception e) {
                            e.printStackTrace ();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                        Toast.makeText ( getContext (), "Error :" + error.getMessage (), Toast.LENGTH_LONG ).show ();

                    }
                } );

        phoneRecharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getContext (), ReplesActivity.class);
                intent.putExtra("position", 1);
                startActivity(intent);

            }
        });
        bikash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getContext (), ReplesActivity.class);
                intent.putExtra("position", 2);
                startActivity(intent);

            }
        });
        nagat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getContext (), ReplesActivity.class);
                intent.putExtra("position", 3);
                startActivity(intent);

            }
        });
        rocket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getContext (), ReplesActivity.class);
                intent.putExtra("position", 4);
                startActivity(intent);

            }
        });


        return view;
    }
}