package kr.nexters.onepage.write;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kr.nexters.onepage.R;
import kr.nexters.onepage.common.NetworkManager;
import kr.nexters.onepage.common.PropertyManager;
import kr.nexters.onepage.common.model.Loc;
import kr.nexters.onepage.common.model.PostPage;
import kr.nexters.onepage.common.model.ServerResponse;
import kr.nexters.onepage.map.MapActivity;
import kr.nexters.onepage.write.model.PageSaveResponse;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by hoody on 2017-01-25.
 */

public class WriteActivity extends UCropBaseActivity {

    private static final String TAG = "WriteActivity";
    private static final String ONE_PAGE_IMAGE_NAME = "OnePage";

    private static final int REQUEST_STORAGE_READ_ACCESS_PERMISSION = 101;
    private static final int REQUEST_STORAGE_WRITE_ACCESS_PERMISSION = 102;
    private static final int PERMISSION_REQUEST_CAMERA = 1001;
    private static final int REQUEST_CAMERA = 100;
    private static final int REQUEST_GALLERY = 200;

    private UCropManager uCropManager;

    @BindView(R.id.ivWriteImage)
    ImageView ivWriteImage;

    @BindView(R.id.etWriteContent)
    EditText etWriteContent;

//    @BindView(R.id.btnWriteSave)
//    Button btnWriteSave;

    Uri imageUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);

        ButterKnife.bind(this);

        uCropManager = new UCropManager();
        setSupportActionBar((Toolbar) findViewById(R.id.writeToolbar));
        final ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

    }

    @OnClick(R.id.btnCamera)
    public void onClickImage() {
        //dialog 커스텀 가능
        AlertDialog.Builder dialog = new AlertDialog.Builder(WriteActivity.this);

        dialog.setTitle("Image");
        dialog.setMessage("select Image");
        dialog.setIcon(R.drawable.check);

        dialog.setPositiveButton("Camera", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                navigateToCamera();
            }
        });

        dialog.setNegativeButton("Gallery",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                navigateToGallery(); //1
            }
        });
        dialog.show();
    }

//    @OnClick(R.id.btnWriteSave)
//    public void onClickBtn() {
//        //blank check
//        if(etWriteContent.getText().length() == 0) {
//            Toast.makeText(WriteActivity.this, getString(R.string.toast_write_check_blank), Toast.LENGTH_LONG).show();
//        }
//        else {
//
//            //Image Save To File
//            File savedImg = saveCroppedImage();
//
//            PostPage postPage = new PostPage();
//
//            postPage.setLocationId("");
//            //MainActivity에서 표시된 장소명을 putExtra로 전달한다음에 getExtra로 꺼내서 넣으면 될듯..!
//            postPage.setEmail(PropertyManager.getInstance().getId());
//            postPage.setImage(savedImg);
//            postPage.setContent(etWriteContent.getText().toString());
//
//            savePage(postPage);
//
//            Toast.makeText(this, postPage.toString(), Toast.LENGTH_LONG).show();
//            Log.i("WriteActivityLog", postPage.toString());
//        }
//    }

    private void savePage(PostPage page) {

        Call<PageSaveResponse> saveCall = NetworkManager.getInstance().getApi().savePage(
                1,
                PropertyManager.getInstance().getId(),
                page.getContent()
        );

        saveCall.enqueue(new Callback<PageSaveResponse>() {
            @Override
            public void onResponse(Call<PageSaveResponse> call, Response<PageSaveResponse> response) {

                Log.d(WriteActivity.class.getSimpleName(), "page code : "+response.code());

                if(response.isSuccessful()) {
//                    Log.d(WriteActivity.class.getSimpleName(), response.body().getMessage());
                    //saveImage(page, response.body().getId());
//                    Toast.makeText(WriteActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PageSaveResponse> call, Throwable t) {
                Toast.makeText(WriteActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveImage(PostPage page, long pageId) {
        // https://github.com/iPaulPro/aFileChooser/blob/master/aFileChooser/src/com/ipaulpro/afilechooser/utils/FileUtils.java
        // use the FileUtils to get the actual file by uri
        File file = page.getImage();

        MultipartBody.Part filePart = MultipartBody.Part.createFormData(
                "multipartFile",
                file.getName(),
                RequestBody.create(MediaType.parse("image/*"), file)
//                RequestBody.create(MediaType.parse("image/*"), file)
        );

        Call<ServerResponse> saveImageCall = NetworkManager.getInstance().getApi().savePageImage(
                pageId,
                filePart
        );

        saveImageCall.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {

                Log.d(WriteActivity.class.getSimpleName(), "image code : "+response.code());

                if(response.isSuccessful() && response.body().isSuccess()) {
                    Log.d(WriteActivity.class.getSimpleName(), response.body().message);
                    Toast.makeText(WriteActivity.this, response.body().message, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Toast.makeText(WriteActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToCamera() {

        //camera permission check
        if(ContextCompat.checkSelfPermission(WriteActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, REQUEST_CAMERA);
        } else {
            ActivityCompat.requestPermissions(WriteActivity.this, new String[] {Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
        }
    }

    //2
    private void navigateToGallery() {
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
                    Toast.makeText(WriteActivity.this, R.string.toast_cannot_retrieve_selected_image, Toast.LENGTH_SHORT).show();
                }
            } else if (requestCode == REQUEST_CAMERA) {
                final Uri selectedUri = data.getData();
                if (selectedUri != null) {
                    startCropActivity(data.getData());
                } else {
                    Toast.makeText(WriteActivity.this, R.string.toast_cannot_retrieve_selected_image, Toast.LENGTH_SHORT).show();
                }

            } else if (requestCode == UCrop.REQUEST_CROP) {
                handleCropResult(data);
            } else if (requestCode == ResultActivity.REQUEST_SAVE_RESULT) { //show crop image
                try {
                    imageUri = data.getData();
                    ivWriteImage.setImageBitmap(MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if (resultCode == UCrop.RESULT_ERROR) {
            handleCropError(data);
        }
    }

    //   * Callback received when a permissions request has been completed.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_STORAGE_WRITE_ACCESS_PERMISSION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    saveCroppedImage();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    //4
    public void startCropActivity(@NonNull Uri uri) {
        String destinationFileName = ONE_PAGE_IMAGE_NAME;

        destinationFileName += ".jpg";

        UCrop uCrop = UCrop.of(uri, Uri.fromFile(new File(getCacheDir(), destinationFileName)));

        uCrop = uCropManager.basisConfig(uCrop, getApplicationContext());

        uCrop.start(WriteActivity.this);
    }

    //5
    public void handleCropResult(@NonNull Intent result) {
        final Uri resultUri = UCrop.getOutput(result);
        if (resultUri != null) {
            ResultActivity.startWithUri(WriteActivity.this, resultUri);
        } else {
            Toast.makeText(WriteActivity.this, R.string.toast_cannot_retrieve_cropped_image, Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    public void handleCropError(@NonNull Intent result) {
        final Throwable cropError = UCrop.getError(result);
        if (cropError != null) {
            Log.e(TAG, "handleCropError: ", cropError);
            Toast.makeText(WriteActivity.this, cropError.getMessage(), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(WriteActivity.this, R.string.toast_unexpected_error, Toast.LENGTH_SHORT).show();
        }
    }

    public File saveCroppedImage() {
        File savedImg = null;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    getString(R.string.permission_write_storage_rationale),
                    REQUEST_STORAGE_WRITE_ACCESS_PERMISSION);
        } else {
//            Uri imageUri = getIntent().getData();
            if (imageUri != null && imageUri.getScheme().equals("file")) {
                try {
                    savedImg = uCropManager.getSaveImgFile(imageUri);
                } catch (Exception e) {
                    Toast.makeText(WriteActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, imageUri.toString(), e);
                }
            } else {
                Toast.makeText(WriteActivity.this, getString(R.string.toast_unexpected_error), Toast.LENGTH_SHORT).show();
            }
        }

        return savedImg;
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_write, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_write) {
            //blank check
            if(etWriteContent.getText().length() == 0) {
                Toast.makeText(WriteActivity.this, getString(R.string.toast_write_check_blank), Toast.LENGTH_LONG).show();
            }
            else {

                PostPage postPage =
                        new PostPage("", PropertyManager.getInstance().getId(), saveCroppedImage(), etWriteContent.getText().toString());
                //MainActivity에서 표시된 장소명을 putExtra로 전달한다음에 getExtra로 꺼내서 넣으면 될듯..!

                savePage(postPage);

                Toast.makeText(this, postPage.toString(), Toast.LENGTH_LONG).show();
                Log.i("WriteActivityLog", postPage.toString());
            }
        }

        else if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
