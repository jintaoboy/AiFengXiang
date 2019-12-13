package com.myapplication.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.myapplication.MainActivity;
import com.myapplication.R;
import com.myapplication.databases.CommentsService;
import com.myapplication.databases.UsersService;
import com.myapplication.databases.ZanService;
import com.myapplication.fragments.HomeFragment;
import com.myapplication.model.Article;
import com.myapplication.model.Comments;
import com.myapplication.model.Users;
import com.myapplication.model.Zan;
import com.myapplication.utils.AsyncLoadImage;
import com.myapplication.utils.ImageUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.myapplication.utils.ImageUtils.toRoundCorner;

/**
 * Created by Lin on 2019/6/10.
 */

public class ArticleRecylerViewAdapter extends RecyclerView.Adapter {

    //第一步：自定义一个回调接口来实现Click事件
    public interface OnItemClickListener{
        void onItemClick(View view, int position);
    }
    //第二步：声明自定义的接口
    private OnItemClickListener listener;

    //第三步：定义方法并暴露给外面的调用者
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    private Context context;//上下文
    private ArrayList<Article> list;//数据源
    private String userName;

    public ArticleRecylerViewAdapter(){}

    public ArticleRecylerViewAdapter(Context context, ArrayList<Article> list, String userName){
        this.context = context;
        this.list = list;
        this.userName = userName;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home,parent,false);
        return new MyViewHolder(view);
    }

    //绑定
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        final MyViewHolder viewHolder = (MyViewHolder) holder;
        viewHolder.tv_username.setText(list.get(position).getUserName());
        viewHolder.tv_content.setText(list.get(position).getContent());

        //显示头像
        UsersService usersService = new UsersService(context);
        Users users = usersService.getUsersByUserName(list.get(position).getUserName());
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
            viewHolder.iv_portrait.setImageBitmap(headImage);
        }

        //判断是不是该用户发的贴，如果是给予删除操作
        if (list.get(position).getUserName().equals(userName)){
            viewHolder.iv_delete.setVisibility(View.VISIBLE);
        }else {
            viewHolder.iv_delete.setVisibility(View.GONE);
        }

        //图片获取和展示
        String pic = list.get(position).getPic();
        if (!pic.equals("")){
            //new AsyncLoadImage(viewHolder.iv_picture).execute(pic);

            viewHolder.iv_picture.setImageBitmap(ImageUtils.compressImage(6,pic));
        }else{
            viewHolder.iv_picture.setVisibility(View.GONE);
        }

        //帖子id
        String id = String.valueOf(list.get(position).getId());

        //获取评论数
        CommentsService service = new CommentsService(context);
        ArrayList<Comments> commentses = service.getCommentsByArticleId(id);
        int count = commentses.size();
        if (count > 0) {
            viewHolder.tv_count.setText(String.valueOf(count));
        }else{
            viewHolder.tv_count.setText("评论");
        }

        //获得点赞数
        ZanService zanService = new ZanService(context);
        ArrayList<Zan> zans = zanService.getZanByArticleId(id);
        int zanCount = zans.size();
        if (zanCount > 0){
            viewHolder.tv_zanCount.setText(String.valueOf(zanCount));
        }else{
            viewHolder.tv_zanCount.setText("点赞");
        }

        //判读是否有点赞
        Zan zan = new Zan(userName,id);

        if (zanService.isZan(zan)){
            viewHolder.iv_zan.setImageResource(R.drawable.zan_true);
        }else{
            viewHolder.iv_zan.setImageResource(R.drawable.zan_false);
        }

        //设置监听器
        if (listener != null) {
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(v, holder.getAdapterPosition());
                }
            });
            viewHolder.iv_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(v,holder.getAdapterPosition());
                }
            });
            viewHolder.iv_picture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(v,holder.getAdapterPosition());
                }
            });
            viewHolder.iv_zan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(v,holder.getAdapterPosition());
                }
            });
        }
    }

    //有多少个item
    @Override
    public int getItemCount() {
        return list.size();
    }

    //创建ViewHolder继承RecyclerView.ViewHolder
    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv_username,tv_content,tv_count,tv_zanCount;
        ImageView iv_picture,iv_delete,iv_zan,iv_portrait;

        public MyViewHolder(View itemView) {
            super(itemView);

            tv_username = itemView.findViewById(R.id.tv_username);
            tv_content = itemView.findViewById(R.id.tv_content);
            iv_picture = itemView.findViewById(R.id.iv_picture);
            iv_delete = itemView.findViewById(R.id.iv_delete);
            tv_count = itemView.findViewById(R.id.tv_count);
            iv_zan = itemView.findViewById(R.id.iv_zan);
            tv_zanCount = itemView.findViewById(R.id.tv_zanCount);
            iv_portrait = itemView.findViewById(R.id.iv_portrait);
        }
    }

}