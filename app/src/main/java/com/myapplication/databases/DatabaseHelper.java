package com.myapplication.databases;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


/**
 * Created by Lin on 2019/6/8.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "mydb";
    public static final int DB_VERSION = 1;
    public static final String TB_USERS = "users";//用户表
    public static final String COL_ID = "id";
    public static final String COL_USERNAME = "userName";//用户名
    public static final String COL_PWD = "pwd";//密码
    public static final String COL_SEX = "sex";//性别
    public static final String COL_GXQM = "gxqm";//个性签名
    //创建用户表
    public static final String SQL = "CREATE TABLE IF NOT EXISTS " + TB_USERS + "(" +
            COL_ID + " integer primary key autoincrement," +
            COL_USERNAME + " text," +
            COL_PWD + " text," +
            COL_SEX + " text default '男'," +
            COL_GXQM + " text" + ")";

    public static final String TB_ARTICLE = "article";//帖子表
    public static final String COL_CONTENT = "content";//内容
    public static final String COL_DATE = "date";//时间
    public static final String COL_PIC = "pic";//图片路径
    //创建帖子表
    public static final String SQL_FILES = "CREATE TABLE IF NOT EXISTS " + TB_ARTICLE + "(" +
            COL_ID + " integer primary key autoincrement," +
            COL_USERNAME + " text," +
            COL_CONTENT + " text," +
            COL_DATE + " text," +
            COL_PIC + " text" + ")";

    public static final String TB_COMMENTS ="comments";//评论表
    public static final String COL_ARTICLE_ID = "article_id";//帖子id
    //创建评论表
    public static final String SQL_COMMENTS = "CREATE TABLE IF NOT EXISTS " + TB_COMMENTS + "(" +
            COL_ID + " integer primary key autoincrement," +
            COL_USERNAME + " text," +
            COL_ARTICLE_ID + " text," +
            COL_CONTENT + " text," +
            COL_DATE + " text" + ")";

    public static final String TB_ZAN = "zan";//点赞表
    //创建点赞表
    public static final String SQL_ZAN = "CREATE TABLE IF NOT EXISTS " + TB_ZAN + "(" +
            COL_ID + " integer primary key autoincrement," +
            COL_USERNAME + " text," +
            COL_ARTICLE_ID + " text" + ")";


    private static DatabaseHelper databaseHelper;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL);
        db.execSQL(SQL_FILES);
        db.execSQL(SQL_COMMENTS);
        db.execSQL(SQL_ZAN);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        /*switch (oldVersion) {
            case 1:
                db.execSQL(SQL_ZAN);
                Log.v("sql","更新");
                break;
            case 2:

                break;
            default:
                break;
        }*/

    }

    public static synchronized DatabaseHelper getInstance(Context context){
        if(databaseHelper == null){
            databaseHelper = new DatabaseHelper(context);
        }
        return databaseHelper;
    }

}
