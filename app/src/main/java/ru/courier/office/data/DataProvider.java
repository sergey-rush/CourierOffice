package ru.courier.office.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
public class DataProvider extends DataAccess {

    public DataProvider(Context context) {
        super(context);
    }

    @Override
    public Application getApplicationById(int id) {
        Application application = null;
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT Id, ApplicationGuid, ApplicationStatus, MerchantGuid, MerchantName, Inn, Email, Site, ManagerName, ManagerPhone, PersonGuid, PersonName, BirthDate, Gender, Amount, DeliveryAddress, Created FROM Applications WHERE Id = ?", new String[]{String.valueOf(String.valueOf(id))});
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                application = new Application();
                application.Id = cursor.getInt(cursor.getColumnIndex("Id"));
                application.ApplicationGuid = cursor.getString(cursor.getColumnIndex("ApplicationGuid"));
                application.ApplicationStatus = ApplicationStatus.fromInt(cursor.getInt(cursor.getColumnIndex("ApplicationStatus")));
                application.MerchantGuid = cursor.getString(cursor.getColumnIndex("MerchantGuid"));
                application.MerchantName = cursor.getString(cursor.getColumnIndex("MerchantName"));
                application.Inn = cursor.getString(cursor.getColumnIndex("Inn"));
                application.Email = cursor.getString(cursor.getColumnIndex("Email"));
                application.Site = cursor.getString(cursor.getColumnIndex("Site"));
                application.ManagerName = cursor.getString(cursor.getColumnIndex("ManagerName"));
                application.ManagerPhone = cursor.getString(cursor.getColumnIndex("ManagerPhone"));
                application.PersonGuid = cursor.getString(cursor.getColumnIndex("PersonGuid"));
                application.PersonName = cursor.getString(cursor.getColumnIndex("PersonName"));
                String birthDate = cursor.getString(cursor.getColumnIndex("BirthDate"));
                application.Gender = cursor.getInt(cursor.getColumnIndex("Gender"));
                application.Amount = cursor.getString(cursor.getColumnIndex("Amount"));
                application.DeliveryAddress = cursor.getString(cursor.getColumnIndex("DeliveryAddress"));
                String created = cursor.getString(cursor.getColumnIndex("Created"));
                SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
                application.Created = format.parse(created);
                application.BirthDate = format.parse(birthDate);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (SQLiteException ex) {
            ex.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return application;
    }

    @Override
    public Application getApplicationByApplicationGuid(String applicationGuid) {
        Application application = null;
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT Id, ApplicationGuid, ApplicationStatus, MerchantGuid, MerchantName, Inn, Email, Site, ManagerName, ManagerPhone, PersonGuid, PersonName, BirthDate, Gender, Amount, DeliveryAddress, Created FROM Applications WHERE ApplicationGuid = ? COLLATE NOCASE", new String[]{applicationGuid});
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                application = new Application();
                application.Id = cursor.getInt(cursor.getColumnIndex("Id"));
                application.ApplicationGuid = cursor.getString(cursor.getColumnIndex("ApplicationGuid"));
                application.ApplicationStatus = ApplicationStatus.fromInt(cursor.getInt(cursor.getColumnIndex("ApplicationStatus")));
                application.MerchantGuid = cursor.getString(cursor.getColumnIndex("MerchantGuid"));
                application.MerchantName = cursor.getString(cursor.getColumnIndex("MerchantName"));
                application.Inn = cursor.getString(cursor.getColumnIndex("Inn"));
                application.Email = cursor.getString(cursor.getColumnIndex("Email"));
                application.Site = cursor.getString(cursor.getColumnIndex("Site"));
                application.ManagerName = cursor.getString(cursor.getColumnIndex("ManagerName"));
                application.ManagerPhone = cursor.getString(cursor.getColumnIndex("ManagerPhone"));
                application.PersonGuid = cursor.getString(cursor.getColumnIndex("PersonGuid"));
                application.PersonName = cursor.getString(cursor.getColumnIndex("PersonName"));
                String birthDate = cursor.getString(cursor.getColumnIndex("BirthDate"));
                application.Gender = cursor.getInt(cursor.getColumnIndex("Gender"));
                application.Amount = cursor.getString(cursor.getColumnIndex("Amount"));
                application.DeliveryAddress = cursor.getString(cursor.getColumnIndex("DeliveryAddress"));
                String created = cursor.getString(cursor.getColumnIndex("Created"));
                SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
                application.Created = format.parse(created);
                application.BirthDate = format.parse(birthDate);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (SQLiteException ex) {
            ex.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return application;
    }

    @Override
    public List<Application> getApplicationsByApplicationStatus(ApplicationStatus applicationStatus) {
        List<Application> applications = new ArrayList<Application>();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT Id, ApplicationGuid, ApplicationStatus, MerchantGuid, MerchantName, Inn, Email, Site, ManagerName, ManagerPhone, PersonGuid, PersonName, BirthDate, Gender, Amount, DeliveryAddress, Created FROM Applications WHERE ApplicationStatus = ?", new String[]{String.valueOf(String.valueOf(applicationStatus.ordinal()))});
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Application application = new Application();
                application.Id = cursor.getInt(cursor.getColumnIndex("Id"));
                application.ApplicationGuid = cursor.getString(cursor.getColumnIndex("ApplicationGuid"));
                application.ApplicationStatus = ApplicationStatus.fromInt(cursor.getInt(cursor.getColumnIndex("ApplicationStatus")));
                application.MerchantGuid = cursor.getString(cursor.getColumnIndex("MerchantGuid"));
                application.MerchantName = cursor.getString(cursor.getColumnIndex("MerchantName"));
                application.Inn = cursor.getString(cursor.getColumnIndex("Inn"));
                application.Email = cursor.getString(cursor.getColumnIndex("Email"));
                application.Site = cursor.getString(cursor.getColumnIndex("Site"));
                application.ManagerName = cursor.getString(cursor.getColumnIndex("ManagerName"));
                application.ManagerPhone = cursor.getString(cursor.getColumnIndex("ManagerPhone"));
                application.PersonGuid = cursor.getString(cursor.getColumnIndex("PersonGuid"));
                application.PersonName = cursor.getString(cursor.getColumnIndex("PersonName"));
                String birthDate = cursor.getString(cursor.getColumnIndex("BirthDate"));
                application.Gender = cursor.getInt(cursor.getColumnIndex("Gender"));
                application.Amount = cursor.getString(cursor.getColumnIndex("Amount"));
                application.DeliveryAddress = cursor.getString(cursor.getColumnIndex("DeliveryAddress"));
                String created = cursor.getString(cursor.getColumnIndex("Created"));
                SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
                application.Created = format.parse(created);
                application.BirthDate = format.parse(birthDate);
                applications.add(application);
                cursor.moveToNext();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (SQLiteException ex) {
            ex.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return applications;
    }

    @Override
    public List<Application> getApplications(int limit) {
        List<Application> applications = new ArrayList<Application>();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT Id, ApplicationGuid, ApplicationStatus, MerchantGuid, MerchantName, Inn, Email, Site, ManagerName, ManagerPhone, PersonGuid, PersonName, BirthDate, Gender, Amount, DeliveryAddress, Created FROM Applications ORDER BY Created DESC Limit ?", new String[]{String.valueOf(String.valueOf(limit))});
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Application application = new Application();
                application.Id = cursor.getInt(cursor.getColumnIndex("Id"));
                application.ApplicationGuid = cursor.getString(cursor.getColumnIndex("ApplicationGuid"));
                application.ApplicationStatus = ApplicationStatus.fromInt(cursor.getInt(cursor.getColumnIndex("ApplicationStatus")));
                application.MerchantGuid = cursor.getString(cursor.getColumnIndex("MerchantGuid"));
                application.MerchantName = cursor.getString(cursor.getColumnIndex("MerchantName"));
                application.Inn = cursor.getString(cursor.getColumnIndex("Inn"));
                application.Email = cursor.getString(cursor.getColumnIndex("Email"));
                application.Site = cursor.getString(cursor.getColumnIndex("Site"));
                application.ManagerName = cursor.getString(cursor.getColumnIndex("ManagerName"));
                application.ManagerPhone = cursor.getString(cursor.getColumnIndex("ManagerPhone"));
                application.PersonGuid = cursor.getString(cursor.getColumnIndex("PersonGuid"));
                application.PersonName = cursor.getString(cursor.getColumnIndex("PersonName"));
                String birthDate = cursor.getString(cursor.getColumnIndex("BirthDate"));
                application.Gender = cursor.getInt(cursor.getColumnIndex("Gender"));
                application.Amount = cursor.getString(cursor.getColumnIndex("Amount"));
                application.DeliveryAddress = cursor.getString(cursor.getColumnIndex("DeliveryAddress"));
                String created = cursor.getString(cursor.getColumnIndex("Created"));
                SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
                application.Created = format.parse(created);
                application.BirthDate = format.parse(birthDate);
                applications.add(application);
                cursor.moveToNext();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (SQLiteException ex) {
            ex.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return applications;
    }

    @Override
    public int countApplications() {
        int count = 0;
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT COUNT(*) AS Total FROM Applications", null);
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                count = cursor.getInt(cursor.getColumnIndex("Total"));
            }
        } catch (SQLiteException ex) {
            ex.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return count;
    }

    @Override
    public int insertApplication(Application application) {
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        ContentValues contentValues = new ContentValues();
        contentValues.put("ApplicationGuid", application.ApplicationGuid);
        contentValues.put("ApplicationStatus", application.ApplicationStatus.ordinal());
        contentValues.put("MerchantGuid", application.MerchantGuid);
        contentValues.put("MerchantName", application.MerchantName);
        contentValues.put("Inn", application.Inn);
        contentValues.put("Email", application.Email);
        contentValues.put("Site", application.Site);
        contentValues.put("ManagerName", application.ManagerName);
        contentValues.put("ManagerPhone", application.ManagerPhone);
        contentValues.put("PersonGuid", application.PersonGuid);
        contentValues.put("PersonName", application.PersonName);
        contentValues.put("BirthDate", dateFormat.format(application.BirthDate));
        contentValues.put("Gender", application.Gender);
        contentValues.put("Amount", application.Amount);
        contentValues.put("DeliveryAddress", application.DeliveryAddress);
        contentValues.put("Created", dateFormat.format(application.Created));
        int ret = (int) db.insert("Applications", null, contentValues);
        return ret;
    }

    @Override
    public boolean updateApplicationByApplicationStatus(int id, ApplicationStatus applicationStatus) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("ApplicationStatus", applicationStatus.ordinal());
        int ret = (int) db.update("Applications", contentValues, "Id = ?", new String[]{String.valueOf(id)});
        return ret > 0;
    }

    @Override
    public boolean deleteApplicationById(int id) {
        int ret = db.delete("Applications", "Id = ?", new String[]{String.valueOf(id)});
        return ret == 1;
    }

    @Override
    public int addApplication(Application application) {
        int applicationId = insertApplication(application);
        for (ru.courier.office.core.Document document : application.DocumentList) {
            document.ApplicationId = applicationId;
            insertDocument(document);
        }

        for (ru.courier.office.core.Status status : application.StatusList) {
            status.ApplicationId = applicationId;
            insertStatus(status);
        }
        return applicationId;
    }

    @Override
    public boolean removeApplication(int id) {

        Application application = getApplicationById(id);
        deleteScansByApplicationGuid(application.ApplicationGuid);
        deleteDocumentsByApplicationGuid(application.ApplicationGuid);
        deleteStatusesByApplicationId(application.Id);
        deleteScansByApplicationGuid(application.ApplicationGuid);
        deleteApplicationById(id);
        return true;
    }

    @Override
    public Document getDocumentById(int id) {
        Document document = null;
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT Id, DocumentGuid, ApplicationId, ApplicationGuid, Title, Count FROM Documents FROM Documents WHERE Id = ?", new String[]{String.valueOf(id)});
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                document = new Document();
                document.Id = cursor.getInt(cursor.getColumnIndex("Id"));
                document.DocumentGuid = cursor.getString(cursor.getColumnIndex("DocumentGuid"));
                document.ApplicationId = cursor.getInt(cursor.getColumnIndex("ApplicationId"));
                document.ApplicationGuid = cursor.getString(cursor.getColumnIndex("ApplicationGuid"));
                document.Title = cursor.getString(cursor.getColumnIndex("Title"));
                document.Count = cursor.getInt(cursor.getColumnIndex("Count"));
            }
        } catch (SQLiteException ex) {
            ex.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return document;
    }

    @Override
    public List<Document> getDocumentsByApplicationId(int applicationId) {
        List<Document> documents = null;
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT Id, DocumentGuid, ApplicationId, ApplicationGuid, Title, Count FROM Documents WHERE ApplicationId = ? ORDER BY Id", new String[]{String.valueOf(applicationId)});
            cursor.moveToFirst();
            documents = new ArrayList<Document>();
            while (!cursor.isAfterLast()) {
                Document document = new Document();
                document.Id = cursor.getInt(cursor.getColumnIndex("Id"));
                document.DocumentGuid = cursor.getString(cursor.getColumnIndex("DocumentGuid"));
                document.ApplicationId = cursor.getInt(cursor.getColumnIndex("ApplicationId"));
                document.ApplicationGuid = cursor.getString(cursor.getColumnIndex("ApplicationGuid"));
                document.Title = cursor.getString(cursor.getColumnIndex("Title"));
                document.Count = cursor.getInt(cursor.getColumnIndex("Count"));
                documents.add(document);
                cursor.moveToNext();
            }
        } catch (SQLiteException ex) {
            ex.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return documents;
    }

    @Override
    public List<Document> getDocumentsByApplicationGuid(String applicationGuid) {
        List<Document> documents = null;
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT Id, DocumentGuid, ApplicationId, ApplicationGuid, Title, Count FROM Documents WHERE ApplicationGuid = ? COLLATE NOCASE ORDER BY Id", new String[]{applicationGuid});
            cursor.moveToFirst();
            documents = new ArrayList<Document>();
            while (!cursor.isAfterLast()) {
                Document document = new Document();
                document.Id = cursor.getInt(cursor.getColumnIndex("Id"));
                document.DocumentGuid = cursor.getString(cursor.getColumnIndex("DocumentGuid"));
                document.ApplicationId = cursor.getInt(cursor.getColumnIndex("ApplicationId"));
                document.ApplicationGuid = cursor.getString(cursor.getColumnIndex("ApplicationGuid"));
                document.Title = cursor.getString(cursor.getColumnIndex("Title"));
                document.Count = cursor.getInt(cursor.getColumnIndex("Count"));
                documents.add(document);
                cursor.moveToNext();
            }
        } catch (SQLiteException ex) {
            ex.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return documents;
    }

    @Override
    public int countDocuments() {
        int count = 0;
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT COUNT(*) AS Total FROM Documents", null);
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                count = cursor.getInt(cursor.getColumnIndex("Total"));
            }
        } catch (SQLiteException ex) {
            ex.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return count;
    }

    @Override
    public int insertDocument(Document document) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("DocumentGuid", document.DocumentGuid);
        contentValues.put("ApplicationId", document.ApplicationId);
        contentValues.put("ApplicationGuid", document.ApplicationGuid);
        contentValues.put("Title", document.Title);
        contentValues.put("Count", document.Count);
        int ret = (int) db.insert("Documents", null, contentValues);
        return ret;
    }

    @Override
    public void updateDocumentCountByScan(int id, OperationType operationType) {
        try {
            if (operationType == OperationType.Increment) {
                db.execSQL("UPDATE Documents SET Count = Count + 1 WHERE Id = ?", new String[]{String.valueOf(id)});
            } else {
                db.execSQL("UPDATE Documents SET Count = Count - 1 WHERE Id = ?", new String[]{String.valueOf(id)});
            }
        } catch (SQLiteException ex) {
            ex.printStackTrace();
        } finally {

        }
    }

    @Override
    public boolean deleteDocumentsByApplicationGuid(String applicationGuid) {
        int ret = db.delete("Documents", "ApplicationGuid = ?", new String[]{String.valueOf(applicationGuid)});
        return ret == 1;
    }

    @Override
    public byte[] getScanImage(int scanId, int offset, int length) {
        byte[] buffer = null;
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT substr(LargePhoto, ?, ?) AS ImageBytes FROM Scans WHERE Id = ?", new String[]{String.valueOf(offset), String.valueOf(length), String.valueOf(scanId)});
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                buffer = cursor.getBlob(cursor.getColumnIndex("ImageBytes"));
            }
        } catch (SQLiteException ex) {
            ex.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return buffer;
    }

    @Override
    public Scan getScanById(int scanId) {
        Scan scan = null;
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT Id, PhotoGuid, StreamGuid, ApplicationGuid, DocumentGuid, DocumentId, PageNum, ImageLength, ScanStatus, SmallPhoto FROM Scans WHERE Id = ?", new String[]{String.valueOf(scanId)});
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                scan = new Scan();
                scan.Id = cursor.getInt(cursor.getColumnIndex("Id"));
                scan.PhotoGuid = cursor.getString(cursor.getColumnIndex("PhotoGuid"));
                scan.StreamGuid = cursor.getString(cursor.getColumnIndex("StreamGuid"));
                scan.ApplicationGuid = cursor.getString(cursor.getColumnIndex("ApplicationGuid"));
                scan.DocumentGuid = cursor.getString(cursor.getColumnIndex("DocumentGuid"));
                scan.DocumentId = cursor.getInt(cursor.getColumnIndex("DocumentId"));
                scan.PageNum = cursor.getInt(cursor.getColumnIndex("PageNum"));
                scan.ImageLength = cursor.getInt(cursor.getColumnIndex("ImageLength"));
                scan.ScanStatus = ScanStatus.fromInt(cursor.getInt(cursor.getColumnIndex("ScanStatus")));
                scan.SmallPhoto = cursor.getBlob(cursor.getColumnIndex("SmallPhoto"));
            }
        } catch (SQLiteException ex) {
            ex.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return scan;
    }

    @Override
    public List<Scan> getScansByDocumentId(int documentId) {
        List<Scan> scans = null;
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT Id, PhotoGuid, StreamGuid, ApplicationGuid, DocumentGuid, DocumentId, PageNum, ImageLength, ScanStatus, SmallPhoto FROM Scans WHERE DocumentId = ? ORDER BY PageNum", new String[]{String.valueOf(documentId)});
            cursor.moveToFirst();
            scans = new ArrayList<Scan>();
            while (!cursor.isAfterLast()) {
                Scan scan = new Scan();
                scan.Id = cursor.getInt(cursor.getColumnIndex("Id"));
                scan.PhotoGuid = cursor.getString(cursor.getColumnIndex("PhotoGuid"));
                scan.StreamGuid = cursor.getString(cursor.getColumnIndex("StreamGuid"));
                scan.ApplicationGuid = cursor.getString(cursor.getColumnIndex("ApplicationGuid"));
                scan.DocumentGuid = cursor.getString(cursor.getColumnIndex("DocumentGuid"));
                scan.DocumentId = cursor.getInt(cursor.getColumnIndex("DocumentId"));
                scan.PageNum = cursor.getInt(cursor.getColumnIndex("PageNum"));
                scan.ImageLength = cursor.getInt(cursor.getColumnIndex("ImageLength"));
                scan.ScanStatus = ScanStatus.fromInt(cursor.getInt(cursor.getColumnIndex("ScanStatus")));
                scan.SmallPhoto = cursor.getBlob(cursor.getColumnIndex("SmallPhoto"));
                scans.add(scan);
                cursor.moveToNext();
            }
        } catch (SQLiteException ex) {
            ex.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return scans;
    }

    @Override
    public int countScans() {
        int count = 0;
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT COUNT(*) AS Total FROM Scans", null);
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                count = cursor.getInt(cursor.getColumnIndex("Total"));
            }
        } catch (SQLiteException ex) {
            ex.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return count;
    }

    @Override
    public int countScansByDocumentId(int documentId) {
        int count = 0;
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT COUNT(*) AS Total FROM Scans WHERE DocumentId = ?", new String[]{String.valueOf(documentId)});
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                count = cursor.getInt(cursor.getColumnIndex("Total"));
            }
        } catch (SQLiteException ex) {
            ex.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return count;
    }

    @Override
    public int insertScan(Scan scan) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("ApplicationGuid", scan.ApplicationGuid);
        contentValues.put("DocumentGuid", scan.DocumentGuid);
        contentValues.put("DocumentId", scan.DocumentId);
        contentValues.put("PageNum", scan.PageNum);
        contentValues.put("ImageLength", scan.ImageLength);
        contentValues.put("ScanStatus", scan.ScanStatus.ordinal());
        contentValues.put("SmallPhoto", scan.SmallPhoto);
        contentValues.put("LargePhoto", scan.LargePhoto);
        int ret = (int) db.insert("Scans", null, contentValues);

        if (ret > 0) {
            updateDocumentCountByScan(scan.DocumentId, OperationType.Increment);
        }

        return ret;
    }

    @Override
    public boolean updateScan(Scan scan) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("PhotoGuid", scan.PhotoGuid);
        contentValues.put("StreamGuid", scan.StreamGuid);
        contentValues.put("ScanStatus", scan.ScanStatus.ordinal());
        int ret = (int) db.update("Scans", contentValues, "Id = ?", new String[]{String.valueOf(scan.Id)});
        return ret == 1;
    }

    @Override
    public boolean updateScansByScanStatus(ScanStatus scanStatus) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("ScanStatus", scanStatus.ordinal());
        int ret = (int) db.update("Scans", contentValues, "Id > ? AND ScanStatus != ?", new String[]{String.valueOf(0), String.valueOf(scanStatus.ordinal())});
        return ret > 0;
    }

    @Override
    public boolean updateScansByApplicationGuid(String applicationGuid, ScanStatus scanStatus) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("ScanStatus", scanStatus.ordinal());
        int ret = (int) db.update("Scans", contentValues, "ApplicationGuid = ? AND ScanStatus != ?", new String[]{applicationGuid, String.valueOf(scanStatus.ordinal())});
        return ret > 1;
    }

    @Override
    public boolean deleteScansByApplicationGuid(String applicationGuid) {
        int ret = db.delete("Scans", "ApplicationGuid = ?", new String[]{String.valueOf(applicationGuid)});
        return ret == 1;
    }

    @Override
    public boolean deleteScansByDocumentId(int documentId) {
        int ret = db.delete("Scans", "DocumentId = ?", new String[]{String.valueOf(documentId)});
        return ret == 1;
    }

    @Override
    public boolean deleteScanById(int id) {
        Scan scan = getScanById(id);
        int ret = db.delete("Scans", "Id = ?", new String[]{String.valueOf(id)});
        if (ret > 0) {
            updateDocumentCountByScan(scan.DocumentId, OperationType.Decrement);
        }
        return ret == 1;
    }

    @Override
    public List<Status> getStatusesByApplicationId(int applicationId) {
        List<Status> statuses = null;
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT Id, ApplicationId, ApplicationGuid, Code, Category, Info, Created FROM Statuses WHERE ApplicationId = ? ORDER BY Created DESC", new String[]{String.valueOf(applicationId)});
            cursor.moveToFirst();
            statuses = new ArrayList<Status>();
            while (!cursor.isAfterLast()) {
                Status status = new Status();
                status.Id = cursor.getInt(cursor.getColumnIndex("Id"));
                status.ApplicationId = cursor.getInt(cursor.getColumnIndex("ApplicationId"));
                status.ApplicationGuid = cursor.getString(cursor.getColumnIndex("ApplicationGuid"));
                status.Code = cursor.getString(cursor.getColumnIndex("Code"));
                status.Category = cursor.getString(cursor.getColumnIndex("Category"));
                status.Info = cursor.getString(cursor.getColumnIndex("Info"));
                String datetime = cursor.getString(cursor.getColumnIndex("Created"));
                SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
                status.Created = format.parse(datetime);
                statuses.add(status);
                cursor.moveToNext();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (SQLiteException ex) {
            ex.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return statuses;
    }

    @Override
    public int countStatuses() {
        int count = 0;
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT COUNT(*) AS Total FROM Statuses", null);
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                count = cursor.getInt(cursor.getColumnIndex("Total"));
            }
        } catch (SQLiteException ex) {
            ex.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return count;
    }

    @Override
    public int insertStatus(Status status) {
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        ContentValues contentValues = new ContentValues();
        contentValues.put("ApplicationId", status.ApplicationId);
        contentValues.put("ApplicationGuid", status.ApplicationGuid);
        contentValues.put("Code", status.Code);
        contentValues.put("Category", status.Category);
        contentValues.put("Info", status.Info);
        contentValues.put("Created", dateFormat.format(status.Created));
        int ret = (int) db.insert("Statuses", null, contentValues);
        return ret;
    }

    @Override
    public boolean deleteStatusesByApplicationId(int applicationId) {
        int ret = db.delete("Statuses", "ApplicationId = ?", new String[]{String.valueOf(applicationId)});
        return ret == 1;
    }

    @Override
    public List<Note> getNotesByLimit(int limit) {
        List<Note> notes = null;
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT Id, Info, Created FROM Notes ORDER BY Created DESC Limit ?", new String[]{String.valueOf(limit)});
            cursor.moveToFirst();
            notes = new ArrayList<Note>();
            while (!cursor.isAfterLast()) {
                Note note = new Note();
                note.Id = cursor.getInt(cursor.getColumnIndex("Id"));
                note.Info = cursor.getString(cursor.getColumnIndex("Info"));
                String datetime = cursor.getString(cursor.getColumnIndex("Created"));
                SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
                note.Created = format.parse(datetime);
                notes.add(note);
                cursor.moveToNext();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (SQLiteException ex) {
            ex.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return notes;
    }

    @Override
    public int getNoteMaxId() {
        int count = 0;
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT MAX(Id) AS MaxId FROM Notes", null);
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                count = cursor.getInt(cursor.getColumnIndex("MaxId"));
            }
        } catch (SQLiteException ex) {
            ex.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return count;
    }

    @Override
    public int countNotes() {
        int count = 0;
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT COUNT(*) AS Total FROM Notes", null);
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                count = cursor.getInt(cursor.getColumnIndex("Total"));
            }
        } catch (SQLiteException ex) {
            ex.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return count;
    }

    @Override
    public int insertNote(Note note) {
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        ContentValues contentValues = new ContentValues();
        contentValues.put("Id", note.Id);
        contentValues.put("Info", note.Info);
        contentValues.put("Created", dateFormat.format(note.Created));
        int ret = (int) db.insert("Notes", null, contentValues);
        return ret;
    }

    @Override
    public boolean deleteNotesById(int id) {
        int ret = db.delete("Notes", "Id < ?", new String[]{String.valueOf(id)});
        return ret == 1;
    }

    @Override
    public void addNotes(List<Note> notes) {
        for (ru.courier.office.core.Note note : notes) {
            insertNote(note);
        }
        int countNotes = countNotes();
        if (countNotes > 100) {
            int maxId = getNoteMaxId();
            deleteNotesById(maxId - 100);
        }
    }
}
