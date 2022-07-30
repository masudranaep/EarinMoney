package com.WinQoapp.earinmoney.quiz;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.WinQoapp.earinmoney.Model.ProfileModel;
import com.example.earinmoney.R;
import com.example.earinmoney.databinding.FragmentQuizBinding;
import com.google.android.gms.ads.AdView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class QuizFragment extends Fragment {

    private TextView coinsTv;

    private AdView adView;
    FirebaseFirestore databse;
    DatabaseReference reference;

    public QuizFragment() {
        // Required empty public constructor
    }

    FragmentQuizBinding binding;



    FirebaseFirestore database;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentQuizBinding.inflate (inflater, container, false);


        database = FirebaseFirestore.getInstance ();

        final ArrayList<CategoryModel> categorys = new ArrayList<> ();
        final CategoryAdapter adapter = new CategoryAdapter ( getContext (), categorys );

        //input category 1 set (1)

        database.collection ( "categoris" )
                .addSnapshotListener ( new EventListener<QuerySnapshot> () {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {


                        categorys.clear ();

                        for (DocumentSnapshot snapshot : value.getDocuments ()){

                            CategoryModel model = snapshot.toObject ( CategoryModel.class );


                            model.setCategoryId ( snapshot.getId () );
                            categorys.add ( model );
                        }
                        adapter.notifyDataSetChanged ();

                    }
                } );
        binding.recyclerView.setLayoutManager ( new GridLayoutManager ( getContext (), 2 ) );

        binding.recyclerView.setAdapter (adapter);




        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        reference = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());


        reference.addValueEventListener ( new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                ProfileModel model = snapshot.getValue (ProfileModel.class);

                 coinsTv.setText ( String.valueOf ( model.getCoins () ) );

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText ( getContext (), "Error:"+ error.getMessage (), Toast.LENGTH_LONG ).show ();

            }
        } );

        return binding.getRoot ();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated ( view, savedInstanceState );

        coinsTv  = (TextView) view.findViewById ( R.id.coins_Tv );
    }
}