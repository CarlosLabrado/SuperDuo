package it.jaschke.alexandria.misc;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Vazh on 29/9/2015.
 */
public class Utility {


    /**
     * Returns true if the network is available or about to become available
     *
     * @param context used to get the ConnectivityManager
     * @return .
     */
    static public boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();

    }
}
