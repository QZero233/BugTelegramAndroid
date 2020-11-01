package com.qzero.telegram.view.activity;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.qzero.telegram.R;
import com.qzero.telegram.contract.UserCenterContract;
import com.qzero.telegram.dao.entity.UserInfo;
import com.qzero.telegram.notice.NoticeMonitorService;
import com.qzero.telegram.presenter.UserCenterPresenter;
import com.qzero.telegram.view.BaseActivity;
import com.qzero.telegram.view.fragment.FriendListFragment;
import com.qzero.telegram.view.fragment.PersonalInfoFragment;
import com.qzero.telegram.view.fragment.SessionFragment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserCenterActivity extends BaseActivity implements UserCenterContract.View, AdapterView.OnItemClickListener {

    private Logger log= LoggerFactory.getLogger(getClass());

    private UserCenterContract.Presenter presenter;

    @BindView(R.id.lv_menu)
    public ListView lv_menu;
    @BindView(R.id.fl_content)
    public FrameLayout fl_content;
    @BindView(R.id.dl_user_center)
    public DrawerLayout dl_user_center;

    private View header;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_center);

        ButterKnife.bind(this);

        log.debug("Started service");
        startService(new Intent(this, NoticeMonitorService.class));

        presenter=new UserCenterPresenter();
        presenter.attachView(this);

        presenter.loadPersonalInfo();

        SessionFragment sessionFragment=new SessionFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_content,sessionFragment).commit();

        lv_menu.setOnItemClickListener(this);
        lv_menu.setAdapter(new ArrayAdapter<>(getContext(),R.layout.view_user_center_drawer_tv,new String[]{"会话","好友列表","设置"}));
    }


    @Override
    public void showPersonalInfo(UserInfo userInfo) {
        lv_menu.removeHeaderView(header);

        header=View.inflate(getContext(),R.layout.view_user_center_drawer_info,null);
        TextView tv_user_name=header.findViewById(R.id.tv_user_name);
        TextView tv_group=header.findViewById(R.id.tv_group);
        TextView tv_motto=header.findViewById(R.id.tv_motto);
        TextView tv_status=header.findViewById(R.id.tv_status);

        tv_user_name.setText(userInfo.getUserName());

        Resources resources = getResources();
        String[] groupLevelString=resources.getStringArray(R.array.array_groups);

        if(userInfo.getGroupLevel()<=groupLevelString.length-1)
            tv_group.setText("用户组别: "+groupLevelString[userInfo.getGroupLevel()]);
        else
            tv_group.setText("未知组别");

        tv_motto.setText("个性签名: "+userInfo.getMotto());

        String[] statusArray=resources.getStringArray(R.array.array_status);
        if(userInfo.getAccountStatus()<=statusArray.length-1)
            tv_status.setText("用户状态: "+statusArray[userInfo.getAccountStatus()]);
        else
            tv_status.setText("未知状态");


        lv_menu.addHeaderView(header);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Fragment newFragment=null;
        switch (position){
            case 0:
                newFragment=new PersonalInfoFragment();
                break;
            case 1:
                newFragment=new SessionFragment();
                break;
            case 2:
                newFragment=new FriendListFragment();
                break;
            case 3:
                //TODO PUT SETTINGS
                break;
        }
        if(newFragment!=null)
            getSupportFragmentManager().beginTransaction().replace(R.id.fl_content,newFragment).commit();
        dl_user_center.closeDrawers();
    }

    public void gotoMainFragment(){
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_content,new SessionFragment()).commit();
    }

    public void reloadPersonalInfo(UserInfo userInfo){
        lv_menu.removeHeaderView(header);
        showPersonalInfo(userInfo);
    }
}