package com.qzero.telegram.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity implements IBaseView {

    /**
     * The progress dialog
     * When showing,it's not null
     * When hiding,it's null
     */
    private Dialog progressDialog;

    @Override
    public void showProgress() {
        ProgressBar progressBar=new ProgressBar(this);
        progressDialog=new AlertDialog.Builder(this).setCancelable(false).setTitle("Please wait wait wait~~~").setView(progressBar).show();
    }

    @Override
    public void hideProgress() {
        if(progressDialog!=null){
            progressDialog.dismiss();
            progressDialog=null;
        }
    }

    @Override
    public void showToast(String text) {
        Toast.makeText(this,text,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showLocalErrorMessage(String localErrorMessage) {
        showToast("Error caused by local:"+localErrorMessage);
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void exit() {
        finish();
    }

}
