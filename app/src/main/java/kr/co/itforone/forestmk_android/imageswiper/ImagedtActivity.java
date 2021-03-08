package kr.co.itforone.forestmk_android.imageswiper;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import java.util.HashMap;
import java.util.Map;
import butterknife.BindView;
import butterknife.ButterKnife;
import kr.co.itforone.forestmk_android.R;
import kr.co.itforone.forestmk_android.imageswiper.retrofit.RetrofitHelper;
import kr.co.itforone.forestmk_android.imageswiper.retrofit.RetrofitService;
import kr.co.itforone.forestmk_android.imageswiper.retrofit.itemModel;
import kr.co.itforone.forestmk_android.util.ActivityManager;
import me.relex.circleindicator.CircleIndicator3;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ImagedtActivity extends AppCompatActivity {

    private ActivityManager am = ActivityManager.getInstance();
    @BindView(R.id.swiperimg)
    ViewPager2 swiperimg;
    @BindView(R.id.indicator)    CircleIndicator3 indicator;
    String wr_id = "",notice="";
    int current=0;
    itemModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imagedt);
        ButterKnife.bind(this);
        am.addActivity(this);

        Intent i = getIntent();
        if(i!=null){
            this.wr_id = i.getStringExtra("wr_id");
            this.notice = i.getStringExtra("notice");
            this.current = i.getIntExtra("current",0);
        }

        Map<String, String> map = new HashMap<>();
        map.put("wr_id", wr_id);
        if(!notice.isEmpty() && !notice.equals("")){
            map.put("notice", notice);
            map.put("bf_no", String.valueOf(current));
        }

        Log.d("map_wrid", map.toString());

        if(wr_id!=null && !wr_id.isEmpty()) {

            RetrofitService networkService = RetrofitHelper.getRetrofit().create(RetrofitService.class);
            Call<itemModel> call = networkService.createPost(map);
            call.enqueue(new Callback<itemModel>() {
                @Override
                public void onResponse(Call<itemModel> call, Response<itemModel> response) {

                    if (response.isSuccessful()) {
                            Log.d("response_retro" , String.valueOf(response.body().writes.get(0).src));
                            model = response.body();

                            Adapter_image adapter_image = new Adapter_image(model.getWrites());
                            swiperimg.setAdapter(adapter_image);
                            if(adapter_image.getItemCount()>1) {
                                indicator.setViewPager(swiperimg);
                                indicator.createIndicators(model.total_wr, 0);
                            }
                            swiperimg.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                                @Override
                                public void onPageSelected(int position) {
                                    super.onPageSelected(position);
                                    if(adapter_image.getItemCount()>1) {
                                        indicator.animatePageSelected(position);
                                    }
                                }
                            });

                            if(adapter_image.getItemCount()>1) {
                                swiperimg.setCurrentItem(current,false);
                            }

                    } else {
                        Log.d("result_call_fail", String.valueOf(response.isSuccessful()));
                        Toast.makeText(ImagedtActivity.this, "이미지를 불러오는데 실패하였습니다.", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                }

                @Override
                public void onFailure(Call<itemModel> call, Throwable t) {
                    Log.d("result_call_fail", t.toString());
                    Toast.makeText(ImagedtActivity.this, "이미지를 불러오는데 실패하였습니다.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });

        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
