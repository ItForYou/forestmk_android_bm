package kr.co.itforone.forestmk_android;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import java.net.NetworkInterface;

public class SplashActivity extends AppCompatActivity {

    private final int SPLASH_DISPLAY_LENGTH = 2000;
    String pushurl="";
    boolean isWifiConn = false;
    boolean isMobileConn = false;
    @Override
    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Intent push = getIntent();

        if (push.getStringExtra("goUrl") != null)
            pushurl = push.getStringExtra("goUrl");
        Log.w("push", pushurl);

        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (connMgr != null) {
                NetworkCapabilities capabilities = connMgr.getNetworkCapabilities(connMgr.getActiveNetwork());
                if (capabilities != null) {
                    if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                        isWifiConn = true;
                    } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
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
                    } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                        isMobileConn = true;
                    }
                }
            }
        }

        Log.d("DEBUG_TAG", "Wifi connected: " + isWifiConn);
        Log.d("DEBUG_TAG", "Mobile connected: " + isMobileConn);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {



                if (isMobileConn == true || isWifiConn == true) {
                    //    finish();
                    Intent main = new Intent(SplashActivity.this, MainActivity.class);

                    if (!pushurl.isEmpty() && !pushurl.equals(""))
                        main.putExtra("goUrl", pushurl);

                    startActivity(main);
                    finish();
                }
                else{
                    settingModal();

                }
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
    public void settingModal(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("");
        builder.setMessage("네트워크 연결이 안되어 있습니다.");
        builder.setNegativeButton("설정하기",   new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
                finish();
            }
        });
        builder.setPositiveButton("확인",  new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });

        builder.setCancelable(true);
        AlertDialog dialog = builder.create();
        //  mainActivity.current_dialog = dialog;
        dialog.show();
        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setTextColor(Color.parseColor("#9dc543"));
        Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        negativeButton.setTextColor(Color.parseColor("#000000"));
    }

    @Override
    public void onBackPressed() {

    }
}

