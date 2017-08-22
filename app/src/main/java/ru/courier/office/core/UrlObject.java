package ru.courier.office.core;

/**
 * Created by rash on 21.08.2017.
 */

public class UrlObject {

    public HttpMethod HttpMethod;
    public String Url;

    public UrlObject(HttpMethod httpMethod, String url) {
        HttpMethod = httpMethod;
        Url = url;
    }
}
