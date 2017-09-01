package ru.courier.office.web;

import android.text.TextUtils;

import ru.courier.office.core.AppMode;
import ru.courier.office.core.Application;
import ru.courier.office.core.HttpMethod;
import ru.courier.office.core.UrlObject;
import ru.courier.office.core.User;
import ru.courier.office.core.Product;
import ru.courier.office.core.UrlType;

import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WebContext {

    private static WebContext current = new WebContext();
    public static WebContext getInstance(){
        return current;
    }

    public AppMode Mode = AppMode.Develop;
    public User User = new User();
    public Application Application = new Application();
    public List<Product> Products = new ArrayList<>();
    private CookieManager CookieManager = new CookieManager();

    private WebContext(){

        initUrls();
    }

    /*
     * Persists cookie in current session.
     */
    public void setCookie(Map<String, List<String>> headers)
    {
        final String COOKIES_HEADER = "Set-Cookie";
        List<String> cookiesHeaders = headers.get(COOKIES_HEADER);
        if (cookiesHeaders != null) {
            for (String cookie : cookiesHeaders) {
                CookieManager.getCookieStore().add(null, HttpCookie.parse(cookie).get(0));
            }
        }
    }

    public void attachCookieTo(HttpURLConnection connection)
    {
        if (CookieManager.getCookieStore().getCookies().size() > 0) {
            connection.setRequestProperty("Cookie", TextUtils.join(";",  CookieManager.getCookieStore().getCookies()));
        }
    }

    Map<AppMode, Map<UrlType, UrlObject>> urlMap = new HashMap<AppMode, Map<UrlType, UrlObject>>();

    private void initUrls()
    {
        Map<UrlType, UrlObject> developMap = new HashMap<UrlType, UrlObject>();
        developMap.put(UrlType.Sign, new UrlObject(HttpMethod.POST, "http://192.168.100.100/courier/api/account/sign"));
        developMap.put(UrlType.User, new UrlObject(HttpMethod.GET, "http://192.168.100.100/courier/api/account"));
        developMap.put(UrlType.Application, new UrlObject(HttpMethod.POST, "http://192.168.100.100/courier/api/application"));
        urlMap.put(AppMode.Develop, developMap);
    }

    public UrlObject getUrl(UrlType urlType) {
        return urlMap.get(Mode).get(urlType);
    }
}

