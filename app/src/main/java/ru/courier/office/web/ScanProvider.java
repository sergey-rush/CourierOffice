package ru.courier.office.web;

import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import ru.courier.office.core.UrlObject;
import ru.courier.office.core.UrlType;

public class ScanProvider extends BaseProvider {

    public int getInfo(String postData) {

        URL url;
        try {
            UrlObject urlObject = webContext.getUrl(UrlType.Scan);
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
                webContext.setCookie(connection.getHeaderFields());
                String output = deserializeToString(connection);
                webContext.Scan = parseToScan(output, webContext.Scan);
            } else {
                return responseCode;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return responseCode;
    }
}
