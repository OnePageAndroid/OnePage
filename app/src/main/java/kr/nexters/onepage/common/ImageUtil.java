package kr.nexters.onepage.common;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
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

    public static Bitmap centerCrop(final Bitmap src, final int w, final int h) {
        return crop(src, w, h, 0.5f, 0.5f);
    }

    /**
     * Returns a new Bitmap copy with a crop effect depending on the crop anchor given. 0.5f is like
     * {@link android.widget.ImageView.ScaleType#CENTER_CROP}. The crop anchor will be be nudged
     * so the entire cropped bitmap will fit inside the src. May return the input bitmap if no
     * scaling is necessary.
     *
     *
     * Example of changing verticalCenterPercent:
     *   _________            _________
     *  |         |          |         |
     *  |         |          |_________|
     *  |         |          |         |/___0.3f
     *  |---------|          |_________|\
     *  |         |<---0.5f  |         |
     *  |---------|          |         |
     *  |         |          |         |
     *  |         |          |         |
     *  |_________|          |_________|
     *
     * @param src original bitmap of any size
     * @param w desired width in px
     * @param h desired height in px
     * @param horizontalCenterPercent determines which part of the src to crop from. Range from 0
     *                                .0f to 1.0f. The value determines which part of the src
     *                                maps to the horizontal center of the resulting bitmap.
     * @param verticalCenterPercent determines which part of the src to crop from. Range from 0
     *                              .0f to 1.0f. The value determines which part of the src maps
     *                              to the vertical center of the resulting bitmap.
     * @return a copy of src conforming to the given width and height, or src itself if it already
     *         matches the given width and height
     */
    public static Bitmap crop(final Bitmap src, final int w, final int h,
                              final float horizontalCenterPercent, final float verticalCenterPercent) {
        if (horizontalCenterPercent < 0 || horizontalCenterPercent > 1 || verticalCenterPercent < 0
                || verticalCenterPercent > 1) {
            throw new IllegalArgumentException(
                    "horizontalCenterPercent and verticalCenterPercent must be between 0.0f and "
                            + "1.0f, inclusive.");
        }
        final int srcWidth = src.getWidth();
        final int srcHeight = src.getHeight();
        // exit early if no resize/crop needed
        if (w == srcWidth && h == srcHeight) {
            return src;
        }
        final Matrix m = new Matrix();
        final float scale = Math.max(
                (float) w / srcWidth,
                (float) h / srcHeight);
        m.setScale(scale, scale);
        final int srcCroppedW, srcCroppedH;
        int srcX, srcY;
        srcCroppedW = Math.round(w / scale);
        srcCroppedH = Math.round(h / scale);
        srcX = (int) (srcWidth * horizontalCenterPercent - srcCroppedW / 2);
        srcY = (int) (srcHeight * verticalCenterPercent - srcCroppedH / 2);
        // Nudge srcX and srcY to be within the bounds of src
        srcX = Math.max(Math.min(srcX, srcWidth - srcCroppedW), 0);
        srcY = Math.max(Math.min(srcY, srcHeight - srcCroppedH), 0);
        final Bitmap cropped = Bitmap.createBitmap(src, srcX, srcY, srcCroppedW, srcCroppedH, m,
                true /* filter */);
//        if (DEBUG) LogUtils.i(PhotoManager.TAG,
//                "IN centerCrop, srcW/H=%s/%s desiredW/H=%s/%s srcX/Y=%s/%s" +
//                " innerW/H=%s/%s scale=%s resultW/H=%s/%s",
//                srcWidth, srcHeight, w, h, srcX, srcY, srcCroppedW, srcCroppedH, scale,
//                cropped.getWidth(), cropped.getHeight());
//        if (DEBUG && (w != cropped.getWidth() || h != cropped.getHeight())) {
//            LogUtils.e(PhotoManager.TAG, new Error(), "last center crop violated assumptions.");
//        }
        return cropped;
    }
}
