package com.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.myapplication.databases.UsersService;
import com.myapplication.fragments.MyFragment;
import com.myapplication.model.Users;

public class LoginActivity extends Activity implements View.OnClickListener {

    ImageView iv_exit;
    EditText et_user,et_pwd;
    TextView tv_register;
    Button btn_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViews();
    }

    public void initViews(){
        iv_exit = findViewById(R.id.iv_exit);
        et_user = findViewById(R.id.et_user);
        et_pwd = findViewById(R.id.et_pwd);
        btn_login = findViewById(R.id.btn_login);
        tv_register = findViewById(R.id.tv_register);

        //设置监听
        iv_exit.setOnClickListener(this);
        tv_register.setOnClickListener(this);
        btn_login.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //返回上一个界面
            case R.id.iv_exit:
                finish();
                break;
            //跳转到注册界面
            case R.id.tv_register:
                startActivityForResult(new Intent(this,RegisterActivity.class),011);
                break;
            //登录
            case R.id.btn_login:
                String userName = et_user.getText().toString();
                String pwd = et_pwd.getText().toString();
                if (userName.equals("") || userName == null){
                    Toast.makeText(this, "账号不能为空！", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (pwd.equals("") || pwd == null){
                    Toast.makeText(this, "密码不能为空！", Toast.LENGTH_SHORT).show();
                    return;
                }
                UsersService service = new UsersService(this);
                Users users = service.getUsersByUserName(userName);
                if (users != null && users.getUserPwd().equals(pwd)){
                    SharedPreferences mSetting = getSharedPreferences("mySetting",MODE_PRIVATE);
                    SharedPreferences.Editor editor = mSetting.edit();
                    editor.putString("userName",userName);
                    editor.commit();
                    setResult(002);
                    finish();
                    startActivity(new Intent(this,MainActivity.class));
                    Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(this, "账号或密码不正确！", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 011 && resultCode == 012){
            setResult(002);
            finish();
        }
    }
}
