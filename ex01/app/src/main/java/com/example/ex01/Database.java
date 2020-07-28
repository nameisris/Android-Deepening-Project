package com.example.ex01;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class Database extends SQLiteOpenHelper {
    public Database(@Nullable Context context) {
        super(context, "project.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sql) {
        sql.execSQL("create table diary(_id integer primary key autoincrement,wdate text, subject text, content text)");
        sql.execSQL("insert into diary(wdate,subject,content) values('2020/07/05','안드로이드','안드로이드를 배워보니 어렵지 않네요!')");
        sql.execSQL("insert into diary(wdate,subject,content) values('2020/07/15','장마시작','장마가 시작되었다.')");
        sql.execSQL("insert into diary(wdate,subject,content) values('2020/07/25','생일파티','정원이 생일에 생일파티를 재밌게 했다.')");
        sql.execSQL("insert into diary(wdate,subject,content) values('2020/08/05','안드로이드','안드로이드를 배워보니 어렵지 않네요!')");
        sql.execSQL("insert into diary(wdate,subject,content) values('2020/08/15','장마시작','장마가 시작되었다.')");
        sql.execSQL("insert into diary(wdate,subject,content) values('2020/08/25','생일파티','정원이 생일에 생일파티를 재밌게 했다.')");
        sql.execSQL("insert into diary(wdate,subject,content) values('2020/09/05','안드로이드','안드로이드를 배워보니 어렵지 않네요!')");
        sql.execSQL("insert into diary(wdate,subject,content) values('2020/09/15','장마시작','장마가 시작되었다.')");
        sql.execSQL("insert into diary(wdate,subject,content) values('2020/09/25','생일파티','정원이 생일에 생일파티를 재밌게 했다.')");
        sql.execSQL("insert into diary(wdate,subject,content) values('2020/10/05','안드로이드','안드로이드를 배워보니 어렵지 않네요!')");
        sql.execSQL("insert into diary(wdate,subject,content) values('2020/10/15','장마시작','장마가 시작되었다.')");
        sql.execSQL("insert into diary(wdate,subject,content) values('2020/10/25','생일파티','정원이 생일에 생일파티를 재밌게 했다.')");
        sql.execSQL("insert into diary(wdate,subject,content) values('2020/11/05','안드로이드','안드로이드를 배워보니 어렵지 않네요!')");
        sql.execSQL("insert into diary(wdate,subject,content) values('2020/11/15','장마시작','장마가 시작되었다.')");
        sql.execSQL("insert into diary(wdate,subject,content) values('2020/11/25','생일파티','정원이 생일에 생일파티를 재밌게 했다.')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
