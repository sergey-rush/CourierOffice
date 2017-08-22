package ru.courier.office.data;

import ru.courier.office.core.Member;
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

            URL url = new URL(String.format("%s=%s", dataContext.getUrl(UrlType.Member), memberId));
            connection = (HttpURLConnection) url.openConnection();
            dataContext.attachCookieTo(connection);
            connection.connect();
            responseCode = connection.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String output = deserializeToString(connection);
                dataContext.Member = parseToMember(output);
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

