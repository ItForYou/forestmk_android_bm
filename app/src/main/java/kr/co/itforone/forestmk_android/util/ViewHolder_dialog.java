package kr.co.itforone.forestmk_android.util;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.chrisbanes.photoview.OnScaleChangedListener;
import com.github.chrisbanes.photoview.PhotoView;

import kr.co.itforone.forestmk_android.R;

public class ViewHolder_dialog extends RecyclerView.ViewHolder {

    public ImageView imageview;

    public ViewHolder_dialog(@NonNull View itemView) {
        super(itemView);
        imageview = itemView.findViewById(R.id.banner_img);

    }
}
