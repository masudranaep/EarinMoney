package com.WinQoapp.earinmoney.learderbord;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.earinmoney.R;

import java.util.List;

public class LeaderboardsAdapter extends RecyclerView.Adapter<LeaderboardsAdapter.leaderboardViewHolder>{


    private List<LeaderboardModel> list;
    private Context context;

    public LeaderboardsAdapter(List<LeaderboardModel> list){
        this.list = list;

    }



    @NonNull
    @Override
    public leaderboardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from ( parent.getContext () )
                .inflate ( R.layout.rank_item, parent,false );
        return new leaderboardViewHolder ( view );

    }

    @Override
    public void onBindViewHolder(@NonNull leaderboardViewHolder holder, int position) {

        LeaderboardModel model = list.get ( position );

        holder.phoneTv.setText ( String.valueOf ( list.get ( position ).getPhone ()));
        holder.statusTv.setText ( list.get ( position ).getStatus () );




        try {
            Glide.with ( holder.imageView.getContext ().getApplicationContext () )
                    .load ( list.get ( position ).getImage () )
                    .into ( holder.imageView );
        } catch (Exception e) {
            e.printStackTrace ();
        }

    }

    @Override
    public int getItemCount() {
        return list.size () ;
    }

    public class leaderboardViewHolder extends RecyclerView.ViewHolder{


        private ImageView imageView;
        private TextView phoneTv, statusTv;

        public leaderboardViewHolder(@NonNull View itemView) {
            super ( itemView );

            imageView = (ImageView)itemView.findViewById ( R.id.image );
            phoneTv = (TextView) itemView.findViewById ( R.id.phone );
            statusTv = (TextView) itemView.findViewById ( R.id.status );


        }
    }
}
