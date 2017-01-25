package kr.nexters.onepage.write;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kr.nexters.onepage.R;
import kr.nexters.onepage.common.NetworkManager;
import kr.nexters.onepage.common.PropertyManager;
import kr.nexters.onepage.common.model.ServerResponse;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import kr.nexters.onepage.common.model.PostPage;

public class WriteActivity extends AppCompatActivity {

    static final int PERMISSION_REQUEST_CAMERA = 1000;
    static final int CALL_GALLERY = 100;
    static final int CALL_CAMERA = 200;
    static final int CALL_CROP = 300;

    private File image;
    private String savePath;
    private String fileName;

    @BindView(R.id.ivWriteImage)
    ImageView ivWriteImage;

    @BindView(R.id.etWriteContent)
    EditText etWriteContent;

    @BindView(R.id.btnWriteSave)
    Button btnWriteSave;

    Intent intent;

    //TODO update alertDialog design
    //TODO Camera에서 찍은 사진이 save 눌렀을때만 저장되게!

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);

        ButterKnife.bind(this);

        //Go MainActivity
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Image save path
        savePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/OnePage/media";
        new File(savePath).mkdirs();
    }


    @OnClick(R.id.ivWriteImage)
    public void onClickImage() {

        //dialog 커스텀 가능
        AlertDialog.Builder dialog = new AlertDialog.Builder(WriteActivity.this);

        dialog.setTitle("Image");
        dialog.setMessage("select Image");
        dialog.setIcon(R.drawable.check);

        dialog.setPositiveButton("Camera", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                //camera permission check
                if(ContextCompat.checkSelfPermission(WriteActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    navigateToCamera();
                }
                else {
                    ActivityCompat.requestPermissions(WriteActivity.this, new String[] {Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
                }

            }
        });

        dialog.setNegativeButton("Gallery",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                navigateToGallery();

            }
        });
        dialog.show();
    }

    @OnClick(R.id.btnWriteSave)
    public void onClickBtn() {
        //blank check

        if(etWriteContent.getText().length() == 0) {
            Toast.makeText(WriteActivity.this, getString(R.string.toast_write_check_blank), Toast.LENGTH_LONG).show();
        }
        else {
            PostPage postPage = new PostPage();

            postPage.setLocationId("");
            //MainActivity에서 표시된 장소명을 putExtra로 전달한다음에 getExtra로 꺼내서 넣으면 될듯..!
            postPage.setEmail(PropertyManager.getInstance().getId());
            postPage.setImage(image);
            postPage.setContent(etWriteContent.getText().toString());

            Toast.makeText(this, "save", Toast.LENGTH_LONG).show();
            savePage(postPage);
            Log.i("WriteActivityLog", postPage.toString());
        }
    }

    private void savePage(PostPage page) {

        Call<ServerResponse> saveCall = NetworkManager.getInstance().getApi().savePage(
                1,
                PropertyManager.getInstance().getId(),
                page.getContent()
        );

        saveCall.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {

                Log.d(WriteActivity.class.getSimpleName(), "page code : "+response.code());

                if(response.isSuccessful() & response.body().isSuccess()) {
                    Log.d(WriteActivity.class.getSimpleName(), response.body().message);
                    saveImage(page);
                    Toast.makeText(WriteActivity.this, response.body().message, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Toast.makeText(WriteActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveImage(PostPage page) {
        // https://github.com/iPaulPro/aFileChooser/blob/master/aFileChooser/src/com/ipaulpro/afilechooser/utils/FileUtils.java
        // use the FileUtils to get the actual file by uri
        File file = page.getImage();


        // create RequestBody instance from file
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);

        // MultipartBody.Part is used to send also the actual file name
        MultipartBody.Part body =
                MultipartBody.Part.createFormData("multipartFile", file.getName(), requestFile);

        MultipartBody.Part filePart = MultipartBody.Part.createFormData(
                "multipartFile",
                file.getName(),
                RequestBody.create(MediaType.parse("image/*"), file)
//                RequestBody.create(MediaType.parse("image/*"), file)
        );

        Call<ServerResponse> saveImageCall = NetworkManager.getInstance().getApi().savePageImage(
                1L,
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

    //Action button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(id) {
            case android.R.id.home :
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK) {
            switch (requestCode) {
                case CALL_CAMERA :

                    ivWriteImage.setImageBitmap(BitmapFactory.decodeFile(savePath+ "/" + fileName));

                    image = new File(savePath+ "/" + fileName); //file to pass db

                    cropImage(Uri.fromFile(image));

                    Log.i("fileName", fileName);
                    break;

                case CALL_GALLERY :
                    //Bitmap getImgFromGallery = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                    //ivWriteImage.setImageBitmap(getImgFromGallery);

                    image = new File(getImgPath(data.getData())); //temp file to pass crop

                    cropImage(Uri.fromFile(image));

                    break;
                case CALL_CROP :

                    if(data.getExtras() != null) {
                        final Bundle extras = data.getExtras();
                        if(extras != null) {
                            Bitmap cropBmp = (Bitmap)extras.get("data");

                            ivWriteImage.setImageBitmap(cropBmp);

                            image = getCropImg(cropBmp);
                        }
                    }
                    break;
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch(requestCode) {
            case PERMISSION_REQUEST_CAMERA :
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    navigateToCamera();
                }
        }
    }

    private void navigateToCamera() {
        fileName = createFileName();

        intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(savePath + "/" + fileName)));

        startActivityForResult(intent, CALL_CAMERA);
    }

    private void navigateToGallery() {
        intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, CALL_GALLERY);

//        startActivityForResult(
//            intent.createChooser(new Intent(Intent.ACTION_GET_CONTENT) .setType("image/*"), getString(R.string.chooser_gallery), CALL_GALLERY);
    }

    //Create image file name with current time
    private String createFileName() {
        Date day = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.KOREA);
        return "OnePage_" + String.valueOf(sdf.format(day)) + ".jpg";
    }

    //Gets real path of the selected file in the gallery
    private String getImgPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        cursor.moveToFirst();

        return cursor.getString(column_index);
    }

    private void cropImage(Uri uri) {
        Intent cropIntent = new Intent("com.android.camera.action.CROP");
        //indicate image type and Uri of image
        cropIntent.setDataAndType(uri, "image/*");
        //set crop properties
        cropIntent.putExtra("crop", "true");
        //indicate aspect of desired crop
        cropIntent.putExtra("aspectX", 1);
        cropIntent.putExtra("aspectY", 1);
        //indicate output X and Y
        cropIntent.putExtra("outputX", ivWriteImage.getWidth());
        cropIntent.putExtra("outputY", ivWriteImage.getHeight());
        //retrieve data on return
        cropIntent.putExtra("return-data", true);
        startActivityForResult(cropIntent, CALL_CROP);
    }

    private File getCropImg(Bitmap bitmap) {

        File cropImg = new File(savePath + "/" + createFileName());

        BufferedOutputStream out;

        try {
            cropImg.createNewFile();
            out = new BufferedOutputStream(new FileOutputStream(cropImg));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(cropImg)));
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cropImg;
    }

}