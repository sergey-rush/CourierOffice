package ru.courier.office.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import ru.courier.office.core.Application;

/**
 * Created by rash on 23.08.2017.
 */
public class ApplicationData{
    SQLiteDatabase db;
    public ApplicationData(SQLiteDatabase database) {
        db = database;
    }

    public List<Application> getApplications(int parentId) {
        Cursor cursor = db.rawQuery("SELECT Id, ParentId, Name FROM Applications WHERE ParentId = ?", new String[]{parentId + ""});
        cursor.moveToFirst();
        List<Application> sections  = new ArrayList<Application>();

        while ( !cursor.isAfterLast()) {
            int id= cursor.getInt(cursor.getColumnIndex("Id"));
            int parent= cursor.getInt(cursor.getColumnIndex("ParentId"));
            String name= cursor.getString(cursor.getColumnIndex("Name"));
            //Application section = new Application(id,parent, name);
            //sections.add(section);
            cursor.moveToNext();
        }
        return sections;
    }

    public long insertApplication(Application section) {

        ContentValues contentValues = new ContentValues();
        contentValues.put("Id", section.Id);
        //contentValues.put("ParentId", section.ParentId);
        //contentValues.put("Name", section.Name);
        long ret = db.insert("Applications", null, contentValues);
        return ret;
    }

    public boolean deleteApplication(int id) {

        db.delete("Applications", "Id = ?", new String[]{id + ""});
        return true;
    }
}
