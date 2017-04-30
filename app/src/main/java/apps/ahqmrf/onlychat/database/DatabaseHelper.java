package apps.ahqmrf.onlychat.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import apps.ahqmrf.onlychat.R;
import apps.ahqmrf.onlychat.utils.Constants;

/**
 * Created by Lenovo on 3/6/2017.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, Constants.DB.DATABASE_NAME, factory, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + Constants.DB.TABLE_CHAT_COLOR + "(" +
                Constants.DB.COLUMN_ID + " INTEGER PRIMARY KEY, " +
                Constants.DB.COLUMN_USERNAME + " TEXT, " +
                Constants.DB.COLUMN_COLOR + " INTEGER " +
                ");";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Constants.DB.TABLE_CHAT_COLOR);
        onCreate(db);
    }

    public void addChatColor (String username, int colorResId) {
        ContentValues values = new ContentValues();
        values.put(Constants.DB.COLUMN_USERNAME, username);
        values.put(Constants.DB.COLUMN_COLOR, colorResId);
        SQLiteDatabase db = getWritableDatabase();
        db.insert(Constants.DB.TABLE_CHAT_COLOR, null, values);
        db.close();
    }

    public void changeChatColor(String username, int colorResId) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + Constants.DB.TABLE_CHAT_COLOR + " WHERE " + Constants.DB.COLUMN_USERNAME  + " = '" + username + "';");
        db.close();
        addChatColor(username, colorResId);
    }

    public int getChatColor(String username) {
        SQLiteDatabase db = getWritableDatabase();

        String query = "SELECT * FROM " + Constants.DB.TABLE_CHAT_COLOR + " WHERE " + Constants.DB.COLUMN_USERNAME +
                " = '" + username + "';";
        Cursor colors = db.rawQuery(query, null);

        try {
            while (colors.moveToNext()) {
                return colors.getInt(colors.getColumnIndex(Constants.DB.COLUMN_COLOR));
            }
        } finally {
            colors.close();
        }
        return R.color.colorPrimary;
    }
}
