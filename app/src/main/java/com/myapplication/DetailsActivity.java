package com.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.CursorAnchorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.myapplication.adapters.CommentsAdapter;
import com.myapplication.databases.ArticleService;
import com.myapplication.databases.CommentsService;
import com.myapplication.databases.UsersService;
import com.myapplication.fragments.HomeFragment;
import com.myapplication.model.Article;
import com.myapplication.model.Comments;
import com.myapplication.model.Users;
import com.myapplication.utils.AsyncLoadImage;
import com.myapplication.utils.DateUtil;
import com.myapplication.utils.ImageUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import static com.myapplication.utils.ImageUtils.toRoundCorner;

public class DetailsActivity extends Activity implements View.OnClickListener {

    ImageView iv_exit,iv_picture,iv_portrait;
    TextView tv_username,tv_content,tv_date;
    EditText et_comments;
    Button btn_send;
    String id;
    RecyclerView rv_details;
    //适配器
    CommentsAdapter commentsAdapter = null;
    ArrayList<Comments> commentses= null;
    LinearLayoutManager layoutManager;
    //评论
    CommentsService service;
    SharedPreferences mSetting;
    String userName;
    //帖子
    ArticleService articleService;
    Article article;

    String pic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        initViews();

        pic = article.getPic();
        if (!pic.equals("")){
            new AsyncLoadImage(iv_picture).execute(pic);
        }else{
            iv_picture.setVisibility(View.GONE);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        service = new CommentsService(this);
        commentses = service.getCommentsByArticleId(id);

        commentsAdapter = new CommentsAdapter(this, commentses);
        layoutManager = new LinearLayoutManager(this);
        //设置布局管理器
        rv_details.setLayoutManager(layoutManager);
        //设置adapter
        rv_details.setAdapter(commentsAdapter);
        //设置Item增加、移除动画
        //rv_home.setItemAnimator(new DefaultItemAnimator());
        
        commentsAdapter.setOnItemLongClickListener(new CommentsAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View view, final int position) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setItems(new String[]{"删除"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (commentses.get(position).getUserName().equals(userName) || article.getUserName().equals(userName)){
                            if (service.deleteById(commentses.get(position).getId())) {
                                Toast.makeText(DetailsActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                                onResume();
                                setResult(032);
                            }else{
                                Toast.makeText(DetailsActivity.this, "删除失败", Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Toast.makeText(DetailsActivity.this, "你没有权利删除！", Toast.LENGTH_SHORT).show();
                        }
                        
                    }
                });
                builder.show();
            }
        });
    }

    public void initViews(){
        iv_exit = findViewById(R.id.iv_exit);
        tv_username = findViewById(R.id.tv_username);
        tv_content = findViewById(R.id.tv_content);
        et_comments = findViewById(R.id.et_comments);
        btn_send = findViewById(R.id.btn_send);
        rv_details = findViewById(R.id.rv_details);
        iv_picture = findViewById(R.id.iv_picture);
        tv_date = findViewById(R.id.tv_date);
        iv_portrait = findViewById(R.id.iv_portrait);

        iv_exit.setOnClickListener(this);
        btn_send.setOnClickListener(this);
        iv_picture.setOnClickListener(this);

        mSetting = getSharedPreferences("mySetting",MODE_PRIVATE);
        userName = mSetting.getString("userName","");

        Intent intent = getIntent();
        id = intent.getStringExtra("id");

        articleService = new ArticleService(this);
        article = articleService.getArticleById(id);
        tv_username.setText(article.getUserName());
        tv_content.setText(article.getContent());
        tv_date.setText(DateUtil.stampToDate(article.getDate()));

        //设置头像
        UsersService usersService = new UsersService(this);
        Users users = usersService.getUsersByUserName(article.getUserName());
        String userId = "";
        if (users != null){
            userId = String.valueOf(users.getId());
        }
        File cropImage = new File(Environment.getExternalStorageDirectory(),"user_" + userId + ".jpg");
        Uri cropImgUri = Uri.fromFile(cropImage);
        if (cropImage.exists()){
            Bitmap headImage = null;
            try{
                headImage = BitmapFactory.decodeStream(getContentResolver().openInputStream(cropImgUri));
                headImage = toRoundCorner(headImage,100);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            iv_portrait.setImageBitmap(headImage);
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            //返回上一个界面
            case R.id.iv_exit:
                finish();
                break;
            //发送评论信息
            case R.id.btn_send:
                //得到评论的内容
                String content = et_comments.getText().toString();

                //判断评论内容是否为空
                if (content.equals("") || content == null){
                    Toast.makeText(this, "请填写内容！", Toast.LENGTH_SHORT).show();
                    return;
                }

                //判断是否有登录
                if (userName.equals("") || userName == null){
                    Toast.makeText(this, "必须登录才能评论！", Toast.LENGTH_SHORT).show();
                    return;
                }

                //得到当前时间戳
                String date = String.valueOf(System.currentTimeMillis());

                //把信息添加到评论表
                Comments comments = new Comments(userName,id,date,content);
                CommentsService service = new CommentsService(this);
                if (service.add(comments)){
                    Toast.makeText(this, "评论成功", Toast.LENGTH_SHORT).show();
                    et_comments.setText("");
                    et_comments.clearFocus();
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(et_comments.getWindowToken(),0);
                    onResume();
                    setResult(032);
                }else{
                    Toast.makeText(this, "评论失败！", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.iv_picture:
                final Dialog dialog = new Dialog(this,android.R.style.Theme_Black_NoTitleBar_Fullscreen);
                ImageView image = getView();
                dialog.setContentView(image);
                dialog.show();
                image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });
                break;
        }
    }

    //获取图片视图
    private ImageView getView() {
        ImageView image = new ImageView(this);
        image.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        new AsyncLoadImage(image).execute(pic);
        return image;
    }
}
