package kr.co.itforone.forestmk_android.sub;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.net.Uri;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import kr.co.itforone.forestmk_android.MainActivity;
import kr.co.itforone.forestmk_android.R;
import kr.co.itforone.forestmk_android.ShowDetailimg;
import kr.co.itforone.forestmk_android.imageswiper.ImagedtActivity;

import static android.content.Context.MODE_PRIVATE;

class SubWebviewJavainterface {

    SubWebveiwActivity activity;


    public SubWebviewJavainterface(SubWebveiwActivity activity){

        this.activity=activity;
    }

    @JavascriptInterface
    public void call(String number){

        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + number));
        activity.startActivity(intent);

    }

    @JavascriptInterface
    public void set_address(String addr12, String addr11){
      //  Toast.makeText(activity.getApplicationContext(),addr11,Toast.LENGTH_LONG).show();
        Intent intent = new Intent();//startActivity()를 할것이 아니므로 그냥 빈 인텐트로 만듦
        intent.putExtra("address12",addr12);
        intent.putExtra("address11",addr11);
        activity.setResult(33,intent);
        activity.finish();

    }

    @JavascriptInterface
    public void get_Address() {
        //Toast.makeText(mainActivity.getApplicationContext(),"get_Address",Toast.LENGTH_LONG).show();
        Intent intent = new Intent(activity.getApplicationContext(), SubWebveiwActivity.class);
        intent.putExtra("subview_url", activity.getString(R.string.address));
        activity.startActivityForResult(intent, 3);
    }

    @JavascriptInterface
    public void getlocation() {
        boolean gps_enabled = false;
        try {
            gps_enabled = SubWebveiwActivity.locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}
        if(gps_enabled) {

            double lat = activity.getlat() * 1000000;
            double lng = activity.getlng() * 1000000;
            lat = Math.ceil(lat) / 1000000;
            lng = Math.ceil(lng) / 1000000;
            double finalLat = lat;
            double finalLng = lng;
            activity.webView.post(new Runnable() {

                @Override
                public void run() {

                    if (activity.webView.getUrl().contains("register_form.php") || activity.webView.getUrl().contains("mymap.php"))
                        activity.webView.loadUrl("javascript:trans_addr('" + finalLat + "','" + finalLng + "');");
                    else
                        activity.webView.loadUrl("javascript:sort_distance('" + finalLat + "','" + finalLng + "');");

                    Log.d("get_location",finalLat+","+finalLng + activity.webView.getUrl());

                }
            });
        //    Toast.makeText(activity.getApplicationContext(), "" + lat + " , " + lng, Toast.LENGTH_LONG).show();

        }
        else{
            activity.settingModal();
        }
    }

    @JavascriptInterface
    public void setLogininfo(String id,String password) {
        SharedPreferences pref = activity.getSharedPreferences("logininfo", activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("id",id);
        editor.putString("pwd",password);
        editor.commit();
    }

    @JavascriptInterface
    public void setlogout() {

        // Toast.makeText(activity.getApplicationContext(),"logout",Toast.LENGTH_LONG).show();
        SharedPreferences pref = activity.getSharedPreferences("logininfo", activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.commit();

    }

    @JavascriptInterface
    public void back_pressed() {

        Log.d("history_interfaceback!!","true");
        activity.webView.post(new Runnable() {
            public void run() {
                activity.onBackPressed();
            }
        });

    }

    @JavascriptInterface
   public void show_snackbar(String text, int flg_value){

       // Toast.makeText(activity.getApplicationContext(),text, Toast.LENGTH_LONG).show();
        if(activity.flg_snackbar!=flg_value)
        Snackbar.make(activity.findViewById(R.id.sub_refreshlayout), text,Snackbar.LENGTH_LONG).show();
        activity.flg_snackbar=flg_value;

    }

    @JavascriptInterface
    public void show_detail(String wrid, int current){


       // Toast.makeText(activity, wrid + current, Toast.LENGTH_SHORT).show();
        Intent test_retro = new Intent(activity, ImagedtActivity.class);
        test_retro.putExtra("wr_id",wrid);
        test_retro.putExtra("notice","");
        test_retro.putExtra("current",current);
        activity.startActivity(test_retro);

    }

    @JavascriptInterface
    public void show_detail_notice(String wrid,int bfno){


        Intent test_retro = new Intent(activity, ImagedtActivity.class);
        test_retro.putExtra("wr_id",wrid);
        test_retro.putExtra("notice","true");
        test_retro.putExtra("current",bfno);
        activity.startActivity(test_retro);

    }

    @JavascriptInterface
    public void detail_img(String src){

        //     Toast.makeText(mainActivity.getApplicationContext(),message, Toast.LENGTH_LONG).show();
        Intent i = new Intent(activity.getApplicationContext(), ShowDetailimg.class);
        i.putExtra("src",activity.getString(R.string.nodejsupload)+src);
        activity.startActivity(i);

    }

    @JavascriptInterface
    public void show_confirm(String message, String state, String href){

        //     Toast.makeText(mainActivity.getApplicationContext(),message, Toast.LENGTH_LONG).show();
        activity.Confirm_alert_cancleable(message,state,href);

    }

    @JavascriptInterface
    public void show_alert(String message){

        //     Toast.makeText(mainActivity.getApplicationContext(),message, Toast.LENGTH_LONG).show();
        activity.alert(message);

    }

    @JavascriptInterface
    public void setflgmodal2(int i) {
       // Toast.makeText(activity.getApplicationContext(),"setflgmodal!!", Toast.LENGTH_LONG).show();
        activity.flg_sortmodal=i;

    }
    @JavascriptInterface
    public void setflgmodal(int i) {
        activity.flg_modal=i;
    }

    @JavascriptInterface
    public void setflgmodal3(int i) {
        activity.flg_dclmodal=i;
    }

    @JavascriptInterface
    public void setflgmodal4(int i) {
        Log.d("backpress_","modal4");
        activity.flg_dclcommmodal=i;
    }

    @JavascriptInterface
    public void setflgmodal5(int i) {
        activity.flg_blockmodal=i;
    }

    @JavascriptInterface
    public void set_flgsave(int flg_save){

        SharedPreferences pref = activity.getSharedPreferences("save_flg", activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("value",flg_save);
        editor.commit();

    }

    @JavascriptInterface
    public void set_flgopensearch(int i){
        activity.flg_opensearch = i;
    }

    @JavascriptInterface
    public void opensearch(){

        SharedPreferences pref = activity.getSharedPreferences("save_flg", MODE_PRIVATE);
        int value  = pref.getInt("value", 1);
        activity.flg_opensearch =1;

        activity.webView.post(new Runnable() {
            @Override
            public void run() {
                if(value ==1) {
                    activity.webView.loadUrl("javascript:sch_saveactive();");
                }
                else{
                    activity.webView.loadUrl("javascript:sch_saveinactive();");
                }
            }
        });

    }

   /* @JavascriptInterface
    public void NoRefresh(){
        //Toast.makeText(mainActivity.getApplicationContext(),"Norefresh",Toast.LENGTH_LONG).show();
        activity.Norefresh();
        activity.flg_refresh=0;
    }

    @JavascriptInterface
    public void YesRefresh(){
        activity.Yesrefresh();
        activity.flg_refresh=1;
    }*/
   @JavascriptInterface
   public void toastmsg(String msg){
       Toast.makeText(activity.getApplicationContext(),msg,Toast.LENGTH_LONG).show();
   }

}
