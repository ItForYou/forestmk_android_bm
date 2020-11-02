package kr.co.itforone.forestmk_android;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Button;

import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;

class ChromeManager extends WebChromeClient {

    private final int MY_PERMISSIONS_REQUEST_CAMERA=1001;
    Activity activity;
    MainActivity mainActivity;
    static final int FILECHOOSER_LOLLIPOP_REQ_CODE=1300;

    public ChromeManager(Activity activity, MainActivity mainActivity) {
        this.activity = activity;
        this.mainActivity = mainActivity;
    }

    @Override
    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams){
        mainActivity.set_filePathCallbackLollipop(filePathCallback);

   /*     Intent i;

        if(webView.getUrl().contains("register_form.php"))
            i =new Intent(Intent.ACTION_PICK);
        else
            i = new Intent(Intent.ACTION_GET_CONTENT);

        i.setType("image/*");
        i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        // Create file chooser intent

        mainActivity.startActivityForResult(i, FILECHOOSER_LOLLIPOP_REQ_CODE);*/

        int maxnum=1;

        if(webView.getUrl().contains("register_form.php"))
            maxnum=1;
        else
            maxnum=10;

        Matisse.from(mainActivity)
                .choose(MimeType.ofAll())
                .countable(true)
                .maxSelectable(maxnum)
                .imageEngine(new GlideEngine())
                .showPreview(true) // Default is `true`
                .countable(false)
                .forResult(FILECHOOSER_LOLLIPOP_REQ_CODE);

        return true;

    }

    @Override
    public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
// Toast.makeText(mainActivity.getApplicationContext(),"chrome"+String.valueOf(mainActivity.flg_alert), Toast.LENGTH_LONG).show();



//
//        new AlertDialog.Builder(view.getContext())
//                .setTitle("")
//                .setMessage(message)
//                .setPositiveButton(android.R.string.ok,
//                        new AlertDialog.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int which) {
//                                result.confirm();
//                            }
//                        })
//                .setCancelable(false)
//                .create()
//                .show();

        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setTitle("");
        builder.setMessage(message);
        builder.setPositiveButton("확인",   new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                result.confirm();
            }
        });
        builder.setCancelable(false);
        AlertDialog dialog = builder.create();
      //  mainActivity.current_dialog = dialog;
        dialog.show();
        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setTextColor(Color.parseColor("#9dc543"));

        return true;
    }

    @Override
    public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
      //mainActivity.flg_alert=1;

        /*new AlertDialog.Builder(view.getContext(),R.style.MyAlertDialogStyle)
                .setTitle("")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                result.confirm();
                            }
                        })
                .setNegativeButton(android.R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                result.cancel();
                            }
                        })
                .setCancelable(false)
                .create()
                .show();*/
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        // Set a title for alert dialog
        builder.setTitle("");
        // Show a message on alert dialog
        builder.setMessage(message);
        // Set the positive button
        builder.setPositiveButton("확인",   new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                result.confirm();
            }
        });

        // Set the negative button
        builder.setNegativeButton("취소",  new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                result.cancel();
            }
        });
        builder.setCancelable(false);
        // Create the alert dialog
        AlertDialog dialog = builder.create();
        // Finally, display the alert dialog
     //   mainActivity.current_dialog = dialog;
        dialog.show();

        // Get the alert dialog buttons reference
        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);

        // Change the alert dialog buttons text and background color
        positiveButton.setTextColor(Color.parseColor("#9dc543"));
        negativeButton.setTextColor(Color.parseColor("#ff0000"));
        return true;
    }
}
