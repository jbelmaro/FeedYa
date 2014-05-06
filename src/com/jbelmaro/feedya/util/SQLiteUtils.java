package com.jbelmaro.feedya.util;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteUtils extends SQLiteOpenHelper {

    // Sentencia SQL para crear la tabla de Usuarios
    String sqlCreateFeed = "CREATE TABLE Feeds (title TEXT, subsId TEXT, feedURL TEXT, icon BLOB, countFeed INTEGER, catId TEXT, catTitle TEXT)";
    String sqlCreateCategory = "CREATE TABLE Category (title TEXT, catId TEXT, countCat INTEGER)";

    public SQLiteUtils(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
        // TODO Auto-generated constructor stub
    }

    public SQLiteUtils(Context context, String name, CursorFactory factory, int version,
            DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Se ejecuta la sentencia SQL de creaci??n de la tabla

        db.execSQL(sqlCreateFeed);
        db.execSQL(sqlCreateCategory);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int versionAnterior, int versionNueva) {

        db.execSQL("DROP TABLE IF EXISTS Feeds");
        db.execSQL("DROP TABLE IF EXISTS Category");
        // Se crea la nueva versi??n de la tabla
        db.execSQL(sqlCreateFeed);
        db.execSQL(sqlCreateCategory);

    }

}
