package com.myapplication.fragments;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.myapplication.ArticleActivity;
import com.myapplication.DetailsActivity;
import com.myapplication.LoginActivity;
import com.myapplication.MainActivity;
import com.myapplication.R;
import com.myapplication.adapters.ArticleRecylerViewAdapter;
import com.myapplication.databases.ArticleService;
import com.myapplication.databases.CommentsService;
import com.myapplication.databases.ZanService;
import com.myapplication.model.Article;
import com.myapplication.model.Zan;
import com.myapplication.utils.AsyncLoadImage;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements View.OnClickListener, ArticleRecylerViewAdapter.OnItemClickListener {

    TextView tv_noData;
    ImageView iv_edit;
    RecyclerView rv_home;
    //适配器
    ArticleRecylerViewAdapter recylerViewAdapter = null;
    ArrayList<Article> articles = null;
    //帖子
    ArticleService articleService;
    //线性布局管理器
    LinearLayoutManager layoutManager;
    //存储用户
    String userName;

    ZanService zanService;
    CommentsService commentsService;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);

        //设置布局管理器
        layoutManager = new LinearLayoutManager(getContext());
        rv_home.setLayoutManager(layoutManager);
        //设置Item增加、移除动画
        //rv_home.setItemAnimator(new DefaultItemAnimator());
        //添加分割线
        DividerItemDecoration decoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        decoration.setDrawable(getResources().getDrawable(R.drawable.divider,null));
        rv_home.addItemDecoration(decoration);

    }

    @Override
    public void onResume() {
        super.onResume();

        //获取所有帖子
        articleService = new ArticleService(getActivity());
        articles = articleService.getAllArticle();

        if (articles.size() <= 0){
            tv_noData.setVisibility(View.VISIBLE);
        }else{
            tv_noData.setVisibility(View.GONE);
        }

        recylerViewAdapter = new ArticleRecylerViewAdapter(getActivity(), articles,userName);

        //设置adapter
        rv_home.setAdapter(recylerViewAdapter);

        //控件点击事件
        recylerViewAdapter.setOnItemClickListener(this);

    }

    public void initViews(View view){
        tv_noData = view.findViewById(R.id.tv_noData);
        iv_edit = view.findViewById(R.id.iv_edit);
        rv_home = view.findViewById(R.id.rv_home);

        iv_edit.setOnClickListener(this);

        zanService = new ZanService(getContext());
        commentsService = new CommentsService(getContext());

        SharedPreferences mSetting = getContext().getSharedPreferences("mySetting",MODE_PRIVATE);
        userName = mSetting.getString("userName","");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //准备发贴
            case R.id.iv_edit:
                if (userName.equals("") || userName == null){
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(v.getContext());
                    alertDialog.setTitle("请先登录")
                            .setMessage("是否跳转到登录界面？")
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    startActivityForResult(new Intent(getContext(), LoginActivity.class),021);
                                }
                            })
                            .show();
                    return;
                }
                //跳转到发帖子
                startActivityForResult(new Intent(getContext(), ArticleActivity.class),021);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //登录成功后返回
        if (requestCode == 021 && (resultCode == 002)){
            getActivity().finish();
        }
        //发帖子和查看详细页面返回后的操作
        if (requestCode == 031 && (resultCode == 022 || resultCode == 032)){
            onResume();
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        final String id = String.valueOf(articles.get(position).getId());
        switch (view.getId()){
            //点赞操作
            case R.id.iv_zan:
                if (userName.equals("") || userName == null){
                    Toast.makeText(getContext(), "请先登录！", Toast.LENGTH_SHORT).show();
                    return;
                }
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
                final Dialog dialog1 = new Dialog(getContext(),android.R.style.Theme_Black_NoTitleBar_Fullscreen);
                ImageView image = new ImageView(getContext());
                image.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
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
                AlertDialog dialog = new AlertDialog.Builder(getContext())
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
                                    Toast.makeText(getContext(), "删除成功", Toast.LENGTH_SHORT).show();
                                    zanService.deleteByArticleId(id);//删除点赞
                                    commentsService.deleteByArticleId(id);//删除评论
                                    onResume();
                                }else{
                                    Toast.makeText(getContext(), "删除失败！", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }).show();
                break;
            //进入选中的贴查看详细
            default:
                Intent intent = new Intent(view.getContext(), DetailsActivity.class);
                intent.putExtra("id",id);
                startActivityForResult(intent,031);
                break;
        }
    }
}
