package ru.courier.office.data;

import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import ru.courier.office.core.UrlObject;
import ru.courier.office.core.UrlType;

/**
 * Created by rash on 21.08.2017.
 */

public class HttpProvider extends BaseProvider {

    public int doSign(UrlType urlType, String postData) {

        URL url;
        try {
            UrlObject urlObject = dataContext.getUrl(UrlType.Sign);
            url = new URL(urlObject.Url);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            connection.setReadTimeout(15000);
            connection.setConnectTimeout(15000);
            connection.setRequestMethod(urlObject.HttpMethod.toString());
            connection.setDoInput(true);
            connection.setDoOutput(true);

            serialisePost(connection, postData);
            responseCode = connection.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                dataContext.setCookie(connection.getHeaderFields());
                String output = deserializeToString(connection);
                dataContext.User = parseToUser(output);
            } else {
                return responseCode;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return responseCode;
    }
}
