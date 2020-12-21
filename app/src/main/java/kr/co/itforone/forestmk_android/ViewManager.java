package kr.co.itforone.forestmk_android;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.annotation.RequiresApi;

import kr.co.itforone.forestmk_android.sub.SubWebveiwActivity;
import kr.co.itforone.forestmk_android.util.BackHistoryManager;

class ViewManager extends WebViewClient{

    Activity context;
    MainActivity mainActivity;
    private BackHistoryManager bm = BackHistoryManager.getInstance();

    public ViewManager(Activity context, MainActivity mainActivity) {
        this.context = context;
        this.mainActivity = mainActivity;
    }

    public ViewManager() {
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        // Toast.makeText(mainActivity.getApplicationContext(),"test-"+url, Toast.LENGTH_LONG).show();

        boolean lastchk = mainActivity.now_refreshlayout;
        Log.d("backpress_should", url);
        if (url.contains("img_view.php")) {

            mainActivity.settings.setSupportZoom(true);   //화면 확대축소
            mainActivity.settings.setBuiltInZoomControls(true);
            mainActivity.settings.setDisplayZoomControls(false);

        } else {
            mainActivity.settings.setSupportZoom(false);   //화면 확대축소
            mainActivity.settings.setBuiltInZoomControls(false);
            mainActivity.settings.setDisplayZoomControls(false);
        }

            if (url.contains("category.php") || url.contains("recent_list.php") || (url.contains("mypage.php") && !url.contains("compulsive"))
                    || (url.contains("board.php") && !url.contains("wr_id=") && !url.contains("compulsive") && !url.contains("flg_snackbar"))
                    || (url.contains("write.php") && url.contains("deal")) || url.contains("mysetting.php") || (url.contains("board.php?bo_table=qna") &&
                    !url.contains("wr_id="))
            ) {

                Log.d("backpress_newintent", url);
                Intent intent = new Intent(mainActivity, SubWebveiwActivity.class);
             /*   if (!mainActivity.user_id.isEmpty() && !mainActivity.user_pwd.isEmpty() && !mainActivity.pushurl.isEmpty()) {
                    intent.putExtra("push_login", true);
                    intent.putExtra("userid", mainActivity.user_id);
                    intent.putExtra("userpwd", mainActivity.user_pwd);
                }*/

                intent.putExtra("subview_url", url);
                intent.putExtra("before_refresh", lastchk);
                //    Toast.makeText(mainActivity.getApplicationContext(),String.valueOf(mainActivity.now_refreshlayout), Toast.LENGTH_LONG).show();


          /*if(url.contains("write.php")){
              bm.addHitory("intent_write");
          }
          else{
              bm.addHitory("intent");
          }*/

                bm.addHitory("intent");
                mainActivity.startActivityForResult(intent, mainActivity.VIEW_REFRESH);
                mainActivity.overridePendingTransition(R.anim.fadein, R.anim.stay);
                Log.d("history_back_should", bm.getHistorylist().toString());
                return true;
            }

            else {
                //Toast.makeText(mainActivity.getApplicationContext(),"view"+String.valueOf(mainActivity.flg_alert), Toast.LENGTH_LONG).show();
                if (url.contains("register_form.php") || url.contains("password_lost.php") ||
                        (url.contains("board.php") && url.contains("wr_id=")) || url.contains("mypage.php") ||
                        url.contains("login.php") || url.contains("mymap.php") || url.contains("img_view.php") ||
                        url.contains("chatting.php")
                ) {
                    mainActivity.Norefresh();
                    mainActivity.flg_refresh = 0;
                } else {
                    mainActivity.Yesrefresh();
                    mainActivity.flg_refresh = 1;
                }
                bm.addHitory(view.getOriginalUrl());
                Log.d("history_original_url", view.getOriginalUrl());
                view.loadUrl(url);
                Log.d("history_back_should", bm.getHistorylist().toString());
                return false;

            }

    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        // mainActivity.dialogloading.show();
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        //Log.d("settoken",mainActivity.token);

        view.loadUrl("javascript:setToken('"+mainActivity.token+"')");
   /*    if(url.contains("login_check.php") || url.contains("write_update.php") || url.contains("register_form_update.php") || url.contains("write_comment_update.php")){
            mainActivity.webView.goBack();
        }*/
    }

    private void animate(final WebView view) {

        Animation anim = AnimationUtils.loadAnimation(mainActivity.getApplicationContext(),
                android.R.anim.fade_in);
        view.startAnimation(anim);

    }
}
