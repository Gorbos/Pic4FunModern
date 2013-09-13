package jpact.pic4funmodern.util;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/** SQLite Database Class **/

public class JpactDB {

	private Activity activity = null;
	private String DB_NAME = "jpact.db";
	private SQLiteDatabase db;
	
	public JpactDB(Activity activity) {
		this.activity = activity;
	}
	
	public boolean isLanguageSelected(Context context) {
		boolean language_selected = false;
	    db = this.activity.openOrCreateDatabase(this.DB_NAME, SQLiteDatabase.CREATE_IF_NECESSARY, null);
	    db.execSQL("CREATE TABLE IF NOT EXISTS tbl_language (id INTEGER PRIMARY KEY AUTOINCREMENT, language TEXT);");
	    final Cursor cur = db.query("tbl_language", null, null, null, null, null, "language");
	       
	    if(cur.moveToNext()) {
	    	language_selected = true;
	    } else {
	    	language_selected = false;
	    }
	    cur.close();
	    db.close();
	   	
	    return language_selected;
	}
	
	public void saveLanguage(String language) {
		db = null;
		try {
		    db = this.activity.openOrCreateDatabase(this.DB_NAME, SQLiteDatabase.CREATE_IF_NECESSARY, null);
		    db.execSQL("drop table tbl_language;");
		    db.execSQL("CREATE TABLE IF NOT EXISTS tbl_language (id INTEGER PRIMARY KEY AUTOINCREMENT, language TEXT);");
		    db.execSQL("insert into tbl_language (language) values ('" + language + "')");
		}
		catch (Exception e) {
			Log.e("db error", e.getMessage());
		}
		finally {
			db.close();
		}
	}
	
	public String getLanguage() {
		String language = null;
        db = this.activity.openOrCreateDatabase(this.DB_NAME, SQLiteDatabase.CREATE_IF_NECESSARY, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS tbl_language (id INTEGER PRIMARY KEY AUTOINCREMENT, language TEXT);");
        final Cursor cur = db.query("tbl_language", null, null, null, null, null, "language");
        
        if(cur.moveToNext()) {
     	   language = cur.getString(1);
        }
        cur.close();
        db.close();
        
        return language;
    }
	
	public void deleteLanguage(String id) {
    	db = null;
    	try {
		    db = this.activity.openOrCreateDatabase(this.DB_NAME, SQLiteDatabase.CREATE_IF_NECESSARY, null);
		    Cursor cur = db.query("tbl_language", null, null, null, null, null, null);
		    if(cur.getCount() == 1) {
		    	db.execSQL("INSERT INTO tbl_language (language) values ('   ')");
		    }
		    db.execSQL("delete from tbl_language where id = " + id);
		    Log.v("db msg", "deleted");
    	}
    	catch (Exception e) {
			Log.e("db error", e.getMessage());
		}
    	finally {
    		db.close();
    	}
    }
}