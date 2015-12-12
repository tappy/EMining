package com.example.hackme.emining.model;

import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.RequestBody;

import java.io.File;

/**
 * Created by kongsin on 10/12/2558.
 */
public class UpdateDataLoader {
    public UpdateDataLoader(File file, String userId, String tableName, ModelLoader.DataLoadingListener listener) {
        RequestBody multiPath = new MultipartBuilder()
                .type(MultipartBuilder.FORM)
                .addFormDataPart("filUpload", file.getName(), RequestBody.create(ModelLoader.FILE, file))
                .addFormDataPart("body", userId)
                .addFormDataPart("table_name", tableName).build();
        new ModelLoader().uploadFile("loadFileName.php", multiPath, listener);
    }
}
