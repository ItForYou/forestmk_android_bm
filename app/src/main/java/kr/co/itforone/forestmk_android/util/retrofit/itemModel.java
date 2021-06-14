package kr.co.itforone.forestmk_android.util.retrofit;

import java.util.ArrayList;
import kr.co.itforone.forestmk_android.imageswiper.ListSrc;

public class itemModel {

    public ArrayList<String> list_path;
    public ArrayList<String> list_link;
    public ArrayList<Integer> list_idx;

    public int total_wr;

    public ArrayList<String> getList_path() {
        return list_path;
    }
    public ArrayList<String> getList_link() {
        return list_link;
    }
    public ArrayList<Integer> getList_idx() {
        return list_idx;
    }

}
