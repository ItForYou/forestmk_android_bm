package kr.co.itforone.forestmk_android;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.net.Uri;
import android.util.Log;
import android.webkit.JavascriptInterface;

import com.google.android.material.snackbar.Snackbar;

import kr.co.itforone.forestmk_android.imageswiper.ImagedtActivity;
import kr.co.itforone.forestmk_android.sub.SubWebveiwActivity;

import static android.content.Context.MODE_PRIVATE;

class WebviewJavainterface {
    Activity activity;
    MainActivity mainActivity;

    public WebviewJavainterface(Activity activity, MainActivity mainActivity){

        this.mainActivity = mainActivity;
        this.activity=activity;

    }

    @JavascriptInterface
    public void call(String number){

        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + number));
        mainActivity.startActivity(intent);

    }
    @JavascriptInterface
    public void set_flgopensearch(int i){
        mainActivity.flg_opensearch = i;
    }

    @JavascriptInterface
    public void show_snackbar(String message){

       //     Toast.makeText(mainActivity.getApplicationContext(),message, Toast.LENGTH_LONG).show();
        Snackbar.make(mainActivity.findViewById(R.id.refreshlayout), message,Snackbar.LENGTH_LONG).show();

    }
    @JavascriptInterface
    public void show_detail(String wrid){


        Intent test_retro = new Intent(mainActivity, ImagedtActivity.class);
        test_retro.putExtra("wr_id",wrid);
        mainActivity.startActivity(test_retro);

    }

    @JavascriptInterface
    public void show_confirm(String message, String state, String href){

        //     Toast.makeText(mainActivity.getApplicationContext(),message, Toast.LENGTH_LONG).show();
        mainActivity.Confirm_alert_cancleable(message,state,href);

    }

    @JavascriptInterface
    public void show_alert(String message){

        //     Toast.makeText(mainActivity.getApplicationContext(),message, Toast.LENGTH_LONG).show();
        mainActivity.alert(message);

    }

    @JavascriptInterface
    public void set_flgsave(int flg_save){

        SharedPreferences pref = mainActivity.getSharedPreferences("save_flg", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("value",flg_save);
        editor.commit();

    }

    @JavascriptInterface
    public void opensearch(){

        SharedPreferences pref = mainActivity.getSharedPreferences("save_flg", MODE_PRIVATE);
        int value  = pref.getInt("value", 1);
        mainActivity.flg_opensearch = 1;

            mainActivity.webView.post(new Runnable() {
                @Override
                public void run() {
                        if(value ==1) {


                            mainActivity.webView.loadUrl("javascript:sch_saveactive();");

                        }
                        else{
                            mainActivity.webView.loadUrl("javascript:sch_saveinactive();");
                        }

                }
            });
        }

    @JavascriptInterface
    public void detail_img(String src){

        //     Toast.makeText(mainActivity.getApplicationContext(),message, Toast.LENGTH_LONG).show();
        Intent i = new Intent(mainActivity.getApplicationContext(),ShowDetailimg.class);
        i.putExtra("src",src);
        mainActivity.startActivity(i);

    }

    @JavascriptInterface
    public void sharelink() {
        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=kr.co.itforone.forestmk");
        Intent chooser = Intent.createChooser(intent, "공유하기");
        mainActivity.startActivity(chooser);
    }

    @JavascriptInterface
    public void get_Address() {

        //Toast.makeText(mainActivity.getApplicationContext(),"get_Address",Toast.LENGTH_LONG).show();
        Intent intent = new Intent(mainActivity.getApplicationContext(), SubWebveiwActivity.class);
        intent.putExtra("subview_url", mainActivity.getString(R.string.address));
        mainActivity.startActivityForResult(intent, 3);

    }

    @JavascriptInterface
    public void setLogininfo(String id,String password) {
        SharedPreferences pref = mainActivity.getSharedPreferences("logininfo", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("id",id);
        editor.putString("pwd",password);
        editor.commit();
    }

    @JavascriptInterface
    public void setlogout() {
     //   Toast.makeText(mainActivity.getApplicationContext(),"logout",Toast.LENGTH_LONG).show();
        SharedPreferences pref = mainActivity.getSharedPreferences("logininfo", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.commit();
    }

    @JavascriptInterface
    public void setflgmodal(int i) {
        mainActivity.flg_modal=i;
    }

    @JavascriptInterface
    public void setflgmodal2(int i) {
        mainActivity.flg_sortmodal=i;
    }

    @JavascriptInterface
    public void setflgmodal3(int i) {
        mainActivity.flg_dclmodal=i;
    }

    @JavascriptInterface
    public void setflgmodal4(int i) {
        mainActivity.flg_dclcommmodal=i;
    }

    @JavascriptInterface
    public void setflgmodal5(int i) {
        mainActivity.flg_blockmodal=i;
    }

    @JavascriptInterface
    public void getlocation() {
        boolean gps_enabled = false;
        try {
            gps_enabled = SubWebveiwActivity.locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}
        if(gps_enabled) {
            double lat = mainActivity.getlat() * 1000000;
            double lng = mainActivity.getlng() * 1000000;
            lat = Math.ceil(lat) / 1000000;
            lng = Math.ceil(lng) / 1000000;
            double finalLat = lat;
            double finalLng = lng;
            mainActivity.webView.post(new Runnable() {
                @Override
                public void run() {
                    if (mainActivity.webView.getUrl().contains("register_form.php") || mainActivity.webView.getUrl().contains("mymap.php"))
                        mainActivity.webView.loadUrl("javascript:trans_addr('" + finalLat + "','" + finalLng + "');");
                    else
                        mainActivity.webView.loadUrl("javascript:sort_distance('" + finalLat + "','" + finalLng + "');");

                    Log.d("get_location", finalLat + "," + finalLng + mainActivity.webView.getUrl());
                }
            });
        }
        else{
            mainActivity.settingModal();
        }
        // Toast.makeText(mainActivity.getApplicationContext(),""+lat+" , "+lng, Toast.LENGTH_LONG).show();

    }

    @JavascriptInterface
    public void back_pressed() {

        mainActivity.webView.post(new Runnable() {
            public void run() {
                Log.d("history_interfaceback!!","true");
                mainActivity.onBackPressed();
            }
        });
    }

    /*@JavascriptInterface
    public void NoRefresh(){
        //Toast.makeText(mainActivity.getApplicationContext(),"Norefresh",Toast.LENGTH_LONG).show();
        mainActivity.Norefresh();
        mainActivity.flg_refresh=0;
    }

    @JavascriptInterface
    public void YesRefresh(){
        mainActivity.Yesrefresh();
        mainActivity.flg_refresh=1;
    }*/

}
