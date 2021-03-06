package ru.courier.office.web;

import org.json.JSONException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;

import javax.net.ssl.HttpsURLConnection;

import ru.courier.office.core.UrlObject;
import ru.courier.office.core.UrlType;
import ru.courier.office.data.DataAccess;

/**
 * Created by rash on 22.08.2017.
 */

public class StatusProvider extends BaseProvider {

    public int putStatus(String postData) {
        HttpURLConnection connection = null;

        try {
            UrlObject urlObject = webContext.getUrl(UrlType.Status);
            URL url = new URL(urlObject.Url);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            connection.setReadTimeout(15000);
            connection.setConnectTimeout(15000);
            connection.setRequestMethod(urlObject.HttpMethod.toString());
            connection.setDoInput(true);
            connection.setDoOutput(true);
            webContext.attachCookieTo(connection);

            serialisePost(connection, postData);
            responseCode = connection.getResponseCode();

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
