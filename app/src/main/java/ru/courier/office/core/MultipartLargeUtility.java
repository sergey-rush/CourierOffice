package ru.courier.office.core;

import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rash on 06.09.2017.
 */

public class MultipartLargeUtility {

    private final String boundary;
    private static final String LINE_FEED = "\r\n";
    private HttpURLConnection connection;
    private String charset;
    private OutputStream _outputStream;
    private PrintWriter _printWriter;
    private final int maxBufferSize = 4096;
    private long contentLength = 0;
    private URL url;

    private List<FormField> fields;
    private List<FilePart> fileParts;

    private class FormField {
        public String name;
        public String value;

        public FormField(String name, String value) {
            this.name = name;
            this.value = value;
        }
    }

    private class FilePart {
        public String fieldName;
        public String fileName;
        public byte[] fileBytes;

        public FilePart(String fieldName, String fileName, byte[] fileBytes) {
            this.fieldName = fieldName;
            this.fileName = fileName;
            this.fileBytes = fileBytes;
        }
    }

    /**
     * This constructor initializes a new HTTP POST request with content type
     * is set to multipart/form-data
     *
     * @param url
     * @param charset
     * @throws IOException
     */
    public MultipartLargeUtility(URL url, String charset, boolean requireCSRF) throws IOException {

        this.charset = charset;
        // creates a unique boundary based on time stamp
        boundary = "===" + System.currentTimeMillis() + "===";
        this.url = url;
        fields = new ArrayList<>();
        fileParts = new ArrayList<>();

        if (requireCSRF) {
            getCSRF();
        }
    }

    /**
     * Adds a form field to the request
     *
     * @param name  field name
     * @param value field value
     */
    public void addFormField(String name, String value) throws UnsupportedEncodingException {
        String fieldContent = "--" + boundary + LINE_FEED;
        fieldContent += "Content-Disposition: form-data; name=\"" + name + "\"" + LINE_FEED;
        fieldContent += "Content-Type: text/plain; charset=" + charset + LINE_FEED;
        fieldContent += LINE_FEED;
        fieldContent += value + LINE_FEED;
        contentLength += fieldContent.getBytes(charset).length;
        fields.add(new FormField(name, value));
    }

    /**
     * Adds a upload file section to the request
     *
     * @param fieldName  name attribute in <input type="file" name="..." />
     * @param fileBytes a File to be uploaded
     * @throws IOException
     */
    public void addFilePart(String fieldName, byte[] fileBytes) throws IOException {

        String fileName = "Page1.jpg";
        String fieldContent = "--" + boundary + LINE_FEED;
        fieldContent += "Content-Disposition: form-data; name=\"" + fieldName + "\"; filename=\"" + fileName + "\"" + LINE_FEED;
        fieldContent += "Content-Type: " + URLConnection.guessContentTypeFromName(fileName) + LINE_FEED;
        fieldContent += "Content-Transfer-Encoding: binary" + LINE_FEED;
        fieldContent += LINE_FEED;
        // file content would go here
        fieldContent += LINE_FEED;
        contentLength += fieldContent.getBytes(charset).length;
        contentLength += fileBytes.length;
        fileParts.add(new FilePart(fieldName, fileName, fileBytes));
    }

    /**
     * Adds a header field to the request.
     *
     * @param name  - name of the header field
     * @param value - value of the header field
     */
    //public void addHeaderField(String name, String value) {
    //    _printWriter.append(name + ": " + value).append(LINE_FEED);
    //    _printWriter.flush();
    //}

    /**
     * Completes the request and receives response from the server.
     *
     * @return a list of Strings as response in case the server returned
     * status OK, otherwise an exception is thrown.
     * @throws IOException
     */
    public List<String> finish() throws IOException {
        List<String> response = new ArrayList<String>();
        String content = "--" + boundary + "--" + LINE_FEED;
        contentLength += content.getBytes(charset).length;

        if (!openConnection()) {
            return response;
        }

        writeContent();

        // checks server's status code first
        int status = connection.getResponseCode();
        if (status == HttpURLConnection.HTTP_OK) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line = null;
            while ((line = reader.readLine()) != null) {
                response.add(line);
            }
            reader.close();
            connection.disconnect();
        } else {
            throw new IOException("Server returned non-OK status: " + status);
        }
        return response;
    }

    private boolean getCSRF() throws IOException {
        /// First, need to get CSRF token from server
        /// Use GET request to get the token
        CookieManager cookieManager = new CookieManager();
        CookieHandler.setDefault(cookieManager);
        HttpURLConnection conn = null;

        conn = (HttpURLConnection) url.openConnection();

        conn.setUseCaches(false); // Don't use a Cached Copy
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Connection", "Keep-Alive");
        conn.getContent();
        conn.disconnect();

        /// parse the returned object for the CSRF token
        CookieStore cookieJar = cookieManager.getCookieStore();
        List<HttpCookie> cookies = cookieJar.getCookies();
        String csrf = null;
        for (HttpCookie cookie : cookies) {
            Log.d("cookie", "" + cookie);
            if (cookie.getName().equals("csrftoken")) {
                csrf = cookie.getValue();
                break;
            }
        }
        if (csrf == null) {
            Log.d("TAG", "Unable to get CSRF");
            return false;
        }
        Log.d("TAG", "Received cookie: " + csrf);

        addFormField("csrfmiddlewaretoken", csrf);
        return true;
    }

    private boolean openConnection() throws IOException {
        connection = (HttpURLConnection) url.openConnection();
        connection.setUseCaches(false);
        connection.setDoOutput(true);    // indicates POST method
        connection.setDoInput(true);
        //connection.setRequestProperty("Accept-Encoding", "identity");
        //connection.setFixedLengthStreamingMode(contentLength);
        connection.setRequestProperty("Connection", "Keep-Alive");
        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
        _outputStream = new BufferedOutputStream(connection.getOutputStream());
        _printWriter = new PrintWriter(new OutputStreamWriter(_outputStream, charset), true);
        return true;
    }

    private void writeContent() throws IOException {

        for (FormField field : fields) {
            _printWriter.append("--" + boundary).append(LINE_FEED);
            _printWriter.append("Content-Disposition: form-data; name=\"" + field.name + "\"").append(LINE_FEED);
            _printWriter.append("Content-Type: text/plain; charset=" + charset).append(LINE_FEED);
            _printWriter.append(LINE_FEED);
            _printWriter.append(field.value).append(LINE_FEED);
            _printWriter.flush();
        }

        for (FilePart filePart : fileParts) {

            String fileName = filePart.fileName;
            String contentType = URLConnection.guessContentTypeFromName(fileName);
            _printWriter.append("--" + boundary).append(LINE_FEED);
            _printWriter.append("Content-Disposition: form-data; name=\"" + filePart.fieldName + "\"; filename=\"" + fileName + "\"").append(LINE_FEED);
            _printWriter.append("Content-Type: " + URLConnection.guessContentTypeFromName(fileName)).append(LINE_FEED);
            _printWriter.append("Content-Transfer-Encoding: binary").append(LINE_FEED);
            _printWriter.append(LINE_FEED);
            _printWriter.flush();

            //FileInputStream inputStream = new FileInputStream(filePart.fileBytes);
            //int bufferSize = Math.min(inputStream.available(), maxBufferSize);
            //byte[] buffer = new byte[bufferSize];
            //int bytesRead = -1;
            //while ((bytesRead = inputStream.read(buffer, 0, bufferSize)) != -1) {
                //_outputStream.write(buffer, 0, bytesRead);
            //}
            _outputStream.write(filePart.fileBytes, 0, filePart.fileBytes.length);
            _outputStream.flush();
            //inputStream.close();

            _printWriter.append(LINE_FEED);
            _printWriter.flush();
        }

        _printWriter.append("--" + boundary + "--").append(LINE_FEED);
        _printWriter.close();
    }
}

