package kr.co.itforone.forestmk_android.util;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.viewpager2.widget.ViewPager2;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import kr.co.itforone.forestmk_android.R;
import kr.co.itforone.forestmk_android.imageswiper.Adapter_image;
import kr.co.itforone.forestmk_android.util.retrofit.RetrofitHelper;
import kr.co.itforone.forestmk_android.util.retrofit.getBannerService;
import kr.co.itforone.forestmk_android.util.retrofit.itemModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EndDialog extends Dialog {
    private Activity mContext;
    itemModel model;
    @BindView(R.id.dialog_viewpager)    ViewPager2 dialog_viewpager;
    @BindView(R.id.progress_banner)    ProgressBar progress_banner;

    public EndDialog(Activity context) {
        super(context);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_exit);
        ButterKnife.bind(this);

        Map<String, String> map = new HashMap<>();
        map.put("flg", "getDialog");

        //Log.d("map_wrid", map.toString());

        getBannerService networkService = RetrofitHelper.getRetrofit().create(getBannerService.class);
        Call<itemModel> call = networkService.createPost(map);
        call.enqueue(new Callback<itemModel>() {

            @Override
            public void onResponse(Call<itemModel> call, Response<itemModel> response) {

                Log.d("suc!!",response.body().list_idx.toString());
                model = response.body();
                Adapter_dialog adapter_dialog;
                ArrayList<String> none = new ArrayList<String>();
                none.add("none");
                if(model.getList_path().size()>0) {
                    adapter_dialog = new Adapter_dialog(model.getList_path(), model.getList_link(), model.getList_idx(), EndDialog.this);
                }
                else{
                    adapter_dialog = new Adapter_dialog(none);
                }
                dialog_viewpager.setAdapter(adapter_dialog);
               /* dialog_viewpager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {

                    private int myState;
                    private int currentPosition;

                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                        super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                        if (myState == ViewPager2.SCROLL_STATE_DRAGGING && currentPosition == position && currentPosition == 0)
                            dialog_viewpager.setCurrentItem(model.list_link.size()-1);
                        else if (myState == ViewPager2.SCROLL_STATE_DRAGGING && currentPosition == position && currentPosition == 2)
                            dialog_viewpager.setCurrentItem(0);

                    }

                    @Override
                    public void onPageSelected(int position) {
                        currentPosition = position;
                        super.onPageSelected(position);
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {
                        myState = state;
                        super.onPageScrollStateChanged(state);
                    }
                });*/

            }

            @Override
            public void onFailure(Call<itemModel> call, Throwable t) {

                Log.d("fail!!",t.toString());

            }

        });

    }
    public class ZoomOutPageTransformer implements ViewPager2.PageTransformer {
        private static final float MIN_SCALE = 0.85f;
        private static final float MIN_ALPHA = 0.5f;


        @Override
        public void transformPage(@NonNull @NotNull View page, float position) {

            int pageWidth = dialog_viewpager.getWidth();
            int pageHeight = dialog_viewpager.getHeight();

            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                dialog_viewpager.setAlpha(0f);

            } else if (position <= 1) { // [-1,1]
                // Modify the default slide transition to shrink the page as well
                float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
                float vertMargin = pageHeight * (1 - scaleFactor) / 2;
                float horzMargin = pageWidth * (1 - scaleFactor) / 2;
                if (position < 0) {
                    dialog_viewpager.setTranslationX(horzMargin - vertMargin / 2);
                } else {
                    dialog_viewpager.setTranslationX(-horzMargin + vertMargin / 2);
                }

                // Scale the page down (between MIN_SCALE and 1)
                dialog_viewpager.setScaleX(scaleFactor);
                dialog_viewpager.setScaleY(scaleFactor);

                // Fade the page relative to its size.
                dialog_viewpager.setAlpha(MIN_ALPHA +
                        (scaleFactor - MIN_SCALE) /
                                (1 - MIN_SCALE) * (1 - MIN_ALPHA));

            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                dialog_viewpager.setAlpha(0f);
            }

        }
    }
}
