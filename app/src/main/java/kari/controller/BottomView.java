package kari.controller;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

/**
 * Created by ws-kari on 15-4-20.
 */
public class BottomView extends RelativeLayout implements View.OnClickListener {
    final static String TAG = "LoginFragment";

    private MainActivity activity;
    private EditText mEditIp;
    private EditText mEditPort;
    private EditText mEditToken;
    private Button mBtnLogin;

    public BottomView(Context context) {
        super(context);
        initView(context);
    }

    public BottomView(Context context, AttributeSet attr) {
        super(context, attr);
        initView(context);
    }

    public void initView(Context context) {
        activity = (MainActivity) context;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.view_bottom, this);
        mEditIp = (EditText) view.findViewById(R.id.login_edit_ip);
        mEditPort = (EditText) view.findViewById(R.id.login_edit_port);
        mEditToken = (EditText) view.findViewById(R.id.login_et_token);
        mBtnLogin = (Button) view.findViewById(R.id.login_btn_login);
        mBtnLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        setVisibility(View.GONE);
        activity.mTalkManager.init(mEditIp.getText().toString(),
                Integer.parseInt(mEditPort.getText().toString()),
                Integer.parseInt(mEditToken.getText().toString()));
    }
}
