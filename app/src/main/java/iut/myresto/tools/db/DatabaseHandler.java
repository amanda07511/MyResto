package iut.myresto.tools.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import iut.myresto.models.User;

/**
 * Created by amanda on 30/05/2017.
 */

public class DatabaseHandler implements DatabaseStruct {
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;
    private final Context mCtx;

    public DatabaseHandler(Context mCtx) {
        this.mCtx = mCtx;
    }

    public void open() {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();

    }

    public void addObj(int id, String token, User user) {
        ContentValues values = new ContentValues();
        values.put(KEY_ID, id);
        values.put(KEY_TOKEN, token);
        values.put(KEY_NOM, user.getNom());
        values.put(KEY_PRENOM, user.getPrenom());
        values.put(KEY_EMAIL, user.getEmail());
        values.put(KEY_PASSWORD, user.getPassword());
        mDb.insert(TABLE_LOCATION, null, values);
    }


    public String getToken() {

        String token = null;
        String[] columns = new String[] {
                KEY_ID,
                KEY_TOKEN
        };
        Cursor c = mDb.query(TABLE_LOCATION, columns, "1", null, null, null, null);
        if(c.moveToFirst()){
            do{
                //assing values
                token = c.getString(1);

            }while(c.moveToNext());
        }
        c.close();

        return token;
    }

    public User getUser() {
        User u = null;

        String[] columns = new String[] {
                KEY_NOM,
                KEY_PRENOM,
                KEY_EMAIL,
                KEY_PASSWORD
        };
        Cursor c = mDb.query(TABLE_LOCATION, columns, "1", null, null, null, null);
        if(c.moveToFirst()){
            do{
                 u = new User(c.getString(0), c.getString(1), c.getString(2), c.getString(3), "");

            }while(c.moveToNext());
        }
        c.close();

        return u;
    }

    public void removeObj(int id) {
        mDb.delete(TABLE_LOCATION, KEY_ID + "= ?", new String[]{String.valueOf(id)});
    }

    public void close() {
        mDbHelper.close();
    }
}
