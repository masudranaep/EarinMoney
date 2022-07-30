package com.WinQoapp.earinmoney.Model;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Internet {

    Context context;

    public  Internet(Context context){
        this.context = context;
    }

    //internet   1 set (19)
    public boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService ( Context.CONNECTIVITY_SERVICE );

        NetworkInfo info = connectivityManager.getActiveNetworkInfo ();

        return info !=  null && info.isConnected ();

    }

}
