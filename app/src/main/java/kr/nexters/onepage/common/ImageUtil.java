package kr.nexters.onepage.common;

import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.view.View;
import android.widget.TextView;

/**
 * Created by OhJaeHwan on 2017-02-14.
 */

public class ImageUtil {

    public static Bitmap multiplyBitmap(Bitmap bm1, Bitmap bm2) {
        Bitmap newBitmap = Bitmap.createBitmap(bm1.getWidth(), bm1.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas newCanvas = new Canvas(newBitmap);
        newCanvas.drawBitmap(bm1, 0, 0, null);
        Paint paint = new Paint();
        PorterDuff.Mode selectedMode = PorterDuff.Mode.MULTIPLY;
        paint.setXfermode(new PorterDuffXfermode(selectedMode));
        newCanvas.drawBitmap(bm2, 0, 0, paint);

        return newBitmap;
    }


    public static void applyBlurMaskFilter(TextView tv, BlurMaskFilter.Blur style){

        // Define the blur effect radius
        float radius = tv.getTextSize()/10;

        // Initialize a new BlurMaskFilter instance
        BlurMaskFilter filter = new BlurMaskFilter(radius,style);

        // Set the TextView layer type
        tv.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        // Finally, apply the blur effect on TextView text
        tv.getPaint().setMaskFilter(filter);
    }

    public static void applyBlurMaskFilter(TextView tv, BlurMaskFilter.Blur style, float radius){

        // Define the blur effect radius
//        float radius = tv.getTextSize()/10;

        // Initialize a new BlurMaskFilter instance
        BlurMaskFilter filter = new BlurMaskFilter(radius,style);

        // Set the TextView layer type
        tv.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        // Finally, apply the blur effect on TextView text
        tv.getPaint().setMaskFilter(filter);
    }
}
