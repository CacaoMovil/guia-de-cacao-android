package com.kronoscode.cacao.android.app.database.table;
 
public final class GuideTable {
    private GuideTable() {}
 
    public static final String TABLE_NAME = "guide";
 
    public static final String _ID = "_id"; 
 
    public static final String NAME = "name"; 
  
    public static final String[] ALL_COLUMNS = new String[] { _ID, NAME };
 
    public static final String SQL_CREATE = "CREATE TABLE guide ( _id INTEGER PRIMARY KEY AUTOINCREMENT , name TEXT )";
 
    public static final String SQL_INSERT = "INSERT INTO guide ( name ) VALUES ( ? )";
 
    public static final String SQL_DROP = "DROP TABLE IF EXISTS guide";
 
    public static final String WHERE_ID_EQUALS = _ID + "=?";
}