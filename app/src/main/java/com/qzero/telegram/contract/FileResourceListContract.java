package com.qzero.telegram.contract;

import com.qzero.telegram.dao.entity.FileResource;
import com.qzero.telegram.presenter.IBasePresenter;
import com.qzero.telegram.view.IBaseView;

import java.io.File;
import java.util.List;

public class FileResourceListContract {

    public interface Presenter extends IBasePresenter<View> {
        void loadAllFileResource();

        void newFileResource(File fileResource);
        void deleteFileResource(String resourceId);
    }

    public interface View extends IBaseView{
        void loadAllFileResource(List<FileResource> fileResourceList);
    }

}
