/* 크롬 드라이버의 생성 및 설치 경로 상수 저장하는 util 클래스  */
package KickIt.server.global.util;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.ObjectUtils;

import java.time.Duration;

public class WebDriverUtil {
    // Mac에서 실행 가능하도록 ID / 경로 삭제
    // web driver ID
    //public static final String WEB_DRIVER_ID = "webdriver.chrome.driver";
    // web driver의 경로
    //private static final String WEB_DRIVER_PATH = "src/main/java/KickIt/server/global/common/crawler/chromedriver.exe";

    // chrome driver 생성 함수
    public static WebDriver getChromeDriver(){
        // Mac에서 실행 가능하도록 ID / 경로 삭제
        /*
        if (ObjectUtils.isEmpty(System.getProperty(WEB_DRIVER_ID))){
            System.setProperty(WEB_DRIVER_ID, WEB_DRIVER_PATH);
        }
         */

        // webDriver 옵션 설정
        ChromeOptions chromeOptions = new ChromeOptions();

        //chromeOptions.addArguments("--headless=new");
        chromeOptions.addArguments("--lang=ko");
        chromeOptions.addArguments("--no-sandbox");
        chromeOptions.addArguments("--disable-dev-shm-usage");
        chromeOptions.addArguments("--disable-gpu");
        //chromeOptions.setCapability("ignoreProtectedModeSettings", true);

        // option 대로의 chrome driver 생성
        WebDriver driver = new ChromeDriver(chromeOptions);
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));

        return driver;
    }

    /*
    // Mac에서 실행 가능하도록 ID / 경로 삭제
    // WEB_DRIVER_PATH 상수에 크롬 드라이버 설치 경로 저장
    @Value("{resource['driver.chrome.driver_path']}")
    public void initDriver(String path){
        WEB_DRIVER_PATH = path;
    }

     */


    // 모든 탭 종료
    public static void quit(WebDriver driver){
        if(!ObjectUtils.isEmpty(driver)){
            driver.quit();
        }
    }

    // 현재 보고 있는 크롬 드라이버 다운로드하는 탭만 종료
    public static void close(WebDriver driver){
        if(!ObjectUtils.isEmpty(driver)){
            driver.close();
        }
    }
}
