package com.WinQoapp.earinmoney.quiz;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.earinmoney.R;

import java.util.ArrayList;

public class CategoryAdapter extends  RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>{


    Context context;
    ArrayList<CategoryModel> categoryModels;

    public CategoryAdapter(Context context, ArrayList<CategoryModel> categoryModels){
        this.context = context;
        this.categoryModels = categoryModels;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from ( context ).inflate ( R.layout.intem_quiz, parent, false);

        return new CategoryViewHolder ( view );
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {

        CategoryModel model = categoryModels.get ( position );


        holder.textView.setText ( model.getCategoryName () );

        try {
            Glide.with ( context )
                    .load ( model.getCategoryImage () )
                    .into ( holder.imageView );
        } catch (Exception e) {
            e.printStackTrace ();
        }


        holder.imageView.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent (context, QuizplayActivity.class);
                intent.putExtra ( "catId", model.getCategoryId ());
                context.startActivity ( intent );

            }
        } );

    }

    @Override
    public int getItemCount() {
        return categoryModels.size ();
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder{


        ImageView imageView;
        TextView textView;

        public CategoryViewHolder(@NonNull View itemView) {
            super ( itemView );

            try {
                imageView = (ImageView) itemView.findViewById ( R.id.image );
                textView = (TextView) itemView.findViewById ( R.id.category );
            } catch (Exception e) {
                e.printStackTrace ();
            }
        }
    }
}
