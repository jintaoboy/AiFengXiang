package com.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.myapplication.adapters.ArticleRecylerViewAdapter;
import com.myapplication.databases.ArticleService;
import com.myapplication.databases.CommentsService;
import com.myapplication.databases.ZanService;
import com.myapplication.model.Article;
import com.myapplication.model.Zan;
import com.myapplication.utils.AsyncLoadImage;

import java.util.ArrayList;

public class MyArticleActivity extends Activity implements View.OnClickListener, ArticleRecylerViewAdapter.OnItemClickListener {

    ImageView iv_exit;
    TextView tv_noData;
    RecyclerView rv_myArticle;
    //实例SharedPreferences
    SharedPreferences mSetting;
    //存储用户
    String userName;
    //帖子
    ArticleService articleService;
    ArrayList<Article> articles = null;
    //赞
    ZanService zanService;
    //评论
    CommentsService commentsService;
    //适配器
    ArticleRecylerViewAdapter adapter = null;
    LinearLayoutManager manager;
    //帖子id
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_article);

        initViews();
    }

    private void initViews() {
        iv_exit = findViewById(R.id.iv_exit);
        tv_noData = findViewById(R.id.tv_noData);
        rv_myArticle = findViewById(R.id.rv_myArticle);

        //设置监听
        iv_exit.setOnClickListener(this);

        mSetting = getSharedPreferences("mySetting",MODE_PRIVATE);
        userName = mSetting.getString("userName","");

        articleService = new ArticleService(this);
        manager = new LinearLayoutManager(this);
        //设置布局管理器
        rv_myArticle.setLayoutManager(manager);

        //添加分割线
        DividerItemDecoration decoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        decoration.setDrawable(getResources().getDrawable(R.drawable.divider,null));
        rv_myArticle.addItemDecoration(decoration);

        zanService = new ZanService(this);
        commentsService = new CommentsService(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        articles = articleService.getArticleByUserName(userName);
        if (articles.size() <= 0){
            tv_noData.setVisibility(View.VISIBLE);
        }else{
            tv_noData.setVisibility(View.GONE);
        }
        adapter = new ArticleRecylerViewAdapter(this,articles,userName);
        //设置adapter
        rv_myArticle.setAdapter(adapter);
        //设置Item增加、移除动画
        //rv_home.setItemAnimator(new DefaultItemAnimator());

        adapter.setOnItemClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.iv_exit:
                finish();
                break;
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        id = String.valueOf(articles.get(position).getId());
        switch(view.getId()){
            //点赞
            case R.id.iv_zan:
                Zan zan = new Zan(userName,id);
                if(zanService.isZan(zan)){
                    if (zanService.deleteById(zan)) onResume();
                }else {
                    if(zanService.add(zan)) onResume();
                }
                break;
            //查看图片
            case R.id.iv_picture:
                String pic = articles.get(position).getPic();
                final Dialog dialog1 = new Dialog(this,android.R.style.Theme_Black_NoTitleBar_Fullscreen);
                ImageView image = new ImageView(this);
                image.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
                new AsyncLoadImage(image).execute(pic);
                dialog1.setContentView(image);
                dialog1.show();
                image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog1.cancel();
                    }
                });
                break;
            //删除选中的贴
            case R.id.iv_delete:
                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle("温馨提示：")
                        .setMessage("你确定要删除该记录吗？")
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (articleService.deleteById(id)){
                                    Toast.makeText(MyArticleActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                                    zanService.deleteByArticleId(id);//删除点赞
                                    commentsService.deleteByArticleId(id);//删除评论
                                    onResume();
                                }else{
                                    Toast.makeText(MyArticleActivity.this, "删除失败！", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }).show();
                break;
            default:
                Intent intent = new Intent(view.getContext(), DetailsActivity.class);
                intent.putExtra("id",String.valueOf(id));
                startActivity(intent);
                break;
        }
    }
}
