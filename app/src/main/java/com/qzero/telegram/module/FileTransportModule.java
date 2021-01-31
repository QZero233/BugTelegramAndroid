package com.qzero.telegram.module;

import com.qzero.telegram.http.bean.ActionResult;

import java.io.IOException;
import java.io.InputStream;

import io.reactivex.rxjava3.core.Observable;

public interface FileTransportModule {

    Observable<ActionResult> uploadFileBlock(String resourceId, int blockIndex) throws IOException;

    Observable<ActionResult> downloadFileBlock(String resourceId,int blockIndex) throws IOException;

}
