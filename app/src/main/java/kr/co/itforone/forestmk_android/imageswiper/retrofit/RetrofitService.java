package kr.co.itforone.forestmk_android.imageswiper.retrofit;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface RetrofitService {

    @FormUrlEncoded
    @POST("retrofit_test.php")
    Call<itemModel> createPost(@FieldMap Map<String, String> fields);

}

