package kr.co.itforone.forestmk_android.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import kr.co.itforone.forestmk_android.R;



public class Adapter_dialog extends RecyclerView.Adapter<ViewHolder_dialog> {



    private ArrayList<String> listdata;

    Adapter_dialog(ArrayList<String> data) {
        this.listdata = data;
    }

    @NonNull
    @Override
    public ViewHolder_dialog onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
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
            }


        }

    }

    @Override
    public int getItemCount() {
        return listdata.size();
    }

}
