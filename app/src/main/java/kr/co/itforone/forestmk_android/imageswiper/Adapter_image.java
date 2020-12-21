package kr.co.itforone.forestmk_android.imageswiper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.ArrayList;
import kr.co.itforone.forestmk_android.R;

public class Adapter_image extends RecyclerView.Adapter<ViewHolderPage>{


    private ArrayList<ListSrc> listdata;

    Adapter_image(ArrayList<ListSrc> data) {
        this.listdata = data;
    }

    @NonNull
    @Override
    public ViewHolderPage onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_page, parent, false);
        return new ViewHolderPage(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderPage holder, int position) {
        if(holder instanceof ViewHolderPage){
            ViewHolderPage viewHolder = (ViewHolderPage) holder;
            Glide.with(holder.itemView.getContext())
                    .load(listdata.get(position).getSrc())
                    .into(holder.imageview);
        }
    }

    @Override
    public int getItemCount() {
        return listdata.size();
    }
}
