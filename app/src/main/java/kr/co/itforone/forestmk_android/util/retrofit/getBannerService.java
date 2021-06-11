package kr.co.itforone.forestmk_android.util.retrofit;

import java.util.Map;

import kr.co.itforone.forestmk_android.util.retrofit.itemModel;
import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface getBannerService {

    @FormUrlEncoded
    @POST("get_banner.php")
    Call<itemModel> createPost(@FieldMap Map<String, String> fields);

}
