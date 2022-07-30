package com.WinQoapp.earinmoney.wallet;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
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
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;


public class RechargFragment extends Fragment {

    private EditText nameEt, phoneEt;
    private TextView coinsTv;
    private Button sendBtn;
    private RadioGroup radioGroup;

    private Dialog dialog;
    private DatabaseReference reference;

    private FirebaseUser user;


    public RechargFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate ( R.layout.fragment_recharg, container, false );


        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated ( view, savedInstanceState );

        init ( view );

        loadData ();

        clickListener();
    }

    // amazonWith draw system 1 set (17) <- RedeemActvity





    private void init(View view) {
        radioGroup = (RadioGroup) view.findViewById ( R.id.radioGroup );
        sendBtn = (Button) view.findViewById ( R.id.submiBtn );
        coinsTv = (TextView) view.findViewById ( R.id.coins_Tv );

        nameEt = (EditText)view.findViewById ( R.id.name_Et);
        phoneEt = (EditText) view.findViewById ( R.id.number_Et );

        dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.loading_dialog);
        if (dialog.getWindow() != null)
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);

        reference = FirebaseDatabase.getInstance().getReference().child("users");

        FirebaseAuth auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

    }

    private void loadData() {

        reference.child(user.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        ProfileModel model = snapshot.getValue(ProfileModel.class);
                        coinsTv.setText(String.valueOf(model.getCoins()));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        getActivity().finish();
                    }
                });

    }

    private void clickListener() {

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = nameEt.getText().toString();
                String phone = phoneEt.getText().toString();

                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(phone)) {
                    Toast.makeText(getContext(), "Fill all the fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                int id = radioGroup.getCheckedRadioButtonId();

                switch (id) {

                    case R.id.phone25:
                        checkCoin(name, phone, 20);
                        break;

                    case R.id.phone50:
                        checkCoin(name, phone, 30);
                        break;
                }


            }
        });

    }

    DatabaseReference withdrawRef;

    private void checkCoin(String name, String phone, int amount){

        int current = Integer.parseInt(coinsTv.getText().toString());

        if (amount == 20) {


            if (current < 20000) {
                Toast.makeText(getContext(), "You do not have enough coins", Toast.LENGTH_SHORT).show();
                return;
            }
            sendWithdrawRequest(name, phone, amount);
        }

        if (amount == 30) {


            if (current < 30000) {
                Toast.makeText(getContext(), "You do not have enough coins", Toast.LENGTH_SHORT).show();
                return;
            }
            sendWithdrawRequest(name, phone, amount);


        }


    }

    private void sendWithdrawRequest(String name, String phone, int amount) {

        withdrawRef = FirebaseDatabase.getInstance().getReference().child("Withdraw").child(user.getUid());

        String id = withdrawRef.push().getKey();

        HashMap<String, Object> map = new HashMap<>();

        if (amount == 20) {
            map.put("amount", 20);
        }
        if (amount == 30) {
            map.put("amount", 30);
        }

        //image url
        String phoneImage = "https://gadgets360.com/deals/recharge-offers-coupons";
        map.put("phone", phone);
        map.put("name", name);
        map.put("id", id);
        map.put("type", "Phone Recharge");
        map.put("status", "Pending");
        map.put("image", phoneImage);
        map.put("uid", user.getUid());

        int current = Integer.parseInt(coinsTv.getText().toString());
        int update = 0;

        if (amount == 20)
            update = current - 20000;

        if (amount == 30)
            update = current - 30000;

        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put("coins", update);

        reference.child(user.getUid())
                .updateChildren(userMap);

        withdrawRef.child(id).setValue(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful ()) {
                            Toast.makeText ( getContext (), "Congrats", Toast.LENGTH_SHORT ).show ();


                        }
                    }
                });

    }

     }