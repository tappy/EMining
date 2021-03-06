package com.example.hackme.emining.model;

import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.RequestBody;

import java.io.File;

/**
 * Created by kongsin on 11/12/2558.
 */
public class UploadFileLoader {

    public UploadFileLoader(File file, String userId, ModelLoader.DataLoadingListener listener) {
        RequestBody multiPath = new MultipartBuilder()
                .type(MultipartBuilder.FORM)
                .addFormDataPart("filUpload", file.getName(), RequestBody.create(ModelLoader.FILE, file))
                .addFormDataPart("body", userId).build();
        new ModelLoader().uploadFile(file.getName(), multiPath, listener);
    }

}
