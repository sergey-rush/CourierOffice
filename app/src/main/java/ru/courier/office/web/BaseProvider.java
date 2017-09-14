package ru.courier.office.web;

import ru.courier.office.core.Application;
import ru.courier.office.core.Document;
import ru.courier.office.core.Note;
import ru.courier.office.core.Scan;
import ru.courier.office.core.ScanStatus;
import ru.courier.office.core.Status;
import ru.courier.office.core.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class BaseProvider {

    protected WebContext webContext = WebContext.getInstance();

    protected int responseCode = 0;
    private String applicationGuid;

    protected void serialisePost(HttpURLConnection connection, String postData) throws IOException {
        OutputStream os = connection.getOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
        writer.write(postData);
        writer.flush();
        writer.close();
        os.close();
    }

    protected String deserializeToString(HttpURLConnection connection) throws IOException {
        InputStream stream = connection.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        StringBuffer buffer = new StringBuffer();
        String line = "";
        while ((line = reader.readLine()) != null) {
            buffer.append(line);
        }
        reader.close();

        String output = buffer.toString();
        return output;
    }

    protected Application parseToApplication(String input) throws JSONException, ParseException {

        Application application = new Application();
        JSONObject resultData = new JSONObject(input);
        application.ApplicationGuid = applicationGuid = resultData.getString("Id");
        application.Amount = resultData.getString("Amount");
        application.DeliveryAddress = resultData.getString("DeliveryAddress");
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        application.Created = format.parse(resultData.getString("Created"));
        application.StatusList = parseToStatusList(resultData);
        application.DocumentList = parseToDocumentList(resultData);

        JSONObject merchantData = resultData.getJSONObject("Merchant");
        application = parseToMerchant(merchantData.toString(), application);

        JSONObject personData = resultData.getJSONObject("Person");
        application = parseToPerson(personData.toString(), application);

        return application;
    }

    protected List<Document> parseToDocumentList(JSONObject resultData) throws JSONException, ParseException {

        ArrayList<Document> documentList = new ArrayList<Document>();
        JSONArray items = resultData.getJSONArray("DocumentList");
        for (int i = 0; i < items.length(); i++) {
            JSONObject item = items.getJSONObject(i);
            Document document = new Document();
            document.ApplicationGuid = applicationGuid;
            document.DocumentGuid = item.getString("Id");
            document.Title = item.getString("Title");
            documentList.add(document);
        }

        return documentList;
    }

    protected List<Status> parseToStatusList(JSONObject resultData) throws JSONException, ParseException {

        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        ArrayList<Status> statusList = new ArrayList<Status>();
        JSONArray items = resultData.getJSONArray("StatusList");
        for (int i = 0; i < items.length(); i++) {
            JSONObject item = items.getJSONObject(i);
            Status status = new Status();
            status.ApplicationGuid = applicationGuid;
            status.Code = item.getString("Code");
            status.Category = item.getString("Category");
            status.Info = item.getString("Info");
            status.Created = format.parse(item.getString("Created"));
            statusList.add(status);
        }

        return statusList;
    }

    protected Application parseToMerchant(String input, Application application) throws JSONException {

        JSONObject resultData = new JSONObject(input);
        application.MerchantGuid = resultData.getString("Id");
        application.MerchantName = resultData.getString("MerchantName");
        application.Inn = resultData.getString("Inn");
        application.Email = resultData.getString("Email");
        application.Site = resultData.getString("Site");
        application.ManagerName = resultData.getString("ManagerName");
        application.ManagerPhone = resultData.getString("ManagerPhone");
        return application;
    }

    protected User parseToUser(String input) throws JSONException {

        User user = new User();
        JSONObject resultData = new JSONObject(input);
        user.Id = resultData.getInt("Id");
        user.Name = resultData.getString("Name");
        user.Phone = resultData.getString("Phone");
        user.Email = resultData.getString("Email");
        user.IsValid = resultData.getBoolean("IsValid");
        return user;
    }
    
    protected Application parseToPerson(String input, Application application) throws JSONException, ParseException {

        JSONObject resultData = new JSONObject(input);
        application.PersonGuid = resultData.getString("Id");
        application.PersonName = resultData.getString("PersonName");
        boolean sex = resultData.getBoolean("Gender");
        if(sex)
        {
            application.Gender = 1;
        }
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        application.BirthDate = format.parse(resultData.getString("BirthDate"));
        return application;
    }

    protected Scan parseToScan(String input, Scan scan) throws JSONException, ParseException {

        JSONObject resultData = new JSONObject(input);
        scan.PhotoGuid = resultData.getString("Id");
        scan.StreamGuid = resultData.getString("StreamId");
        //scan.ApplicationGuid = resultData.getString("StreamId");
        //scan.DocumentGuid = resultData.getString("DocumentId");
        //scan.PageNum = resultData.getInt("PageNum");
        scan.ByteNum = resultData.getInt("ByteNum");
        scan.ScanStatus = ScanStatus.fromInt(resultData.getInt("ScanStatus"));
        return scan;
    }

    protected List<Note> parseToNoteList(String input) throws JSONException, ParseException {
        JSONArray items = new JSONArray(input);
        ArrayList<Note> noteList = new ArrayList<Note>();
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        for (int i = 0; i < items.length(); i++) {
            JSONObject item = items.getJSONObject(i);
            Note note = new Note();
            note.Id = item.getInt("Id");
            note.Info = item.getString("Info");
            note.Created = format.parse(item.getString("Created"));
            noteList.add(note);
        }

        return noteList;
    }
}
