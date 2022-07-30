package com.WinQoapp.earinmoney.quiz;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.earinmoney.databinding.ActivityQuizzBinding;

public class QuizzActivity extends AppCompatActivity {


ActivityQuizzBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        binding = ActivityQuizzBinding.inflate ( getLayoutInflater () );

        setContentView ( binding.getRoot () );




//        categorys.add ( new CategoryModel ("", "Math", "https://image.shutterstock.com/image-vector/math-horizontal-banner-presentation-website-260nw-1859813464.jpg") );
//        categorys.add ( new CategoryModel ("", "Science", "https://image.shutterstock.com/image-vector/math-horizontal-banner-presentation-website-260nw-1859813464.jpg") );
//        categorys.add ( new CategoryModel ("", "Bangla", "https://image.shutterstock.com/image-vector/math-horizontal-banner-presentation-website-260nw-1859813464.jpg") );
//        categorys.add ( new CategoryModel ("", "English", "https://image.shutterstock.com/image-vector/math-horizontal-banner-presentation-website-260nw-1859813464.jpg") );
//
//        categorys.add ( new CategoryModel ("", "Math", "https://image.shutterstock.com/image-vector/math-horizontal-banner-presentation-website-260nw-1859813464.jpg") );
//        categorys.add ( new CategoryModel ("", "Science", "https://image.shutterstock.com/image-vector/math-horizontal-banner-presentation-website-260nw-1859813464.jpg") );
//        categorys.add ( new CategoryModel ("", "Bangla", "https://image.shutterstock.com/image-vector/math-horizontal-banner-presentation-website-260nw-1859813464.jpg") );
//        categorys.add ( new CategoryModel ("", "English", "https://image.shutterstock.com/image-vector/math-horizontal-banner-presentation-website-260nw-1859813464.jpg") );





    }
}