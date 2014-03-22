package com.jbelmaro.feedya.util;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteUtils extends SQLiteOpenHelper {

	//Sentencia SQL para crear la tabla de Usuarios
    String sqlCreate = "CREATE TABLE Feeds (title TEXT, iconURL TEXT, feedURL," +
    		"imageURL TEXT, icon BLOB )";
 

	public SQLiteUtils(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	public SQLiteUtils(Context context, String name, CursorFactory factory,
			int version, DatabaseErrorHandler errorHandler) {
		super(context, name, factory, version, errorHandler);
		// TODO Auto-generated constructor stub
	}


    @Override
    public void onCreate(SQLiteDatabase db) {
        //Se ejecuta la sentencia SQL de creaci??n de la tabla
        db.execSQL(sqlCreate);
    }
 
    @Override
    public void onUpgrade(SQLiteDatabase db, int versionAnterior, int versionNueva) {

        db.execSQL("DROP TABLE IF EXISTS Feeds");
 
        //Se crea la nueva versi??n de la tabla
        db.execSQL(sqlCreate);
    }

}
