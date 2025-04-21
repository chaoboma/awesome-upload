package com.application.upload;

import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.fileupload.ProgressListener;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

public class CustomMultipartResolver3 extends CommonsMultipartResolver {

    private ProgressListener progressListener;

    public void setProgressListener(ProgressListener progressListener) {
        this.progressListener = progressListener;
    }

    @Override
    protected FileUpload newFileUpload(FileItemFactory fileItemFactory) {
        FileUpload upload = super.newFileUpload(fileItemFactory);
        if (progressListener != null) {
            upload.setProgressListener(progressListener);
        }
        return upload;
    }
}