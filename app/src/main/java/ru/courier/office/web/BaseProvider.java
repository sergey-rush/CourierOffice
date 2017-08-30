package ru.courier.office.web;

import ru.courier.office.core.Application;
import ru.courier.office.core.Merchant;
import ru.courier.office.core.Person;
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
        application.ApplicationId = resultData.getString("Id");
        application.Amount = resultData.getString("Amount");
        application.DeliveryAddress = resultData.getString("DeliveryAddress");
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        application.Created = format.parse(resultData.getString("Created"));
        application.StatusList = parseToStatusList(resultData);

        JSONObject merchantData = resultData.getJSONObject("Merchant");
        application.Merchant = parseToMerchant(merchantData.toString());
        application.Merchant.ApplicationId = application.ApplicationId;
        application.MerchantId = application.Merchant.Id;
        application.MerchantName = application.Merchant.Name;

        JSONObject personData = resultData.getJSONObject("Person");
        application.Person = parseToPerson(personData.toString());
        if (application.Person != null) {
            application.Person.ApplicationId = application.ApplicationId;
            application.PersonId = application.Person.Id;
            application.PersonName = application.Person.getName();
        }

        return application;
    }

    protected List<Status> parseToStatusList(JSONObject resultData) throws JSONException, ParseException {

        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        String id = resultData.getString("Id");
        ArrayList<Status> statusList = new ArrayList<Status>();
        JSONArray items = resultData.getJSONArray("StatusList");
        for (int i = 0; i < items.length(); i++) {
            JSONObject item = items.getJSONObject(i);
            Status status = new Status();
            status.ApplicationId = id;
            status.Code = item.getString("Code");
            status.Category = item.getString("Category");
            status.Info = item.getString("Info");
            status.Created = format.parse(item.getString("Created"));
            statusList.add(status);
        }

        return statusList;
    }

    protected Merchant parseToMerchant(String input) throws JSONException {

        Merchant merchant = new Merchant();
        JSONObject resultData = new JSONObject(input);
        merchant.MerchantId = resultData.getString("Id");
        merchant.Name = resultData.getString("Name");
        merchant.Inn = resultData.getString("Inn");
        merchant.Email = resultData.getString("Email");
        merchant.Site = resultData.getString("Site");
        merchant.ManagerName = resultData.getString("ManagerName");
        merchant.ManagerPhone = resultData.getString("ManagerPhone");
        merchant.IsActive = resultData.getString("IsActive");
        return merchant;
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
    
    protected Person parseToPerson(String input) throws JSONException, ParseException {
        
        Person person = new Person();
        JSONObject resultData = new JSONObject(input);
        person.PersonId = resultData.getString("Id");
        person.FirstName = resultData.getString("FirstName");
        person.MiddleName = resultData.getString("MiddleName");
        person.LastName = resultData.getString("LastName");
        person.Gender = resultData.getString("Gender");
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        person.BirthDate = format.parse(resultData.getString("BirthDate"));
        return person;
    }
}
