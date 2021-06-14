package kr.co.itforone.forestmk_android.util.retrofit;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface addClickService {
    @FormUrlEncoded
    @POST("bbs/ajax.add.costperclick.php")
    Call<responseClickModel> createPost(@FieldMap Map<String, Object> fields);

}
