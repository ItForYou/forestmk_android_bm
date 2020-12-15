package kr.co.itforone.forestmk_android;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.net.NetworkInterface;

import kr.co.itforone.forestmk_android.util.ActivityManager;

public class SplashActivity extends AppCompatActivity {

    private final int SPLASH_DISPLAY_LENGTH = 2000;
    String pushurl="";
    boolean isWifiConn = false;
    boolean isMobileConn = false;
    private ActivityManager am = ActivityManager.getInstance();
    String[] PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
    };

    static final int PERMISSION_REQUEST_CODE = 1;
    private LocationManager locationManager;
    private Location location;
    private boolean hasPermissions(String[] permissions){
        // 퍼미션 확인해
        int result = -1;
        for (int i = 0; i < permissions.length; i++) {
            result = ContextCompat.checkSelfPermission(getApplicationContext(), permissions[i]);
            if(result!=PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        Log.d("per_result",String.valueOf(result));
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        }else {
            return false;
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (!hasPermissions(PERMISSIONS)){
                    Toast.makeText(getApplicationContext(),"권한을 허용하지 않으면 앱을 사용할 수 없습니다.",Toast.LENGTH_LONG).show();
                    finish();
                }
                else{
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
                            } else {
                                settingModal();
                            }

                        }
                    }, SPLASH_DISPLAY_LENGTH);
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        am.addActivity(this);

        ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_REQUEST_CODE);

        Intent push = getIntent();

        if (push.getStringExtra("goUrl") != null)
            pushurl = push.getStringExtra("goUrl");
        Log.w("push", pushurl);

        //네트워크 상태 체크
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
    }

    public void settingModal(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("");
        builder.setMessage("네트워크 연결이 안되어 있습니다.");
        builder.setNegativeButton("설정하기",   new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Log.d("start_setting","1");
                startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
                Log.d("start_setting","2");
                am.finishAllActivity();
                Log.d("start_setting","3");
            }
        });
        builder.setPositiveButton("확인",  new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                am.finishAllActivity();
            }
        });

        builder.setCancelable(false);
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
        am.finishAllActivity();
    }
}

