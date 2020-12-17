package kr.co.itforone.forestmk_android.imageswiper;

import android.graphics.RectF;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.OnMatrixChangedListener;
import com.github.chrisbanes.photoview.OnScaleChangedListener;
import com.github.chrisbanes.photoview.OnViewDragListener;
import com.github.chrisbanes.photoview.PhotoView;
import com.github.chrisbanes.photoview.PhotoViewAttacher;

import kr.co.itforone.forestmk_android.R;

public class ViewHolderPage extends RecyclerView.ViewHolder{

    public PhotoView imageview;

    public ViewHolderPage(@NonNull View itemView) {
        super(itemView);
        imageview = itemView.findViewById(R.id.photoimage);
        imageview.setOnScaleChangeListener(new OnScaleChangedListener() {
            @Override
            public void onScaleChange(float scaleFactor, float focusX, float focusY) {
                Log.d("onScale",String.valueOf(scaleFactor));
            }
        });
    }

}
