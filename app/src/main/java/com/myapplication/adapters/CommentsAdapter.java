package com.myapplication.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.myapplication.R;
import com.myapplication.databases.UsersService;
import com.myapplication.model.Comments;
import com.myapplication.model.Users;
import com.myapplication.utils.DateUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import static com.myapplication.utils.ImageUtils.toRoundCorner;

/**
 * Created by Lin on 2019/6/11.
 */

public class CommentsAdapter extends RecyclerView.Adapter {

    private Context context;
    private ArrayList<Comments> list;

    //自定义一个回调接口来实现Click事件
    public interface OnItemLongClickListener{
        void onItemLongClick(View view,int position);
    }
    public interface OnItemClickListener{
        void onItemClick(View view,int position);
    }
    //声明自定义接口
    private OnItemLongClickListener listener;
    private OnItemClickListener listener1;
    //定义方法并暴露给外面的调用者
    public void setOnItemLongClickListener(OnItemLongClickListener listener){
        this.listener = listener;
    }
    public void setOnItemClickListener(OnItemClickListener listener1){
        this.listener1 = listener1;
    }

    public CommentsAdapter() {
    }

    public CommentsAdapter(Context context, ArrayList<Comments> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comments,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        MyViewHolder myViewHolder = (MyViewHolder) holder;
        myViewHolder.tv_username.setText(list.get(position).getUserName());
        String date = DateUtil.stampToDate(list.get(position).getDate());
        myViewHolder.tv_date.setText(date);
        myViewHolder.tv_content.setText(list.get(position).getContent());

        //显示头像
        String userName = list.get(position).getUserName();
        UsersService usersService = new UsersService(context);
        Users users = usersService.getUsersByUserName(userName);
        String userId = "";
        if (users != null) {
            userId = String.valueOf(users.getId());
        }
        //创建file文件，用于存储剪裁后的照片
        File cropImage = new File(Environment.getExternalStorageDirectory(), "user_" + userId + ".jpg");
        Uri cropImgUri = Uri.fromFile(cropImage);

        if (cropImage.exists()){
            Bitmap headImage = null;
            try {
                headImage = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(cropImgUri));
                headImage = toRoundCorner(headImage,100);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            myViewHolder.iv_portrait.setImageBitmap(headImage);
        }

        if (listener != null){
            myViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    listener.onItemLongClick(v,position);
                    return true;
                }
            });
        }
        if (listener1 != null){
            myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener1.onItemClick(v,position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv_username,tv_date,tv_content;
        ImageView iv_portrait;

        public MyViewHolder(View itemView) {
            super(itemView);

            tv_username = itemView.findViewById(R.id.tv_username);
            tv_date = itemView.findViewById(R.id.tv_date);
            tv_content = itemView.findViewById(R.id.tv_content);
            iv_portrait = itemView.findViewById(R.id.iv_portrait);
        }
    }
}
