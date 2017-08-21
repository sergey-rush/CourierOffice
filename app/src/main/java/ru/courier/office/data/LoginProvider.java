package com.clientoffice.data;

import com.clientoffice.core.UrlType;

import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class LoginProvider extends BaseProvider {

    public int sign(String postData) {

        URL url;
        try {
            url = new URL(dataContext.getUrl(UrlType.Sign));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            connection.setReadTimeout(15000);
            connection.setConnectTimeout(15000);
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);

            serialisePost(connection, postData);
            responseCode = connection.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                dataContext.setCookie(connection.getHeaderFields());
                String output = deserializeToString(connection);
                dataContext.Member = parseToMember(output);
            } else {
                return responseCode;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return responseCode;
    }
}
