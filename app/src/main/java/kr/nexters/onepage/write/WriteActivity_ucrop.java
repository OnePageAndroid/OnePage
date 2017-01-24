package kr.nexters.onepage.write;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.yalantis.ucrop.UCrop;

import java.io.File;

import butterknife.ButterKnife;
import butterknife.OnClick;
import kr.nexters.onepage.R;

/**
 * Created by hoody on 2017-01-25.
 */


public class WriteActivity_ucrop extends UCropBaseActivity {

    private static final String TAG = "WriteActivity";

    private static final String ONE_PAGE_IMAGE_NAME = "OnePage";

    private static final int REQUEST_STORAGE_READ_ACCESS_PERMISSION = 101;
    private static final int REQUEST_STORAGE_WRITE_ACCESS_PERMISSION = 102;

    private static final int PERMISSION_REQUEST_CAMERA = 1001;
    private static final int REQUEST_CAMERA = 100;
    private static final int REQUEST_GALLERY = 200;
    private static final int REQUEST_CROP = 300;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);

        ButterKnife.bind(this);
    }


    @OnClick(R.id.ivWriteImage)
    public void onClickImage() {

        //dialog 커스텀 가능
        AlertDialog.Builder dialog = new AlertDialog.Builder(WriteActivity_ucrop.this);

        dialog.setTitle("Image");
        dialog.setMessage("select Image");
        dialog.setIcon(R.drawable.check);

        dialog.setPositiveButton("Camera", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                //camera permission check
                if(ContextCompat.checkSelfPermission(WriteActivity_ucrop.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    //navigateToCamera();
                }
                else {
                    ActivityCompat.requestPermissions(WriteActivity_ucrop.this, new String[] {Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
                }

            }
        });

        dialog.setNegativeButton("Gallery",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //1
                pickFromGallery();
            }
        });
        dialog.show();
    }

    //2
    private void pickFromGallery() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE,
                    getString(R.string.permission_read_storage_rationale),
                    REQUEST_STORAGE_READ_ACCESS_PERMISSION);
        } else {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(Intent.createChooser(intent, getString(R.string.label_select_picture)), REQUEST_GALLERY);
        }
    }

    //3
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_GALLERY) {
                final Uri selectedUri = data.getData();
                if (selectedUri != null) {
                    //4
                    startCropActivity(data.getData());
                } else {
                    Toast.makeText(WriteActivity_ucrop.this, R.string.toast_cannot_retrieve_selected_image, Toast.LENGTH_SHORT).show();
                }
            } else if (requestCode == UCrop.REQUEST_CROP) {
                handleCropResult(data);
            }
        }
        if (resultCode == UCrop.RESULT_ERROR) {
            handleCropError(data);
        }
    }

    //4
    private void startCropActivity(@NonNull Uri uri) {
        String destinationFileName = ONE_PAGE_IMAGE_NAME;

        destinationFileName += ".jpg";

        UCrop uCrop = UCrop.of(uri, Uri.fromFile(new File(getCacheDir(), destinationFileName)));

        uCrop = basisConfig(uCrop);

        uCrop.start(WriteActivity_ucrop.this);
    }


    //Setting default configuration
    private UCrop basisConfig(@NonNull UCrop uCrop) {

        uCrop = uCrop.withAspectRatio(1, 1);

        uCrop.withMaxResultSize(1500, 1500);

        return uCrop;
    }




    //5
    private void handleCropResult(@NonNull Intent result) {
        final Uri resultUri = UCrop.getOutput(result);
        if (resultUri != null) {
            ResultActivity.startWithUri(WriteActivity_ucrop.this, resultUri);
        } else {
            Toast.makeText(WriteActivity_ucrop.this, R.string.toast_cannot_retrieve_cropped_image, Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    private void handleCropError(@NonNull Intent result) {
        final Throwable cropError = UCrop.getError(result);
        if (cropError != null) {
            Log.e(TAG, "handleCropError: ", cropError);
            Toast.makeText(WriteActivity_ucrop.this, cropError.getMessage(), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(WriteActivity_ucrop.this, R.string.toast_unexpected_error, Toast.LENGTH_SHORT).show();
        }
    }
}
