package ru.courier.office.web;

import ru.courier.office.core.Person;
import ru.courier.office.core.Product;
import ru.courier.office.core.UrlType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class ProductProvider extends BaseProvider {

    public int getProducts(String memberId) {

        HttpURLConnection connection = null;

        try {

            URL url = new URL(String.format("%s=%s", webContext.getUrl(UrlType.Products), memberId));
            connection = (HttpURLConnection) url.openConnection();
            webContext.attachCookieTo(connection);
            connection.connect();
            responseCode = connection.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String output = deserializeToString(connection);

                JSONObject jsonObj = new JSONObject(output);
                //JSONObject resultData = jsonObj.getJSONObject("ResultData");
                JSONArray items = jsonObj.getJSONArray("ResultData");
                for (int i = 0; i < items.length(); i++) {
                    JSONObject item = items.getJSONObject(i);
                    Product product = new Product();
                    product.Id = item.getString("Id");
                    product.Amount = item.getString("Amount");
                    product.Created = item.getString("Created");
                    webContext.Products.add(product);
                }

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

