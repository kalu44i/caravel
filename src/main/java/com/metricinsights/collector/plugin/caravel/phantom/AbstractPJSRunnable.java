package com.metricinsights.collector.plugin.caravel.phantom;

import com.metricinsights.collector.plugin.caravel.CaravelSettings;
import com.metricinsights.collector.plugin.settings.CommandType;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

/**
 * @author NickVeremeichyk
 * @version 0.0.1
 * @since 2015-09-22
 */
public abstract class AbstractPJSRunnable implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractPJSRunnable.class);


    protected Exception throwedException;
    protected CountDownLatch ready;

    protected RemoteWebDriver webDriver;
    protected CaravelSettings settings;
    protected String url;

    @Override
    public void run() {
        try {
            String wUrl = url;
            LOGGER.debug("Open page: {}", wUrl);
            webDriver.get(wUrl);
            Thread.sleep(1500);
            makeLogin();

            // Check URL
            if (!webDriver.getCurrentUrl().equals(url)) {
                webDriver.get(url);
            }


            pullData();
        } catch (Exception e) {
            LOGGER.error("Error in thread: {}", e.getMessage());
            throwedException = e;
        } finally {
            // Release lock
            ready.countDown();
        }
    }

    /**
     * Login to Tibco Spotfire WebPlayer
     */
    protected void makeLogin() {
        if (webDriver.getCurrentUrl().contains("/caravel/welcome")) {
            LOGGER.debug("Already logged in");
            return;
        }

//        if (webDriver.getCurrentUrl().startsWith(
//                "http://qlikview2.metricinsights.com/SpotfireWeb/Login.aspx")) {
//            LOGGER.debug("Redirect to login page {}", webDriver.getCurrentUrl());
//        }
        LOGGER.debug("Login");

        WebElement usernameInput = webDriver.findElementByName("username");
        WebElement passwordInput = webDriver.findElementByName("password");
        usernameInput.sendKeys(settings.getUsername());
        passwordInput.sendKeys(settings.getPassword());
        webDriver.findElementByClassName("btn-primary").click();
        LOGGER.debug("Curr URL {}", webDriver.getCurrentUrl());
        for (int attemp = 0; attemp < 3; attemp++) {
            String currUrl = settings.getServer() + "/caravel/welcome";
            LOGGER.debug("CurrUrl {}", webDriver.getCurrentUrl());
            if (webDriver.getCurrentUrl().startsWith(currUrl)) {
                return;
            }
            LOGGER.debug("Waiting for redirect. {}. Curr URL {}", attemp,
                    webDriver.getCurrentUrl());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {
            }
        }
        if (!webDriver.getCurrentUrl().startsWith(settings.getServer() + "/caravel/welcome")) {
            throw new IllegalStateException(
                    "Cannot open Spotfire Web Player. Check your credentials.");
        }
    }

    /**
     * Extract cookies from Web Driver and transform it to the Apache cookies
     *
     * @param webDriver web driver
     * @return Convenient Cookies for HttpClient as CookieStore
     */
    private CookieStore seleniumCookiesToCookieStore(RemoteWebDriver webDriver) {

        Set<Cookie> seleniumCookies = webDriver.manage().getCookies();
        CookieStore cookieStore = new BasicCookieStore();

        for (Cookie seleniumCookie : seleniumCookies) {
            BasicClientCookie basicClientCookie =
                    new BasicClientCookie(seleniumCookie.getName(), seleniumCookie.getValue());
            basicClientCookie.setDomain(seleniumCookie.getDomain());
            basicClientCookie.setExpiryDate(seleniumCookie.getExpiry());
            basicClientCookie.setPath(seleniumCookie.getPath());
            cookieStore.addCookie(basicClientCookie);
        }
        return cookieStore;
    }

    //TODO check error when it tries to get Data from Graph
    /**
     * Get all Cookies from WebDriver and pull it into HttpClient. It executes GET method with received URL and returns CloseableHttpResponse
     *
     * @param url
     * @param type
     * @return
     * @throws IOException
     */
    public CloseableHttpResponse fromWebDriverToHttpClient(String url, CommandType type) throws IOException {
        CookieStore cookieStore = seleniumCookiesToCookieStore(webDriver);
        CloseableHttpClient httpClient = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
        LOGGER.debug("{} from {}", type, url);
        HttpGet httpGet = new HttpGet(url);

        CloseableHttpResponse response = httpClient.execute(httpGet);
        LOGGER.debug("Code: {}", response.getStatusLine().getStatusCode());
        return response;
    }

    public void setWebDriver(RemoteWebDriver webDriver) {
        this.webDriver = webDriver;
    }

    public Exception getThrowedException() {

        return throwedException;
    }

    public CountDownLatch getCountDownLatch() {
        return ready;
    }

    protected abstract void pullData() throws Exception;

}
