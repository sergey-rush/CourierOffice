package com.clientoffice.data;

import android.text.TextUtils;

import com.clientoffice.core.AppMode;
import com.clientoffice.core.Member;
import com.clientoffice.core.Product;
import com.clientoffice.core.UrlType;

import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataContext {

    private static DataContext current = new DataContext();
    public static DataContext getInstance(){
        return current;
    }

    public AppMode Mode = AppMode.Develop;
    public Member Member = new Member();
    public List<Product> Products = new ArrayList<>();
    private CookieManager CookieManager = new CookieManager();
    private DataContext(){

        InitUrls();
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

    public void getCookie(HttpURLConnection connection)
    {
        if (CookieManager.getCookieStore().getCookies().size() > 0) {
            connection.setRequestProperty("Cookie", TextUtils.join(";",  CookieManager.getCookieStore().getCookies()));
        }
    }

    Map<AppMode, Map<UrlType, String>> urlMap = new HashMap<AppMode, Map<UrlType, String>>();

    private void InitUrls()
    {
        Map<UrlType, String> productMap = new HashMap<UrlType, String>();
        productMap.put(UrlType.Sign, "http://7seconds.ru/widget/api/mobile/Sign");
        urlMap.put(AppMode.Product, productMap);

        Map<UrlType, String> testMap = new HashMap<UrlType, String>();
        testMap.put(UrlType.Sign, "http://52.233.157.183:1000/widget/api/mobile/Sign");
        urlMap.put(AppMode.Test, testMap);

        Map<UrlType, String> developMap = new HashMap<UrlType, String>();
        developMap.put(UrlType.Sign, "http://192.168.100.100/widget/api/mobile/Sign");
        developMap.put(UrlType.Member, "http://192.168.100.100/widget/api/mobile/GetPersonData?personId");
        developMap.put(UrlType.Products, "http://192.168.100.100/widget/api/mobile/GetApplications?personId");
        developMap.put(UrlType.Product, "http://192.168.100.100/widget/api/mobile/GetApplication?applicationId");

        urlMap.put(AppMode.Develop, developMap);
    }

    public String getUrl(UrlType urlType) {
        return urlMap.get(Mode).get(urlType);
    }
}

