package ru.courier.office.web;

import android.text.TextUtils;

import ru.courier.office.core.AppMode;
import ru.courier.office.core.Application;
import ru.courier.office.core.HttpMethod;
import ru.courier.office.core.Note;
import ru.courier.office.core.UrlObject;
import ru.courier.office.core.User;
import ru.courier.office.core.UrlType;
import ru.courier.office.core.Scan;

import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
    public Scan Scan = new Scan();

    public Application Application = new Application();
    public List<Note> Notes = new ArrayList<Note>();

    private CookieManager CookieManager = new CookieManager();

    private WebContext() {

        if (Mode != AppMode.Product) {
            User = new User(1, "Раш Сергей Николаевич", "79267026528", "sr@7seconds.ru", true);
        }

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
        developMap.put(UrlType.Note, new UrlObject(HttpMethod.GET, "http://192.168.100.100/courier/api/note"));
        developMap.put(UrlType.Position, new UrlObject(HttpMethod.PUT, "http://192.168.100.100/courier/api/position"));
        developMap.put(UrlType.Scan, new UrlObject(HttpMethod.PUT, "http://192.168.100.100/courier/api/document"));
        developMap.put(UrlType.Sign, new UrlObject(HttpMethod.POST, "http://192.168.100.100/courier/api/account/sign"));
        developMap.put(UrlType.User, new UrlObject(HttpMethod.GET, "http://192.168.100.100/courier/api/account"));
        developMap.put(UrlType.Application, new UrlObject(HttpMethod.POST, "http://192.168.100.100/courier/api/application"));
        developMap.put(UrlType.Image, new UrlObject(HttpMethod.POST, "http://192.168.100.100/courier/api/document"));
        urlMap.put(AppMode.Develop, developMap);
    }

    public UrlObject getUrl(UrlType urlType) {
        return urlMap.get(Mode).get(urlType);
    }

    public static String getCurrentDateFormatted() {
        return new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(new Date());
    }
}

