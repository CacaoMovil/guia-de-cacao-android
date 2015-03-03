package com.kronoscode.cacao.android.app.database.table;
 
public final class GuideVersionTable {
    private GuideVersionTable() {}
 
    public static final String TABLE_NAME = "guideversion";
 
    public static final String _ID = "_id"; 
 
    public static final String NAME = "name"; 
 
    public static final String FILE = "file"; 
 
    public static final String DATE = "date"; 
 
    public static final String NUM_VERSION = "num_version"; 
 
    public static final String GUIDE_ID = "guide_id"; 
  
    public static final String[] ALL_COLUMNS = new String[] { _ID, NAME, FILE, DATE, NUM_VERSION, GUIDE_ID };
 
    public static final String SQL_CREATE = "CREATE TABLE guideversion ( _id INTEGER PRIMARY KEY AUTOINCREMENT , name TEXT, file TEXT, date TEXT, num_version TEXT, guide_id TEXT, FOREIGN KEY(guide_id) REFERENCES guide(_id)  )";
 
    public static final String SQL_INSERT = "INSERT INTO guideversion ( name, file, date, num_version, guide_id ) VALUES ( ?, ?, ?, ?, ? )";
 
    public static final String SQL_DROP = "DROP TABLE IF EXISTS guideversion";
 
    public static final String WHERE_ID_EQUALS = _ID + "=?";
}