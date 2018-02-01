package testcase;

import base.PostResult2File;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.*;

import java.io.IOException;
import java.util.List;

/**
 * @Author You Jia
 * @Date 1/18/2018 1:22 PM
 */
public class WedDriverTest {


    private WebDriver webDriver = null;
    private String htmlFile = "src/test/resources/Html.html";
    ChromeOptions chromeOptions;
    @BeforeGroups(groups={"WebUI"})
    public void setupUI() throws IOException {
	//for windows platform
        System.setProperty("webdriver.chrome.driver", "src/test/resources/chromedriver.exe");
	//for linux platform
	// System.setProperty("webdriver.chrome.driver", "src/test/resources/chromedriver");
       
	//System.setProperty("webdriver.chrome.driver", "/usr/dev/selenium/chromedriver");
	chromeOptions = new ChromeOptions();
	//for windows platform
        chromeOptions.setBinary("C:\\Program Files (x86)\\Google\\Chrome\\Application\\chrome.exe");
        //for linux platform
	//chromeOptions.setBinary("/usr/bin/google-chrome");
        chromeOptions.addArguments();
        webDriver = new ChromeDriver(chromeOptions);
        }
    @BeforeGroups(groups={"ChromeHeadless"})
    public void setupChromeHeadless() throws IOException {

        //for windows platform
        System.setProperty("webdriver.chrome.driver", "src/test/resources/chromedriver.exe");
	//for linux platform
	//System.setProperty("webdriver.chrome.driver", "src/test/resources/chromedriver");
        //System.setProperty("webdriver.chrome.driver", "/usr/dev/selenium/chromedriver");
	chromeOptions = new ChromeOptions();
	//for windows platform
        chromeOptions.setBinary("C:\\Program Files (x86)\\Google\\Chrome\\Application\\chrome.exe");
        //for linux platform
        // chromeOptions.setBinary("/usr/bin/google-chrome");
        chromeOptions.addArguments("--headless");
        webDriver = new ChromeDriver(chromeOptions);
    }
    @Test(groups={"WebUI"})
    public void WebUITest() throws InterruptedException, IOException {

        webDriver.get("https://sag-chen0.appsdev.mindsphere.io/apps/sewc-ui/");
        WebDriverWait wait = new WebDriverWait(webDriver, 60);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("logOnFormSubmit")));
        wait.until(ExpectedConditions.elementToBeClickable(By.id("logOnFormSubmit")));
        WebElement userName = webDriver.findElement(By.id("j_username"));
        WebElement passWord = webDriver.findElement(By.id("j_password"));
        WebElement submit = webDriver.findElement(By.id("logOnFormSubmit"));
        userName.sendKeys("nanzhang@siemens.com");
        passWord.sendKeys("Lsff0uosm$");
        submit.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//select[@id='indicatorSelect']/option")));
        wait.until(ExpectedConditions.elementToBeClickable(By.id("indicatorSelect")));
        List<WebElement> indicators = webDriver.findElements(By.xpath("//select[@id='indicatorSelect']/option"));
        for(WebElement indicator: indicators){
            if (indicator.getText().equals("TEEP")){
                indicator.click();
                break;
            }
        }
        WebElement daily = webDriver.findElement(By.xpath("//content/div/div/div/div[2]/div/div[1]/div[1]/label[4]"));
        daily.click();
        WebElement go = webDriver.findElement(By.xpath("//*[@id='buttonGroup']/button[2]"));
        go.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("svg > g.highcharts-data-labels.highcharts-series-6.highcharts-column-series.highcharts-tracker > g:nth-child(9) > text > tspan.highcharts-text-outline")));
        WebElement Jan6 = webDriver.findElement(By.cssSelector("svg > g.highcharts-data-labels.highcharts-series-6.highcharts-column-series.highcharts-tracker > g:nth-child(9) > text > tspan.highcharts-text-outline"));

        Assert.assertEquals(Jan6.getText(), "18");


    }

    @Test(groups={"ChromeHeadless"})
    public void ChromeHeadlessTest() throws InterruptedException, IOException {


        webDriver.get("https://sag-chen0.appsdev.mindsphere.io/apps/sewc-ui/");
        WebDriverWait wait = new WebDriverWait(webDriver, 60);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("logOnFormSubmit")));
        wait.until(ExpectedConditions.elementToBeClickable(By.id("logOnFormSubmit")));
        WebElement userName = webDriver.findElement(By.id("j_username"));
        WebElement passWord = webDriver.findElement(By.id("j_password"));
        WebElement submit = webDriver.findElement(By.id("logOnFormSubmit"));
        userName.sendKeys("nanzhang@siemens.com");
        passWord.sendKeys("Lsff0uosm$");
        submit.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//select[@id='indicatorSelect']/option")));
        wait.until(ExpectedConditions.elementToBeClickable(By.id("indicatorSelect")));
        List<WebElement> indicators = webDriver.findElements(By.xpath("//select[@id='indicatorSelect']/option"));
        for (WebElement indicator : indicators) {
            if (indicator.getText().equals("TEEP")) {
                indicator.click();
                System.out.println("indicator Text: " + indicator.getText());
                break;
            }
        }
        WebElement daily = webDriver.findElement(By.xpath("//content/div/div/div/div[2]/div/div[1]/div[1]/label[4]"));
        System.out.println("daily label: " + daily.getText());
        daily.click();
        WebElement go = webDriver.findElement(By.xpath("//*[@id='buttonGroup']/button[2]"));
        System.out.println("button go: " + go.getText());
        go.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("svg > g.highcharts-data-labels.highcharts-series-2.highcharts-column-series.highcharts-tracker > g:nth-child(9) > text > tspan.highcharts-text-outline")));
        PostResult2File.writedata(webDriver.getPageSource().toString(), htmlFile);
        WebElement Jan6 = webDriver.findElement(By.cssSelector("svg > g.highcharts-data-labels.highcharts-series-2.highcharts-column-series.highcharts-tracker > g:nth-child(9) > text > tspan.highcharts-text-outline"));

        Assert.assertEquals(Jan6.getText(), "4");
    }

    @AfterGroups(groups={"WebUI"})
    public void teardownAfterMethod(){
            webDriver.close();
            webDriver.quit();
    }

}
