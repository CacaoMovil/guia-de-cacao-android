package com.kronoscode.cacao.android.app.model;
 
import android.content.ContentValues;
import android.database.Cursor;
 
import android.os.Parcel;
import android.os.Parcelable;
   
import com.kronoscode.cacao.android.app.database.table.EventTable; 
  
import java.util.ArrayList;
import java.util.List;
 
public class Event implements Parcelable {
    private transient long mRowId;
    private String mEventId; 
    private String mDescription; 
    private String mStartDate; 
    private String mEndDate; 
  
    private transient ContentValues mValues = new ContentValues();
 
    public Event() {}
 
    public Event(final Cursor cursor) {
        this(cursor, false);
    }
 
    public Event(final Cursor cursor, boolean prependTableName) {
        String prefix = prependTableName ? EventTable.TABLE_NAME + "_" : "";
        setRowId(cursor.getLong(cursor.getColumnIndex(prefix + EventTable._ID)));
        setEventId(cursor.getString(cursor.getColumnIndex(prefix + EventTable.EVENTID))); 
        setDescription(cursor.getString(cursor.getColumnIndex(prefix + EventTable.DESCRIPTION))); 
        setStartDate(cursor.getString(cursor.getColumnIndex(prefix + EventTable.STARTDATE))); 
        setEndDate(cursor.getString(cursor.getColumnIndex(prefix + EventTable.ENDDATE))); 
    }
 
    public Event(Parcel parcel) {
        mRowId = parcel.readLong();
 
        setEventId(parcel.readString()); 
 
        setDescription(parcel.readString()); 
 
        setStartDate(parcel.readString()); 
 
        setEndDate(parcel.readString()); 
    }
    
    @Override
    public int describeContents() {
        return 0;
    }
 
    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeLong(mRowId);
 
        parcel.writeString(getEventId()); 
 
        parcel.writeString(getDescription()); 
 
        parcel.writeString(getStartDate()); 
 
        parcel.writeString(getEndDate()); 
    }
 
    public static final Creator<Event> CREATOR = new Creator<Event>() {
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }
 
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };
  
    public final void setRowId(long _id) {
        mRowId = _id;
        mValues.put(EventTable._ID, _id);
    }
 
    public final void setEventId(String eventId) {
        mEventId = eventId;
        mValues.put(EventTable.EVENTID, eventId);
    }
 
    public final void setDescription(String description) {
        mDescription = description;
        mValues.put(EventTable.DESCRIPTION, description);
    }
 
    public final void setStartDate(String startDate) {
        mStartDate = startDate;
        mValues.put(EventTable.STARTDATE, startDate);
    }
 
    public final void setEndDate(String endDate) {
        mEndDate = endDate;
        mValues.put(EventTable.ENDDATE, endDate);
    }
  
    public long getRowId() {
        return mRowId;
    }
 
    public String getEventId() {
        return mEventId;
    }
 
    public String getDescription() {
        return mDescription;
    }
 
    public String getStartDate() {
        return mStartDate;
    }
 
    public String getEndDate() {
        return mEndDate;
    }
  
    public ContentValues getContentValues() {
        return mValues;
    }
  
    public static List<Event> listFromCursor(Cursor cursor) {
        List<Event> list = new ArrayList<Event>();
 
        if (cursor != null && cursor.moveToFirst()) {
            do {
                list.add(new Event(cursor));
            } while (cursor.moveToNext());
        }
 
        return list;
    }
 
    // BEGIN PERSISTED SECTION - put custom methods here

    // END PERSISTED SECTION
}