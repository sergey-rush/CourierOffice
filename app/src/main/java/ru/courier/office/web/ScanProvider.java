package ru.courier.office.web;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.net.ssl.HttpsURLConnection;

import ru.courier.office.core.Scan;
import ru.courier.office.core.UrlObject;
import ru.courier.office.core.UrlType;
/**
 * A simple {@link ScanProvider} subclass.
 * This class retrieve data information about the uploaded image
 */
public class ScanProvider extends BaseProvider {


    private String boundary = "===" + System.currentTimeMillis() + "===";
    private String lineEnd = "\r\n";
    private String twoHyphens = "--";
    private String charset = "UTF-8";
    private PrintWriter _printWriter;

    public int doUpload(Scan scan, byte[] imageBytes) {

        HttpURLConnection connection = null;
        int imageBytesLength = imageBytes.length;
        String fileName = String.format("PageNum%s.jpg", Integer.toString(scan.PageNum));
        int maxBufferSize = 1 * 1024 * 1024;
        URL url;

        try {

            UrlObject urlObject = webContext.getUrl(UrlType.Image);
            url = new URL(urlObject.Url);
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setRequestMethod(urlObject.HttpMethod.toString());
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

            OutputStream outputStream = new BufferedOutputStream(connection.getOutputStream());
            _printWriter = new PrintWriter(new OutputStreamWriter(outputStream, charset), true);

            addFormField("StreamId", scan.StreamGuid);


            _printWriter.append(twoHyphens + boundary + lineEnd);
            _printWriter.append("Content-Disposition: form-data; name=\"attachedFile\";filename=" + fileName + "" + lineEnd);
            _printWriter.append("Content-Type: " + URLConnection.guessContentTypeFromName(fileName) + lineEnd);
            _printWriter.append("Content-Transfer-Encoding: binary" + lineEnd);
            _printWriter.append(lineEnd);
            _printWriter.flush();

            outputStream.write(imageBytes, 0, imageBytesLength);
            outputStream.flush();

            _printWriter.append(lineEnd);
            _printWriter.flush();

            _printWriter.append(twoHyphens + boundary + twoHyphens + lineEnd);
            _printWriter.close();


            responseCode = connection.getResponseCode();
            String serverResponseMessage = connection.getResponseMessage();

            if (responseCode == 200) {

            }

        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return responseCode;

    }

    private void addFormField(String name, String value) throws IOException {

        _printWriter.append(twoHyphens + boundary + lineEnd);
        _printWriter.append("Content-Disposition: form-data; name=\"" + name + "\"" + lineEnd);
        _printWriter.append("Content-Type: text/plain; charset=" + charset + lineEnd);
        _printWriter.append(lineEnd);
        _printWriter.append(value);
        _printWriter.append(lineEnd);
        _printWriter.flush();
    }

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
