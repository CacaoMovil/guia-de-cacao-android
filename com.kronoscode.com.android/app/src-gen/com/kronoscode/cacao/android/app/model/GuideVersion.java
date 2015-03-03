package com.kronoscode.cacao.android.app.model;
 
import android.content.ContentValues;
import android.database.Cursor;
 
import android.os.Parcel;
import android.os.Parcelable;
   
import com.kronoscode.cacao.android.app.database.table.GuideVersionTable; 
  
import java.util.ArrayList;
import java.util.List;
 
public class GuideVersion implements Parcelable {
    private transient long mRowId;
    private String mName; 
    private String mFile; 
    private String mDate; 
    private String mNumVersion; 
    private long mGuideId; 
  
    private transient ContentValues mValues = new ContentValues();
 
    public GuideVersion() {}
 
    public GuideVersion(final Cursor cursor) {
        this(cursor, false);
    }
 
    public GuideVersion(final Cursor cursor, boolean prependTableName) {
        String prefix = prependTableName ? GuideVersionTable.TABLE_NAME + "_" : "";
        setRowId(cursor.getLong(cursor.getColumnIndex(prefix + GuideVersionTable._ID)));
        setName(cursor.getString(cursor.getColumnIndex(prefix + GuideVersionTable.NAME))); 
        setFile(cursor.getString(cursor.getColumnIndex(prefix + GuideVersionTable.FILE))); 
        setDate(cursor.getString(cursor.getColumnIndex(prefix + GuideVersionTable.DATE))); 
        setNumVersion(cursor.getString(cursor.getColumnIndex(prefix + GuideVersionTable.NUM_VERSION))); 
        setGuideId(cursor.getLong(cursor.getColumnIndex(prefix + GuideVersionTable.GUIDE_ID))); 
    }
 
    public GuideVersion(Parcel parcel) {
        mRowId = parcel.readLong();
 
        setName(parcel.readString()); 
 
        setFile(parcel.readString()); 
 
        setDate(parcel.readString()); 
 
        setNumVersion(parcel.readString()); 
 
        setGuideId(parcel.readLong()); 
    }
    
    @Override
    public int describeContents() {
        return 0;
    }
 
    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeLong(mRowId);
 
        parcel.writeString(getName()); 
 
        parcel.writeString(getFile()); 
 
        parcel.writeString(getDate()); 
 
        parcel.writeString(getNumVersion()); 
 
        parcel.writeLong(getGuideId()); 
    }
 
    public static final Creator<GuideVersion> CREATOR = new Creator<GuideVersion>() {
        public GuideVersion createFromParcel(Parcel in) {
            return new GuideVersion(in);
        }
 
        public GuideVersion[] newArray(int size) {
            return new GuideVersion[size];
        }
    };
  
    public final void setRowId(long _id) {
        mRowId = _id;
        mValues.put(GuideVersionTable._ID, _id);
    }
 
    public final void setName(String name) {
        mName = name;
        mValues.put(GuideVersionTable.NAME, name);
    }
 
    public final void setFile(String file) {
        mFile = file;
        mValues.put(GuideVersionTable.FILE, file);
    }
 
    public final void setDate(String date) {
        mDate = date;
        mValues.put(GuideVersionTable.DATE, date);
    }
 
    public final void setNumVersion(String numVersion) {
        mNumVersion = numVersion;
        mValues.put(GuideVersionTable.NUM_VERSION, numVersion);
    }
 
    public final void setGuideId(long guideId) {
        mGuideId = guideId;
        mValues.put(GuideVersionTable.GUIDE_ID, guideId);
    }
  
    public long getRowId() {
        return mRowId;
    }
 
    public String getName() {
        return mName;
    }
 
    public String getFile() {
        return mFile;
    }
 
    public String getDate() {
        return mDate;
    }
 
    public String getNumVersion() {
        return mNumVersion;
    }
 
    public long getGuideId() {
        return mGuideId;
    }
  
    public ContentValues getContentValues() {
        return mValues;
    }
  
    public static List<GuideVersion> listFromCursor(Cursor cursor) {
        List<GuideVersion> list = new ArrayList<GuideVersion>();
 
        if (cursor != null && cursor.moveToFirst()) {
            do {
                list.add(new GuideVersion(cursor));
            } while (cursor.moveToNext());
        }
 
        return list;
    }
 
    // BEGIN PERSISTED SECTION - put custom methods here

    // END PERSISTED SECTION
}