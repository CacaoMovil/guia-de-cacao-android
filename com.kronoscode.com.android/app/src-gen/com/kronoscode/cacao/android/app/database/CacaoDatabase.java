package com.kronoscode.cacao.android.app.database;
 
import com.kronoscode.cacao.android.app.database.table.*;
 
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
 
import android.util.Log;
 
public class CacaoDatabase extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "cacao.db";
    private static final int DATABASE_VERSION = 11; 
    public static final String TAG = "CacaoDatabase";
 
    public CacaoDatabase(final Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
 
    @Override
    public final void onCreate(final SQLiteDatabase db) {
        db.execSQL(GuideTable.SQL_CREATE);
 
        db.execSQL(EventTable.SQL_CREATE);
 
        db.execSQL(GuideVersionTable.SQL_CREATE);
    }
  
    @Override
    public final void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
        upgrade(db, oldVersion, newVersion);
    }
  
    private void dropTablesAndCreate(final SQLiteDatabase db) {
        db.execSQL(GuideTable.SQL_DROP);
 
        db.execSQL(EventTable.SQL_DROP);
 
        db.execSQL(GuideVersionTable.SQL_DROP);
   
        onCreate(db);
    }
 
    // BEGIN PERSISTED SECTION - put custom methods here
    // you may change the contents of this method, but do not rename/remove it
    private void upgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
        Log.e(TAG, "Updating database from version " + oldVersion + " to " + newVersion + ".");
        dropTablesAndCreate(db);
    }
    // END PERSISTED SECTION
}