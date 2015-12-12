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
                .addFormDataPart("userID", userId)
                .addFormDataPart("tableName", tableName).build();
        new ModelLoader().uploadFile("updateTable.php", multiPath, listener);
    }
}
