package ru.courier.office.web;

import ru.courier.office.core.UrlType;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class MemberProvider extends BaseProvider {

    public int getMember(String memberId) {

        HttpURLConnection connection = null;

        try {

            URL url = new URL(String.format("%s=%s", webContext.getUrl(UrlType.Person), memberId));
            connection = (HttpURLConnection) url.openConnection();
            webContext.attachCookieTo(connection);
            connection.connect();
            responseCode = connection.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String output = deserializeToString(connection);
                //webContext.Person = parseToMember(output);
            } else {
                return responseCode;
            }

        } catch (MalformedURLException mex) {
            mex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }

        }

        return responseCode;

    }
}

