package com.myapplication.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.MediaStore;
import android.util.Log;

import com.myapplication.model.Article;
import com.myapplication.model.Users;

import java.util.ArrayList;

/**
 * Created by Lin on 2019/6/9.
 */

public class ArticleService {

    DatabaseHelper helper;
    SQLiteDatabase db;

    public ArticleService(Context context){
        helper = DatabaseHelper.getInstance(context);
        db = helper.getReadableDatabase();
    }

    public boolean add(Article article){
        long flag = 0;
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_USERNAME,article.getUserName());
        values.put(DatabaseHelper.COL_CONTENT,article.getContent());
        values.put(DatabaseHelper.COL_DATE,article.getDate());
        values.put(DatabaseHelper.COL_PIC,article.getPic());
        try{
            flag = db.insert(DatabaseHelper.TB_ARTICLE,null,values);
        }catch (Exception e){
            Log.v("error",e.getMessage());
        }
        if (flag > 0){
            return true;
        }
        return false;
    }
    public ArrayList<Article> getAllArticle() {
        ArrayList<Article> list = new ArrayList<>();
        try {
            Cursor cursor = null;
            cursor = db.rawQuery("select *  from " + DatabaseHelper.TB_ARTICLE,null);
            if (cursor != null && cursor.moveToLast()) {
                do {
                    Article article = new Article();
                    article.setId(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COL_ID)));
                    article.setUserName(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_USERNAME)));
                    article.setContent(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_CONTENT)));
                    article.setDate(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_DATE)));
                    article.setPic(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_PIC)));
                    list.add(article);
                } while (cursor.moveToPrevious());
            }
            if (cursor != null) {
                cursor.close();
            }
        } catch (Exception e) {
            Log.v("mydb", e.getMessage());
        }
        return list;
    }
    public Article getArticleById(String id){
        Article article = new Article();
        Cursor cursor = null;
        try{
            cursor = db.query(DatabaseHelper.TB_ARTICLE,new String[]{DatabaseHelper.COL_ID,DatabaseHelper.COL_USERNAME,DatabaseHelper.COL_CONTENT,DatabaseHelper.COL_DATE,DatabaseHelper.COL_PIC},
                    DatabaseHelper.COL_ID + " = ? ",new String[]{id},null,null,null);
            if (cursor != null && cursor.moveToFirst()){
                article.setId(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COL_ID)));
                article.setUserName(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_USERNAME)));
                article.setContent(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_CONTENT)));
                article.setDate(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_DATE)));
                article.setPic(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_PIC)));
            }
        }catch (Exception e){
            Log.v("mydb",e.getMessage());
        }
        if (cursor != null){
            cursor.close();
        }
        return article;
    }
    public ArrayList<Article> getArticleByUserName(String userName){
        ArrayList<Article> list = new ArrayList<>();
        Cursor cursor = null;
        try{
            cursor = db.query(DatabaseHelper.TB_ARTICLE,new String[]{DatabaseHelper.COL_ID,DatabaseHelper.COL_USERNAME,DatabaseHelper.COL_CONTENT,DatabaseHelper.COL_DATE,DatabaseHelper.COL_PIC},
                    DatabaseHelper.COL_USERNAME + " = ? ",new String[]{userName},null,null,null);
            if (cursor != null && cursor.moveToLast()) {
                do {
                    Article article = new Article();
                    article.setId(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COL_ID)));
                    article.setUserName(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_USERNAME)));
                    article.setContent(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_CONTENT)));
                    article.setDate(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_DATE)));
                    article.setPic(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_PIC)));
                    list.add(article);
                } while (cursor.moveToPrevious());
            }
            if (cursor != null) {
                cursor.close();
            }
        }catch (Exception e){
            Log.v("mydb",e.getMessage());
        }
        return list;
    }
    public boolean updateArticle(Article article){
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_ID,article.getId());
        values.put(DatabaseHelper.COL_USERNAME,article.getUserName());
        values.put(DatabaseHelper.COL_CONTENT,article.getContent());
        values.put(DatabaseHelper.COL_DATE,article.getDate());
        values.put(DatabaseHelper.COL_PIC,article.getPic());
        int flag = 0;
        try{
            flag = db.update(DatabaseHelper.TB_ARTICLE,values,DatabaseHelper.COL_ID + " = ? ",new String[]{String.valueOf(values.getAsInteger(DatabaseHelper.COL_ID))});
        }catch(Exception e){
            Log.v("db",e.getMessage());
        }
        if (flag > 0) return true;
        return false;
    }
    public boolean deleteById(String id){
        int flag = 0;
        try{
            flag = db.delete(DatabaseHelper.TB_ARTICLE,DatabaseHelper.COL_ID + " = ? ",new String[]{id});
        }catch(Exception e){
            Log.v("mydb",e.getMessage());
        }
        if (flag > 0) return true;
        return false;
    }
}
