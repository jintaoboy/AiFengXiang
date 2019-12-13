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
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.myapplication.adapters.ArticleRecylerViewAdapter;
import com.myapplication.databases.ArticleService;
import com.myapplication.databases.ZanService;
import com.myapplication.model.Article;
import com.myapplication.model.Zan;
import com.myapplication.utils.AsyncLoadImage;

import java.util.ArrayList;

public class MyZanActivity extends Activity implements View.OnClickListener, ArticleRecylerViewAdapter.OnItemClickListener {

    TextView tv_noData;
    ImageView iv_exit;
    RecyclerView rv_myZan;

    //获取存储的用户
    SharedPreferences mSetting;
    String userName;

    //布局管理器
    LinearLayoutManager manager;

    //帖子
    ArticleService articleService;
    ArrayList<Article> articleList = null;
    String id;
    Article article;

    //点赞
    ZanService zanService;
    ArrayList<Zan> zanList = null;

    //适配器
    ArticleRecylerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_zan);

        initViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //获取该用户点过赞
        zanList = zanService.getZanByUserName(userName);

        //通过帖子id的到帖子
        for (int i = 0;i < zanList.size();i++){
            article = articleService.getArticleById(zanList.get(i).getArticleId());
            articleList.add(article);
        }
        //判断有没有数据
        if (articleList.size() <= 0){
            tv_noData.setVisibility(View.VISIBLE);
            return;
        }else{
            tv_noData.setVisibility(View.GONE);
        }
        //配置适配器
        adapter = new ArticleRecylerViewAdapter(this,articleList,userName);
        rv_myZan.setAdapter(adapter);

        adapter.setOnItemClickListener(this);
    }

    private void initViews() {
        tv_noData = findViewById(R.id.tv_noData);
        iv_exit = findViewById(R.id.iv_exit);
        rv_myZan = findViewById(R.id.rv_myZan);

        iv_exit.setOnClickListener(this);

        //用户
        mSetting = getSharedPreferences("mySetting",MODE_PRIVATE);
        userName = mSetting.getString("userName","");

        //设置布局
        manager = new LinearLayoutManager(this);
        rv_myZan.setLayoutManager(manager);

        articleService = new ArticleService(this);
        zanService = new ZanService(this);

        //添加分割线
        DividerItemDecoration decoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        decoration.setDrawable(getResources().getDrawable(R.drawable.divider,null));
        rv_myZan.addItemDecoration(decoration);

        articleList = new ArrayList<>();
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
        id = String.valueOf(articleList.get(position).getId());
        switch(view.getId()){
            //点赞
            case R.id.iv_zan:
                Zan zan = new Zan(userName,id);
                if(zanService.isZan(zan)){
                    if (zanService.deleteById(zan)){
                        articleList.clear();
                        onResume();
                    }
                }else {
                    if(zanService.add(zan)) {
                        articleList.clear();
                        onResume();
                    }
                }
                break;
            //查看图片
            case R.id.iv_picture:
                String pic = articleList.get(position).getPic();
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
                                if (articleService.deleteById(id) && zanService.deleteByArticleId(id)){
                                    Toast.makeText(MyZanActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                                    articleList.clear();
                                    onResume();
                                }else{
                                    Toast.makeText(MyZanActivity.this, "删除失败！", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }).show();
                break;
            default:
                Intent intent = new Intent(view.getContext(), DetailsActivity.class);
                intent.putExtra("id",String.valueOf(id));
                startActivity(intent);
                articleList.clear();
                break;
        }
    }
}
