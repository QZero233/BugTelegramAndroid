package com.qzero.telegram.view.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.jakewharton.rxbinding4.view.RxView;
import com.qzero.telegram.R;
import com.qzero.telegram.contract.InputSessionInfoContract;
import com.qzero.telegram.dao.entity.ChatSessionParameter;
import com.qzero.telegram.presenter.session.NormalSessionInfoInputPresenter;
import com.qzero.telegram.presenter.session.PersonalSessionInfoInputPresenter;
import com.qzero.telegram.view.BaseActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

public class InputSessionInfoActivity extends BaseActivity implements InputSessionInfoContract.View {

    @BindView(R.id.btn_submit)
    public Button btn_submit;

    @BindView(R.id.ll_input)
    public LinearLayout ll_input;

    @BindView(R.id.et_session_name)
    public TextInputEditText et_session_name;

    @BindView(R.id.sp_dst_user)
    public Spinner sp_dst_user;

    private InputSessionInfoContract.Presenter presenter;

    private String sessionType;

    private String[] friendNameArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_session_info);

        ButterKnife.bind(this);

        sessionType=getIntent().getStringExtra("sessionType");
        switch (sessionType){
            case ChatSessionParameter.SESSION_TYPE_NORMAL:
                presenter=new NormalSessionInfoInputPresenter();
                break;
            case ChatSessionParameter.SESSION_TYPE_PERSONAL:
                presenter=new PersonalSessionInfoInputPresenter();
                break;
            default:
                exit();
                showToast("暂时不支持创建该类型的会话");
                return;
        }

        presenter.attachView(this);

        RxView.clicks(btn_submit)
                .subscribe(u -> {
                    List<ChatSessionParameter> parameterList=new ArrayList<>();
                    parameterList.add(new ChatSessionParameter(null,null,ChatSessionParameter.NAME_SESSION_TYPE,sessionType));
                    parameterList.add(new ChatSessionParameter(null,null,ChatSessionParameter.NAME_SESSION_NAME,
                            et_session_name.getText().toString()));

                    switch (sessionType){
                        case ChatSessionParameter.SESSION_TYPE_NORMAL:
                            submitForNormalSession(parameterList);
                            break;
                        case ChatSessionParameter.SESSION_TYPE_PERSONAL:
                            submitForPersonalSession(parameterList);
                            break;
                    }

                });
    }

    private void submitForNormalSession(List<ChatSessionParameter> preset){
        presenter.submit(preset);
    }

    private void submitForPersonalSession(List<ChatSessionParameter> preset){
        String dstUserName=friendNameArray[sp_dst_user.getSelectedItemPosition()];
        preset.add(0,new ChatSessionParameter(null,null,"dstUserName",dstUserName));
        presenter.submit(preset);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(presenter!=null)
            presenter.detachView();
    }

    @Override
    public void showNormalSessionInput() {

    }

    @Override
    public void showPersonalSessionInput() {
        sp_dst_user.setVisibility(View.VISIBLE);
        friendNameArray=presenter.getFriendNames();
        if(friendNameArray==null || friendNameArray.length==0){
            showToast("你还无好友，请前往添加");
            exit();
            return;
        }
        sp_dst_user.setAdapter(new ArrayAdapter(getContext(),R.layout.view_personal_info_sp_tv,friendNameArray));
    }
}