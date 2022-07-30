package com.WinQoapp.earinmoney.Email;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.earinmoney.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class RegistaerActivity extends AppCompatActivity {


    private EditText nameEdit, emailEdit, passwordEdit,confromPassEdit;
    private ProgressBar progressBar;

    private Button registerBtn;
    private TextView loginTv;

    private FirebaseAuth auth;
    private  String deviceID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_registaer );

        auth = FirebaseAuth.getInstance ();

        deviceID = Settings.Secure.getString (getContentResolver (), Settings.Secure.ANDROID_ID );


        init();

        clickListener();



    }
    //register 1 set (1)

    private  void init(){
        registerBtn = findViewById ( R.id.registerBtn );
        nameEdit = (EditText) findViewById ( R.id.nameET );
        emailEdit = (EditText) findViewById ( R.id.emailET );
        passwordEdit = findViewById ( R.id.passwordET );
        confromPassEdit = findViewById ( R.id.confimPassET );
        progressBar = findViewById ( R.id.progressbar );
        loginTv = findViewById ( R.id.login_tv );

    }



    //registerBtn 2 set (1)

    private void clickListener() {

        loginTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegistaerActivity.this, LoginActivity.class));
                finish();
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = nameEdit.getText().toString();
                String email = emailEdit.getText().toString();
                String pass = passwordEdit.getText().toString();
                String confirmPass = confromPassEdit.getText().toString();

                if (name.isEmpty()) {
                    nameEdit.setError("Required");
                    return;
                }

                if (email.isEmpty()) {
                    emailEdit.setError("Required");
                    return;
                }

                if (pass.isEmpty()) {
                    passwordEdit.setError("Required");
                    return;
                }

                if (confirmPass.isEmpty() || !pass.equals(confirmPass)) {
                    confromPassEdit.setError("Invalid Password");
                    return;
                }


                createAccount(email, pass);
              //  queryAccountExistence(email, pass);

            }
        });

    }

    private void createAccount(final String email, String password) {

        auth.createUserWithEmailAndPassword ( email, password )
                .addOnCompleteListener ( new OnCompleteListener<AuthResult> () {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {


                        if (task.isSuccessful ()){

                            FirebaseUser user = auth.getCurrentUser ();

                            assert user != null;
                            updateUi(user, email);

                        }else {
                            progressBar.setVisibility ( View.GONE );
                            //registation
                            Toast.makeText ( RegistaerActivity.this, "Error"+task.getException ().getMessage (), Toast.LENGTH_LONG ).show ();

                        }
                    }

                } );

    }




    private void updateUi(FirebaseUser user, String email) {

        String refer = email.substring(0, email.lastIndexOf("@"));
        String referCode = refer.replace(".", "");

        //identify that this user already sign up

        HashMap<String, Object> map = new HashMap<>();
        map.put("name", nameEdit.getText().toString());
        map.put("email", email);
        map.put("uid", user.getUid());
        map.put("image", " ");
        map.put("coins", 1000);
        map.put("referCode", referCode);
        map.put("spins", 2);
        map.put("deviceID", deviceID);
        map.put("redeemed", false);
        map.put ( "quiz", "" );

        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, -1); // to get yesterday date
        Date previousDate = calendar.getTime();

        String dateString = dateFormat.format(previousDate);

        FirebaseDatabase.getInstance().getReference().child("Daily Check")
                .child(user.getUid())
                .child("date")
                .setValue(dateString);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users");

        reference.child(user.getUid())
                .setValue(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {

                            Toast.makeText(RegistaerActivity.this, "WelCome hear",
                                    Toast.LENGTH_SHORT).show();

                            startActivity(new Intent(RegistaerActivity.this, LoginActivity.class));

                            finish();

                        } else {
                            Toast.makeText(RegistaerActivity.this, "Error: " +
                                            task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }

                        progressBar.setVisibility(View.GONE);

                    }
                });


    }



//    private void createAccount(final String email, String password) {
//
//        progressBar.setVisibility(View.VISIBLE);
//
//        auth.createUserWithEmailAndPassword(email, password)
//                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//
//                        if (task.isSuccessful()) {
//
//                            //Registration success:
//                            final FirebaseUser user = auth.getCurrentUser();
//                            assert user != null;
//
//
//                            //send email verification link
//                            auth.getCurrentUser().sendEmailVerification()
//                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                        @Override
//                                        public void onComplete(@NonNull Task<Void> task) {
//
//                                            if (task.isSuccessful()) {
//
//                                                updateUi(user, email);
//
//                                            } else {
//                                                Toast.makeText(RegistaerActivity.this, "Error: "
//                                                                + task.getException().getMessage(),
//                                                        Toast.LENGTH_SHORT).show();
//                                            }
//
//                                        }
//                                    });
//
//                        } else {
//
//                            progressBar.setVisibility(View.GONE);
//                            //Registration failed:
//                            Toast.makeText(RegistaerActivity.this, "Error: " +
//                                    task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//
//                        }
//
//                    }
//                });
//
//    }



//
//    private void queryAccountExistence(final String email,final String pass) {
//
//        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users");
//
//        Query query = ref.orderByChild("deviceID").equalTo(deviceID);
//
//        query.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//
//                if (snapshot.exists()) {
//                    //device already registered
//                    Toast.makeText(RegistaerActivity.this,
//                            "This device is already registered on another email, please login",
//                            Toast.LENGTH_SHORT).show();
//
//                } else {
//                    //device id not found
//                   // createAccount(email, pass);
//
//                }
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//
//    }



}