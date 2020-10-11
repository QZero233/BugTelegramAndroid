package com.qzero.telegram.module.impl;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.qzero.telegram.module.BroadcastModule;
import com.qzero.telegram.notice.bean.NoticeDataType;

import java.util.ArrayList;
import java.util.List;

public class BroadcastModuleImpl implements BroadcastModule {

    private Context context;
    private LocalBroadcastManager localBroadcastManager;
    private List<BroadcastReceiver> receiverList=new ArrayList<>();

    public BroadcastModuleImpl(Context context) {
        this.context = context;
        localBroadcastManager=LocalBroadcastManager.getInstance(context);
    }

    @Override
    public void registerReceiverForCertainData(NoticeDataType dataType, Receiver receiver) {
        BroadcastReceiver broadcastReceiver=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String dataId=intent.getStringExtra("id");
                ActionType actionType= (ActionType) intent.getSerializableExtra("actionType");
                receiver.onReceived(dataId,actionType);
            }
        };

        receiverList.add(broadcastReceiver);
        IntentFilter intentFilter=new IntentFilter(dataType.getTypeInString());
        localBroadcastManager.registerReceiver(broadcastReceiver,intentFilter);
    }

    @Override
    public void unregisterAllReceivers() {
        for(BroadcastReceiver receiver:receiverList){
            localBroadcastManager.unregisterReceiver(receiver);
        }
    }

    @Override
    public void sendBroadcast(NoticeDataType dataType, String dataId, ActionType actionType) {
        Intent intent=new Intent(dataType.getTypeInString());
        intent.putExtra("id",dataId);
        intent.putExtra("actionType",actionType);
        localBroadcastManager.sendBroadcast(intent);
    }
}
