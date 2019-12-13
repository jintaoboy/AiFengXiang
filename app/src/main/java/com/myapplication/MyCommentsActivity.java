package com.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.myapplication.adapters.CommentsAdapter;
import com.myapplication.databases.CommentsService;
import com.myapplication.model.Comments;

import java.util.ArrayList;

public class MyCommentsActivity extends Activity implements View.OnClickListener, CommentsAdapter.OnItemClickListener, CommentsAdapter.OnItemLongClickListener {

    ImageView iv_exit;
    TextView tv_noData;
    RecyclerView rv_myComments;

    //评论
    CommentsAdapter adapter = null;
    CommentsService service;
    ArrayList<Comments> list = null;
    //获取用户
    String userName;
    LinearLayoutManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_comments);

        initViews();
    }

    private void initViews() {
        iv_exit = findViewById(R.id.iv_exit);
        tv_noData = findViewById(R.id.tv_noData);
        rv_myComments = findViewById(R.id.rv_myComments);

        iv_exit.setOnClickListener(this);

        SharedPreferences mSetting = getSharedPreferences("mySetting",MODE_PRIVATE);
        userName = mSetting.getString("userName","");

        service = new CommentsService(this);

        //设置布局
        manager = new LinearLayoutManager(this);
        rv_myComments.setLayoutManager(manager);
        DividerItemDecoration divider = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        divider.setDrawable(getResources().getDrawable(R.drawable.divider,null));
        rv_myComments.addItemDecoration(divider);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //获取评论数据
        list = service.getCommentsByUserName(userName);
        //添加适配器
        adapter = new CommentsAdapter(this,list);
        rv_myComments.setAdapter(adapter);

        if (list.size() <= 0){
            tv_noData.setVisibility(View.VISIBLE);
        }else{
            tv_noData.setVisibility(View.GONE);
        }

        adapter.setOnItemClickListener(this);
        adapter.setOnItemLongClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_exit:
                finish();
                break;
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        String id = list.get(position).getArticleId();
        Intent intent = new Intent(this,DetailsActivity.class);
        intent.putExtra("id",id);
        startActivity(intent);
    }

    @Override
    public void onItemLongClick(View view, int position) {
        final int id = list.get(position).getId();
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
                        if (service.deleteById(id)){
                            Toast.makeText(MyCommentsActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                            onResume();
                        }else{
                            Toast.makeText(MyCommentsActivity.this, "删除失败！", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).show();
    }
}
