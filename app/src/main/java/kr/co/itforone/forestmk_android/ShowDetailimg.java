package kr.co.itforone.forestmk_android;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.OnMatrixChangedListener;
import com.github.chrisbanes.photoview.OnOutsidePhotoTapListener;
import com.github.chrisbanes.photoview.OnScaleChangedListener;
import com.github.chrisbanes.photoview.OnSingleFlingListener;
import com.github.chrisbanes.photoview.OnViewDragListener;
import com.github.chrisbanes.photoview.PhotoView;
import com.github.chrisbanes.photoview.PhotoViewAttacher;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import kr.co.itforone.forestmk_android.util.ActivityManager;

public class ShowDetailimg extends AppCompatActivity {
    @BindView(R.id.detail_img)    ImageView detail_img;
    @BindView(R.id.bt_saveimg)    Button bt_saveimg;
    PhotoViewAttacher mAttacher;
    private ActivityManager am = ActivityManager.getInstance();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saveimg);
        ButterKnife.bind(this);
        am.addActivity(this);
        Intent i = getIntent();
        String src = "";

        if(i!=null){
            src = i.getStringExtra("src");
        }

        if(!src.isEmpty() && !src.equals("")) {

            Glide.with(this)
                    .load(src)
                    .into(detail_img);

            mAttacher = new PhotoViewAttacher(detail_img);
            mAttacher.setMinimumScale(1);
            mAttacher.setScaleType(ImageView.ScaleType.FIT_XY);

        }
    }

    public void saveimg(View view){

        if(view.getId() ==R.id.bt_saveimg){
            detail_img.setDrawingCacheEnabled(true);
            Bitmap bitmap = detail_img.getDrawingCache();

            if(bitmap!=null) {
                Log.d("bt_click",bitmap.toString());
                String dirPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
                        + File.separator + getString(R.string.app_name) + File.separator;
                //   Toast.makeText(mainActivity.getApplicationContext(), dirPath, Toast.LENGTH_SHORT).show();
                String file_name = System.currentTimeMillis() + ".jpg";
                //Toast.makeText(mainActivity.getApplicationContext(), "Saving Image...", Toast.LENGTH_SHORT).show();
                saveImage(bitmap, dirPath, file_name);
            }
            else{
                Log.d("bt_click","null!");
            }

        }
    }

    private void scanFile(String path) {

        MediaScannerConnection.scanFile(this,
                new String[] { path }, null,
                new MediaScannerConnection.OnScanCompletedListener() {

                    public void onScanCompleted(String path, Uri uri) {
                        Log.i("TAG", "Finished scanning " + path);
                    }
                });
    }

    private void saveImage(Bitmap image, String storageDir, String imageFileName){

        File imageFile = new File(storageDir, imageFileName);
        String savedImagePath = imageFile.getAbsolutePath();
        //   Toast.makeText(mainActivity.getApplicationContext(), imageFile.toString(), Toast.LENGTH_SHORT).show();

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

            final File file_path;

            try {

                file_path = new File(storageDir);
                if (!file_path.isDirectory()) {
                    file_path.mkdirs();
                }
                File ImageFile = new File(file_path, imageFileName);
                OutputStream fOut = new FileOutputStream(ImageFile);
                image.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                fOut.flush();
                fOut.close();
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {// A
                    scanFile(storageDir);
                }
                else{
                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(ImageFile)));
                }

                Toast.makeText(getApplicationContext(), "이미지가 저장되었습니다. 갤러리를 확인해주세요.", Toast.LENGTH_SHORT).show();


            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Error while saving image!" + e.toString(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

        }
    }


}
