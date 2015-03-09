package com.kronoscode.cacao.android.app.provider;
 
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;
import android.util.Log;

import com.kronoscode.cacao.android.app.database.CacaoDatabase;
import com.kronoscode.cacao.android.app.database.table.GuideTable;
import com.kronoscode.cacao.android.app.database.table.GuideVersionTable;

import java.util.ArrayList;
import java.util.List;
 
public class CacaoProvider extends ContentProvider {
 
    public static final String AUTHORITY = "com.kronoscode.cacao.android.app.provider.cacao";
  
    public static final Uri AUTHORITY_URI = Uri.parse("content://" + AUTHORITY);
    public static final String TAG = "CacaoProvider";
 
    public static final Uri GUIDE_CONTENT_URI = Uri.withAppendedPath(CacaoProvider.AUTHORITY_URI, GuideContent.CONTENT_PATH);
 
    public static final Uri GUIDEVERSION_CONTENT_URI = Uri.withAppendedPath(CacaoProvider.AUTHORITY_URI, GuideVersionContent.CONTENT_PATH);
   
    private static final UriMatcher URI_MATCHER;
    private CacaoDatabase mDatabase;
 
    private static final int GUIDE_DIR = 0;
    private static final int GUIDE_ID = 1;
 
    private static final int GUIDEVERSION_DIR = 2;
    private static final int GUIDEVERSION_ID = 3;
   
    static {
        URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
 
        URI_MATCHER.addURI(AUTHORITY, GuideContent.CONTENT_PATH, GUIDE_DIR);
        URI_MATCHER.addURI(AUTHORITY, GuideContent.CONTENT_PATH + "/#", GUIDE_ID);
 
        URI_MATCHER.addURI(AUTHORITY, GuideVersionContent.CONTENT_PATH, GUIDEVERSION_DIR);
        URI_MATCHER.addURI(AUTHORITY, GuideVersionContent.CONTENT_PATH + "/#", GUIDEVERSION_ID);
    }
 
    private static class GuideContent implements BaseColumns {
        private GuideContent() {}
 
        public static final String CONTENT_PATH = "guide";
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.cacao.guide";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.cacao.guide";
    }
 
    private static class GuideVersionContent implements BaseColumns {
        private GuideVersionContent() {}
 
        public static final String CONTENT_PATH = "guideversion";
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.cacao.guideversion";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.cacao.guideversion";
    }
   
    @Override
    public final boolean onCreate() {
        mDatabase = new CacaoDatabase(getContext());
        return true;
    }
 
    @Override
    public final String getType(final Uri uri) {
        switch (URI_MATCHER.match(uri)) {
            case GUIDE_DIR:
                return GuideContent.CONTENT_TYPE;
            case GUIDE_ID:
                return GuideContent.CONTENT_ITEM_TYPE;
 
            case GUIDEVERSION_DIR:
                return GuideVersionContent.CONTENT_TYPE;
            case GUIDEVERSION_ID:
                return GuideVersionContent.CONTENT_ITEM_TYPE;
   
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }
 
    @Override
    public final Cursor query(final Uri uri, String[] projection, final String selection, final String[] selectionArgs, final String sortOrder) {
        final SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        final SQLiteDatabase dbConnection = mDatabase.getReadableDatabase();
 
        switch (URI_MATCHER.match(uri)) {
            case GUIDE_ID:
                queryBuilder.appendWhere(GuideTable._ID + "=" + uri.getLastPathSegment());
                break;
            case GUIDE_DIR:
                queryBuilder.setTables(GuideTable.TABLE_NAME);
                break;
 
            case GUIDEVERSION_ID:
                queryBuilder.appendWhere(GuideVersionTable._ID + "=" + uri.getLastPathSegment());
                break;
            case GUIDEVERSION_DIR:
                queryBuilder.setTables(GuideVersionTable.TABLE_NAME);
                break;
   
            default :
                throw new IllegalArgumentException("Unsupported URI:" + uri);
        }
 
        Cursor cursor = queryBuilder.query(dbConnection, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
 
        return cursor;
 
    }
 
    @Override
    public final Uri insert(final Uri uri, final ContentValues values) {
        final SQLiteDatabase dbConnection = mDatabase.getWritableDatabase();
 
        try {
            dbConnection.beginTransaction();
 
            switch (URI_MATCHER.match(uri)) {
                case GUIDE_DIR:
                case GUIDE_ID:
                    final long guideId = dbConnection.insertOrThrow(GuideTable.TABLE_NAME, null, values);
                    final Uri newGuideUri = ContentUris.withAppendedId(GUIDE_CONTENT_URI, guideId);
                    getContext().getContentResolver().notifyChange(newGuideUri, null); 
                    return newGuideUri;
 
                case GUIDEVERSION_DIR:
                case GUIDEVERSION_ID:
                    final long guideversionId = dbConnection.insertOrThrow(GuideVersionTable.TABLE_NAME, null, values);
                    final Uri newGuideVersionUri = ContentUris.withAppendedId(GUIDEVERSION_CONTENT_URI, guideversionId);
                    getContext().getContentResolver().notifyChange(newGuideVersionUri, null); 
                    return newGuideVersionUri;
  
                default :
                    throw new IllegalArgumentException("Unsupported URI:" + uri);
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        } finally {
            dbConnection.setTransactionSuccessful();
            dbConnection.endTransaction();
        }
 
        return null;
    }
 
    @Override
    public final int update(final Uri uri, final ContentValues values, final String selection, final String[] selectionArgs) {
        final SQLiteDatabase dbConnection = mDatabase.getWritableDatabase();
        int updateCount = 0;
        List<Uri> joinUris = new ArrayList<Uri>();
 
        try {
            dbConnection.beginTransaction();
 
            switch (URI_MATCHER.match(uri)) {
               case GUIDE_DIR :
                   updateCount = dbConnection.update(GuideTable.TABLE_NAME, values, selection, selectionArgs);
  
                   break;
               case GUIDE_ID :
                   final long guideId = ContentUris.parseId(uri);
                   updateCount = dbConnection.update(GuideTable.TABLE_NAME, values, 
                       GuideTable._ID + "=" + guideId + (TextUtils.isEmpty(selection) ? "" : " AND (" + selection + ")"), selectionArgs);
  
                   break;
 
               case GUIDEVERSION_DIR :
                   updateCount = dbConnection.update(GuideVersionTable.TABLE_NAME, values, selection, selectionArgs);
  
                   break;
               case GUIDEVERSION_ID :
                   final long guideversionId = ContentUris.parseId(uri);
                   updateCount = dbConnection.update(GuideVersionTable.TABLE_NAME, values, 
                       GuideVersionTable._ID + "=" + guideversionId + (TextUtils.isEmpty(selection) ? "" : " AND (" + selection + ")"), selectionArgs);
  
                   break;
  
                default :
                    throw new IllegalArgumentException("Unsupported URI:" + uri);
            }
        } finally {
            dbConnection.setTransactionSuccessful();
            dbConnection.endTransaction();
        }
 
        if (updateCount > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
 
            for (Uri joinUri : joinUris) {
                getContext().getContentResolver().notifyChange(joinUri, null);
            }
        }
 
        return updateCount;
 
    }
 
    @Override
    public final int delete(final Uri uri, final String selection, final String[] selectionArgs) {
        final SQLiteDatabase dbConnection = mDatabase.getWritableDatabase();
        int deleteCount = 0;
        List<Uri> joinUris = new ArrayList<Uri>();
 
        try {
            dbConnection.beginTransaction();
 
            switch (URI_MATCHER.match(uri)) {
                case GUIDE_DIR :
                    deleteCount = dbConnection.delete(GuideTable.TABLE_NAME, selection, selectionArgs);
  
                    break;
                case GUIDE_ID :
                    deleteCount = dbConnection.delete(GuideTable.TABLE_NAME, GuideTable.WHERE_ID_EQUALS, new String[] { uri.getLastPathSegment() });
  
                    break;
 
                case GUIDEVERSION_DIR :
                    deleteCount = dbConnection.delete(GuideVersionTable.TABLE_NAME, selection, selectionArgs);
  
                    break;
                case GUIDEVERSION_ID :
                    deleteCount = dbConnection.delete(GuideVersionTable.TABLE_NAME, GuideVersionTable.WHERE_ID_EQUALS, new String[] { uri.getLastPathSegment() });
  
                    break;
  
                default :
                    throw new IllegalArgumentException("Unsupported URI:" + uri);
            }
        } finally {
            dbConnection.setTransactionSuccessful();
            dbConnection.endTransaction();
        }
 
        if (deleteCount > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
 
            for (Uri joinUri : joinUris) {
                getContext().getContentResolver().notifyChange(joinUri, null);
            }
        }
 
        return deleteCount;
    }
}