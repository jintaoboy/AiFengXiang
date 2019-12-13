package com.myapplication.fragments;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.myapplication.MyArticleActivity;
import com.myapplication.MyCommentsActivity;
import com.myapplication.MyZanActivity;
import com.myapplication.R;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class MessageFragment extends Fragment implements View.OnClickListener {

    LinearLayout ll_mytz,ll_mypl,ll_mydz;
    //定义SharedPreferences
    SharedPreferences mSetting;
    //存储用户
    String userName;

    public MessageFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_message, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
    }

    private void initViews(View view) {
        ll_mytz = view.findViewById(R.id.ll_mytz);
        ll_mypl = view.findViewById(R.id.ll_mypl);
        ll_mydz = view.findViewById(R.id.ll_mydz);

        ll_mytz.setOnClickListener(this);
        ll_mypl.setOnClickListener(this);
        ll_mydz.setOnClickListener(this);

        mSetting = getContext().getSharedPreferences("mySetting", MODE_PRIVATE);
        userName = mSetting.getString("userName","");
    }

    @Override
    public void onClick(View v) {
        if (userName.equals("") || userName ==null){
            Toast.makeText(v.getContext(), "请先登录！再查看", Toast.LENGTH_SHORT).show();
            return;
        }
        switch(v.getId()){
            case R.id.ll_mytz:
                startActivity(new Intent(getContext(), MyArticleActivity.class));
                break;
            case R.id.ll_mypl:
                startActivity(new Intent(getContext(), MyCommentsActivity.class));
                break;
            case R.id.ll_mydz:
                startActivity(new Intent(getContext(), MyZanActivity.class));
        }
    }
}
