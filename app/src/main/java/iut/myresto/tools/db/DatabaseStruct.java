package iut.myresto.tools.db;

/**
 * Created by amanda on 30/05/2017.
 */

public interface DatabaseStruct {

    // Location table name
    String TABLE_LOCATION = "location";

    // Generic id column
    String KEY_ID = "id";

    // Location Table Columns names
    String KEY_TOKEN = "token";
    String KEY_NOM = "nom";
    String KEY_PRENOM = "prenom";
    String KEY_EMAIL = "email";
    String KEY_PASSWORD = "password";

    String CREATE_LOCATION_TABLE = "" +
            "CREATE TABLE " + TABLE_LOCATION + " (" +
            KEY_ID + " INTEGER PRIMARY KEY, " +
            KEY_TOKEN + " TEXT, "+
            KEY_NOM + " TEXT, "+
            KEY_PRENOM + " TEXT, "+
            KEY_EMAIL + " TEXT, "+
            KEY_PASSWORD + " TEXT)";

    String DROP_LOCATION_TABLE = "" +
            "DROP TABLE IF EXISTS " + TABLE_LOCATION;
}
