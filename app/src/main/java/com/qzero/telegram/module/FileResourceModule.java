package com.qzero.telegram.module;

import com.qzero.telegram.dao.entity.FileTransportTask;
import com.qzero.telegram.http.bean.ActionResult;
import com.qzero.telegram.dao.entity.FileResource;

import java.io.File;
import java.util.List;

import io.reactivex.rxjava3.core.Observable;

public interface FileResourceModule {

    Observable<ActionResult> newFileResource(File resource, long resourceLength);

    Observable<ActionResult> deleteFileResource(String resourceId);

    Observable<FileResource> getFileResourceInfo(String resourceId);

    Observable<List<FileResource>> getAllFileResources();

}
