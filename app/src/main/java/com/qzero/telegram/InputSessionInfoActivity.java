package com.qzero.telegram;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.jakewharton.rxbinding4.view.RxView;
import com.qzero.telegram.contract.InputSessionInfoContract;
import com.qzero.telegram.dao.entity.ChatSessionParameter;
import com.qzero.telegram.presenter.session.NormalSessionInputPresenter;
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

    private InputSessionInfoContract.Presenter presenter;

    private Map<String,TextInputEditText> inputMap=new HashMap<>();

    private String sessionType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_session_info);

        ButterKnife.bind(this);

        sessionType=getIntent().getStringExtra("sessionType");
        switch (sessionType){
            case ChatSessionParameter.SESSION_TYPE_NORMAL:
                presenter=new NormalSessionInputPresenter();
                break;
            default:
                exit();
                showToast("暂时不支持创建该类型的会话");
                return;
        }

        presenter.attachView(this);

        inputMap.put(ChatSessionParameter.NAME_SESSION_NAME,et_session_name);

        RxView.clicks(btn_submit)
                .subscribe(u -> {
                    List<ChatSessionParameter> parameterList=new ArrayList<>();

                    parameterList.add(new ChatSessionParameter(null,null,ChatSessionParameter.NAME_SESSION_TYPE,sessionType));

                    Set<String> keySet=inputMap.keySet();
                    for(String key:keySet){
                        TextInputEditText et=inputMap.get(key);
                        String parameterValue=et.getText().toString();
                        parameterList.add(new ChatSessionParameter(null,null,key,parameterValue));
                    }

                    presenter.submit(parameterList);
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(presenter!=null)
            presenter.detachView();
    }

    @Override
    public void addNormalTextInput(String parameterName,String hint) {
        if(ll_input==null)
            return;

        TextInputLayout textInputLayout= (TextInputLayout) View.inflate(getContext(),R.layout.view_normal_et,null);
        TextInputEditText et=textInputLayout.findViewById(R.id.et);
        et.setHint(hint);

        inputMap.put(parameterName,et);

        ll_input.addView(textInputLayout);
    }
}