package com.qzero.telegram.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

public class BaseFragment extends Fragment implements IBaseView {

    /**
     * The progress dialog
     * When showing,it's not null
     * When hiding,it's null
     */
    private Dialog progressDialog;

    @Override
    public void showProgress() {
        ProgressBar progressBar=new ProgressBar(getContext());
        progressDialog=new AlertDialog.Builder(getContext()).setCancelable(false).setTitle("Please wait wait wait~~~").setView(progressBar).show();
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
        Toast.makeText(getContext(),text,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showLocalErrorMessage(String localErrorMessage) {
        showToast("Error caused by local:"+localErrorMessage);
    }

    @Override
    public void exit() {
        getActivity().finish();
    }

}
