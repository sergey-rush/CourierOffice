package ru.courier.office.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ru.courier.office.core.Application;
import ru.courier.office.core.Merchant;
import ru.courier.office.core.Person;
import ru.courier.office.core.Status;
import ru.courier.office.web.ApplicationManager;

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
            cursor = db.rawQuery("SELECT Id, ApplicationId, MerchantId, PersonId, PersonName, MerchantName, Amount, DeliveryAddress, Created FROM Applications WHERE Id = ?", new String[]{String.valueOf(String.valueOf(id))});
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                application = new Application();
                application.Id = cursor.getInt(cursor.getColumnIndex("Id"));
                application.ApplicationId = cursor.getString(cursor.getColumnIndex("ApplicationId"));
                application.MerchantId = cursor.getInt(cursor.getColumnIndex("MerchantId"));
                application.PersonId = cursor.getInt(cursor.getColumnIndex("PersonId"));
                application.PersonName = cursor.getString(cursor.getColumnIndex("PersonName"));
                application.MerchantName = cursor.getString(cursor.getColumnIndex("MerchantName"));
                application.Amount = cursor.getString(cursor.getColumnIndex("Amount"));
                application.DeliveryAddress = cursor.getString(cursor.getColumnIndex("DeliveryAddress"));
                String datetime = cursor.getString(cursor.getColumnIndex("Created"));
                SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
                application.Created = format.parse(datetime);
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
            cursor = db.rawQuery("SELECT Id, ApplicationId, MerchantId, PersonId, PersonName, MerchantName, Amount, DeliveryAddress, Created FROM Applications WHERE ApplicationId = ? COLLATE NOCASE", new String[]{String.valueOf(applicationId)});
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                application = new Application();
                application.Id = cursor.getInt(cursor.getColumnIndex("Id"));
                application.ApplicationId = cursor.getString(cursor.getColumnIndex("ApplicationId"));
                application.MerchantId = cursor.getInt(cursor.getColumnIndex("MerchantId"));
                application.PersonId = cursor.getInt(cursor.getColumnIndex("PersonId"));
                application.PersonName = cursor.getString(cursor.getColumnIndex("PersonName"));
                application.MerchantName = cursor.getString(cursor.getColumnIndex("MerchantName"));
                application.Amount = cursor.getString(cursor.getColumnIndex("Amount"));
                application.DeliveryAddress = cursor.getString(cursor.getColumnIndex("DeliveryAddress"));
                String datetime = cursor.getString(cursor.getColumnIndex("Created"));
                SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
                application.Created = format.parse(datetime);
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
            cursor = db.rawQuery("SELECT Id, ApplicationId, MerchantId, PersonId, PersonName, MerchantName, Amount, DeliveryAddress, Created FROM Applications ORDER BY DATETIME(Created) DESC Limit ?;", new String[]{ String.valueOf(limit) });
            cursor.moveToFirst();

            while (!cursor.isAfterLast()) {
                Application application = new Application();
                application.Id = cursor.getInt(cursor.getColumnIndex("Id"));
                application.ApplicationId = cursor.getString(cursor.getColumnIndex("ApplicationId"));
                application.MerchantId = cursor.getInt(cursor.getColumnIndex("MerchantId"));
                application.PersonId = cursor.getInt(cursor.getColumnIndex("PersonId"));
                application.PersonName = cursor.getString(cursor.getColumnIndex("PersonName"));
                application.MerchantName = cursor.getString(cursor.getColumnIndex("MerchantName"));
                application.Amount = cursor.getString(cursor.getColumnIndex("Amount"));
                application.DeliveryAddress = cursor.getString(cursor.getColumnIndex("DeliveryAddress"));
                String datetime = cursor.getString(cursor.getColumnIndex("Created"));
                SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
                application.Created = format.parse(datetime);
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
        contentValues.put("PersonId", application.PersonId);
        contentValues.put("PersonName", application.PersonName);
        contentValues.put("MerchantName", application.MerchantName);
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

        application.MerchantId = insertMerchant(application.Merchant);

        if (application.Person != null) {
            application.PersonId = insertPerson(application.Person);
        }

        int appId = insertApplication(application);

        if (appId == -1) {
            deleteMerchantById(application.MerchantId);
            deletePersonById(application.PersonId);
        } else {
            for (ru.courier.office.core.Status status : application.StatusList) {
                insertStatus(status);
            }
        }

        return appId;
    }

    @Override
    public boolean removeApplication(int id) {

        Application application = getApplicationById(id);
        deleteMerchantById(application.MerchantId);
        deletePersonById(application.PersonId);
        deleteStatusesByApplicationId(application.ApplicationId);
        deleteApplicationById(id);
        return true;
    }

    @Override
    public Person getPersonById(int personId) {
        Person person = null;
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT Id, PersonId, ApplicationId, FirstName, MiddleName, LastName, BirthDate, Gender FROM Persons WHERE Id = ?", new String[]{String.valueOf(personId)});
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                person = new Person();
                person.Id = cursor.getInt(cursor.getColumnIndex("Id"));
                person.PersonId = cursor.getString(cursor.getColumnIndex("PersonId"));
                person.ApplicationId = cursor.getString(cursor.getColumnIndex("ApplicationId"));
                person.FirstName = cursor.getString(cursor.getColumnIndex("FirstName"));
                person.MiddleName = cursor.getString(cursor.getColumnIndex("MiddleName"));
                person.LastName = cursor.getString(cursor.getColumnIndex("LastName"));
                String datetime = cursor.getString(cursor.getColumnIndex("BirthDate"));
                SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
                person.BirthDate = format.parse(datetime);
                person.Gender = cursor.getString(cursor.getColumnIndex("Gender"));
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
        return person;
    }

    @Override
    public int countPersons() {
        int count = 0;
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT COUNT(*) AS Total FROM Persons", null);
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
    public int insertPerson(Person person) {
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        ContentValues contentValues = new ContentValues();
        contentValues.put("PersonId", person.PersonId);
        contentValues.put("ApplicationId", person.ApplicationId);
        contentValues.put("FirstName", person.FirstName);
        contentValues.put("MiddleName", person.MiddleName);
        contentValues.put("LastName", person.LastName);
        contentValues.put("Gender", person.Gender);
        contentValues.put("BirthDate", dateFormat.format(person.BirthDate));
        int ret = (int)db.insert("Persons", null, contentValues);
        return ret;
    }

    @Override
    public boolean deletePersonById(int id) {
        int ret = db.delete("Persons", "Id = ?", new String[]{String.valueOf(id)});
        return ret == 1;
    }

    @Override
    public Merchant getMerchantById(int merchantId) {
        Merchant merchant = null;
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT Id, MerchantId, ApplicationId, Name, Inn, Email, Site, ManagerName, ManagerPhone FROM Merchants WHERE Id = ?", new String[]{String.valueOf(merchantId)});
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                merchant = new Merchant();
                merchant.Id = cursor.getInt(cursor.getColumnIndex("Id"));
                merchant.MerchantId = cursor.getString(cursor.getColumnIndex("MerchantId"));
                merchant.ApplicationId = cursor.getString(cursor.getColumnIndex("ApplicationId"));
                merchant.Name = cursor.getString(cursor.getColumnIndex("Name"));
                merchant.Inn = cursor.getString(cursor.getColumnIndex("Inn"));
                merchant.Email = cursor.getString(cursor.getColumnIndex("Email"));
                merchant.Site = cursor.getString(cursor.getColumnIndex("Site"));
                merchant.ManagerName = cursor.getString(cursor.getColumnIndex("ManagerName"));
                merchant.ManagerPhone = cursor.getString(cursor.getColumnIndex("ManagerPhone"));
            }
        } catch (SQLiteException ex) {
            ex.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return merchant;
    }

    @Override
    public int countMerchants() {
        int count = 0;
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT COUNT(*) AS Total FROM Merchants", null);
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
    public int insertMerchant(Merchant merchant) {

        ContentValues contentValues = new ContentValues();
        contentValues.put("MerchantId", merchant.MerchantId);
        contentValues.put("ApplicationId", merchant.ApplicationId);
        contentValues.put("Name", merchant.Name);
        contentValues.put("Inn", merchant.Inn);
        contentValues.put("Email", merchant.Email);
        contentValues.put("Site", merchant.Site);
        contentValues.put("ManagerName", merchant.ManagerName);
        contentValues.put("ManagerPhone", merchant.ManagerPhone);
        int ret = (int)db.insert("Merchants", null, contentValues);
        return ret;
    }

    @Override
    public boolean deleteMerchantById(int id) {
        int ret = db.delete("Merchants", "Id = ?", new String[]{String.valueOf(id)});
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
