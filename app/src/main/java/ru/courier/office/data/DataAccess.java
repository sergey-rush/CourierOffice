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
import ru.courier.office.core.ApplicationStatus;
import ru.courier.office.core.Document;
import ru.courier.office.core.Note;
import ru.courier.office.core.OperationType;
import ru.courier.office.core.Scan;
import ru.courier.office.core.ScanStatus;
import ru.courier.office.core.Status;

/**
 * Created by rash on 23.08.2017.
 */

public abstract class DataAccess extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "COURIEROFFICEDB.db";
    private static final int DATABASE_VERSION = 1;
    private Context _context;
    protected SQLiteDatabase db;

    protected DataAccess(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        _context = context;
        db = getWritableDatabase();
    }

    private static DataAccess _instance = null;

    public static DataAccess getInstance(Context context) {
        if (_instance == null) {
            _instance = new DataProvider(context);
        }
        return _instance;
    }

    public abstract Application getApplicationById(int id);

    public abstract Application getApplicationByApplicationGuid(String applicationGuid);

    public abstract List<Application> getApplicationsByApplicationStatus(ApplicationStatus applicationStatus);

    public abstract List<Application> getApplications(int limit);

    public abstract int countApplications();

    public abstract int countApplicationsExceptApplicationStatus(ApplicationStatus applicationStatus);

    public abstract int insertApplication(Application application);

    public abstract boolean updateApplicationByApplicationStatus(int id, ApplicationStatus applicationStatus);

    public abstract boolean deleteApplicationById(int id);

    public abstract int addApplication(Application application);

    public abstract boolean removeApplication(int id);

    public abstract Document getDocumentById(int id);

    public abstract List<Document> getDocumentsByApplicationId(int applicationId);

    public abstract List<Document> getDocumentsByApplicationGuid(String applicationGuid);

    public abstract int countDocuments();

    public abstract int insertDocument(Document document);

    public abstract void updateDocumentCountByScan(int id, OperationType operationType);

    public abstract boolean deleteDocumentsByApplicationGuid(String applicationGuid);

    public abstract List<Scan> getScansByDocumentId(int documentId);

    public abstract int countScans();

    public abstract int countScansByApplicationId(int applicationId);

    public abstract int countScansByDocumentId(int documentId);

    public abstract int insertScan(Scan scan);

    public abstract boolean updateScanImage(Scan scan);

    public abstract boolean updateScan(Scan scan);

    public abstract boolean updateScansByScanStatus(ScanStatus scanStatus);

    public abstract boolean updateScansByApplicationGuid(String applicationGuid, ScanStatus scanStatus);

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

    /**
     * The method is called on the application start up
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE Applications(Id INTEGER PRIMARY KEY AUTOINCREMENT, ApplicationGuid TEXT, ApplicationStatus INTEGER, MerchantGuid TEXT, MerchantName TEXT, Inn TEXT, Email TEXT, Site TEXT, ManagerName TEXT, ManagerPhone TEXT, PersonGuid TEXT, PersonName TEXT, BirthDate TEXT, Gender INTEGER, Amount TEXT, DeliveryAddress TEXT, Created TEXT)");
        db.execSQL("CREATE TABLE Statuses(Id INTEGER PRIMARY KEY AUTOINCREMENT, ApplicationId INTEGER, ApplicationGuid TEXT, Code TEXT, Category TEXT, Info TEXT, Created TEXT)");
        db.execSQL("CREATE TABLE Documents(Id INTEGER PRIMARY KEY AUTOINCREMENT, DocumentGuid TEXT, ApplicationId INTEGER, ApplicationGuid TEXT, Title TEXT, Count INTEGER)");
        db.execSQL("CREATE TABLE Scans(Id INTEGER PRIMARY KEY AUTOINCREMENT, PhotoGuid TEXT, StreamGuid TEXT, ApplicationId INTEGER, ApplicationGuid TEXT, DocumentGuid TEXT, DocumentId INTEGER, PageNum INTEGER, ImageLength INTEGER, ScanStatus INTEGER, SmallPhoto BLOB, LargePhoto BLOB)");
        db.execSQL("CREATE TABLE Notes(Id INTEGER PRIMARY KEY, Info TEXT, Created TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Applications");
        db.execSQL("DROP TABLE IF EXISTS Statuses");
        db.execSQL("DROP TABLE IF EXISTS Documents");
        db.execSQL("DROP TABLE IF EXISTS Scans");
        db.execSQL("DROP TABLE IF EXISTS Notes");
        onCreate(db);
    }

    /**
     * Drops all tables in current database
     */
    public void onDrop() {
        _context.deleteDatabase(DATABASE_NAME);
        Log.d("Drop", "Database is dropped");
    }
}