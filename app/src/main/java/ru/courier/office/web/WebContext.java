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

    public User User = new User();
    public Scan Scan = new Scan();
    public int SelectedDocumentId;
    public Application Application = new Application();
    public String Imei;
    public List<Note> NoteList;

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

    public AppMode Mode = AppMode.Product;
    Map<AppMode, Map<UrlType, UrlObject>> urlMap = new HashMap<AppMode, Map<UrlType, UrlObject>>();

    private void initUrls()
    {
        Map<UrlType, UrlObject> developMap = new HashMap<UrlType, UrlObject>();
        developMap.put(UrlType.Note, new UrlObject(HttpMethod.POST, "https://dev-api.7seconds.ru/api/CourierAppV3/PostNotes"));
        developMap.put(UrlType.Position, new UrlObject(HttpMethod.PUT, "https://dev-api.7seconds.ru/api/CourierAppV3/PutPosition"));
        developMap.put(UrlType.Scan, new UrlObject(HttpMethod.PUT, "https://dev-api.7seconds.ru/api/CourierAppV3/PutPhoto"));
        developMap.put(UrlType.Sign, new UrlObject(HttpMethod.POST, "https://dev-api.7seconds.ru/api/CourierAppV3/Sign"));
        developMap.put(UrlType.User, new UrlObject(HttpMethod.GET, "https://dev-api.7seconds.ru/api/CourierAppV3/GetUser"));
        developMap.put(UrlType.Application, new UrlObject(HttpMethod.POST, "https://dev-api.7seconds.ru/api/CourierAppV3/PostApplication"));
        developMap.put(UrlType.Image, new UrlObject(HttpMethod.POST, "https://dev-api.7seconds.ru/api/CourierAppV3/PostPhoto"));
        developMap.put(UrlType.Status, new UrlObject(HttpMethod.PUT, "https://dev-api.7seconds.ru/api/CourierAppV3/PutStatus"));
        urlMap.put(AppMode.Develop, developMap);

        Map<UrlType, UrlObject> testMap = new HashMap<UrlType, UrlObject>();
        testMap.put(UrlType.Note, new UrlObject(HttpMethod.POST, "http://192.168.100.100/Courier/api/CourierAppV3/PostNotes"));
        testMap.put(UrlType.Position, new UrlObject(HttpMethod.PUT, "http://192.168.100.100/Courier/api/CourierAppV3/PutPosition"));
        testMap.put(UrlType.Scan, new UrlObject(HttpMethod.PUT, "http://192.168.100.100/Courier/api/CourierAppV3/PutPhoto"));
        testMap.put(UrlType.Sign, new UrlObject(HttpMethod.POST, "http://192.168.100.100/Courier/api/CourierAppV3/Sign"));
        testMap.put(UrlType.User, new UrlObject(HttpMethod.GET, "http://192.168.100.100/Courier/api/CourierAppV3/GetUser"));
        testMap.put(UrlType.Application, new UrlObject(HttpMethod.POST, "http://192.168.100.100/Courier/api/CourierAppV3/PostApplication"));
        testMap.put(UrlType.Image, new UrlObject(HttpMethod.POST, "http://192.168.100.100/Courier/api/CourierAppV3/PostPhoto"));
        testMap.put(UrlType.Status, new UrlObject(HttpMethod.PUT, "http://192.168.100.100/Courier/api/CourierAppV3/PutStatus"));
        urlMap.put(AppMode.Test, testMap);

        Map<UrlType, UrlObject> prodMap = new HashMap<UrlType, UrlObject>();
        prodMap.put(UrlType.Note, new UrlObject(HttpMethod.POST, "http://192.168.100.100/PublicAPI/api/CourierAppV3/PostNotes"));
        prodMap.put(UrlType.Position, new UrlObject(HttpMethod.PUT, "http://192.168.100.100/PublicAPI/api/CourierAppV3/PutPosition"));
        prodMap.put(UrlType.Scan, new UrlObject(HttpMethod.PUT, "http://192.168.100.100/PublicAPI/api/CourierAppV3/PutPhoto"));
        prodMap.put(UrlType.Sign, new UrlObject(HttpMethod.POST, "http://192.168.100.100/PublicAPI/api/CourierAppV3/Sign"));
        prodMap.put(UrlType.User, new UrlObject(HttpMethod.GET, "http://192.168.100.100/PublicAPI/api/CourierAppV3/GetUser"));
        prodMap.put(UrlType.Application, new UrlObject(HttpMethod.POST, "http://192.168.100.100/PublicAPI/api/CourierAppV3/PostApplication"));
        prodMap.put(UrlType.Image, new UrlObject(HttpMethod.POST, "http://192.168.100.100/PublicAPI/api/CourierAppV3/PostPhoto"));
        prodMap.put(UrlType.Status, new UrlObject(HttpMethod.PUT, "http://192.168.100.100/PublicAPI/api/CourierAppV3/PutStatus"));
        urlMap.put(AppMode.Product, prodMap);
    }

    public UrlObject getUrl(UrlType urlType) {
        return urlMap.get(Mode).get(urlType);
    }

    public static String getCurrentDateFormatted() {
        return new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(new Date());
    }
}

