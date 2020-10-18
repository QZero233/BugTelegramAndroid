package com.qzero.telegram.view.fragment;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.textfield.TextInputEditText;
import com.jakewharton.rxbinding4.view.RxView;
import com.qzero.telegram.R;
import com.qzero.telegram.contract.PersonalInfoContract;
import com.qzero.telegram.dao.entity.UserInfo;
import com.qzero.telegram.presenter.PersonalInfoPresenter;
import com.qzero.telegram.view.BaseFragment;
import com.qzero.telegram.view.activity.UserCenterActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PersonalInfoFragment extends BaseFragment implements PersonalInfoContract.View {

    @BindView(R.id.tv_user_name)
    public TextView tv_user_name;
    @BindView(R.id.tv_group)
    public TextView tv_group;
    @BindView(R.id.sp_status)
    public Spinner sp_status;
    @BindView(R.id.et_motto)
    public TextInputEditText et_motto;
    @BindView(R.id.btn_submit)
    public Button btn_submit;

    private PersonalInfoContract.Presenter presenter;

    private UserInfo currentInfo;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=View.inflate(getContext(), R.layout.fragment_personal_info,null);
        ButterKnife.bind(this,view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        presenter=new PersonalInfoPresenter();
        presenter.attachView(this);

        sp_status.setAdapter(new ArrayAdapter<>(getContext(),R.layout.view_personal_info_sp_tv,getResources().getStringArray(R.array.array_status)));

        presenter.loadPersonalInfo();

        RxView.clicks(btn_submit)
                .subscribe(o -> {
                    currentInfo.setMotto(et_motto.getText().toString());
                    currentInfo.setAccountStatus(sp_status.getSelectedItemPosition());
                    presenter.updatePersonalInfo(currentInfo);
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.detachView();
    }

    @Override
    public void loadPersonalInfo(UserInfo userInfo) {
        currentInfo=userInfo;
        tv_user_name.setText(userInfo.getUserName());

        Resources resources = getResources();
        String[] groupLevelString=resources.getStringArray(R.array.array_groups);

        if(userInfo.getGroupLevel()<=groupLevelString.length-1)
            tv_group.setText("用户组别: "+groupLevelString[userInfo.getGroupLevel()]);
        else
            tv_group.setText("未知组别");

        sp_status.setSelection(userInfo.getAccountStatus());

        et_motto.setText(userInfo.getMotto());
    }

    @Override
    public void disableSubmitButton() {
        btn_submit.setEnabled(false);
    }

    @Override
    public void gotoMainFragmentAndReloadInfo(UserInfo userInfo) {
        UserCenterActivity activity= (UserCenterActivity) getActivity();
        activity.gotoMainFragment();
        if(userInfo!=null)
            activity.reloadPersonalInfo(userInfo);
    }


}
