package ru.courier.office.web;

import org.json.JSONException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import ru.courier.office.core.Note;
import ru.courier.office.core.UrlObject;
import ru.courier.office.core.UrlType;

public class NoteProvider extends BaseProvider {

    public int getNotes(int id) {

        HttpURLConnection connection = null;

        try {
            UrlObject urlObject = webContext.getUrl(UrlType.Note);
            URL url = new URL(String.format("%s?id=%d", urlObject.Url, id));
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            connection.setReadTimeout(15000);
            connection.setConnectTimeout(15000);
            connection.setRequestMethod(urlObject.HttpMethod.toString());
            connection.setDoOutput(true);
            webContext.attachCookieTo(connection);

            responseCode = connection.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String output = deserializeToString(connection);
                List<Note> noteList = parseToNoteList(output);
            } else {
                return responseCode;
            }

        } catch (MalformedURLException mex) {
            mex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
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

