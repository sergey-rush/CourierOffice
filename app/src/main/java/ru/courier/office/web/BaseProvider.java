package ru.courier.office.web;

import ru.courier.office.core.Application;
import ru.courier.office.core.Person;
import ru.courier.office.core.Product;
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
import java.util.Date;
import java.util.List;

public class BaseProvider {

    protected WebContext dataContext = WebContext.getInstance();

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
        application.Id = resultData.getString("Id");
        application.MerchantId = resultData.getString("MerchantId");
        application.PersonId = resultData.getString("PersonId");
        application.Amount = resultData.getString("Amount");
        application.DeliveryAddress = resultData.getString("DeliveryAddress");
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        application.Created = format.parse(resultData.getString("Created"));
        application.StatusList = new ArrayList<Status>();

        JSONArray items = resultData.getJSONArray("StatusList");
        for (int i = 0; i < items.length(); i++) {
            JSONObject item = items.getJSONObject(i);
            Status status = new Status();
            status.Title = item.getString("Title");
            status.Created = format.parse(item.getString("Created"));
            application.StatusList.add(status);
        }

        JSONObject personData = resultData.getJSONObject("Person");
        application.Person = parseToPerson(personData.toString());

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
    
    protected Person parseToPerson(String input) throws JSONException, ParseException {
        
        Person member = new Person();
        JSONObject resultData = new JSONObject(input);

        member.Id = resultData.getString("Id");
        member.FirstName = resultData.getString("FirstName");
        member.MiddleName = resultData.getString("MiddleName");
        member.LastName = resultData.getString("LastName");
        member.Gender = resultData.getString("Gender");
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        member.BirthDate = format.parse(resultData.getString("BirthDate"));
        return member;
    }
}
