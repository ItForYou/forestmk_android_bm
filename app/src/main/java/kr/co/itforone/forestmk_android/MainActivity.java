package kr.co.itforone.forestmk_android;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.webkit.ValueCallback;
import android.webkit.WebBackForwardList;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import kr.co.itforone.forestmk_android.imageswiper.ImagedtActivity;
import kr.co.itforone.forestmk_android.util.ActivityManager;
import kr.co.itforone.forestmk_android.util.BackHistoryManager;
import kr.co.itforone.forestmk_android.util.EndDialog;

import static kr.co.itforone.forestmk_android.SplashActivity.receiver;


public class MainActivity extends AppCompatActivity {

    @BindView(R.id.webView)    WebView webView;
    @BindView(R.id.refreshlayout)    SwipeRefreshLayout refreshlayout;

    static public String token = "", pushurl="";
    
    public int flg_refresh = 1;
    ValueCallback<Uri[]> filePathCallbackLollipop;
    Dialog current_dialog;
    private Location location;
    private ActivityManager am = ActivityManager.getInstance();
    private BackHistoryManager bm = BackHistoryManager.getInstance();
    public Uri mImageCaptureUri,croppath;
    static final int FILECHOOSER_LOLLIPOP_REQ_CODE=1300;
    static final int PERMISSION_REQUEST_CODE = 1;
    static final int CROP_FROM_ALBUM =2;
    static final int GET_ADDRESS =3;
    static final int VIEW_REFRESH =4;
    private LocationManager locationManager;
    private EndDialog mEndDialog;
    boolean isWifiConn = false;
    boolean isMobileConn = false;
    WebSettings settings;
    boolean gps_enabled = false, now_refreshlayout=true;
    String user_id, user_pwd;
    int flg_alert = 0, flg_confirm=0, flg_modal=0,flg_sortmodal=0, flg_dclmodal=0, flg_dclcommmodal=0, flg_blockmodal=0, flg_opensearch=0;
    long backPrssedTime =0;
    private AlertDialog dialog_network;

    String[] PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
    };

    private final int MY_PERMISSIONS_REQUEST_CAMERA=1001;

    private boolean hasPermissions(String[] permissions){
        // 퍼미션 확인해
        int result = -1;
        for (int i = 0; i < permissions.length; i++) {
            result = ContextCompat.checkSelfPermission(getApplicationContext(), permissions[i]);
            if(result!=PackageManager.PERMISSION_GRANTED){
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

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (!hasPermissions(PERMISSIONS)){

                }else{
                    locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                   /* LocationPosition.act=MainActivity.this;
                    LocationPosition.setPosition(this);
                    if(LocationPosition.lng==0.0){
                        LocationPosition.setPosition(this);
                    }
                    String place= LocationPosition.getAddress(LocationPosition.lat,LocationPosition.lng);
                    webView.loadUrl("javascript:getAddress('"+place+"')");*/
                }
                return;
            }
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        am.addActivity(this);

        //네트워크 체인지 리시버
        IntentFilter filter = new IntentFilter
                (ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new BroadcastReceiver(){

            @Override
            public void onReceive(Context context, Intent intent) {

                Log.d("receive_chk","main");

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

                if (isMobileConn == false && isWifiConn == false) {
                    Log.d("receiver_log","on");

                    if(dialog_network==null) {
                        settingModal2();
                    }
                    else{
                        if(!dialog_network.isShowing())
                            settingModal2();
                    }
                }
            }
        };
        this.registerReceiver(receiver, filter);

       ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_REQUEST_CODE);

        if(hasPermissions(PERMISSIONS)) {

            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        }


        FirebaseInstanceId.getInstance()
                .getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w("D", "getInstanceId failed", task.getException());
                            return;
                        }
                        // Get new Instance ID token
                        token = task.getResult().getToken();
           //             Toast.makeText(getApplicationContext(), token, Toast.LENGTH_SHORT).show();
                    }
        });

        settings = webView.getSettings();
        webView.setWebChromeClient(new ChromeManager(this,this));
        webView.setWebViewClient(new ViewManager(this, this));
        webView.addJavascriptInterface(new WebviewJavainterface(this, this),"Android");

        settings.setJavaScriptEnabled(true);
        settings.setAllowFileAccess(true);//웹에서 파일 접근 여부
        settings.setAppCacheEnabled(true);//캐쉬 사용여부
        settings.setDatabaseEnabled(true);//HTML5에서 db 사용여부 -> indexDB
        settings.setDomStorageEnabled(true);//HTML5에서 DOM 사용여부
        settings.setUseWideViewPort(true);//웹에서 view port 사용여부
        settings.setUserAgentString("forestmk");
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);//캐시 사용모드 LOAD_NO_CACHE는 캐시를 사용않는다는 뜻
        settings.setTextZoom(100);       // 폰트크기 고정
        webView.setWebContentsDebuggingEnabled(true);
       // webView.setLongClickable(true);

        SharedPreferences pref = getSharedPreferences("logininfo", MODE_PRIVATE);
        user_id = pref.getString("id", "");
        user_pwd = pref.getString("pwd", "");

        //Toast.makeText(getApplicationContext(),id+"-"+pwd,Toast.LENGTH_LONG).show();
        Intent push = getIntent();

        if (push.getStringExtra("goUrl") != null)
            pushurl = push.getStringExtra("goUrl");

        Log.d("pushurl", pushurl);

        if(!pushurl.isEmpty() && !pushurl.equals("")){

            if(!user_id.isEmpty() && !user_pwd.isEmpty()){

                Log.d("history_loadurl1", "true");

                if(bm.getHistorylist().size()<=0)
                    bm.addHitory(getString(R.string.home));

                if(pushurl.length()>1){
                    webView.loadUrl(pushurl);
                    //webView.loadUrl(getString(R.string.login) + "mb_id=" + user_id + "&mb_password=" + user_pwd + "&"+pushurl);
                    pushurl = "";
                }
                else {
                    webView.loadUrl(getString(R.string.login) + "mb_id=" + user_id + "&mb_password=" + user_pwd + "&android_push=1");
                    pushurl = "";
                }

            }
            else{
                Log.d("history_loadurl2", "true");
                if(bm.getHistorylist().size()<=0)
                    bm.addHitory(getString(R.string.home));
                webView.loadUrl(pushurl);
                pushurl="";
            }

        }
        else if(!user_id.isEmpty() && !user_pwd.isEmpty()){
                webView.loadUrl(getString(R.string.login) + "mb_id=" + user_id + "&mb_password=" + user_pwd);
         //   webView.clearCache(true);
         //   webView.clearHistory();
        }
        else {
            webView.loadUrl(getString(R.string.home));
        }

        refreshlayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                webView.clearCache(true);
                webView.reload();
                refreshlayout.setRefreshing(false);
            }

        });

        refreshlayout.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {

                if(webView.getScrollY() == 0 && flg_refresh ==1){
                   Log.d("nowrefre",String.valueOf(now_refreshlayout));
                    now_refreshlayout = true;
                    refreshlayout.setEnabled(true);
                }
                else{
                    Log.d("nowrefre",String.valueOf(now_refreshlayout));
                  //  refreshlayout.setEnabled(false);
                }
            }
        });

        Log.d("lastchk",String.valueOf(now_refreshlayout));

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(pushurl!=null||!pushurl.isEmpty()) {
         //   this.unregisterReceiver(receiver);
        }


    }

    @Override
    public void onBackPressed(){

        Log.d("history_back_init", "init!");
        ArrayList<String> now_his = bm.getHistorylist();
        Log.d("history_back", now_his.toString());

        WebBackForwardList list = null;
        String back2_url ="", backurl="",last="";

        if(now_his.size()>0) {
            last = now_his.get(now_his.size() - 1);
        }

        settings.setSupportZoom(false);   //화면 확대축소
        settings.setBuiltInZoomControls(false);
        settings.setDisplayZoomControls(false);

        try{

            list = webView.copyBackForwardList();
            Log.d("history_webview_size", list.toString());
            if (list.getSize() >1){
                backurl = list.getItemAtIndex(list.getCurrentIndex() - 1).getUrl();
            }
            if(list.getSize() >2){
                back2_url = list.getItemAtIndex(list.getCurrentIndex() - 2).getUrl();
                Log.d("history_webview", back2_url);
            }

        } catch (NullPointerException e) {

            e.printStackTrace();

        }

        if(last.contains("register_form.php") || last.contains("password_lost.php") ||
                (last.contains("board.php") && last.contains("wr_id=")) || last.contains("mypage.php") ||
                last.contains("login.php") || last.contains("mymap.php") || last.contains("mysetting.php") || last.contains("chkservice.php") || (last.contains("board.php?bo_table=qna") &&
                !last.contains("wr_id="))) {

            Log.d("history_NoRefresh!!", backurl);
            Norefresh();

        }

        else{

            Log.d("history_YesRefresh!!", backurl);
            Yesrefresh();
        }


        if (flg_modal==1 && ((webView.getUrl().contains("bo_table=deal") && !webView.getUrl().contains("wr_id=")) || webView.getUrl().contains("recent_list.php"))){

            Log.d("backpress_closemd1", webView.getUrl());
            webView.loadUrl("javascript:close_writemd()");

        }
        else if (flg_sortmodal!=0 && ((webView.getUrl().contains("bo_table=deal")&&!webView.getUrl().contains("wr_id=")) || webView.getUrl().equals(getString(R.string.home2)))){

            Log.d("backpress_closemd2", webView.getUrl());
            webView.loadUrl("javascript:close_sortmd("+flg_sortmodal+")");

        }
        else if(flg_dclmodal!=0 && (webView.getUrl().contains("bo_table=deal")&&webView.getUrl().contains("wr_id="))){

            Log.d("backpress_closemd3", webView.getUrl());
            webView.loadUrl("javascript:close_dclmd()");
            Norefresh();

        }
        else if(flg_dclcommmodal!=0 && (webView.getUrl().contains("bo_table=deal")&&webView.getUrl().contains("wr_id="))){

            Log.d("backpress_closemd4", webView.getUrl());
            webView.loadUrl("javascript:close_declarecomm()");
            Norefresh();

        }
        else if(flg_blockmodal!=0 && (webView.getUrl().contains("bo_table=deal")&&webView.getUrl().contains("wr_id="))){

            Log.d("backpress_closemd5", webView.getUrl());
            webView.loadUrl("javascript:close_blockmd()");
            Norefresh();

        }

        else if(webView.getUrl().equals(getString(R.string.home)) || webView.getUrl().equals(getString(R.string.home2))
                || webView.getUrl().contains("flg_snackbar=") ){

            mEndDialog = new EndDialog(MainActivity.this);
            mEndDialog.setCancelable(true);
            mEndDialog.show();
          /*  Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);

            Window window = mEndDialog.getWindow();
            int x = (int)(size.x * 0.8f);
            int y = (int)(size.y* 0.45f);

            window.setLayout(x,y);*/

            bm.removeAllHistory();
        }

        else if(webView.getUrl().contains("chatting.list.php")){

            bm.removeAllHistory();
          //  webView.clearCache(true);
            webView.loadUrl(getString(R.string.home));

        }

        else if(webView.getUrl().contains("chatting.php")){

            bm.removeAllHistory();
        //    webView.clearCache(true);
            webView.loadUrl("javascript:leavepage()");
          //  webView.loadUrl(getString(R.string.chattinglist));

        }

        else if(now_his.size()>0){

            Log.d("history_back_last", last);
            if(last.equals("intent")) {

                bm.removelast();
                finish();
                overridePendingTransition(R.anim.stay, R.anim.fadeout);

            }

            else{

                if(last.contains("write_update.php") || last.contains("login_check.php")
                        || last.contains("register_form_update.php") || last.contains("delete_comment.php")
                ){
                    bm.removelast();
                    onBackPressed();
                }
                else if(last.contains("write_comment_update.php")) {
                    if(back2_url.contains("w=cu")){
                        bm.removelast();
                        bm.removelast();
                        onBackPressed();
                        back2_url ="";
                    }
                    else{
                        bm.removelast();
                        onBackPressed();
                    }
                }
                else if(last.contains("delete_comment.php")){
                    bm.removelast();
                    bm.removelast();
                    onBackPressed();
                }
                else{
                    bm.removelast();
                    webView.loadUrl(last);
                }
            }
        }

        else{
/*
            mEndDialog = new EndDialog(MainActivity.this);
            mEndDialog.setCancelable(true);
            mEndDialog.show();
            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);

            Window window = mEndDialog.getWindow();
            int x = (int)(size.x * 0.8f);
            int y = (int)(size.y* 0.45f);

            window.setLayout(x,y);
*/
        }
     /*  if(webView.canGoBack()){
            webView.goBack();
        }

        else{

            //종료 다이얼로그 창
            mEndDialog = new EndDialog(this);
            mEndDialog.setCancelable(true);
            //current_dialog = mEndDialog;
            mEndDialog.show();
            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);

            Window window = mEndDialog.getWindow();
            int x = (int)(size.x * 0.8f);
            int y = (int)(size.y* 0.45f);

            window.setLayout(x,y);

        }*/
    }

    public void Confirm_alert(String Message){

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        // Set a title for alert dialog
        builder.setTitle("");
        // Show a message on alert dialog
        builder.setMessage(Message);
        // Set the positive button
        builder.setPositiveButton("확인",   new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                ArrayList <String> dialog_list = bm.getHistorylist();
                String dialog_last = "";

                if(dialog_list.size()>0) {
                    dialog_last = dialog_list.get(dialog_list.size() - 1);
                }

                if(dialog_last.equals("intent")) {

                    bm.removelast();
                    Intent intent = new Intent();
                    intent.putExtra("refresh",true);
                    setResult(RESULT_OK,intent);
                    finish();
                    overridePendingTransition(R.anim.stay, R.anim.fadeout);

                }
                else{
                    bm.removelast();
                    webView.loadUrl(dialog_last);
                }
            }

        });
        // Set the negative button
        builder.setNegativeButton("취소",  new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

            }

        });

        builder.setCancelable(false);
        // Create the alert dialog
        AlertDialog dialog = builder.create();
        // Finally, display the alert dialog
        //   current_dialog = dialog;
        dialog.show();
        // Get the alert dialog buttons reference
        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);

        // Change the alert dialog buttons text and background color
        positiveButton.setTextColor(Color.parseColor("#9dc543"));
        negativeButton.setTextColor(Color.parseColor("#ff0000"));

    }

    public void Confirm_alert_cancleable(String Message,String state, String href){

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        // Set a title for alert dialog
        builder.setTitle("");
        // Show a message on alert dialog
        builder.setMessage(Message);
        // Set the positive button
        builder.setPositiveButton("확인",   new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                switch(state){

                    case "delete_deal" :

                        webView.post(new Runnable() {

                            public void run() {
                                //String temp_url = href.substring(1,href.length());
                                Log.d("confirm_url",href);
                                webView.loadUrl(href);
                            }
                        });

                        break;
                    case "logout":
                        webView.post(new Runnable() {
                            public void run() {
                                webView.loadUrl(getString(R.string.g5_bbs)+href);
                            }
                        });
                        break;
                    case "delete_comment":
                        webView.post(new Runnable() {
                            public void run() {
                                String temp_url = href.substring(1,href.length());
                                webView.loadUrl(getString(R.string.g5_bbs)+temp_url);
                            }
                        });
                        break;

                    default: break;

                }
            }

        });
        // Set the negative button
        builder.setNegativeButton("취소",  new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.setCancelable(true);
        // Create the alert dialog
        AlertDialog dialog = builder.create();
        // Finally, display the alert dialog
        //   current_dialog = dialog;
        dialog.show();
        // Get the alert dialog buttons reference
        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);

        // Change the alert dialog buttons text and background color
        positiveButton.setTextColor(Color.parseColor("#9dc543"));
        negativeButton.setTextColor(Color.parseColor("#ff0000"));

    }

    public void alert(String text){

        Log.d("alert","show");
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("");
        builder.setMessage(text);
        builder.setPositiveButton("확인",   new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setCancelable(true);
        AlertDialog dialog = builder.create();
        //  mainActivity.current_dialog = dialog;
        dialog.show();
        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setTextColor(Color.parseColor("#9dc543"));

    }

    public void click_dialogN(View view){
      //  Toast.makeText(mContext.getApplicationContext(),"test",Toast.LENGTH_LONG).show();
        mEndDialog.dismiss();
    }

    public void click_dialogY(View view){
    //    Toast.makeText(mContext.getApplicationContext(),"test2",Toast.LENGTH_LONG).show();
          am.finishAllActivity();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            //광고 글쓰기 주소 검색
            case GET_ADDRESS:
                    if(resultCode==33) {
                        String data_address12 = data.getStringExtra("address12");
                        String data_address11 = data.getStringExtra("address11");
                        webView.loadUrl("javascript:set_wr12('" + data_address12 + "','"+data_address11+"')");
                    }
                   //Toast.makeText(getApplicationContext(),"get_addr", Toast.LENGTH_LONG).show();
                break;
            case ChromeManager.FILECHOOSER_LOLLIPOP_REQ_CODE:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                    if (resultCode == RESULT_OK && webView.getUrl().contains("register_form.php")) {
                        if (data != null) {
                            //String dataString = data.getDataString();
                            //  ClipData clipData = data.getClipData();
                            Uri result = (data == null || resultCode != RESULT_OK) ? null : data.getData();
                            CropImage.activity(result)
                                    .setAspectRatio(1,1)//가로 세로 1:1로 자르기 기능 * 1:1 4:3 16:9로 정해져 있어요
                                    .setCropShape(CropImageView.CropShape.OVAL)
                                    .start(this);
                        } else {
                            filePathCallbackLollipop.onReceiveValue(null);
                            filePathCallbackLollipop = null;
                        }
                    } else if (resultCode == RESULT_OK && !webView.getUrl().contains("register_form.php")) {
                        Uri[] result = null;
                        if (data != null) {
                            //String dataString = data.getDataString();
                            ClipData clipData = data.getClipData();
                            if (clipData != null) {
                                result = new Uri[clipData.getItemCount()];
                                for (int i = 0; i < clipData.getItemCount(); i++) {
                                    ClipData.Item item = clipData.getItemAt(i);
                                    result[i] = item.getUri();
                                }
                            } else {
                                result = ChromeManager.FileChooserParams.parseResult(resultCode, data);
                                //result = (data == null) ? new Uri[]{mCapturedImageURI} : WebChromeClient.FileChooserParams.parseResult(resultCode, data);
                            }
                            filePathCallbackLollipop.onReceiveValue(result);
                        } else {
                            filePathCallbackLollipop.onReceiveValue(null);
                            filePathCallbackLollipop = null;
                        }
                    } else {
                        try {
                            if (filePathCallbackLollipop != null) {
                                filePathCallbackLollipop.onReceiveValue(null);
                                filePathCallbackLollipop = null;

                            }
                        } catch (Exception e) {

                        }
                    }
                }
                break;
            case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:

                        CropImage.ActivityResult result = CropImage.getActivityResult(data);
                        if(result!=null) {
                                Uri resultUri = result.getUri();
                                Uri[] arr_Uri = new Uri[1];
                                arr_Uri[0] = resultUri;
                                filePathCallbackLollipop.onReceiveValue(arr_Uri);
                                filePathCallbackLollipop = null;
                            }
                        else {
                            try {
                                if (filePathCallbackLollipop != null) {
                                    filePathCallbackLollipop.onReceiveValue(null);
                                    filePathCallbackLollipop = null;
                                }
                            } catch (Exception e) {
                            }
                        }
                            break;
            case VIEW_REFRESH:

                            if(data!=null){

                                boolean backflg_refresh = data.getExtras().getBoolean("refresh");


                                if(webView.getOriginalUrl().contains("android_push=1")){

                                        String last_now="";
                                        if(bm.getHistorylist().size()>0)
                                            last_now = bm.getHistorylist().get(bm.getHistorylist().size() - 1);
                                        Log.d("history_refresh_last", bm.getHistorylist().toString());
                                        if (!last_now.isEmpty() && last_now.equals("intent")) {
                                            bm.removeAllHistory();
                                            //bm.addHitory(getString(R.string.home));
                                            webView.loadUrl(getString(R.string.home));
                                        } else {
                                            webView.loadUrl(last_now);
                                            //onBackPressed();
                                        }
                                    //onBackPressed();
                                }
                                else if(backflg_refresh==true) {
                                    webView.reload();
                                }

                                break;

                            }
                            else{
                                break;
                            }
        }
    }

    public void set_filePathCallbackLollipop(ValueCallback<Uri[]> filePathCallbackLollipop){
        this.filePathCallbackLollipop = filePathCallbackLollipop;
    }

    @SuppressLint("MissingPermission")
    public double getlat(){
        //Toast.makeText(getApplicationContext(),""+location.getLatitude() + "//" +location.getLongitude(),Toast.LENGTH_LONG).show();
        if(hasPermissions(PERMISSIONS)) {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
        if(location!=null) {
            return location.getLatitude();
        }
        else return 0;
    }

    @SuppressLint("MissingPermission")
    public double getlng(){
        if(hasPermissions(PERMISSIONS)) {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
        //Toast.makeText(getApplicationContext(),""+location.getLatitude() + "//" +location.getLongitude(),Toast.LENGTH_LONG).show();
        if(location!=null) {
            return location.getLongitude();
        }
        else return 0;

    }

    public void Norefresh(){
        now_refreshlayout = false;
        refreshlayout.setEnabled(false);
        Log.d("nowrefre",String.valueOf(now_refreshlayout));
        //Log.d("nowrefre","false");
    }
    public void Yesrefresh(){
        now_refreshlayout = true;
        refreshlayout.setEnabled(true);
        Log.d("nowrefre",String.valueOf(now_refreshlayout));
        //Log.d("nowrefre","false");
    }
    public void settingModal(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("");
        builder.setMessage("핸드폰 위치를 켜주세요!");
        builder.setNegativeButton("설정하기",   new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        });
        builder.setPositiveButton("확인",  new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
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


    public void settingModal2(){

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
                //android.os.Process.killProcess(android.os.Process.myPid());
                am.finishAllActivity();
            }
        });

        builder.setCancelable(false);
        dialog_network = builder.create();
        //  mainActivity.current_dialog = dialog;
        dialog_network.show();
        Button positiveButton = dialog_network.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setTextColor(Color.parseColor("#9dc543"));
        Button negativeButton = dialog_network.getButton(AlertDialog.BUTTON_NEGATIVE);
        negativeButton.setTextColor(Color.parseColor("#000000"));
    }


}