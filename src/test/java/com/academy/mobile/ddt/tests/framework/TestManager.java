package com.academy.mobile.ddt.tests.framework;

import com.academy.mobile.ddt.tests.framework.bd.helper.SubscriberBdHelper;
import com.academy.mobile.ddt.tests.framework.rest.helper.SubscriberRestHelper;
import com.academy.mobile.ddt.tests.framework.ui.helper.NavigationUiHelper;
import com.academy.mobile.ddt.tests.framework.ui.helper.SubscriberUiHelper;
import com.academy.util.PropertyManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class TestManager {
    protected static final Logger LOG = LogManager.getLogger(TestManager.class);

    private static final String COMMON = "common";
    private static final String MOBILE = "mobile";

    private UiManager uiManager = new UiManager();
    private RestManager restManager = new RestManager();
    private BdManager bdManager = new BdManager();

    public void init(String browser) throws IOException  {
        uiManager.init(browser);
        restManager.init();
        bdManager.init();
    }

    public void stop() {
        uiManager.stop();
    }

    public UiManager ui() {
        return uiManager;
    }

    public RestManager rest() {
        return restManager;
    }

    public BdManager bd() {
        return bdManager;
    }

    public class UiManager {
        private final int DEFAULT_WAIT = 10;
        private boolean uiMode;

        protected WebDriver driver;

        private NavigationUiHelper navigationHelper;
        private SubscriberUiHelper subscriberHelper;

        public void init(String browser) throws IOException {
            uiMode = PropertyManager.from(MOBILE).getBoolean("ui.mode");

            if (uiMode) {
                switch (browser) {
                    case "chrome":
                        System.setProperty("webdriver.chrome.driver", PropertyManager.from(COMMON).getProperty("chrome.driver"));

                        // start the browser up
                        driver = new ChromeDriver();

                        break;

                    case "firefox":
                        System.setProperty("webdriver.gecko.driver", PropertyManager.from(COMMON).getProperty("firefox.driver"));
                        driver = new FirefoxDriver();
                        break;
                }

                driver.manage().timeouts().implicitlyWait(DEFAULT_WAIT, TimeUnit.SECONDS);
                //        driver.manage().window().maximize();
            }

            navigationHelper = new NavigationUiHelper(driver, PropertyManager.from(MOBILE).getProperty("baseurl"));
            navigationHelper.setUiMode(uiMode);

            subscriberHelper = new SubscriberUiHelper(driver);
            subscriberHelper.setUiMode(uiMode);
        }

        public void stop() {
            if (uiMode)
                driver.quit();
        }

        public NavigationUiHelper goTo() {
            return navigationHelper;
        }

        public SubscriberUiHelper subscriber() {
            return subscriberHelper;
        }

        public boolean ifOn() {
            return uiMode;
        }
    }

    public class RestManager {
        private boolean restMode;

        private SubscriberRestHelper subscriberHelper;

        public void init() {
            restMode = PropertyManager.from(MOBILE).getBoolean("rest.mode");
            subscriberHelper = new SubscriberRestHelper(PropertyManager.from(MOBILE).getProperty("baseurl"));
            subscriberHelper.setRestMode(restMode);
        }

        public SubscriberRestHelper subscriber() {
            return subscriberHelper;
        }
    }

    public class BdManager {
        private boolean bdMode;

        private SubscriberBdHelper subscriberHelper;

        public void init() {
            bdMode = PropertyManager.from(MOBILE).getBoolean("bd.mode");
            subscriberHelper = new SubscriberBdHelper(
                    PropertyManager.from(MOBILE).getProperty("jdbc.driver"),
                    PropertyManager.from(MOBILE).getProperty("jdbc.url")
                    );
            subscriberHelper.setBdMode(bdMode);
        }

        public SubscriberBdHelper subscriber() {
            return subscriberHelper;
        }
    }
}
