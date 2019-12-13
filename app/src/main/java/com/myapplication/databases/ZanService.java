package com.myapplication.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import com.myapplication.MainActivity;
import com.myapplication.model.Zan;

import java.util.ArrayList;

/**
 * Created by Lin on 2019/6/18.
 */

public class ZanService {

    DatabaseHelper helper;
    SQLiteDatabase db;

    public ZanService(Context context){
        helper = DatabaseHelper.getInstance(context);
        db = helper.getReadableDatabase();
    }
    //点赞
    public boolean add(Zan zan){
        long flag = 0;
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_USERNAME,zan.getUserName());
        values.put(DatabaseHelper.COL_ARTICLE_ID,zan.getArticleId());
        try{
            flag = db.insert(DatabaseHelper.TB_ZAN,null,values);
        }catch(Exception e){
            Log.v("error",e.getMessage());
        }
        if (flag > 0) return true;
        return false;
    }

    //获取某个帖子的点赞
    public ArrayList<Zan> getZanByArticleId(String articleId){
        ArrayList<Zan> list = new ArrayList<>();
        try{
            Cursor cursor = null;
            cursor = db.query(DatabaseHelper.TB_ZAN,null,DatabaseHelper.COL_ARTICLE_ID + " = ? ",
                    new String[]{articleId},null,null,null);
            if (cursor != null && cursor.moveToLast()){
                do {
                    Zan zan = new Zan();
                    zan.setId(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COL_ID)));
                    zan.setUserName(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_USERNAME)));
                    zan.setArticleId(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_ARTICLE_ID)));
                    list.add(zan);
                }while (cursor.moveToPrevious());
            }
            if (cursor != null) cursor.close();
        }catch(Exception e){
            Log.v("error",e.getMessage());
        }
        return list;
    }

    //获取某个用户的点赞
    public ArrayList<Zan> getZanByUserName(String userName){
        ArrayList<Zan> list = new ArrayList<>();
        try{
            Cursor cursor = null;
            cursor = db.query(DatabaseHelper.TB_ZAN,null,DatabaseHelper.COL_USERNAME + " = ? ",
                    new String[]{userName},null,null,null);
            if (cursor != null && cursor.moveToLast()){
                do {
                    Zan zan = new Zan();
                    zan.setId(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COL_ID)));
                    zan.setUserName(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_USERNAME)));
                    zan.setArticleId(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_ARTICLE_ID)));
                    list.add(zan);
                }while (cursor.moveToPrevious());
            }
            if (cursor != null) cursor.close();
        }catch(Exception e){
            Log.v("error",e.getMessage());
        }
        return list;
    }

    //得到某用户是否在该贴有点赞
    public boolean isZan(Zan zan){
        try{
            Cursor cursor = null;
            cursor = db.query(DatabaseHelper.TB_ZAN,null,DatabaseHelper.COL_USERNAME + " = ? and " + DatabaseHelper.COL_ARTICLE_ID + " = ? ",
                    new String[]{zan.getUserName(),zan.getArticleId()},null,null,null);
            if (cursor != null && cursor.getCount() > 0){
                cursor.close();
                return true;
            }
        }catch(Exception e){
            Log.v("error",e.getMessage());
        }
        return false;
    }

    //取消点赞
    public boolean deleteById(Zan zan){
        long flag = 0;
        try{
            flag = db.delete(DatabaseHelper.TB_ZAN,DatabaseHelper.COL_USERNAME + " = ? and " + DatabaseHelper.COL_ARTICLE_ID + " = ? ",
                    new String[]{zan.getUserName(),zan.getArticleId()});
        }catch (Exception e){
            Log.v("error",e.getMessage());
        }
        if (flag > 0) return true;
        return false;
    }

    //删除帖子为id的赞
    public boolean deleteByArticleId(String articleId){
        long flag = 0;
        try{
            flag = db.delete(DatabaseHelper.TB_ZAN,DatabaseHelper.COL_ARTICLE_ID + " = ? ",new String[]{articleId});
        }catch (Exception e){
            Log.v("error",e.getMessage());
        }
        if (flag > 0) return true;
        return false;
    }
}
