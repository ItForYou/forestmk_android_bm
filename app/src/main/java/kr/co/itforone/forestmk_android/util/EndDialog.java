package kr.co.itforone.forestmk_android.util;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import butterknife.ButterKnife;
import kr.co.itforone.forestmk_android.R;

public class EndDialog extends Dialog {
    private Activity mContext;

    public EndDialog(Activity context) {
        super(context);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_exit);
        ButterKnife.bind(this);

    }
}
