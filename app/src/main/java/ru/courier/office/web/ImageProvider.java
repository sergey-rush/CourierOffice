package ru.courier.office.web;

import java.io.DataOutputStream;
import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import ru.courier.office.core.MultipartLargeUtility;
import ru.courier.office.core.Scan;
import ru.courier.office.core.UrlObject;
import ru.courier.office.core.UrlType;

public class ImageProvider extends BaseProvider {

    public int doUpload(Scan scan, byte[] imageBytes) {

        URL url;
        try {
            UrlObject urlObject = webContext.getUrl(UrlType.Image);
            url = new URL(urlObject.Url);

            boolean useCSRF = false;
            MultipartLargeUtility multipart = new MultipartLargeUtility(url, "UTF-8", useCSRF);

            multipart.addFormField("ApplicationGuid", scan.ApplicationGuid);
            multipart.addFormField("DocumentGuid", Integer.toString(scan.DocumentId));
            multipart.addFormField("PageNum", Integer.toString(scan.PageNum));


            multipart.addFilePart("filefield",imageBytes);
            List<String> response = multipart.finish();


        } catch (Exception e) {
            e.printStackTrace();
        }

        return responseCode;
    }
}
