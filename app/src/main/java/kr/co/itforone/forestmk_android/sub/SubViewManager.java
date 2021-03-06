package kr.co.itforone.forestmk_android.sub;


import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import kr.co.itforone.forestmk_android.util.BackHistoryManager;
import kr.co.itforone.forestmk_android.MainActivity;
import kr.co.itforone.forestmk_android.R;

class SubViewManager extends WebViewClient {
    SubWebveiwActivity context;
    MainActivity mainActivity;
    private BackHistoryManager bm = BackHistoryManager.getInstance();

    public SubViewManager(SubWebveiwActivity context, MainActivity mainActivity) {
        this.context = context;
        this.mainActivity = mainActivity;
    }

    public SubViewManager(SubWebveiwActivity context) {
        this.context = context;
    }

    public SubViewManager() {
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {

        if(!url.contains(context.getString(R.string.home2))){

            Intent i  = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            i.setPackage("com.android.chrome");
            context.startActivity(i);
            return true;

        }

      //  Toast.makeText(context.getApplicationContext(),"sub - " +url,Toast.LENGTH_LONG).show();
        context.flg_opensearch=0;
        boolean lastchk = context.now_refreshlayout;
        Log.d("backpress_should",url);

        if(url.contains("img_view.php")){

            context.settings.setSupportZoom(true);   //화면 확대축소
            context.settings.setBuiltInZoomControls(true);
            context.settings.setDisplayZoomControls(false);

        }

        else{

            context.settings.setSupportZoom(false);   //화면 확대축소
            context.settings.setBuiltInZoomControls(false);
            context.settings.setDisplayZoomControls(false);

        }

        if(url.contains("category.php") || url.contains("recent_list.php") || (url.contains("mypage.php") && !url.contains("compulsive"))
                ||  (url.contains("board.php")&&!url.contains("wr_id") && !url.contains("compulsive") && !url.contains("flg_snackbar"))  || (url.contains("write.php") && url.contains("deal"))
                || url.contains("list_message.php")
                ){

            Log.d("backpress_newintent",url);

            Intent intent = new Intent(context, SubWebveiwActivity.class);
            intent.putExtra("subview_url", url);
            intent.putExtra("before_refresh", lastchk);

            /*if(url.contains("write.php")){
                bm.addHitory("intent_write");
            }
            else{
                bm.addHitory("intent");
            }*/

            bm.addHitory("intent");
            context.startActivityForResult(intent,context.VIEW_REFRESH);
            context.overridePendingTransition(R.anim.fadein, R.anim.stay);
            Log.d("history_back_should",bm.getHistorylist().toString());
            return true;

        }

        else {

            //Toast.makeText(mainActivity.getApplicationContext(),"view"+String.valueOf(mainActivity.flg_alert), Toast.LENGTH_LONG).show();
            if(url.contains("register_form.php") || url.contains("password_lost.php") ||
                    (url.contains("board.php") && !url.contains("wr_id=") && !url.contains("bo_table=deal")) || url.contains("mypage.php") ||
                    url.contains("login.php") || url.contains("mymap.php") || url.contains("myhp.php") || url.contains("img_view.php")||
                    url.contains("chat_room.php") || url.contains("mysetting.php") || url.contains("chkservice.php") || (url.contains("board.php?bo_table=qna") &&
            !url.contains("wr_id=")) || (url.contains("board.php?bo_table=deal") &&  url.contains("wr_id=")) || (url.contains("fn=") && url.contains("bo_table=notice"))
            ){
                Log.d("history_refresh","no");
                context.Norefresh();
                context.flg_refresh=0;
            }

            else{
                Log.d("history_refresh","yes");
                context.Yesrefresh();
                context.flg_refresh=1;
            }

            bm.addHitory(view.getOriginalUrl());
            Log.d("history_original_url",view.getOriginalUrl());
            view.loadUrl(url);

            Log.d("history_back_should",bm.getHistorylist().toString());

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
        view.loadUrl("javascript:setToken('"+mainActivity.token+"')");

        /*if(url.contains("login_check.php") || url.contains("write_update.php") || url.contains("register_form_update.php")){
            WebBackForwardList list = null;
            String backurl ="";
            try{
                list = context.webView.copyBackForwardList();
                if(list.getSize()>2){
                    context.webView.goBack();
                    context.webView.goBack();
                }
                else{
                    context.finish();
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }*/
    }
}
