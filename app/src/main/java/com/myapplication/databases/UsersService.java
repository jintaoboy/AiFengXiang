package com.myapplication.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.ContactsContract;
import android.util.Log;

import com.myapplication.model.Users;

import java.util.ArrayList;

/**
 * Created by Lin on 2019/6/8.
 */

public class UsersService {

    DatabaseHelper helper;
    SQLiteDatabase db;

    public UsersService(Context context){
        helper = DatabaseHelper.getInstance(context);
        db = helper.getReadableDatabase();
    }

    public boolean add(Users users){
        long flag = 0;
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_USERNAME,users.getUserName());
        values.put(DatabaseHelper.COL_PWD,users.getUserPwd());
        try{
            flag = db.insert(DatabaseHelper.TB_USERS,null,values);
        }catch (Exception e){
            Log.v("error",e.getMessage());
            return false;
        }
        if (flag > 0){
            return true;
        }
        return false;
    }
    public ArrayList<Users> getAllUsers(){
        ArrayList<Users> list = new ArrayList<>();
        try {
            Cursor cursor = null;
            cursor = db.rawQuery("select * from " + DatabaseHelper.TB_USERS, null);
            if (cursor != null && cursor.moveToFirst()){
                do {
                    Users users = new Users();
                    users.setId(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COL_ID)));
                    users.setUserName(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_USERNAME)));
                    users.setUserPwd(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_PWD)));
                    list.add(users);
                }while(cursor.moveToNext());
            }
            if (cursor != null){
                cursor.close();
            }
        }catch(Exception e){
            Log.v("mydb",e.getMessage());
        }
        return list;
    }
    public Users getUsersByUserName(String userName){
        Users users = null;
        Cursor cursor = null;
        try{
            cursor = db.query(DatabaseHelper.TB_USERS,new String[]{DatabaseHelper.COL_ID,DatabaseHelper.COL_USERNAME,DatabaseHelper.COL_PWD,DatabaseHelper.COL_SEX,DatabaseHelper.COL_GXQM},
                    DatabaseHelper.COL_USERNAME + " = ? ",new String[]{userName},null,null,null);
            if (cursor != null && cursor.moveToFirst()){
                users = new Users();
                users.setId(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COL_ID)));
                users.setUserName(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_USERNAME)));
                users.setUserPwd(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_PWD)));
                users.setSex(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_SEX)));
                users.setGxqm(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_GXQM)));
            }
        }catch (Exception e){
            Log.v("mydb",e.getMessage());
        }
        if (cursor != null){
            cursor.close();
        }
        return users;
    }
    public boolean updateUsers(Users users){
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_ID,users.getId());
        values.put(DatabaseHelper.COL_USERNAME,users.getUserName());
        values.put(DatabaseHelper.COL_PWD,users.getUserPwd());
        int flag = 0;
        try{
            flag = db.update(DatabaseHelper.TB_USERS,values,DatabaseHelper.COL_ID + " = ? ",new String[]{String.valueOf(values.getAsInteger(DatabaseHelper.COL_ID))});
        }catch (Exception e){
            Log.v("mydb",e.getMessage());
        }
        if (flag >0 ) return true;
        return false;
    }

    //修改个人资料
    public boolean updateByUserName(Users users){
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_SEX,users.getSex());
        values.put(DatabaseHelper.COL_GXQM,users.getGxqm());
        int flag = 0;
        try{
            flag = db.update(DatabaseHelper.TB_USERS,values,DatabaseHelper.COL_USERNAME + " = ? ",new String[]{users.getUserName()});
        }catch(Exception e){
            Log.v("error",e.getMessage());
        }
        if (flag > 0) return true;
        return false;
    }
    public boolean deleteById(int id){
        int flag = 0;
        try{
            flag = db.delete(DatabaseHelper.TB_USERS,DatabaseHelper.COL_ID + " = ? ",new String[]{String.valueOf(id)});
        }catch(Exception e){
            Log.v("mydb",e.getMessage());
        }
        if (flag > 0) return true;
        return false;
    }

}
