package ru.courier.office.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import ru.courier.office.core.Application;
import ru.courier.office.core.Document;
import ru.courier.office.core.Note;
import ru.courier.office.core.OperationType;
import ru.courier.office.core.Scan;
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

    public abstract Application getApplicationById(int id);
    public abstract Application getApplicationByApplicationGuid(String applicationGuid);
    public abstract List<Application> getApplications(int limit);
    public abstract int countApplications();
    public abstract int insertApplication(Application application);
    public abstract boolean refreshApplication(Application application);
    public abstract boolean deleteApplicationById(int id);
    public abstract int addApplication(Application application);
    public abstract boolean removeApplication(int id);

    public abstract Document getDocumentById(int id);
    public abstract List<Document> getDocumentsByApplicationGuid(String applicationGuid);
    public abstract int countDocuments();
    public abstract int insertDocument(Document document);
    public abstract void updateDocumentCountByScan(int id, OperationType operationType);
    public abstract boolean deleteDocumentsByApplicationGuid(String applicationGuid);

    public abstract List<Scan> getScansByDocumentId(int documentId);
    public abstract int countScans();
    public abstract int countScansByDocumentId(int documentId);
    public abstract int insertScan(Scan scan);
    public abstract boolean updateScan(Scan scan);
    public abstract byte[] getScanImage(int scanId, int offset, int length);
    public abstract Scan getScanById(int scanId);

    public abstract boolean deleteScansByApplicationGuid(String applicationGuid);
    public abstract boolean deleteScansByDocumentId(int documentId);
    public abstract boolean deleteScanById(int id);

    public abstract List<Status> getStatusesByApplicationId(int applicationId);
    public abstract int countStatuses();
    public abstract int insertStatus(Status status);
    public abstract boolean deleteStatusesByApplicationId(int applicationId);

    public abstract List<Note> getNotesByLimit(int limit);
    public abstract int getNoteMaxId();
    public abstract int countNotes();
    public abstract int insertNote(Note note);
    public abstract boolean deleteNotesById(int id);
    public abstract void addNotes(List<Note> notes);

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

    private int IncrementVersion()
    {
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        Date date = new Date();
        ContentValues contentValues = new ContentValues();
        contentValues.put("Created", dateFormat.format(date));
        int ret = (int)db.insert("Versions", null, contentValues);
        return ret;
    }

    /**
     * Creates the database structure
     */
    public void onSetup() {
        Status status = getVersion();
        if(status.Id > 0) {

            db.execSQL("DROP TABLE IF EXISTS Applications");
            db.execSQL("DROP TABLE IF EXISTS Statuses");
            db.execSQL("DROP TABLE IF EXISTS Documents");
            db.execSQL("DROP TABLE IF EXISTS Scans");
            db.execSQL("DROP TABLE IF EXISTS Notes");

            db.execSQL("CREATE TABLE Applications(Id INTEGER PRIMARY KEY AUTOINCREMENT, ApplicationGuid TEXT, MerchantGuid TEXT, MerchantName TEXT, Inn TEXT, Email TEXT, Site TEXT, ManagerName TEXT, ManagerPhone TEXT, PersonGuid TEXT, PersonName TEXT, BirthDate TEXT, Gender INTEGER, Amount TEXT, DeliveryAddress TEXT, Created TEXT)");
            db.execSQL("CREATE TABLE Statuses(Id INTEGER PRIMARY KEY AUTOINCREMENT, ApplicationId INTEGER, ApplicationGuid TEXT, Code TEXT, Category TEXT, Info TEXT, Created TEXT)");
            db.execSQL("CREATE TABLE Documents(Id INTEGER PRIMARY KEY AUTOINCREMENT, DocumentGuid TEXT, ApplicationGuid TEXT, Title TEXT, Count INTEGER)");
            db.execSQL("CREATE TABLE Scans(Id INTEGER PRIMARY KEY AUTOINCREMENT, PhotoGuid TEXT, StreamGuid TEXT, ApplicationGuid TEXT, DocumentGuid TEXT, DocumentId INTEGER, PageNum INTEGER, ImageLength INTEGER, ScanStatus INTEGER, SmallPhoto BLOB, LargePhoto BLOB)");
            db.execSQL("CREATE TABLE Notes(Id INTEGER PRIMARY KEY, Info TEXT, Created TEXT)");
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
        db.execSQL("DROP TABLE IF EXISTS Documents");
        db.execSQL("DROP TABLE IF EXISTS Scans");
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
