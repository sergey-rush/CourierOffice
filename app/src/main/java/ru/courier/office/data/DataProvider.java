package ru.courier.office.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import ru.courier.office.core.Application;
import ru.courier.office.core.Document;
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
            cursor = db.rawQuery("SELECT Id, ApplicationId, MerchantId, MerchantName, Inn, Email, Site, ManagerName, ManagerPhone, PersonId, PersonName, BirthDate, Gender, Amount, DeliveryAddress, Created FROM Applications WHERE Id = ?", new String[]{String.valueOf(String.valueOf(id))});
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                application = new Application();
                application.Id = cursor.getInt(cursor.getColumnIndex("Id"));
                application.ApplicationId = cursor.getString(cursor.getColumnIndex("ApplicationId"));
                application.MerchantId = cursor.getString(cursor.getColumnIndex("MerchantId"));
                application.MerchantName = cursor.getString(cursor.getColumnIndex("MerchantName"));
                application.Inn = cursor.getString(cursor.getColumnIndex("Inn"));
                application.Email = cursor.getString(cursor.getColumnIndex("Email"));
                application.Site = cursor.getString(cursor.getColumnIndex("Site"));
                application.ManagerName = cursor.getString(cursor.getColumnIndex("ManagerName"));
                application.ManagerPhone = cursor.getString(cursor.getColumnIndex("ManagerPhone"));
                application.PersonId = cursor.getString(cursor.getColumnIndex("PersonId"));
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
    public Application getApplicationByApplicationId(String applicationId) {
        Application application = null;
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT Id, ApplicationId, MerchantId, MerchantName, Inn, Email, Site, ManagerName, ManagerPhone, PersonId, PersonName, BirthDate, Gender, Amount, DeliveryAddress, Created FROM Applications WHERE ApplicationId = ? COLLATE NOCASE", new String[]{String.valueOf(String.valueOf(applicationId))});
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                application = new Application();
                application.Id = cursor.getInt(cursor.getColumnIndex("Id"));
                application.ApplicationId = cursor.getString(cursor.getColumnIndex("ApplicationId"));
                application.MerchantId = cursor.getString(cursor.getColumnIndex("MerchantId"));
                application.MerchantName = cursor.getString(cursor.getColumnIndex("MerchantName"));
                application.Inn = cursor.getString(cursor.getColumnIndex("Inn"));
                application.Email = cursor.getString(cursor.getColumnIndex("Email"));
                application.Site = cursor.getString(cursor.getColumnIndex("Site"));
                application.ManagerName = cursor.getString(cursor.getColumnIndex("ManagerName"));
                application.ManagerPhone = cursor.getString(cursor.getColumnIndex("ManagerPhone"));
                application.PersonId = cursor.getString(cursor.getColumnIndex("PersonId"));
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
    public List<Application> getApplications(int limit) {
        List<Application> applications = new ArrayList<Application>();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT Id, ApplicationId, MerchantId, MerchantName, Inn, Email, Site, ManagerName, ManagerPhone, PersonId, PersonName, BirthDate, Gender, Amount, DeliveryAddress, Created FROM Applications ORDER BY DATETIME(Created) DESC Limit ?", new String[]{String.valueOf(String.valueOf(limit))});           cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Application application = new Application();
                application.Id = cursor.getInt(cursor.getColumnIndex("Id"));
                application.ApplicationId = cursor.getString(cursor.getColumnIndex("ApplicationId"));
                application.MerchantId = cursor.getString(cursor.getColumnIndex("MerchantId"));
                application.MerchantName = cursor.getString(cursor.getColumnIndex("MerchantName"));
                application.Inn = cursor.getString(cursor.getColumnIndex("Inn"));
                application.Email = cursor.getString(cursor.getColumnIndex("Email"));
                application.Site = cursor.getString(cursor.getColumnIndex("Site"));
                application.ManagerName = cursor.getString(cursor.getColumnIndex("ManagerName"));
                application.ManagerPhone = cursor.getString(cursor.getColumnIndex("ManagerPhone"));
                application.PersonId = cursor.getString(cursor.getColumnIndex("PersonId"));
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
        contentValues.put("ApplicationId", application.ApplicationId);
        contentValues.put("MerchantId", application.MerchantId);
        contentValues.put("MerchantName", application.MerchantName);
        contentValues.put("Inn", application.Inn);
        contentValues.put("Email", application.Email);
        contentValues.put("Site", application.Site);
        contentValues.put("ManagerName", application.ManagerName);
        contentValues.put("ManagerPhone", application.ManagerPhone);
        contentValues.put("PersonId", application.PersonId);
        contentValues.put("PersonName", application.PersonName);
        contentValues.put("BirthDate", dateFormat.format(application.BirthDate));
        contentValues.put("Gender", application.Gender);
        contentValues.put("Amount", application.Amount);
        contentValues.put("DeliveryAddress", application.DeliveryAddress);
        contentValues.put("Created", dateFormat.format(application.Created));        
        int ret = (int)db.insert("Applications", null, contentValues);
        return ret;
    }

    @Override
    public boolean refreshApplication(Application application) {

        //ApplicationManager applicationManager = new ApplicationManager(getContext(), this, qrCodeValue);
        //final AsyncTask<Void, Void, Void> execute = applicationManager.execute();

        return true;
    }

    @Override
    public boolean deleteApplicationById(int id) {
        int ret = db.delete("Applications", "Id = ?", new String[]{String.valueOf(id)});
        return ret == 1;
    }

    @Override
    public int addApplication(Application application) {
        int appId = insertApplication(application);
        for (ru.courier.office.core.Document document : application.DocumentList) {
            insertDocument(document);
        }

        for (ru.courier.office.core.Status status : application.StatusList) {
            insertStatus(status);
        }
        return appId;
    }

    @Override
    public boolean removeApplication(int id) {

        Application application = getApplicationById(id);
        deleteScansByApplicationId(application.ApplicationId);
        deleteDocumentsByApplicationId(application.ApplicationId);
        deleteStatusesByApplicationId(application.ApplicationId);
        deleteApplicationById(id);
        return true;
    }

//    public int Id;
//    public String DocumentId;
//    public String ApplicationId;
//    public String Title;
//    public List<Scan> ScanList;
    @Override
    public List<Document> getDocumentsByApplicationId(String applicationId) {
        List<Document> documents = null;
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT Id, DocumentId, ApplicationId, Title, Count FROM Documents WHERE ApplicationId = ? COLLATE NOCASE ORDER BY Id", new String[]{String.valueOf(applicationId)});
            cursor.moveToFirst();
            documents = new ArrayList<Document>();
            while (!cursor.isAfterLast()) {
                Document document = new Document();
                document.Id = cursor.getInt(cursor.getColumnIndex("Id"));
                document.DocumentId = cursor.getString(cursor.getColumnIndex("DocumentId"));
                document.ApplicationId = cursor.getString(cursor.getColumnIndex("ApplicationId"));
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
        contentValues.put("DocumentId", document.DocumentId);
        contentValues.put("ApplicationId", document.ApplicationId);
        contentValues.put("Title", document.Title);
        contentValues.put("Count", document.Count);
        int ret = (int)db.insert("Documents", null, contentValues);
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
    public boolean deleteDocumentsByApplicationId(String applicationId) {
        int ret = db.delete("Documents", "ApplicationId = ?", new String[]{String.valueOf(applicationId)});
        return ret == 1;
    }

//    public int Id;
//    public int DocumentId;
//    public int Page;
//    public byte[] Photo;

    @Override
    public List<Scan> getScansByDocumentId(int documentId) {
        List<Scan> scans = null;
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT Id, ApplicationId, DocumentId, Page, ScanStatus, SmallPhoto FROM Scans WHERE DocumentId = ? ORDER BY Page", new String[]{String.valueOf(documentId)});
            cursor.moveToFirst();
            scans = new ArrayList<Scan>();
            while (!cursor.isAfterLast()) {
                Scan scan = new Scan();
                scan.Id = cursor.getInt(cursor.getColumnIndex("Id"));
                scan.ApplicationId = cursor.getString(cursor.getColumnIndex("ApplicationId"));
                scan.DocumentId = cursor.getInt(cursor.getColumnIndex("DocumentId"));
                scan.Page = cursor.getInt(cursor.getColumnIndex("Page"));
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
    public int countScansByDocumentId(int documentId){
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
        contentValues.put("ApplicationId", scan.ApplicationId);
        contentValues.put("DocumentId", scan.DocumentId);
        contentValues.put("Page", scan.Page);
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
    public boolean deleteScansByApplicationId(String applicationId) {
        int ret = db.delete("Scans", "ApplicationId = ?", new String[]{String.valueOf(applicationId)});
        return ret == 1;
    }

    @Override
    public boolean deleteScansByDocumentId(int documentId) {
        int ret = db.delete("Scans", "DocumentId = ?", new String[]{String.valueOf(documentId)});
        return ret == 1;
    }

    @Override
    public boolean deleteScanById(int id) {
        int ret = db.delete("Scans", "Id = ?", new String[]{String.valueOf(id)});
        return ret == 1;
    }

    @Override
    public List<Status> getStatuses(String applicationId) {
        List<Status> statuses = null;
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT Id, ApplicationId, Code, Category, Info, Created FROM Statuses WHERE ApplicationId = ? COLLATE NOCASE ORDER BY Created DESC", new String[]{applicationId + ""});
            cursor.moveToFirst();
            statuses = new ArrayList<Status>();
            while (!cursor.isAfterLast()) {
                Status status = new Status();
                status.Id = cursor.getInt(cursor.getColumnIndex("Id"));
                status.ApplicationId = cursor.getString(cursor.getColumnIndex("ApplicationId"));
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
        contentValues.put("Code", status.Code);
        contentValues.put("Category", status.Category);
        contentValues.put("Info", status.Info);
        contentValues.put("Created", dateFormat.format(status.Created));
        int ret = (int)db.insert("Statuses", null, contentValues);
        return ret;
    }

    @Override
    public boolean deleteStatusesByApplicationId(String applicationId) {
        int ret = db.delete("Statuses", "ApplicationId = ?", new String[]{String.valueOf(applicationId)});
        return ret == 1;
    }
}
