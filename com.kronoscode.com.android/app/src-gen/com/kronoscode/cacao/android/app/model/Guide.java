package com.kronoscode.cacao.android.app.model;
 
import android.content.ContentValues;
import android.database.Cursor;
 
import android.os.Parcel;
import android.os.Parcelable;
   
import com.kronoscode.cacao.android.app.database.table.GuideTable; 
  
import java.util.ArrayList;
import java.util.List;
 
public class Guide implements Parcelable {
    private transient long mRowId;
    private String mName; 
  
    private transient ContentValues mValues = new ContentValues();
 
    public Guide() {}
 
    public Guide(final Cursor cursor) {
        this(cursor, false);
    }
 
    public Guide(final Cursor cursor, boolean prependTableName) {
        String prefix = prependTableName ? GuideTable.TABLE_NAME + "_" : "";
        setRowId(cursor.getLong(cursor.getColumnIndex(prefix + GuideTable._ID)));
        setName(cursor.getString(cursor.getColumnIndex(prefix + GuideTable.NAME))); 
    }
 
    public Guide(Parcel parcel) {
        mRowId = parcel.readLong();
 
        setName(parcel.readString()); 
    }
    
    @Override
    public int describeContents() {
        return 0;
    }
 
    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeLong(mRowId);
 
        parcel.writeString(getName()); 
    }
 
    public static final Creator<Guide> CREATOR = new Creator<Guide>() {
        public Guide createFromParcel(Parcel in) {
            return new Guide(in);
        }
 
        public Guide[] newArray(int size) {
            return new Guide[size];
        }
    };
  
    public final void setRowId(long _id) {
        mRowId = _id;
        mValues.put(GuideTable._ID, _id);
    }
 
    public final void setName(String name) {
        mName = name;
        mValues.put(GuideTable.NAME, name);
    }
  
    public long getRowId() {
        return mRowId;
    }
 
    public String getName() {
        return mName;
    }
  
    public ContentValues getContentValues() {
        return mValues;
    }
  
    public static List<Guide> listFromCursor(Cursor cursor) {
        List<Guide> list = new ArrayList<Guide>();
 
        if (cursor != null && cursor.moveToFirst()) {
            do {
                list.add(new Guide(cursor));
            } while (cursor.moveToNext());
        }
 
        return list;
    }
 
    // BEGIN PERSISTED SECTION - put custom methods here

    // END PERSISTED SECTION
}