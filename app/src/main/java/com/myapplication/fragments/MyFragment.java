package com.myapplication.fragments;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.myapplication.LoginActivity;
import com.myapplication.MainActivity;
import com.myapplication.R;
import com.myapplication.WDZLActivity;
import com.myapplication.databases.ArticleService;
import com.myapplication.databases.UsersService;
import com.myapplication.model.Article;
import com.myapplication.model.Users;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;
import static com.myapplication.utils.ImageUtils.toRoundCorner;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyFragment extends Fragment implements View.OnClickListener {

    ImageView iv_login,iv_portrait;
    TextView tv_login_1,tv_login_2,tv_username;
    Button btn_exit;
    LinearLayout ll_wdzl,ll_gywm;
    String userName;

    File cropImage = null;
    Uri cropImgUri = null;
    String id;

    public MyFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences mSetting = getContext().getSharedPreferences("mySetting", MODE_PRIVATE);
        userName = mSetting.getString("userName","");
        if (userName.equals("") || userName == null){
            iv_portrait.setVisibility(View.GONE);
            iv_login.setVisibility(View.VISIBLE);
            tv_username.setVisibility(View.GONE);
            btn_exit.setVisibility(View.GONE);
            tv_login_1.setVisibility(View.VISIBLE);
            tv_login_2.setVisibility(View.VISIBLE);

        }else {
            tv_username.setText("昵称：" + userName);

            iv_portrait.setVisibility(View.VISIBLE);
            iv_login.setVisibility(View.GONE);
            tv_username.setVisibility(View.VISIBLE);
            btn_exit.setVisibility(View.VISIBLE);
            tv_login_1.setVisibility(View.GONE);
            tv_login_2.setVisibility(View.GONE);

            UsersService usersService = new UsersService(getContext());
            Users users = usersService.getUsersByUserName(userName);
            id = String.valueOf(users.getId());
            //创建file文件，用于存储剪裁后的照片
            cropImage = new File(Environment.getExternalStorageDirectory(), "user_" + id + ".jpg");
            cropImgUri = Uri.fromFile(cropImage);

            if (cropImage.exists()){
                Bitmap headImage = null;
                try {
                    headImage = BitmapFactory.decodeStream(getContext().getContentResolver().openInputStream(cropImgUri));
                    headImage = toRoundCorner(headImage,100);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                iv_portrait.setImageBitmap(headImage);
            }
        }
    }

    public void initViews(View view){
        iv_portrait = view.findViewById(R.id.iv_portrait);
        iv_login = view.findViewById(R.id.iv_login);
        tv_login_1 = view.findViewById(R.id.tv_login_1);
        tv_login_2 = view.findViewById(R.id.tv_login_2);
        tv_username = view.findViewById(R.id.tv_username);
        btn_exit = view.findViewById(R.id.btn_exit);
        ll_wdzl = view.findViewById(R.id.ll_wdzl);
        ll_gywm = view.findViewById(R.id.ll_gywm);

        iv_login.setOnClickListener(this);
        tv_login_1.setOnClickListener(this);
        tv_login_2.setOnClickListener(this);
        btn_exit.setOnClickListener(this);
        btn_exit.setOnClickListener(this);
        ll_wdzl.setOnClickListener(this);
        ll_gywm.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_login:
            case R.id.tv_login_1:
            case R.id.tv_login_2:
                startActivityForResult(new Intent(v.getContext(),LoginActivity.class),001);
                break;
            case R.id.btn_exit:
                SharedPreferences mSetting = getContext().getSharedPreferences("mySetting",MODE_PRIVATE);
                SharedPreferences.Editor editor = mSetting.edit();
                editor.clear();
                editor.commit();
                getActivity().finish();
                startActivity(new Intent(v.getContext(),MainActivity.class));
                break;
            case R.id.ll_wdzl:
                if (userName.equals("") || userName == null){
                    Toast.makeText(getContext(), "请登录账号！", Toast.LENGTH_SHORT).show();
                    return;
                }
                startActivity(new Intent(v.getContext(), WDZLActivity.class));
                break;
            case R.id.ll_gywm:
                Dialog dialog = new Dialog(getContext());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_gywm);
                dialog.setCancelable(true);
                dialog.show();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 001 && resultCode == 002){
            getActivity().finish();
        }
    }
}
