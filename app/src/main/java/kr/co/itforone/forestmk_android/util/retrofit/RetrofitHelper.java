package kr.co.itforone.forestmk_android.util.retrofit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitHelper {

    public static Retrofit getRetrofit(){

        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        //클라이언트가 서버를 연결할 때 필요한 객체
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(httpLoggingInterceptor)//json파싱을 문서 파싱 된 것을 console에 띄우기 위함
                .build();


        return new Retrofit.Builder()
                .baseUrl("http://www.forestmarket.co.kr")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

    }


}
