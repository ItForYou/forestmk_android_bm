package kr.co.itforone.forestmk_android.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import kr.co.itforone.forestmk_android.R;
import kr.co.itforone.forestmk_android.util.retrofit.RetrofitHelper;
import kr.co.itforone.forestmk_android.util.retrofit.addClickService;
import kr.co.itforone.forestmk_android.util.retrofit.itemModel;
import kr.co.itforone.forestmk_android.util.retrofit.responseClickModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Adapter_dialog extends RecyclerView.Adapter<ViewHolder_dialog> {



    private ArrayList<String> listdata;
    private ArrayList<String> listdata2;
    private ArrayList<Integer> listdata3;
    responseClickModel model;
    Context context;
    Adapter_dialog(ArrayList<String> data, ArrayList<String> data2, ArrayList<Integer> data3) {
        this.listdata = data;
        this.listdata2 = data2;
        this.listdata3 = data3;
    }
    Adapter_dialog(ArrayList<String> data, ArrayList<String> data2) {
        this.listdata = data;
        this.listdata2 = data2;
    }

    Adapter_dialog(ArrayList<String> data) {
        this.listdata = data;
    }

    @NonNull
    @Override
    public ViewHolder_dialog onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
         context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_banner, parent, false);
        return new ViewHolder_dialog(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder_dialog holder, int position) {

        if(holder instanceof ViewHolder_dialog){
            ViewHolder_dialog viewHolder = (ViewHolder_dialog) holder;
            if(listdata.get(0).equals("none")){
                Glide.with(holder.itemView.getContext())
                        .load(R.drawable.noimg)
                        .into(holder.imageview);
            }
            else {
                Glide.with(holder.itemView.getContext())
                        .load(listdata.get(position))
                        .into(holder.imageview);
                holder.imageview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(listdata2.get(position)));
                        i.setPackage("com.android.chrome");
                        holder.itemView.getContext().startActivity(i);

                        Map<String, Object> map = new HashMap<>();
                        map.put("idx", listdata3.get(position));
                        map.put("from", "android");

                        addClickService networkService = RetrofitHelper.getRetrofit().create(addClickService.class);
                        Call<responseClickModel> call = networkService.createPost(map);
                        call.enqueue(new Callback<responseClickModel>() {

                            @Override
                            public void onResponse(Call<responseClickModel> call, Response<responseClickModel> response) {

                                Log.d("suc!!",String.valueOf(response.body().result_code));
                                model = response.body();

                                switch (model.getResult_code()){

                                    case -1:
                                        if(context!=null)
                                                Toast.makeText(context,"배너 아이디 값이 존재하지 않습니다.",Toast.LENGTH_LONG).show();
                                        break;
                                    case 0:
                                        if(context!=null)
                                            Toast.makeText(context,"배너가 삭제 되었습니다.",Toast.LENGTH_LONG).show();
                                        break;
                                    default: break;
                                }

                            }

                            @Override
                            public void onFailure(Call<responseClickModel> call, Throwable t) {

                                Log.d("fail!!",t.toString());

                            }

                        });






                    }
                });

            }



        }



    }

    @Override
    public int getItemCount() {
        return listdata.size();
    }

}
