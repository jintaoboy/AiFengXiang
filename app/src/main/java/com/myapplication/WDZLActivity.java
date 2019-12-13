package com.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.text.LoginFilter;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.myapplication.databases.UsersService;
import com.myapplication.model.Users;
import com.myapplication.utils.ImageUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.content.ContentValues.TAG;
import static com.myapplication.utils.ImageUtils.toRoundCorner;

public class WDZLActivity extends Activity implements View.OnClickListener {

    ImageView iv_exit,iv_portrait;
    EditText et_gxqm;
    Spinner s_sex;
    TextView tv_save,tv_username;

    /* 请求识别码 */
    private static final int ALBUM_OK= 0xa0;//本地
    private static final int CAMERA_OK= 0xa1;//拍照
    private static final int CUT_OK = 0xa2;//最终裁剪后的结果

    // 裁剪后图片的宽(X)和高(Y),200 X 200的正方形。
    private static int output_X = 200;
    private static int output_Y = 200;

    private File cropImage = null;
    private Uri cropImgUri = null;

    SharedPreferences mSetting;
    String userName;
    String id;

    UsersService usersService;
    Users users;
    String sex;
    String gxqm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wdzl);

        initViews();
        //创建file文件，用于存储剪裁后的照片
        cropImage = new File(Environment.getExternalStorageDirectory(), "user_" + id + ".jpg");
        cropImgUri = Uri.fromFile(cropImage);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Bitmap headImage = null;
        if (cropImage.exists()){
            try {
                headImage = BitmapFactory.decodeStream(getContentResolver().openInputStream(cropImgUri));
                headImage = toRoundCorner(headImage,100);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            iv_portrait.setImageBitmap(headImage);
        }else{
            iv_portrait.setBackgroundResource(R.drawable.portrait);
        }
    }

    private void initViews() {
        iv_exit = findViewById(R.id.iv_exit);
        et_gxqm = findViewById(R.id.et_gxqm);
        s_sex = findViewById(R.id.s_sex);
        tv_save = findViewById(R.id.tv_save);
        iv_portrait = findViewById(R.id.iv_portrait);
        tv_username = findViewById(R.id.tv_username);

        iv_exit.setOnClickListener(this);
        tv_save.setOnClickListener(this);
        iv_portrait.setOnClickListener(this);

        mSetting = getSharedPreferences("mySetting",MODE_PRIVATE);
        userName = mSetting.getString("userName","");
        tv_username.setText(userName);

        usersService = new UsersService(this);
        users = usersService.getUsersByUserName(userName);
        id = String.valueOf(users.getId());

        if (users.getSex().equals("男")){
            s_sex.setSelection(0);
        }else {
            s_sex.setSelection(1);
        }
        et_gxqm.setText(users.getGxqm());
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.iv_exit:
                finish();
                break;
            //保存我的资料
            case R.id.tv_save:
                sex = s_sex.getSelectedItem().toString();
                gxqm = et_gxqm.getText().toString();
                Users users1 = new Users(userName,sex,gxqm);
                if (usersService.updateByUserName(users1)){
                    Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
                    finish();
                }else{
                    Toast.makeText(this, "保存失败！", Toast.LENGTH_SHORT).show();
                }
                break;
            //更换头像
            case R.id.iv_portrait:
                Intent intent = new Intent(Intent.ACTION_PICK,null);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");
                startActivityForResult(intent,ALBUM_OK);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 用户没有进行有效的设置操作，返回
        switch (requestCode){
            case ALBUM_OK:
                Uri uri = null;
                if (data == null) return;
                if (resultCode == RESULT_OK){
                    if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
                        Toast.makeText(this, "SD不可用", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    uri = data.getData();
                    startImageAction(uri,output_X,output_Y,CUT_OK);
                }else{
                    Toast.makeText(this, "照片获取失败", Toast.LENGTH_SHORT).show();
                }
                break;
            //点击确定进行裁剪
            case CUT_OK:
                if (data == null) return;
                Bitmap headImage = null;
                try {
                    headImage = BitmapFactory.decodeStream(getContentResolver().openInputStream(cropImgUri));
                    headImage = toRoundCorner(headImage,100);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                iv_portrait.setImageBitmap(headImage);

                if (headImage != null && headImage.isRecycled()){
                    headImage.recycle();
                }
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void startImageAction(Uri uri, int outputX, int outputY, int requestCode) {
        Intent intent = null;
        intent = new Intent("com.android.camera.action.CROP");
        //可以选择图片类型，如果是*表明所有类型的图片
        intent.setDataAndType(uri, "image/*");
        // 下面这个crop = true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例，这里设置的是正方形（长宽比为1:1）
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", outputX);
        intent.putExtra("outputY", outputY);
        //裁剪时是否保留图片的比例，这里的比例是1:1
        intent.putExtra("scale", true);
        //设置目的地址uri
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cropImgUri);
        /*是否将数据保留在Bitmap中返回,当我们裁剪图片的尺寸大小在300像素以上时，
         APP有极大可能会crash，更甚者会死机直到你拆掉手机电池重新安上才解决。
         因此，当我们使用这个接口时，应尽量避免通过onActivityResult返回裁剪结果。*/
        intent.putExtra("return-data", false);
        //设置输出的格式
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        //是否取消人脸识别
        intent.putExtra("noFaceDetection", true);
        startActivityForResult(intent,requestCode);
    }


}
