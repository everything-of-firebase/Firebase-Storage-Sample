package app.pwdr.firebasestoragesample.manager;

import android.net.Uri;
import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static app.pwdr.firebasestoragesample.Constants.STORAGE_BUCKET_TOKYO;
import static app.pwdr.firebasestoragesample.Constants.STORAGE_PATH_SAMPLE;

public class StorageManager {
    public static final String TAG = "StorageManager";

    public static FirebaseStorage getInstance() {
        return FirebaseStorage.getInstance(STORAGE_BUCKET_TOKYO);
    }

    public static StorageReference getRef() {
        return getInstance().getReference();
    }

    public static StorageReference getRef(String path) {
        return getInstance().getReference(path);
    }

    // User
    public static StorageReference getSampleRef() {
        return getRef().child(STORAGE_PATH_SAMPLE);
    }

    public static Task<List<String>> putFiles(final StorageReference ref, final List<Uri> uris) {
        Log.i(TAG, "putFiles");
        return TasksManager.call(() -> {
            List<String> filenames = new ArrayList<>();
            for (Uri uri : uris) {
                String filename = UUID.randomUUID().toString() + ".jpg";
                Task<UploadTask.TaskSnapshot> putFileTask = putFile(ref.child(filename), uri);
                UploadTask.TaskSnapshot snapshot = Tasks.await(putFileTask);
                String uploadedName = snapshot.getMetadata().getName();
                Log.d(TAG, "putFiles:putFileTask:uploadedName:" + uploadedName);
                filenames.add(filename);
            }
            return filenames;
        }).continueWith(task -> {
            if (!task.isSuccessful()) {
                Exception e = task.getException();
                Log.w(TAG, "putFiles:putFileTask:ERROR:", e);
                throw e;
            }
            Log.d(TAG, "putFiles:putFileTask:SUCCESS");
            return task.getResult();
        });
    }

    public static Task<UploadTask.TaskSnapshot> putFile(final StorageReference ref, final Uri uri) {
        Log.i(TAG, "putFile:uri: " + uri.toString());
        return ref.putFile(uri).continueWithTask(task -> {
            if (!task.isSuccessful()) {
                Log.w(TAG, "putFile:ERROR", task.getException());
                throw task.getException();
            }
            Log.d(TAG, "putFile:SUCCESS:" + task.getResult().getMetadata().getPath());
            return task;
        });
    }

    public static Task<Void> delete(StorageReference ref) {
        Log.i(TAG, "delete");
        return ref.delete();
    }

    public static Task<Void> delete(final StorageReference ref, final List<String> filenames) {
        Log.i(TAG, "delete");
        return TasksManager.call(() -> {
            for (String filename : filenames) {
                StorageReference fileRef = ref.child(filename);
                Log.i(TAG, "delete:loop:path:" + fileRef.getPath());
                Task<Void> deleteTask = delete(fileRef);
                Tasks.await(deleteTask);
                Log.i(TAG, "delete:loop:deleteTask:SUCCESS");
            }
            return null;
        }).continueWith(task -> {
            if (!task.isSuccessful()) {
                Exception e = task.getException();
                Log.w(TAG, "delete:loop:deleteTask:ERROR:", e);
                throw e;
            }
            Log.w(TAG, "delete:loop:deleteTask:SUCCESS");
            return null;
        });
    }

    public static String getPath(StorageReference ref) {
        return ref.getPath();
    }
}
