package kr.nexters.onepage.write;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import com.yalantis.ucrop.UCrop;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.Calendar;

/**
 * Created by hoody on 2017-01-25.
 */

public class UCropManager {

    //Setting default configuration
    public UCrop basisConfig(@NonNull UCrop uCrop, Context context) {

        uCrop = uCrop.withAspectRatio(1, 1);

        uCrop.withMaxResultSize(800, 800);

        return advancedConfig(uCrop, context); //customizing
    }

    public File getSaveImgFile(Uri croppedFileUri) throws Exception {
        String downloadsDirectoryPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();

        Calendar cal = Calendar.getInstance();

        String filename = String.format("%d%d%d_%d_%s",
                cal.get(Calendar.YEAR), cal.get(Calendar.MONTH)+1, cal.get(Calendar.DAY_OF_MONTH), cal.getTimeInMillis(), croppedFileUri.getLastPathSegment());

        File saveFile = new File(downloadsDirectoryPath, filename);

        FileInputStream inStream = new FileInputStream(new File(croppedFileUri.getPath()));
        FileOutputStream outStream = new FileOutputStream(saveFile);
        FileChannel inChannel = inStream.getChannel();
        FileChannel outChannel = outStream.getChannel();
        inChannel.transferTo(0, inChannel.size(), outChannel);
        inStream.close();
        outStream.close();

        return saveFile;
    }

    //add customizing
    public UCrop advancedConfig(@NonNull UCrop uCrop, Context context) {

        UCrop.Options options = new UCrop.Options();

        options.setCompressionQuality(100);

        //Tune everything (ﾉ◕ヮ◕)ﾉ*:･ﾟ✧

        // Color palette
//        options.setToolbarColor(ContextCompat.getColor(context, R.color.colorTest)); //edit photo toolbar color
//        options.setStatusBarColor(ContextCompat.getColor(context, R.color.colorAccent)); // edit photo statusbar color
//        options.setActiveWidgetColor(ContextCompat.getColor(context, R.color.colorTest)); // bottom icon color
//        options.setToolbarWidgetColor(ContextCompat.getColor(context, R.color.colorAccent)); // toolbar icon & text color
//        options.setRootViewBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));

        return uCrop.withOptions(options);
    }

}
