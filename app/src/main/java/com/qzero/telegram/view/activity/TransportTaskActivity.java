package com.qzero.telegram.view.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jakewharton.rxbinding4.view.RxView;
import com.jakewharton.rxbinding4.widget.RxAdapterView;
import com.qzero.telegram.R;
import com.qzero.telegram.dao.SessionManager;
import com.qzero.telegram.dao.entity.FileTransportTask;
import com.qzero.telegram.dao.gen.FileTransportTaskDao;
import com.qzero.telegram.file.FileTransportProgress;
import com.qzero.telegram.file.FileTransportTaskManager;
import com.qzero.telegram.http.bean.ActionResult;
import com.qzero.telegram.module.FileTransportModule;
import com.qzero.telegram.module.impl.FileTransportModuleImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;

public class TransportTaskActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    @BindView(R.id.lv_tasks)
    public ListView lv_tasks;

    private FileTransportTaskDao taskDao;

    private List<FileTransportTask> taskList;

    private FileTransportTaskManager fileTransportTaskManager;

    private Logger log= LoggerFactory.getLogger(getClass());

    private Disposable disposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transport_task);

        ButterKnife.bind(this);

        taskDao= SessionManager.getInstance(this).getSession().getFileTransportTaskDao();

        lv_tasks.setOnItemClickListener(this);

        fileTransportTaskManager=FileTransportTaskManager.getInstance();

        reloadAllTasks();

        disposable= Observable.interval(1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe( o -> {
            reloadAllTasks();
        });
    }

    private void reloadAllTasks(){
        taskList=taskDao.loadAll();
        if(taskList==null || taskList.isEmpty())
            return;

        lv_tasks.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return taskList.size();
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
                View v=View.inflate(TransportTaskActivity.this,R.layout.view_transport_task,null);

                TextView tv_resource_name=v.findViewById(R.id.tv_resource_name);
                TextView tv_progress=v.findViewById(R.id.tv_progress);

                FileTransportTask task=taskList.get(position);

                tv_resource_name.setText(task.getFileName());

                FileTransportProgress progress= fileTransportTaskManager.getProgress(task.getResourceId());
                boolean isRunning=fileTransportTaskManager.isTaskRunning(task.getResourceId());

                tv_progress.setText((isRunning?"(Running)":"(Paused)")+String.format("%.2f(%d/%d)", progress.getPercentage()*100,progress.getFinished(),progress.getTotal()));

                return v;
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        AlertDialog.Builder builder=new AlertDialog.Builder(TransportTaskActivity.this);

        View v=View.inflate(this,R.layout.view_manage_transport_task,null);

        TextView tv_resource_name=v.findViewById(R.id.tv_resource_name);
        Button btn_start=v.findViewById(R.id.btn_start);
        Button btn_stop=v.findViewById(R.id.btn_stop);
        Button btn_delete=v.findViewById(R.id.btn_delete);

        FileTransportTask task=taskList.get(position);
        if(fileTransportTaskManager.isTaskRunning(task.getResourceId())){
            btn_start.setVisibility(View.GONE);
        }else{
            btn_stop.setVisibility(View.GONE);
        }

        RxView.clicks(btn_start).subscribe(unit -> {
            fileTransportTaskManager.startTask(task.getResourceId());
        });

        RxView.clicks(btn_stop).subscribe(unit -> {
            fileTransportTaskManager.stopTask(task.getResourceId());
        });

        RxView.clicks(btn_delete).subscribe(unit -> {
            AlertDialog.Builder b=new AlertDialog.Builder(TransportTaskActivity.this);

            if(task.getTransportType()==FileTransportTask.TRANSPORT_TYPE_UPLOAD){
                b.setMessage("是否删除上传文件 "+task.getFileName()+" 的任务，这将会从云端删除该文件");
                b.setPositiveButton("删除", (dialog, which) -> {
                    fileTransportTaskManager.stopTask(task.getResourceId());
                    FileTransportModule transportModule=new FileTransportModuleImpl(TransportTaskActivity.this);
                    transportModule.deleteUploadTask(task.getResourceId())
                            .subscribe(new Observer<ActionResult>() {
                                @Override
                                public void onSubscribe(@NonNull Disposable d) {

                                }

                                @Override
                                public void onNext(@NonNull ActionResult actionResult) {

                                }

                                @Override
                                public void onError(@NonNull Throwable e) {
                                    log.error("Failed to delete upload task with id "+task.getResourceId(),e);
                                    Toast.makeText(TransportTaskActivity.this,"删除失败",Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onComplete() {
                                    Toast.makeText(TransportTaskActivity.this,"删除成功",Toast.LENGTH_SHORT).show();
                                }
                            });
                });
            }else{
                b.setMessage("是否删除下载文件 "+task.getFileName()+" 的任务");
                b.setPositiveButton("删除",(dialog, which) -> {
                    fileTransportTaskManager.stopTask(task.getResourceId());
                    FileTransportModule transportModule=new FileTransportModuleImpl(TransportTaskActivity.this);
                    transportModule.deleteDownloadTask(task.getResourceId());

                    Toast.makeText(TransportTaskActivity.this,"删除成功",Toast.LENGTH_SHORT).show();
                });
            }

            b.setNegativeButton("取消",null);
            b.show();
        });


        builder.setView(v);
        builder.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(disposable!=null)
            disposable.dispose();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_task_manager, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId()==R.id.m_start_all){
            //TODO START ALL
        }else if(item.getItemId()==R.id.m_stop_all){

        }

        return super.onOptionsItemSelected(item);
    }


}