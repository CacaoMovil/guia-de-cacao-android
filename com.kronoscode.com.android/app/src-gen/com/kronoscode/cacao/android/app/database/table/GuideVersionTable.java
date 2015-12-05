package com.kronoscode.cacao.android.app.database.table;
 
public final class GuideVersionTable {
    private GuideVersionTable() {}
 
    public static final String TABLE_NAME = "guideversion";
 
    public static final String _ID = "_id"; 
 
    public static final String NAME = "name";

    public static final String TAGS = "tags";

    public static final String FILE = "file"; 
 
    public static final String DATE = "date"; 
 
    public static final String NUM_VERSION = "num_version"; 
 
    public static final String PATH = "path"; 
  
    public static final String[] ALL_COLUMNS = new String[] { _ID, NAME, FILE, DATE, NUM_VERSION, PATH, TAGS };
 
    public static final String SQL_CREATE = "CREATE TABLE guideversion ( _id INTEGER PRIMARY KEY AUTOINCREMENT , name TEXT, file TEXT, date TEXT, num_version TEXT, path TEXT, tags TEXT )";
 
    public static final String SQL_INSERT = "INSERT INTO guideversion ( name, file, date, num_version, path, tags ) VALUES ( ?, ?, ?, ?, ?, ? )";
 
    public static final String SQL_DROP = "DROP TABLE IF EXISTS guideversion";
 
    public static final String WHERE_ID_EQUALS = _ID + "=?";
}