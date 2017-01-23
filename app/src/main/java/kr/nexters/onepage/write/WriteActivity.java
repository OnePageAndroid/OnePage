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

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kr.nexters.onepage.R;
import kr.nexters.onepage.common.BaseActivity;
import kr.nexters.onepage.common.PropertyManager;
import kr.nexters.onepage.common.model.Page;

public class WriteActivity extends BaseActivity {

    static final int PERMISSION_REQUEST_CAMERA = 1000;
    static final int CALL_GALLERY = 100;
    static final int CALL_CAMERA = 200;


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
            Page page = new Page();

            page.setLocation("");
            //MainActivity에서 표시된 장소명을 putExtra로 전달한다음에 getExtra로 꺼내서 넣으면 될듯..!
            page.setEmail(PropertyManager.getInstance().getId());
            page.setImage(image);
            page.setContent(etWriteContent.getText().toString());

            Toast.makeText(this, "save", Toast.LENGTH_LONG).show();
            Log.i("WriteActivityLog", page.toString());
        }
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
                    //이미지 처리는 라이브러리써서.....

                    ivWriteImage.setImageBitmap(BitmapFactory.decodeFile(savePath+ "/" + fileName));

                    image = new File(savePath+ "/" + fileName); //file to pass db

                    Log.i("fileName", fileName);
                    break;
                case CALL_GALLERY :
                    try {

                        Bitmap getImgFromGallery = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                        ivWriteImage.setImageBitmap(getImgFromGallery);

                        image = new File(getImgPath(data.getData())); //file to pass db
                    } catch (IOException e) {
                        e.printStackTrace();
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

}