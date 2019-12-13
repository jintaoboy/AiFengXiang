package com.myapplication.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;

import com.myapplication.R;

/**
 * Created by Lin on 2019/6/13.
 */

//异步加载图片
public class AsyncLoadImage extends AsyncTask<Object,Object,Object> {

    ImageView iv;

    public AsyncLoadImage(ImageView iv){
        this.iv = iv;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        if (o != null){
            iv.setVisibility(View.VISIBLE);
            iv.setImageBitmap((Bitmap) o);
        }else{
            iv.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onProgressUpdate(Object... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected Object doInBackground(Object... params) {
        String path = (String) params[0];
        byte[] bytes = ImageUtils.readStream(path);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
        return bitmap;
    }

}