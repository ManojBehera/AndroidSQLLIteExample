package com.example.merlinosqlliteexample;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class MyContentProvider extends ContentProvider {

	// 1 authority ha il nome del package name completo
	public static final String AUTHORITY = "com.example.merlinosqlliteexample.mycontentprovider";
	// 2 path è il nome della tabella possibilmente al plurale
	public static final String ITEMS_PATH = "items";
	// 3 definisco il mio Uri
	public static final Uri ITEMS_URI = Uri
			.parse(ContentResolver.SCHEME_CONTENT + "://" + AUTHORITY + "/"
					+ ITEMS_PATH);
	// 4 definisco variabili per l'uri matcher e l'uri matcher stesso
	private final static int FULL_ITEMS_TABLE = 0;
	private final static int SINGLE_ITEM = 1;
	private final static UriMatcher mUriMatcher = new UriMatcher(
			UriMatcher.NO_MATCH);
	static { // chiamo le funzioni su l'oggetto mUriMatcher che è statico
		mUriMatcher.addURI(AUTHORITY, ITEMS_PATH, FULL_ITEMS_TABLE);
		mUriMatcher.addURI(AUTHORITY, ITEMS_PATH + "/#", SINGLE_ITEM);
	}
	// 5 creo variabile per dbhelper
	private DBHelper mHelper;

	@Override
	public boolean onCreate() {
		// è la funzione che viene chiamata quando viene creato il cp
		// 6 creo dbhelper e ritorno true
		mHelper = new DBHelper(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		// è la funzione che viene chiamata quando interrogo il cp
		// 7 faccio la query sul db e torno un cursore
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		switch (mUriMatcher.match(uri)) {

		case FULL_ITEMS_TABLE:
			// SELECT COLONNE IN PROJECTIONS FROM ITEMS WHERE TUTTO IL CONTENUTO
			// DI SELECTIONS ...
			queryBuilder.setTables(ItemsHelper.TABLE_NAME);
			break;

		case SINGLE_ITEM:
			// SELECT COLONNE IN PROJECTIONS FROM ITEMS WHERE _ID == IL VALORE
			// ...
			queryBuilder.setTables(ItemsHelper.TABLE_NAME);
			queryBuilder.appendWhere(ItemsHelper._ID + "="
					+ uri.getLastPathSegment());
			break;
		}
		SQLiteDatabase vDB = mHelper.getWritableDatabase();
		Cursor vCursor = queryBuilder.query(vDB, projection, selection,
				selectionArgs, null, null, sortOrder);
		// attacco la notifica della modifica dell'uri
		vCursor.setNotificationUri(getContext().getContentResolver(), uri);
		return vCursor;
	}

	public static final String MIME_TYPE_ITEMS = ContentResolver.CURSOR_DIR_BASE_TYPE + "/items";
	public static final String MIME_TYPE_ITEM = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/item";
		
	@Override
	public String getType(Uri uri) {
		// è la funzione che dato un determinato uri mi dice se torna tutti gli
		// elementi o un solo elemento
		// 11
		String vResult = "";
		switch (mUriMatcher.match(uri)) {
		case FULL_ITEMS_TABLE:
			vResult = MIME_TYPE_ITEMS;
			break;
		case SINGLE_ITEM:
			vResult = MIME_TYPE_ITEM;
			break;
		}
		return vResult;
		// -------->> seguo alla dichiarazione del cp nel manifest
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// è la funzione che viene chiamata quando viene inserito qualcosa nel cp
		// 8
		// verifico se l'uri corrisponde a /items e non /item/id
		if (mUriMatcher.match(uri) == FULL_ITEMS_TABLE) {
			SQLiteDatabase vDB = mHelper.getWritableDatabase();
			long vResult = vDB.insert(ItemsHelper.TABLE_NAME, null, values);
			// notifico l'inserimento
			getContext().getContentResolver().notifyChange(uri, null);
			return uri.parse(ITEMS_URI + "/" + vResult);
		} else {
			return null;
		}
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// è la funzione che viene chiamata quando cancello qualcosa dal cp
		// 9
		SQLiteDatabase vDB = mHelper.getWritableDatabase();
		int vResult = 0;
		
		if (mUriMatcher.match(uri) == FULL_ITEMS_TABLE) 
		{		
			vResult = vDB.delete(ItemsHelper.TABLE_NAME, selection, selectionArgs);
		} 
		else if(mUriMatcher.match(uri) == SINGLE_ITEM)  
		{			
			String vTmp = ItemsHelper._ID + " = " + uri.getLastPathSegment();
			vResult = vDB.delete(ItemsHelper.TABLE_NAME, selection + " AND " + vTmp, selectionArgs);
		}		
		if(vResult > 0) 
		{
			getContext().getContentResolver().notifyChange(uri, null);
		}
		return vResult;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// è la funzione che viene chiamata quando aggiorno un dato nel cp
		// 10
		
		int vResult = 0;
		SQLiteDatabase vDB = mHelper.getWritableDatabase();
		
		if(mUriMatcher.match(uri) == FULL_ITEMS_TABLE) 
		{
			vResult = vDB.update(ItemsHelper.TABLE_NAME, values, selection, selectionArgs);
		} 
		else if(mUriMatcher.match(uri) == SINGLE_ITEM) 
		{
			String vTmp = ItemsHelper._ID + " = " + uri.getLastPathSegment();
			vResult = vDB.update(ItemsHelper.TABLE_NAME, values, selection + " AND " + vTmp, selectionArgs);
		}
		if(vResult > 0)
		{
			getContext().getContentResolver().notifyChange(uri, null);
		}
		return vResult;
	}

}
