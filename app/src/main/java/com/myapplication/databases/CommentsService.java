package com.myapplication.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.myapplication.model.Comments;

import java.util.ArrayList;

/**
 * Created by Lin on 2019/6/11.
 */

public class CommentsService {

    DatabaseHelper helper;
    SQLiteDatabase db;

    public CommentsService(){}

    public CommentsService(Context context){
        helper = DatabaseHelper.getInstance(context);
        db = helper.getReadableDatabase();
    }

    public boolean add(Comments comments){
        long flag = 0;
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_USERNAME,comments.getUserName());
        values.put(DatabaseHelper.COL_ARTICLE_ID,comments.getArticleId());
        values.put(DatabaseHelper.COL_DATE,comments.getDate());
        values.put(DatabaseHelper.COL_CONTENT,comments.getContent());
        try{
            flag = db.insert(DatabaseHelper.TB_COMMENTS,null,values);
        }catch (Exception e){
            Log.v("error",e.getMessage());
            return false;
        }
        if (flag > 0){
            return true;
        }
        return false;
    }

    public ArrayList<Comments> getAllComments(){
        ArrayList<Comments> list = new ArrayList<>();
        try {
            Cursor cursor = null;
            cursor = db.rawQuery("select * from " + DatabaseHelper.TB_COMMENTS, null);
            if (cursor != null && cursor.moveToLast()){
                do {
                    Comments comments = new Comments();
                    comments.setId(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COL_ID)));
                    comments.setUserName(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_USERNAME)));
                    comments.setArticleId(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_ARTICLE_ID)));
                    comments.setDate(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_DATE)));
                    comments.setContent(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_CONTENT)));
                    list.add(comments);
                }while(cursor.moveToPrevious());
            }
            if (cursor != null){
                cursor.close();
            }
        }catch(Exception e){
            Log.v("mydb",e.getMessage());
        }
        return list;
    }

    //通过文章的id找出该贴所有评论
    public ArrayList<Comments> getCommentsByArticleId(String id){
        ArrayList<Comments> list = new ArrayList<>();
        try {
            Cursor cursor = null;
            cursor = db.query(DatabaseHelper.TB_COMMENTS,new String[]{DatabaseHelper.COL_ID,DatabaseHelper.COL_USERNAME,DatabaseHelper.COL_ARTICLE_ID,DatabaseHelper.COL_DATE,DatabaseHelper.COL_CONTENT},
                    DatabaseHelper.COL_ARTICLE_ID + " = ? ",new String[]{id},null,null,null);
            if (cursor != null && cursor.moveToLast()){
                do {
                    Comments comments = new Comments();
                    comments.setId(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COL_ID)));
                    comments.setUserName(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_USERNAME)));
                    comments.setArticleId(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_ARTICLE_ID)));
                    comments.setDate(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_DATE)));
                    comments.setContent(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_CONTENT)));
                    list.add(comments);
                }while(cursor.moveToPrevious());
            }
            if (cursor != null){
                cursor.close();
            }
        }catch(Exception e){
            Log.v("mydb",e.getMessage());
        }
        return list;
    }

    public ArrayList<Comments> getCommentsByUserName(String userName){
        ArrayList<Comments> list = new ArrayList<>();
        Cursor cursor = null;
        try{
            cursor = db.query(DatabaseHelper.TB_COMMENTS,new String[]{DatabaseHelper.COL_ID,DatabaseHelper.COL_USERNAME,DatabaseHelper.COL_ARTICLE_ID,DatabaseHelper.COL_DATE,DatabaseHelper.COL_CONTENT},
                    DatabaseHelper.COL_USERNAME + " = ? ",new String[]{userName},null,null,null);
            if (cursor != null && cursor.moveToLast()){
                do {
                    Comments comments = new Comments();
                    comments.setId(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COL_ID)));
                    comments.setUserName(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_USERNAME)));
                    comments.setArticleId(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_ARTICLE_ID)));
                    comments.setDate(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_DATE)));
                    comments.setContent(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_CONTENT)));
                    list.add(comments);
                }while(cursor.moveToPrevious());
            }
            if (cursor != null){
                cursor.close();
            }
        }catch (Exception e){
            Log.v("mydb",e.getMessage());
        }
        return list;
    }

    public boolean updateComments(Comments comments){
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_ID,comments.getId());
        values.put(DatabaseHelper.COL_USERNAME,comments.getUserName());
        values.put(DatabaseHelper.COL_ARTICLE_ID,comments.getArticleId());
        values.put(DatabaseHelper.COL_DATE,comments.getDate());
        values.put(DatabaseHelper.COL_CONTENT,comments.getContent());
        int flag = 0;
        try{
            flag = db.update(DatabaseHelper.TB_COMMENTS,values,DatabaseHelper.COL_ID + " = ? ",new String[]{String.valueOf(values.getAsInteger(DatabaseHelper.COL_ID))});
        }catch (Exception e){
            Log.v("mydb",e.getMessage());
        }
        if (flag >0 ) return true;
        return false;
    }

    //删除评论
    public boolean deleteById(int id){
        int flag = 0;
        try{
            flag = db.delete(DatabaseHelper.TB_COMMENTS,DatabaseHelper.COL_ID + " = ? ",new String[]{String.valueOf(id)});
        }catch(Exception e){
            Log.v("mydb",e.getMessage());
        }
        if (flag > 0) return true;
        return false;
    }

    //文章删除后评论也删除
    public boolean deleteByArticleId(String articleId){
        int flag = 0;
        try{
            flag = db.delete(DatabaseHelper.TB_COMMENTS,DatabaseHelper.COL_ARTICLE_ID + " = ? ",new String[]{articleId});
        }catch (Exception e){
            Log.v("error",e.getMessage());
        }
        if (flag > 0) return true;
        return false;
    }
}
