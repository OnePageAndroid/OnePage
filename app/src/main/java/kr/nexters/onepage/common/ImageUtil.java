package kr.nexters.onepage.common;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

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
}
