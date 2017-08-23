package database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Reza Rajan on 2017-05-24.
 */

public class DatabaseHelperExtras extends SQLiteOpenHelper{

    public static final String DATABASE_NAME = "extras.db";
    public static final String TABLE_NAME = "extras_table";
    public static final String ID = "ID";       //column 1      //String 0
    public static final String RESTAURANT = "RESTAURANT";       //column 2    //String 1
    public static final String ITEM = "ITEM";       //column 3      //String 2
    public static final String COST = "COST";       //column 4      //String 3
    public static final String UNIQUETAG = "UNIQUETAG";       //column 5      //String 4
    public static final String EXTRA = "EXTRA";       //column 6      //String 5
    public static final String QUANTITY = "QUANTITY";       //column 7      //String 6
    public static final String TYPE = "TYPE";       //column 8      //String 7


    public DatabaseHelperExtras(Context context) {
        super(context, DATABASE_NAME, null, 1);

    }



    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_String = "CREATE TABLE " + TABLE_NAME + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + RESTAURANT + " TEXT," + ITEM + " TEXT," + COST + " LONG," + EXTRA + " TEXT," + QUANTITY + " TEXT," + TYPE + " TEXT," + UNIQUETAG + " TEXT)";
        db.execSQL(SQL_String);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData(String restaurant, String item, String cost, String uniquetag, String extra, String quantity, String type){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(RESTAURANT, restaurant);
        contentValues.put(ITEM, item);
        contentValues.put(COST, cost);
        contentValues.put(UNIQUETAG, uniquetag);
        contentValues.put(EXTRA, extra);
        contentValues.put(QUANTITY, quantity);
        contentValues.put(TYPE, type);
        long result = db.insert(TABLE_NAME, null, contentValues);
        return result != -1;
    }

    public Cursor getAllData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        return res;
    }

    public Cursor getColumnData(String column){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT " + column + " FROM " + TABLE_NAME, null);
        return res;
    }

    //The variable ITEM just has to be changed to the column you are interested in deleting items from
    public Integer deleteData(String TODELETE){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, ITEM + " = ?", new String[] {TODELETE});
    }

    public void deleteAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, null, null);
    }

    public Cursor getItemID(String name){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT " + ID + " FROM " + TABLE_NAME + " WHERE " + ITEM + " = '" + name + "'";

        Cursor data = db.rawQuery(query, null);
        return data;
    }

    public void updateName(String newName, int id, String oldName){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + TABLE_NAME + " SET " + ITEM + " = '" + newName + "' WHERE " + ID + " = '" + id + "'" + " AND " + ITEM + " = '" + oldName + "'";

        Log.d("DatabaseHelper: ", "updateName: query: " + query);
        Log.d("DatabaseHelper: ", "updateName: Setting name to: " + newName);
        db.execSQL(query);
    }

    public void deleteName(int id, String name){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + TABLE_NAME + " WHERE " + ID + " = '" + id + "'" + " AND " + ITEM + " = '" + name + "'";

        Log.d("DatabaseHelper: ", "deleteName: query: " + query);
        Log.d("DatabaseHelper: ", "deleteName: Deleting: " + name + " from database");
        db.execSQL(query);
    }

    public Cursor orderExtras(String uniquetag, String restaurant){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT " + EXTRA + "," + COST + "," + QUANTITY + "," + TYPE + " FROM " + TABLE_NAME  + " WHERE " + UNIQUETAG + " = '" + uniquetag + "'" + " AND " + RESTAURANT + " = '" + restaurant + "'", null);
        return res;
    }
}
