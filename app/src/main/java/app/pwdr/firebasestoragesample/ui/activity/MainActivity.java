package app.pwdr.firebasestoragesample.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.storage.StorageReference;
import com.sangcomz.fishbun.define.Define;

import java.util.ArrayList;
import java.util.List;

import app.pwdr.firebasestoragesample.R;
import app.pwdr.firebasestoragesample.manager.StorageManager;
import app.pwdr.firebasestoragesample.util.PickerUtil;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static app.pwdr.firebasestoragesample.Constants.RC_STORAGE_PERMS;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private ArrayList<Uri> mSelectedImages;
    private List<String> mUploadedFilenames;

    private Button mBtnSelectImages;
    private Button mBtnUpload;
    private TextView mTvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSelectedImages = new ArrayList<>();
        initView();
    }

    private void initView() {
        mBtnSelectImages = findViewById(R.id.select_images_button);
        mBtnSelectImages.setOnClickListener(view -> {
            selectImages();
        });

        mBtnUpload = findViewById(R.id.upload_button);
        mBtnUpload.setOnClickListener(view -> {
            upload();
        });

        mTvResult = findViewById(R.id.result_text);
    }

    @AfterPermissionGranted(RC_STORAGE_PERMS)
    protected void selectImages() {
        Log.d(TAG, "checkPermissions");
        String[] perm = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (!EasyPermissions.hasPermissions(this, perm)) {
            EasyPermissions.requestPermissions(this, getString(R.string.rationale_storage_perm), RC_STORAGE_PERMS, perm);
            return;
        }
        PickerUtil.pick(this, 5, mSelectedImages);
    }

    private void upload() {
        if (mSelectedImages.size() == 0) {
            Toast.makeText(this, "Select Images First.", Toast.LENGTH_SHORT).show();
            return;
        }
        StorageReference sampleStoRef = StorageManager.getSampleRef();
        StorageManager.putFiles(sampleStoRef, mSelectedImages).continueWith(task -> {
            if (!task.isSuccessful()) {
                logResult("Image Upload Failed. Error Message: " + task.getException().getMessage());
                return null;
            }
            logResult("Image Uploaded Successfully.");
            mUploadedFilenames = task.getResult();
            logResult("Uploaded Filenames: " + mUploadedFilenames);
            return null;
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, String.format("onActivityResult:requestCode:%d|resultCode:%d", requestCode, resultCode));

        if (requestCode == Define.ALBUM_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                logResult("Images Selected Successfully.");
                logResult("Selected Images: " + mSelectedImages);
            }

            if (resultCode == RESULT_CANCELED) {
                logResult("Images Selection Canceled.");
                mSelectedImages = new ArrayList<>();
            }
        }
    }

    private void logResult(String log) {
        mTvResult.append(log + "\n\n");
    }
}
