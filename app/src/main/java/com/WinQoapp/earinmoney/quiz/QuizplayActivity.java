package com.WinQoapp.earinmoney.quiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.WinQoapp.earinmoney.Model.ProfileModel;
import com.example.earinmoney.R;
import com.example.earinmoney.databinding.ActivityQuizplayBinding;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class QuizplayActivity extends AppCompatActivity {


    private TextView coinsTv;

    ActivityQuizplayBinding binding;

    ArrayList<Question> questions;
    Question question;

    CountDownTimer timer;
    private AdView adView;

    FirebaseFirestore databse;
    DatabaseReference reference;

    int correctAnswer = 0;


    int index  = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        binding = ActivityQuizplayBinding.inflate ( getLayoutInflater () );
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


        coinsTv = (TextView)findViewById ( R.id.coins_Tv );

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        reference = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());



        questions = new ArrayList<> ();


        final String catId = getIntent ().getStringExtra ( "catId" );

        Random random = new Random ();
        final int rand = random.nextInt (10);

        databse = FirebaseFirestore.getInstance ();

        databse.collection ( "categoris" )
                .document (catId)
                .collection ( "questions" )
                .whereGreaterThanOrEqualTo ( "index", rand )
                .orderBy ( "index" )
                .limit ( 5 ).get ().addOnSuccessListener ( new OnSuccessListener<QuerySnapshot> () {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                if (queryDocumentSnapshots.getDocuments ().size () < 5){
                databse.collection ( "categoris" )
                        .document (catId)
                        .collection ( "questions" )
                        .whereLessThanOrEqualTo ( "index", rand )
                        .orderBy ( "index" )
                        .limit ( 5 ).get ().addOnSuccessListener ( new OnSuccessListener<QuerySnapshot> () {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {


                        for (DocumentSnapshot snapshot : queryDocumentSnapshots){

                            Question question = snapshot.toObject ( Question.class );
                            questions.add ( question );
                        }
                        setNextQuestion ();
                    }
                } );

            }else {

                    for (DocumentSnapshot snapshot : queryDocumentSnapshots){

                        Question question = snapshot.toObject ( Question.class );
                        questions.add ( question );
                    }
                    setNextQuestion ();
                }
            }
        } );

        // set 1
//        questions.add ( new Question ("what is earth?", "Planet", "Sun", "Human", "Car", "Planet") );
//        questions.add ( new Question ("what is Samosa?", "Food", "Sun", "Human", "Car", "Food") );

        resetTimer ();

        reference.addValueEventListener ( new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ProfileModel model = snapshot.getValue (ProfileModel.class);

                coinsTv.setText(String.valueOf(model.getCoins()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText ( QuizplayActivity.this, "Error"+ error.getMessage (), Toast.LENGTH_LONG ).show ();
                finish ();

            }
        } );

    }

    // set 2
    void setNextQuestion(){


        //time set 8.1
        if (timer != null)

            timer.cancel ();


        timer.start ();

        if (index < questions.size ()){

            //quistion counter 7 set
            binding.quistionConter.setText ( String.format ( "%d/%d", (index+1),questions.size () ) );

            question = questions.get ( index );
            binding.Question.setText ( question.getQuestion () );
            binding.option1.setText ( question.getOption1 () );
            binding.option2.setText ( question.getOption2 () );
            binding.option3.setText ( question.getOption3 () );
            binding.option4.setText ( question.getOption4 () );



        }
    }

    //set timer set 8
    void  resetTimer(){

        timer = new CountDownTimer (30000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                binding.timer.setText ( String.valueOf ( millisUntilFinished/1000 ) );
            }

            @Override
            public void onFinish() {

            }
        };
    }

    //set 4
void checkAnswer(TextView textView){

        String selecteAnswer = textView.getText ().toString ();

        if (selecteAnswer.equals ( question.getAnswer () )){

            updateDataFirebase ();
            correctAnswer++;
            textView.setBackground ( getResources ().getDrawable ( R.drawable.option_right ) );
        }else {
            showanswer ();
            textView.setBackground ( getResources ().getDrawable ( R.drawable.option_wrong ) );
        }

}

// set 5
void  reset(){

        binding.option1.setBackground ( getResources ().getDrawable ( R.drawable.option_unselect ) );
        binding.option2.setBackground ( getResources ().getDrawable ( R.drawable.option_unselect ) );
        binding.option3.setBackground ( getResources ().getDrawable ( R.drawable.option_unselect ) );
        binding.option4.setBackground ( getResources ().getDrawable ( R.drawable.option_unselect ) );

}

//show answer set 6
    void  showanswer(){
        if (question.getAnswer ().equals ( binding.option1.getText ().toString () ))
            binding.option1.setBackground ( getResources ().getDrawable ( R.drawable.option_right ) );
        else if (question.getAnswer ().equals ( binding.option2.getText ().toString () ))
            binding.option2.setBackground ( getResources ().getDrawable ( R.drawable.option_right ) );

        else if (question.getAnswer ().equals ( binding.option3.getText ().toString () ))
            binding.option2.setBackground ( getResources ().getDrawable ( R.drawable.option_right ) );
      else if (question.getAnswer ().equals ( binding.option4.getText ().toString () ))
            binding.option3.setBackground ( getResources ().getDrawable ( R.drawable.option_right ) );

    }

//set 3
    public void onClick(View view){

        switch (view.getId ()){
            case R.id.option1:
            case R.id.option2:
            case R.id.option3:
            case R.id.option4:

                if (timer != null)
                    timer.cancel ();

                TextView selected = (TextView) view;
                checkAnswer ( selected );

                break;

        }
        switch (view.getId ()){

            case R.id.nextBtn:

                reset ();

                if (index < questions.size ()) {
                    index++;
                    setNextQuestion ();
                }else {

                    //currectAnser set 9
                    Intent intent = new Intent (QuizplayActivity.this, ResultActivity.class);
                    intent.putExtra ( "correct", correctAnswer );
                    intent.putExtra ( "total", questions.size () );

                    startActivity ( intent );
                    //Toast.makeText ( this, "Quiz Finished.", Toast.LENGTH_LONG ).show ();
                }
                break;
        }
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
                                Toast.makeText( QuizplayActivity.this, "Coins added successfully", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
        } catch (NumberFormatException e) {
            e.printStackTrace ();
        }

    }

}