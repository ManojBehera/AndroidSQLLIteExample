package com.example.merlinosqlliteexample;

import java.util.Random;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
	
	private final static String DB_NAME = "ilmiodatabse.db";
	private final static int DB_VERSION = 1;
	
	public DBHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(ItemsHelper.CREATE_QUERY);
		createSampleData(db);
	}

	private void createSampleData(SQLiteDatabase db) {
		ContentValues values;
		for(int i = 1; i <= 100; i++) {
			values = new ContentValues();
			values.put(ItemsHelper.NAME, "Titolo " + i);
			
			Random vRand = new Random();
			values.put(ItemsHelper.QUANTITY, vRand.nextInt(1000));
			
			db.insert(ItemsHelper.TABLE_NAME, null, values);
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {	
		// combiando il numero di versione del db verrà chiamata questa funzione
		int i = 0;
		i++;
	}

}
