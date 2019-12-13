package com.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.myapplication.databases.ArticleService;
import com.myapplication.model.Article;
import com.myapplication.utils.ImageUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class ArticleActivity extends Activity implements View.OnClickListener {

    ImageView iv_exit,iv_picture;
    TextView tv_release;
    EditText et_content;
    String pic = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);

        initViews();
    }
    public void initViews(){
        iv_exit = findViewById(R.id.iv_exit);
        tv_release = findViewById(R.id.tv_release);
        et_content = findViewById(R.id.et_content);
        iv_picture = findViewById(R.id.iv_picture);

        iv_exit.setOnClickListener(this);
        tv_release.setOnClickListener(this);
        iv_picture.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            //返回上一个界面
            case R.id.iv_exit:
                finish();
                break;
            //发帖
            case R.id.tv_release:
                //获取内容，并判断是否为空
                String content = et_content.getText().toString();
                if (content.equals("") || content == null){
                    Toast.makeText(this, "您还没填写内容，请先填写内容", Toast.LENGTH_SHORT).show();
                    return;
                }
                SharedPreferences mSetting = getSharedPreferences("mySetting",MODE_PRIVATE);
                String userName = mSetting.getString("userName","");

                //获取当前时间戳
                String date = String.valueOf(System.currentTimeMillis());

                //把数据添加到发帖表
                Article article = new Article(userName,content,date,pic);
                ArticleService service = new ArticleService(this);
                if (service.add(article)){
                    Toast.makeText(this, "发布成功", Toast.LENGTH_SHORT).show();
                    setResult(022);
                    finish();
                }else{
                    Toast.makeText(this, "发布失败！", Toast.LENGTH_SHORT).show();
                }
                break;
            //获取图片
            case R.id.iv_picture:
                //跳转到本地图库
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(intent,111);

                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            //返回选中的图片
            case 111:
                //判断是否有数据返回
                if (data ==null) return;

                //获取选中图片的路径
                Uri uri = data.getData();
                String[] filePath = {MediaStore.Images.Media.DATA};
                Cursor c = getContentResolver().query(uri,filePath,null,null,null);
                c.moveToFirst();
                int index = c.getColumnIndex(filePath[0]);
                String imagePath = c.getString(index);
                c.close();

                //返回图片路径，并压缩图片显示到控件上
                pic = imagePath;
                iv_picture.setImageBitmap(ImageUtils.compressImage(6,pic));
                break;
        }
    }

}
