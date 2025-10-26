// app/src/main/java/com/germinare/simbia_mobile/utils/StorageUtils.java
package com.germinare.simbia_mobile.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class StorageUtils {

    private static final FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();

    public void uploadImage(
            Context ctx,
            StorageDataLoad dataLoad,
            CallbackDownloadUri callbackDownloadUri
    ){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        dataLoad.bitmap.compress(Bitmap.CompressFormat.JPEG, 40, baos);
        byte[] databyte = baos.toByteArray();

        firebaseStorage.getReference(dataLoad.nameReference).child(dataLoad.nameFile)
                .putBytes(databyte).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(ctx, "Upload feito com sucesso!", Toast.LENGTH_SHORT).show();
                        taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                callbackDownloadUri.getDownloadUri(uri.toString());
                            }
                            public void onFailure(Exception e) {
                                Toast.makeText(ctx ,"Falha ao enviar imagem: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    public void onFailure(Exception e) {
                        Toast.makeText(ctx ,"Falha ao enviar imagem: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public interface CallbackDownloadUri{
        void getDownloadUri(String downloadUri);
    }

    public static class StorageDataLoad{
        final String nameReference;
        final String nameFile;
        final Bitmap bitmap;

        public StorageDataLoad(String nameReference, String nameFile, Bitmap bitmap) {
            this.nameReference = nameReference;
            this.nameFile = nameFile + ".jpeg";
            this.bitmap = bitmap;
        }
    }

}
