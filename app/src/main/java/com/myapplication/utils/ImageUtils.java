package com.myapplication.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.ImageView;

import com.myapplication.R;
import com.myapplication.databases.UsersService;
import com.myapplication.model.Users;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Lin on 2019/6/13.
 */

public class ImageUtils {
    //图片采样率压缩
    public static Bitmap compressImage(int inSampleSize, String path){
        BitmapFactory.Options options = new BitmapFactory.Options();
        //设置此参数是仅仅读取图片的宽高到options中，不会将整张图片读取到内存中
        options.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(path,options);

        options.inJustDecodeBounds = false;
        options.inSampleSize = inSampleSize;

        bitmap = BitmapFactory.decodeFile(path,options);

        return bitmap;
    }

    /**
     * 将图片路径转换成字节数组
     * @param imagePath
     * @return  byte[]
     * @throws IOException
     */
    public static byte[] readStream(String imagePath) {
        FileInputStream fs = null;
        ByteArrayOutputStream outStream = null;
        try {
            fs = new FileInputStream(imagePath);
            outStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while(-1 != (len = fs.read(buffer))){
                outStream.write(buffer,0,len);
            }
            outStream.close();
            fs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return outStream.toByteArray();
    }

    /**
     * 将图片变为圆角
     * @param bitmap 原Bitmap图片
     * @param pixels 图片圆角的弧度（单位：像素（px））
     * @return 带有圆角的图片（Bitmap 类型）
     */
    public static Bitmap toRoundCorner(Bitmap bitmap,int pixels){
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }
}
