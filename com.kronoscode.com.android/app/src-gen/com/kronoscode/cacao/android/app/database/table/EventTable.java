package com.kronoscode.cacao.android.app.database.table;
 
public final class EventTable {
    private EventTable() {}
 
    public static final String TABLE_NAME = "event";
 
    public static final String _ID = "_id"; 
 
    public static final String EVENTID = "eventid"; 
 
    public static final String DESCRIPTION = "description"; 
 
    public static final String STARTDATE = "startdate"; 
 
    public static final String ENDDATE = "enddate"; 
  
    public static final String[] ALL_COLUMNS = new String[] { _ID, EVENTID, DESCRIPTION, STARTDATE, ENDDATE };
 
    public static final String SQL_CREATE = "CREATE TABLE event ( _id INTEGER PRIMARY KEY AUTOINCREMENT , eventid TEXT, description TEXT, startdate TEXT, enddate TEXT )";
 
    public static final String SQL_INSERT = "INSERT INTO event ( eventid, description, startdate, enddate ) VALUES ( ?, ?, ?, ? )";
 
    public static final String SQL_DROP = "DROP TABLE IF EXISTS event";
 
    public static final String WHERE_ID_EQUALS = _ID + "=?";
}