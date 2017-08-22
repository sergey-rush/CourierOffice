package ru.courier.office.data;

import ru.courier.office.core.Member;
import ru.courier.office.core.User;

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

public class BaseProvider {

    protected DataContext dataContext = DataContext.getInstance();

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

    protected User parseToUser(String input) throws JSONException {

        User member = new User();
        JSONObject resultData = new JSONObject(input);
        //JSONObject resultData = jsonObj.getJSONObject("ResultData");
        member.Id = resultData.getInt("Id");
        member.Name = resultData.getString("Name");
        member.Phone = resultData.getString("Phone");
        member.Email = resultData.getString("Email");
        member.IsValid = resultData.getBoolean("IsValid");
        return member;
    }
    
    protected Member parseToMember(String input) throws JSONException {
        
        Member member = new Member();
        JSONObject resultData = new JSONObject(input);
        //JSONObject resultData = jsonObj.getJSONObject("ResultData");
        member.Id = resultData.getString("Id");
        member.FirstName = resultData.getString("FirstName");
        member.MiddleName = resultData.getString("MiddleName");
        member.LastName = resultData.getString("LastName");
        member.Gender = resultData.getString("Gender");
        member.BirthDate = resultData.getString("BirthDate");
        member.BirthPlace = resultData.getString("BirthPlace");
        member.PasportNum = resultData.getString("PasportNum");
        member.PasportSerial = resultData.getString("PasportSerial");
        member.Authority = resultData.getString("Authority");
        member.Snils = resultData.getString("Snils");
        member.Inn = resultData.getString("Inn");
        member.Marital = resultData.getString("Marital");
        member.Children = resultData.getString("Children");
        member.Address = resultData.getString("Address");
        return member;
    }
}
