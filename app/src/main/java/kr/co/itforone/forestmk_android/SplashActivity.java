package kr.co.itforone.forestmk_android;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private final int SPLASH_DISPLAY_LENGTH = 2000;
    String pushurl="";
    @Override
    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Intent push = getIntent();

        if (push.getStringExtra("goUrl") != null)
            pushurl = push.getStringExtra("goUrl");
        Log.w("push", pushurl);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
            //    finish();
                Intent main = new Intent(SplashActivity.this,MainActivity.class);

                if(!pushurl.isEmpty() && !pushurl.equals(""))
                    main.putExtra("goUrl",pushurl);

                startActivity(main);
                finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }

    @Override
    public void onBackPressed() {

    }
}

