package com.qzero.telegram.view.activity;

import android.content.res.Resources;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.jakewharton.rxbinding4.view.RxView;
import com.qzero.telegram.R;
import com.qzero.telegram.contract.UserInfoDetailContract;
import com.qzero.telegram.dao.entity.UserInfo;
import com.qzero.telegram.presenter.UserInfoDetailPresenter;
import com.qzero.telegram.view.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserInfoDetailActivity extends BaseActivity implements UserInfoDetailContract.View {

    @BindView(R.id.tv_user_name)
    public TextView tv_user_name;
    @BindView(R.id.tv_group)
    public TextView tv_group;
    @BindView(R.id.tv_status)
    public TextView tv_status;
    @BindView(R.id.tv_motto)
    public TextView tv_motto;
    @BindView(R.id.btn_delete)
    public Button btn_delete;

    private UserInfoDetailContract.Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info_detail);

        ButterKnife.bind(this);

        presenter=new UserInfoDetailPresenter();
        presenter.attachView(this);

        String userName=getIntent().getStringExtra("userName");
        presenter.loadUserInfoDetail(userName);

        RxView.clicks(btn_delete)
                .subscribe(u -> {
                    presenter.delete(userName);
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detachView();
    }


    @Override
    public void loadUserInfoDetail(UserInfo userInfo) {
        if(userInfo==null)
            return;

        tv_user_name.setText(userInfo.getUserName());
        tv_motto.setText("个性签名: "+userInfo.getMotto());

        Resources resources = getResources();
        String[] groupLevelString=resources.getStringArray(R.array.array_groups);

        if(userInfo.getGroupLevel()<=groupLevelString.length-1)
            tv_group.setText("用户组别: "+groupLevelString[userInfo.getGroupLevel()]);
        else
            tv_group.setText("未知组别");

        String[] statusArray=resources.getStringArray(R.array.array_status);
        if(userInfo.getAccountStatus()<=statusArray.length-1)
            tv_status.setText("用户状态: "+statusArray[userInfo.getAccountStatus()]);
        else
            tv_status.setText("未知状态");
    }
}