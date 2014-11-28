package com.example.merlinosqlliteexample;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor>{

	private static final int ITEMS_LOADER_ID = 0;
	MyCursorAdapter vCursorAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		/*DBHelper vDBHelper = new DBHelper(this);
		SQLiteDatabase vDB = vDBHelper.getWritableDatabase();*/

		// IGNORANZA
		// Cursor vCursor = vDB.rawQuery("SELECT * FROM " +
		// ItemsHelper.TABLE_NAME, null);
		// IGNORANZA

		/*Cursor vCursor = vDB.query(ItemsHelper.TABLE_NAME, new String[] {
				ItemsHelper._ID, ItemsHelper.NAME, ItemsHelper.QUANTITY },
				ItemsHelper.WHERE_QUERY, null, null, null, null);*/

		//dumpData(vCursor);
		
		// ------>> 13
		/*Cursor vCursor = getContentResolver().query(MyContentProvider.ITEMS_URI
													, null
													, null
													, null
													, null);*/
		// fine 13
		
		
		
		
		// 15
		// rimuovo startmanagingcursor in quanto deprecato e utilizzo al suo posto un loader che esegue
		// il tutto in modo asincrono
		
		// è stato deprecato a causa della lentezza nel caricamento dei dati sul
		// thread dell'interfaccia grafica e perchè quando viene ruotato lo schermo
		// deve rieffettuare la query
		//startManagingCursor(vCursor);
		
		//20 commento il secondo parametro
		vCursorAdapter = new MyCursorAdapter(this, null);
		
		ListView cursorListView = (ListView) findViewById(R.id.cursorListView);
		cursorListView.setAdapter(vCursorAdapter);
		
		cursorListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// grazie al fatto di aver chiamato la colonna ID sul Database _ID,
				// ciò mi consente di ricevere l'id corretto come parametro
				Toast.makeText(MainActivity.this, "ID = " + id, Toast.LENGTH_SHORT).show();
			}
		});
		
		cursorListView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				deleteID(id);
				return false;
			}
		});
		// 16
		getLoaderManager().initLoader(ITEMS_LOADER_ID, null, this);
	}
	
	private void deleteID(long id) {
		/*DBHelper vDBHelper = new DBHelper(this);
		SQLiteDatabase vDB = vDBHelper.getWritableDatabase();
		vDB.delete(ItemsHelper.TABLE_NAME, ItemsHelper._ID + "=" + id, null);*/
		
		//14	
		getContentResolver().delete(MyContentProvider.ITEMS_URI, ItemsHelper._ID + "=" + id, null);
	}

	private void dumpData(Cursor vCursor) {
		while (vCursor.moveToNext()) {
			int vNameColumnIndex = vCursor.getColumnIndex(ItemsHelper.NAME);
			String vName = vCursor.getString(vNameColumnIndex);

			Log.d("DB", vName);
		}

		vCursor.close();
	}

	private class MyCursorAdapter extends CursorAdapter {

		public MyCursorAdapter(Context context, Cursor c) {
			//21 aggiungo un parametro 0
			super(context, c,0);
			// TODO Auto-generated constructor stub
		}
		
		private class ViewHolder {
			public TextView mName, mQuantity;
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			
			LayoutInflater vInflater = LayoutInflater.from(context);
			
			View vView = vInflater.inflate(R.layout.cella, null);
			
			ViewHolder vHolder = new ViewHolder();
			
			vHolder.mName = (TextView)vView.findViewById(R.id.name);
			vHolder.mQuantity = (TextView)vView.findViewById(R.id.quantity);
			
			vView.setTag(vHolder);
			
			return vView;
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			
			int vNameColumnIndex = cursor.getColumnIndex(ItemsHelper.NAME);
			int vQuantityColumnIndex = cursor.getColumnIndex(ItemsHelper.QUANTITY);
			
			ViewHolder vHolder = (ViewHolder)view.getTag();
			
			vHolder.mName.setText(cursor.getString(vNameColumnIndex));
			vHolder.mQuantity.setText(cursor.getString(vQuantityColumnIndex));
		}
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) 
	{
		//17
		CursorLoader vLoader = new CursorLoader(this, MyContentProvider.ITEMS_URI, null, null, null, null);
		return vLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) 
	{
		//18
		vCursorAdapter.swapCursor(data);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) 
	{
		//19
		vCursorAdapter.swapCursor(null);
	}
}
