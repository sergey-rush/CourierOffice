package ru.courier.office.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.ContactsContract;
import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import ru.courier.office.core.Application;
import ru.courier.office.core.Person;
import ru.courier.office.core.Status;

/**
 * Created by rash on 23.08.2017.
 */

public abstract class DataAccess extends SQLiteOpenHelper {

    private static final String DatabaseName = "SSDB.db";
    private static final int version = 1;
    private Context context;
    protected SQLiteDatabase db;

    public DataAccess(Context context) {
        super(context, DatabaseName, null, version);
        this.context = context;
        db = this.getWritableDatabase();
    }

    private static DataAccess _instance = null;
    public static DataAccess getInstance(Context context)
    {
        if(_instance == null)
        {
            _instance = new DataProvider(context);
        }
        return _instance;
    }

    public abstract Application getApplicationById(String applicationId);
    public abstract List<Application> getApplications(int limit);
    public abstract long countApplications();
    public abstract long insertApplication(Application application);

    public abstract Person getPersonById(String personId);
    public abstract long countPersons();
    public abstract long insertPerson(Person person);

    public abstract List<Status> getStatuses(String applicationId);
    public abstract long countStatuses();
    public abstract long insertStatus(Status status);

    public void createDatabase() {
        onCreate(db);
    }

    public Status getVersion() {
        Status status = new Status();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT MAX(Id) AS Id, Created FROM Versions", null);
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                int id = cursor.getInt(cursor.getColumnIndex("Id"));
                String datetime = cursor.getString(cursor.getColumnIndex("Created"));
                SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
                Date created = format.parse(datetime);
                status.Id = id;
                status.Created = created;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (SQLiteException ex) {
            ex.printStackTrace();
            status.Info = ex.getMessage();
        } finally {
            if(cursor!=null)
            {
                cursor.close();
            }
        }
        return status;
    }

    /**
     * Inits the database with Versions table prior to create other tables
     */
    public void onInit() {

        Status status = getVersion();
        if(status.Id == 0)
        {
            db.execSQL("CREATE TABLE Versions(Id INTEGER PRIMARY KEY AUTOINCREMENT, Created TEXT)");
            IncrementVersion();
        }
    }

    private void IncrementVersion()
    {
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        Date date = new Date();
        ContentValues contentValues = new ContentValues();
        contentValues.put("Created", dateFormat.format(date));
        long ret = db.insert("Versions", null, contentValues);
        //Status status = new Status((int)ret, date);
    }

    /**
     * Creates the database structure
     */
    public void onSetup() {
        Status status = getVersion();
        if(status.Id > 0) {
            db.execSQL("DROP TABLE IF EXISTS Applications");
            db.execSQL("DROP TABLE IF EXISTS Statuses");
            db.execSQL("DROP TABLE IF EXISTS Persons");
            Log.d("Drop", "Tables were dropped");
            db.execSQL("CREATE TABLE Applications(Id TEXT PRIMARY KEY, MerchantId TEXT, PersonId TEXT, Amount TEXT, DeliveryAddress TEXT, Created TEXT)");
            db.execSQL("CREATE TABLE Statuses(Id TEXT PRIMARY KEY, Info TEXT, Created TEXT)");
            db.execSQL("CREATE TABLE Persons(Id TEXT PRIMARY KEY, ApplicationId TEXT, FirstName TEXT, MiddleName TEXT, LastName TEXT, BirthDate TEXT, Gender INTEGER)");
            Log.d("Create", "Tables were created");

            IncrementVersion();
        }
    }


    /**
     * The method is called on the application start up
     */
    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Applications");
        db.execSQL("DROP TABLE IF EXISTS Statuses");
        db.execSQL("DROP TABLE IF EXISTS Persons");
        onCreate(db);
    }

    /**
     * Drops all tables in current database
     */
    public void onDrop() {
        Status status = getVersion();
        if(status.Id > 0) {
        context.deleteDatabase(DatabaseName);
            Log.d("Drop", "Database is deleted");
        }
    }
}
