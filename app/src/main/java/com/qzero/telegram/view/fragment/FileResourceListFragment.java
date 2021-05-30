package com.qzero.telegram.view.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jakewharton.rxbinding4.view.RxView;
import com.jakewharton.rxbinding4.widget.RxAdapterView;
import com.leon.lfilepickerlibrary.LFilePicker;
import com.leon.lfilepickerlibrary.utils.Constant;
import com.qzero.telegram.R;
import com.qzero.telegram.contract.FileResourceListContract;
import com.qzero.telegram.dao.entity.FileResource;
import com.qzero.telegram.presenter.FileResourceListPresenter;
import com.qzero.telegram.view.BaseFragment;
import com.qzero.telegram.view.activity.FileResourceDetailActivity;
import com.qzero.telegram.view.activity.TransportTaskActivity;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import pub.devrel.easypermissions.EasyPermissions;

public class FileResourceListFragment extends BaseFragment implements FileResourceListContract.View {

    @BindView(R.id.lv_file_resource)
    public ListView lv_file_resource;
    @BindView(R.id.fb_add)
    public FloatingActionButton fb_add;

    private FileResourceListContract.Presenter presenter;

    private List<FileResource> fileResourceList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=View.inflate(getContext(), R.layout.fragment_file_resource_list,null);

        setHasOptionsMenu(true);

        checkPermission();

        ButterKnife.bind(this,view);

        presenter=new FileResourceListPresenter();
        presenter.attachView(this);

        presenter.loadAllFileResource();

        RxView.clicks(fb_add).subscribe(u -> {
            new LFilePicker()
                    .withSupportFragment(FileResourceListFragment.this)
                    .withRequestCode(8848)
                    .withMutilyMode(false)
                    .withTitle("Please select file")
                    .start();

        });

        RxAdapterView.itemClickEvents(lv_file_resource).subscribe(event -> {
            FileResource fileResource=fileResourceList.get(event.getPosition());

            Intent intent=new Intent(getContext(), FileResourceDetailActivity.class);
            intent.putExtra("fileResource",fileResource);

            startActivity(intent);
        });

        RxAdapterView.itemLongClickEvents(lv_file_resource).subscribe(event -> {
            FileResource fileResource=fileResourceList.get(event.getPosition());

            AlertDialog.Builder builder=new AlertDialog.Builder(getContext());

            builder.setTitle("是否删除")
                    .setMessage("是否删除文件 "+fileResource.getResourceName()+"\n此操作不可逆")
                    .setNegativeButton("取消",null)
                    .setPositiveButton("确认", (dialog, which) -> {
                        presenter.deleteFileResource(fileResource.getResourceId());
                    })
                    .show();
        });

        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_file_resource_list,menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId()==R.id.m_task){
            startActivity(new Intent(getContext(), TransportTaskActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    private void checkPermission(){
        if(!EasyPermissions.hasPermissions(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            EasyPermissions.requestPermissions(this,"Hello",9999,Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==8848){
            if (resultCode!=Activity.RESULT_OK){
                showToast("None is selected");
                return;
            }

            List<String> list = data.getStringArrayListExtra(Constant.RESULT_INFO);
            String path=list.get(0);
            File file=new File(path);

            presenter.newFileResource(file);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.detachView();
    }

    @Override
    public void loadAllFileResource(List<FileResource> fileResourceList) {
        if(fileResourceList==null || fileResourceList.isEmpty())
            return;

        this.fileResourceList=fileResourceList;

        lv_file_resource.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return fileResourceList.size();
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
                FileResource fileResource=fileResourceList.get(position);

                TextView tv=new TextView(getContext());
                tv.setText(fileResource.getResourceName());
                tv.setTextSize(20);

                switch (fileResource.getResourceStatus()){
                    case FileResource.STATUS_ERROR:
                        tv.setTextColor(Color.RED);
                        tv.setText(tv.getText()+"(Resource Error)");
                        break;
                    case FileResource.STATUS_FREEZING:
                        tv.setTextColor(Color.BLUE);
                        tv.setText(tv.getText()+"(Resource Frozen)");
                        break;
                    case FileResource.STATUS_TRANSPORTING:
                        tv.setTextColor(Color.MAGENTA);
                        tv.setText(tv.getText()+"(Resource Transporting)");
                        break;
                }

                return tv;
            }
        });

    }
}
