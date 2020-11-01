package com.qzero.telegram.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.jakewharton.rxbinding4.widget.RxAdapterView;
import com.jakewharton.rxbinding4.widget.RxSearchView;
import com.qzero.telegram.R;
import com.qzero.telegram.contract.FriendListContract;
import com.qzero.telegram.dao.entity.UserInfo;
import com.qzero.telegram.presenter.FriendListPresenter;
import com.qzero.telegram.view.BaseFragment;
import com.qzero.telegram.view.activity.UserInfoDetailActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FriendListFragment extends BaseFragment implements FriendListContract.View {

    @BindView(R.id.sv_user_name)
    public SearchView sv_user_name;
    @BindView(R.id.lv_friends)
    public ListView lv_friends;

    private FriendListContract.Presenter presenter;

    private List<UserInfo> friendList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=View.inflate(getContext(),R.layout.fragment_friend_list,null);

        ButterKnife.bind(this,view);

        presenter=new FriendListPresenter();
        presenter.attachView(this);

        presenter.loadLocalFriendList();

        sv_user_name.setSubmitButtonEnabled(true);
        sv_user_name.setQueryHint("添加");

        RxSearchView.queryTextChangeEvents(sv_user_name)
                .subscribe(event ->{
                    if(event.isSubmitted()){
                        String userName=event.getQueryText().toString();
                        presenter.findUser(userName);
                    }
                });

        RxAdapterView.itemClickEvents(lv_friends)
                .subscribe(event -> {
                    if(friendList!=null){
                        UserInfo userInfo=friendList.get(event.getPosition());
                        Intent intent=new Intent(getContext(), UserInfoDetailActivity.class);
                        intent.putExtra("userName",userInfo.getUserName());
                        startActivity(intent);
                    }
                });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.loadLocalFriendList();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.detachView();
    }

    @Override
    public void loadLocalFriendList(List<UserInfo> friendList) {
        if(friendList==null || friendList.isEmpty())
            return;

        this.friendList=friendList;

        lv_friends.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return friendList.size();
            }

            @Override
            public Object getItem(int position) {
                return null;
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                UserInfo userInfo=friendList.get(position);

                TextView tv=new TextView(getContext());
                tv.setText(userInfo.getUserName());
                tv.setTextSize(20);

                return tv;
            }
        });
    }
}
