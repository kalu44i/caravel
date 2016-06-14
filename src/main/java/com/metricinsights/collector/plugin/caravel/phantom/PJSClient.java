package com.metricinsights.collector.plugin.caravel.phantom;

import com.metricinsights.collector.plugin.ExternalReportsListResponse;

import com.metricinsights.collector.plugin.caravel.CaravelSettings;
import com.metricinsights.util.FileUtils;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.service.DriverService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Phantom JS client
 *
 * @author NickVeremeichyk
 * @version 0.0.1
 * @since 2015-09-22
 */
public class PJSClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(PJSClient.class);

    private CaravelSettings settings;
    private DriverService driverService;

    public PJSClient(CaravelSettings settings) {
        this.settings = settings;
    }

    /**
     * Returns parsed Objects
     *
     * @return list
     */
    public ExternalReportsListResponse getObjects() throws Exception {
        ListAbstarctPJSRunnable runnable = invokeRunnable(new ListAbstarctPJSRunnable(settings.getServer(), settings));

        ExternalReportsListResponse res = runnable.getList();
        LOGGER.debug("Objects found: {}", res.getData().size());
        return res;
    }

    /**
     * Returns buffer of image
     *
     * @return BufferedImage
     * @throws Exception
     */
    public BufferedImage getImage() throws Exception {
        ImageAbstractPJSRunnable runnable = invokeRunnable(new ImageAbstractPJSRunnable(settings.getServer(), settings));

        BufferedImage res = runnable.getPulledImage();
        LOGGER.debug("Image ready. Size: {}x{}", res.getWidth(),
                res.getHeight());

        return res;
    }

/*    *//**
     * Return response with data
     *
     * @return ServiceResponse
     * @throws Exception
     *//*
    public ServiceResponse getData() throws Exception {
        DataAbstractPJSRunnable runnable = invokeRunnable(new DataAbstractPJSRunnable(settings.getWebPlayer(), settings));

        ServiceResponse response = runnable.getPulledData();
        LOGGER.debug("Data ready. Size: {}", response.serialize().length);

        return response;
    }*/

/*    *//**
     * Return response with success if login or throw Exception if not
     *
     * @return
     * @throws Exception
     *//*
    public ServiceResponse login() throws Exception {
        invokeRunnable(new DataAbstractPJSRunnable(settings.getWebPlayer(), settings));
        return QueryResponse.SUCCESS;
    }*/

    /**
     * @param runnable
     * @param <T>
     * @return
     * @throws Exception
     */
    private <T extends AbstractPJSRunnable> T invokeRunnable(T runnable) throws Exception {
        Thread thread = null;
        RemoteWebDriver driver = null;
        try {
            LOGGER.debug("User: {}", System.getProperty("user.name"));

            driver = initWebDriver();
            runnable.setWebDriver(driver);

            thread = new Thread(runnable);

            LOGGER.debug("Start pulling thread");
            thread.start();

            //TODO Add possibility to set timeout dependently from command
            LOGGER.debug("Wait for result. Timeout {}", 500000);
            boolean hasResult = runnable.getCountDownLatch().await(500000,
                    TimeUnit.MILLISECONDS);

            if (!hasResult) {
                throw new TimeoutException("Pulling timeout");
            }

            if (runnable.getThrowedException() != null) {
                throw runnable.getThrowedException();
            }

            return runnable;

        } finally {
            if (thread != null) {
                try {
                    LOGGER.debug("Stop screenshot thread");
                    thread.interrupt();
                } catch (Exception e) {
                    LOGGER.warn("Screenshot thread actually stoped");
                }
            }
            if (driver != null) {
                try {
                    LOGGER.debug("Close WebDriver");
                    driver.close();
                } catch (Exception e) {
                    LOGGER.warn("WebDriver actually closed");
                }
            }
            if (driverService != null) {
                try {
                    LOGGER.debug("Stop DriverService");
                    driverService.stop();
                } catch (Exception e) {
                    LOGGER.warn("DriverService actually stoped");
                }
            }
        }
    }

    /**
     * @return
     * @throws Exception
     */
    private RemoteWebDriver initWebDriver() throws Exception {
        return initPhantomJS();
    }

    /**
     * Initiate new Remote Web Driver
     *
     * @return remote we driver
     * @throws Exception
     */
    private RemoteWebDriver initPhantomJS() throws Exception {

        LOGGER.debug("Init web driver (phantomJS)");
        DesiredCapabilities dc = new DesiredCapabilities();
        dc.setJavascriptEnabled(true);

        dc.setBrowserName("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:33.0) Gecko/20120101 Firefox/33.0");
        dc.setCapability("takesScreenshot", true);
        dc.setCapability("phantomjs.page.customHeaders.Accept-Language", "en");

        dc.setCapability(
                PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY,
                settings.getPathToPhantomJS());
        PhantomJSDriverService phantomService;
        try {
            File phantomExec = new File(settings.getPathToPhantomJS());
            FileUtils.checkExecutable(phantomExec);
            phantomService = new PhantomJSDriverService.Builder()
                    .usingPhantomJSExecutable(phantomExec).build();
            driverService = phantomService;
        } catch (Exception e) {
            throw new Exception(String.format("Exception occurs in File block! Executable file locates - %s", settings.getPathToPhantomJS()));
        }

        RemoteWebDriver driver = new PhantomJSDriver(phantomService,
                dc);
        LOGGER.debug("PhantomJS version: {}", driver.getCapabilities()
                .getVersion());
        driver.manage().window().setSize(new Dimension(1920, 1080));
        LOGGER.debug("Driver ready");
        return driver;
    }
}
