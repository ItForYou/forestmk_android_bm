package kr.co.itforone.forestmk_android.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import kr.co.itforone.forestmk_android.MainActivity;
import kr.co.itforone.forestmk_android.SplashActivity;

public class NetworkReceiver extends BroadcastReceiver {
    boolean isWifiConn = false;
    boolean isMobileConn = false;
    public int flg_networkchk = 0;
    private ActivityManager am = ActivityManager.getInstance();

    public void setFlg_networkchk(int flg_networkchk) {
        this.flg_networkchk = flg_networkchk;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        isWifiConn = false;
        isMobileConn = false;

        ConnectivityManager connMgr = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (connMgr != null) {
                NetworkCapabilities capabilities = connMgr.getNetworkCapabilities(connMgr.getActiveNetwork());
                if (capabilities != null) {
                    if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                        isWifiConn = true;
                    }
                    if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                        isMobileConn = true;
                    }
                }
            }
        } else {
            if (connMgr != null) {
                NetworkInfo activeNetwork = connMgr.getActiveNetworkInfo();
                if (activeNetwork != null) {
                    // connected to the internet
                    if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                        isWifiConn = true;
                    }
                    if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                        isMobileConn = true;
                    }
                }
            }
        }

        if (isMobileConn == false && isWifiConn == false && flg_networkchk==0) {

                Log.d("receiver_log","on");


                Intent intent_splash = new Intent(context, SplashActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                context.startActivity(intent_splash);
                Log.d("list_am", "receiver");


        }
    }
}
