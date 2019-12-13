package com.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.myapplication.databases.UsersService;
import com.myapplication.fragments.MyFragment;
import com.myapplication.model.Users;

public class RegisterActivity extends Activity implements View.OnClickListener {

    ImageView iv_exit;
    EditText et_user,et_pwd,et_rePwd;
    Button btn_register;
    CheckBox cb_agreement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initViews();
    }

    public void initViews(){
        iv_exit = findViewById(R.id.iv_exit);
        et_user = findViewById(R.id.et_user);
        et_pwd = findViewById(R.id.et_pwd);
        btn_register = findViewById(R.id.btn_register);
        et_rePwd = findViewById(R.id.et_rePwd);
        cb_agreement = findViewById(R.id.cb_agreement);

        iv_exit.setOnClickListener(this);
        btn_register.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_exit:
                finish();
                break;
            //注册
            case R.id.btn_register:
                String userName = et_user.getText().toString();
                String pwd = et_pwd.getText().toString();
                String rePwd = et_rePwd.getText().toString();
                if (userName.equals("") || userName == null){
                    Toast.makeText(this, "账号不能为空！", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (pwd.equals("") || pwd == null){
                    Toast.makeText(this, "密码不能为空！", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (rePwd.equals("") || rePwd == null){
                    Toast.makeText(this, "确认密码不能为空！", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!rePwd.equals(pwd)){
                    Toast.makeText(this, "两次密码不一致！", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!cb_agreement.isChecked()){
                    Toast.makeText(this, "请勾选协议", Toast.LENGTH_SHORT).show();
                    return;
                }
                UsersService service = new UsersService(this);
                Users users = service.getUsersByUserName(userName);
                if (users != null){
                    Toast.makeText(this, "该账号已被注册！，请换其他号试试", Toast.LENGTH_SHORT).show();
                }else{
                    if (service.add(new Users(userName,pwd))){
                        Toast.makeText(this, "注册成功", Toast.LENGTH_SHORT).show();
                        SharedPreferences mSetting = getSharedPreferences("mySetting",MODE_PRIVATE);
                        SharedPreferences.Editor editor = mSetting.edit();
                        editor.putString("userName",userName);
                        editor.commit();
                        setResult(012);
                        finish();
                        startActivity(new Intent(this,MainActivity.class));
                    }else{
                        Toast.makeText(this, "注册失败！", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }
}
