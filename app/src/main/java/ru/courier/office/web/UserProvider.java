package ru.courier.office.web;

import org.json.JSONException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import ru.courier.office.core.HttpMethod;
import ru.courier.office.core.UrlObject;
import ru.courier.office.core.UrlType;

/**
 * Created by rash on 21.08.2017.
 */
public class UserProvider extends BaseProvider {

    public int getUser() {
        HttpURLConnection connection = null;
        URL url;
        try {
            UrlObject urlObject = dataContext.getUrl(UrlType.User);
            url = new URL(urlObject.Url);
            connection = (HttpURLConnection) url.openConnection();
            dataContext.attachCookieTo(connection);
            connection.connect();
            responseCode = connection.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String output = deserializeToString(connection);
                dataContext.User = parseToUser(output);
            } else {
                return responseCode;
            }

        } catch (MalformedURLException mex) {
            mex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return responseCode;
    }
}


