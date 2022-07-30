package com.WinQoapp.earinmoney.About;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.WinQoapp.earinmoney.Email.LoginActivity;
import com.WinQoapp.earinmoney.Model.ProfileModel;
import com.example.earinmoney.R;
import com.WinQoapp.earinmoney.Share.Privacy;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import kotlinx.coroutines.BuildersKt;


public class AboutFragment extends Fragment {


    private static final int RESULT_OK = 1;
    private CircleImageView profileImage;
    private TextView nameTv, emailTv, coinsTv;

    private CardView  shareTv , logoutTv, privecyBtn;
    private ImageButton imageEditButton;
    private Button updateBtn;

    private FirebaseAuth auth;
    private FirebaseUser user;
    private DatabaseReference reference;
    private static final int IMAGE_PICKER = 1;
    private Uri photoUri;
    private String imageUrl;
    private ProgressDialog progressDialog;
    private BuildersKt Dexter;

    public AboutFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate ( R.layout.fragment_about, container, false );


        profileImage = (CircleImageView) view.findViewById ( R.id.profileImage );
        nameTv = (TextView) view.findViewById ( R.id.name_Tv );
        emailTv = (TextView) view.findViewById ( R.id.email_Tv );
        coinsTv = (TextView) view.findViewById ( R.id.coins_Tv );

        shareTv = (CardView) view.findViewById ( R.id.share_Tv );
        logoutTv = (CardView) view.findViewById ( R.id.logout_Tv );
        privecyBtn = (CardView)view.findViewById ( R.id.privecy_Tv );

        imageEditButton = (ImageButton) view.findViewById ( R.id.editImage );
        updateBtn = (Button) view.findViewById ( R.id.updateBtn);



        auth = FirebaseAuth.getInstance ();
        user = auth.getCurrentUser ();
        reference = FirebaseDatabase.getInstance ().getReference ().child ( "users" );

        progressDialog = new ProgressDialog ( getContext () );
        progressDialog.setTitle ( "Please Wait..." );
        progressDialog.setCancelable ( false );


        loadDataFromDatabase();
        clickListener();



        return view;
    }

    //firebase with load data 1 set (6)
    private void loadDataFromDatabase() {

        reference.child ( user.getUid () )
                .addValueEventListener ( new ValueEventListener () {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        ProfileModel model = snapshot.getValue (ProfileModel.class);

                        try {
                            nameTv.setText ( model.getName () );
                            emailTv.setText ( model.getEmail () );
                            coinsTv.setText ( String.valueOf ( model.getCoins () ) );
                        } catch (Exception e) {
                            e.printStackTrace ();
                        }


                        try {
                            Glide.with ( getContext ())
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
    }


    private <MultiplePermissionsListener> void clickListener() {


        //logout  1 set (7)
        logoutTv.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                auth.signOut ();
                startActivity ( new Intent ( getContext (), LoginActivity.class ) );

            }
        } );

        //share  1 set (8)
        shareTv.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {

                String shareBody = "Check out best earning app. Download " + getString ( R.string.app_name )
                        + "From Play Store\n"
                        + "https://play.google.com/store/apps/details?id="
                        + getContext ();

                Intent intent = new Intent ( Intent.ACTION_SEND );
                intent.putExtra ( Intent.EXTRA_TEXT, shareBody );
                intent.setType ( "text/plain" );
                startActivity ( intent );
            }
        } );
        //imageeditButton  1 set (9)


        imageEditButton.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                com.karumi.dexter.Dexter.withContext ( getActivity () )
                        .withPermissions ( Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE )
                        .withListener ( new com.karumi.dexter.listener.multi.MultiplePermissionsListener () {

                            @Override
                            public void onPermissionsChecked(com.karumi.dexter.MultiplePermissionsReport multiplePermissionsReport) {

                                if (multiplePermissionsReport.areAllPermissionsGranted ()) {
                                    Intent intent = new Intent ( Intent.ACTION_PICK );
                                    intent.setType ( "image/*" );
                                    startActivityForResult ( intent, IMAGE_PICKER );

                                } else {
                                    Toast.makeText ( getContext (), "Please allow permission", Toast.LENGTH_LONG ).show ();

                                }
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {

                            }
                        } ).check ();
            }
        } );


        updateBtn.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                uploadImage ();
            }
        } );

        privecyBtn.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {

                startActivity ( new Intent ( getContext (), Privacy.class ) );

                PrivacyPolicylink("https://masudep43.blogspot.com/2022/06/winqo-earning-real-money-app.html");

            }
        } );


    }
    //UpdateButton  1 set (10)
    private void uploadImage() {

        if(photoUri == null){
            return;
        }

        String fileName = user.getUid () + ".jpg";
        FirebaseStorage storage  = FirebaseStorage.getInstance ();
        final StorageReference storageReference = storage.getReference ().child ( "image/" + fileName );


        progressDialog.show ();

        storageReference.putFile ( photoUri )
                .addOnSuccessListener ( new OnSuccessListener<UploadTask.TaskSnapshot> () {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        storageReference.getDownloadUrl ().addOnSuccessListener ( new OnSuccessListener<Uri> () {
                            @Override
                            public void onSuccess(Uri uri) {
                                imageUrl = uri.toString ();

                                uploadImageUrlToDatabase();
                            }
                        } );

                    }
                } ).addOnFailureListener ( new OnFailureListener () {
            @Override
            public void onFailure(@NonNull Exception e) {

                progressDialog.dismiss ();
                Toast.makeText ( getContext (), "Error:"+e.getMessage (), Toast.LENGTH_LONG ).show ();

            }
        } ).addOnProgressListener ( new OnProgressListener<UploadTask.TaskSnapshot> () {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot tasksnapshot) {

                long totalSi = tasksnapshot.getTotalByteCount ();
                long transferS = tasksnapshot.getBytesTransferred ();

                long totalSize = (totalSi / 1024);
                long transferSize = transferS/1024;

                progressDialog.setMessage ( "Uploaded "+((int) transferSize)+"KB/ "+((int) totalSize)+"KB");
            }
        } );

    }
    //firebase uploa data 1 set  set (10)
    private void uploadImageUrlToDatabase() {
        HashMap<String, Object> map = new HashMap<> ();
        map.put ( "image", imageUrl );


        reference.child ( user.getUid () )
                .updateChildren ( map )
                .addOnCompleteListener ( new OnCompleteListener<Void> () {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        updateBtn.setVisibility ( View.GONE );
                        progressDialog.dismiss ();
                    }
                } );
    }

    //imageeditButton  2 set (9)

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult ( requestCode, resultCode, data );

        if (requestCode == IMAGE_PICKER && resultCode == RESULT_OK){
            if (data != null){
                photoUri = data.getData ();

                updateBtn.setVisibility ( View.VISIBLE );
            }
        }
    }



    //privacy



    private void PrivacyPolicylink(String s) {
        Uri uri = Uri.parse ( s );
        startActivity ( new Intent ( Intent.ACTION_VIEW, uri ) );
    }



}