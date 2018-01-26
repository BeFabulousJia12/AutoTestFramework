package testcase;

import base.*;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlLabel;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.mongodb.*;
import com.sun.org.apache.xpath.internal.operations.Bool;
import com.sun.org.apache.xpath.internal.operations.Variable;
import mapper.OEEMapper;
import mapper.UserMapper;
import model.OEEInfo;
import model.TestInfo;
import model.UserInfo;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.*;
import org.testng.collections.CollectionUtils;


import java.io.*;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;


public class HTTPTest {
    public int responseStatus = 0;
    public String responseBody = "";

    private String userDir = System.getProperty("user.dir");


    String filePath = "";

    XSSFWorkbook wb = null;

    XSSFSheet inputSheet = null;
    private DataReader myInputData;

    XSSFSheet outputSheet = null;

    XSSFSheet comparisonSheet = null;

    XSSFSheet resultSheet = null;


    private double totalcase = 0;

    private double failedcase = 0;

    private String startTime = "";

    private String endTime = "";

    private Reader reader;
    private static SqlSessionFactory sqlSessionFactory;
    private DB db;


    private WebClient webClient = null;
    private WebDriver webDriver = null;
    private String failedFile = "src/test/resources/FailedCase.dat";
    private String tokenFile = "src/test/resources/Readtoken.dat";
    private MongoClient mongoClient;
    private String mockDataServerPath;


    public class HttpThread extends Thread {
        public void run() {
            //Start Http Server
            HTTPTestServer.HTTPServerToProcessMsg();
        }

    }


    @BeforeClass
    public void setup() throws IOException {

        try {
            PostResult2File.flush(failedFile);
            PostResult2File.flush(tokenFile);
            //Start http Server
            HttpThread httpThread = new HttpThread();
            httpThread.start();
            //Start MockDataServer
            System.out.println("start");
            System.out.println(userDir);
            mockDataServerPath = userDir + "\\MockDataServer\\";
            //Run python script on windows platform
            String command = "cmd /c start " + mockDataServerPath + "mock_data_server.py";
            System.out.println(command);
            Runtime.getRuntime().exec(command, null, new File(mockDataServerPath));
            Thread.sleep(5000);
            //Start Timer to get Token from Mindsphere
//            command = "cmd /c start java -jar " + userDir + "\\src\\test\\resources\\" + "ConvertTime-1.0-1.0-SNAPSHOT.jar";
//            System.out.println(command);
//            Runtime.getRuntime().exec(command, null, new File("src/test/resources/"));
//            Thread.sleep(60000);
            //Start sql
            //reader = Resources.getResourceAsReader("ConfigurationPostgre.xml");
            reader = Resources.getResourceAsReader("ConfigurationOracle.xml");
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
            //Start mongodb
            mongoClient = MongoDBHandler.connectMongoDB("127.0.0.1", 27017);
            webClient = new WebClient();
            //Get Token
            getToken();

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("start successfully!");

    }
    @BeforeMethod
    public void beforeMethod(){

    }

    @BeforeTest
    @Parameters("WorkBook")
    public void beforeTest(String path) {

        filePath = path;

        try {
            wb = new XSSFWorkbook(new FileInputStream(filePath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        inputSheet = wb.getSheet("Input");
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        startTime = sf.format(new Date());

    }


    @DataProvider(name = "HttpData")
    protected Iterator<Object[]> testProvider(ITestContext context) {
        List<Object[]> test_IDs = new ArrayList<Object[]>();
        myInputData = new DataReader(inputSheet, true, true, 0);
        // sort map in order so that test cases ran in a fixed order
        Map<String, RecordHandler> sortmap = new TreeMap<String, RecordHandler>(new Comparator<String>() {
            public int compare(String key1, String key2) {
                return key1.compareTo(key2);
            }
        });

        sortmap.putAll(myInputData.get_map());

        for (Map.Entry<String, RecordHandler> entry : sortmap.entrySet()) {
            String test_ID = entry.getKey();
            String test_case = entry.getValue().get("TestCase");
            if (!test_ID.equals("") && !test_case.equals("")) {
                test_IDs.add(new Object[]{test_ID, test_case});
            }
            totalcase++;
        }
        return test_IDs.iterator();
    }

    @Test(dataProvider = "HttpData", description = "ReqGenTest")
    public void RestfulAPITest(String ID, String testCase) {
        HashMap<String, Object> responseMap;
        String requestMethod = myInputData.get_record(ID).get("Method");
        String requestBody = myInputData.get_record(ID).get("Body");
        String URI = myInputData.get_record(ID).get("URL");
        String expectedRespMsg = myInputData.get_record(ID).get("Response");
        if (requestMethod != null && URI != null && expectedRespMsg != null) {
            try {
                responseMap = HTTPClientRequest.RequestToPlatform(requestMethod, requestBody != null ? requestBody : "", URI);
                responseStatus = (Integer) responseMap.get("ResponseStatus");
                responseBody = (String) responseMap.get("ResponseBody");
                if (responseStatus == 200) {
                    if (!responseBody.contains(expectedRespMsg)) {
                        PostResult2File.writedata(responseStatus + ", " + ID + ", " + testCase + ", " + "Failed, " + "Actual Result: " + responseBody + "\r\n", failedFile);
                        Assert.assertEquals(responseBody.contains(expectedRespMsg),"true");
                    }

                } else {
                    //record failed test case
                    PostResult2File.writedata(responseStatus + ", " + ID + ", " + testCase + ", " + "Failed" + "\r\n", failedFile);
                    Assert.fail("Test Case Failed due to: " + responseStatus);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Test
    public void OracleQueryOEEInfo() {
        SqlSession session = sqlSessionFactory.openSession();
        OEEMapper mapper = session.getMapper(OEEMapper.class);
        List<OEEInfo> oeeInfoList = mapper.getOEEInfo("01-JAN-18 07.30.00.000000 PM","02-JAN-18 07.30.00.000000 AM");
        for (OEEInfo oeeInfo : oeeInfoList) {
            System.out.println("Get Data from Oracle, " + "ShiftName: " + oeeInfo.getShiftName());
            System.out.println("Get Data from Oracle, " + "RUNNING: " + oeeInfo.getRunning());
            Assert.assertEquals(oeeInfo.getShiftName(), "B");
            Assert.assertEquals(oeeInfo.getRunning(), 42540);
        }

    }
    @Test
    public void mongodbFindOneTest() {
        DB db = mongoClient.getDB("test");
        DBCollection dbCollection = db.getCollection("testjia");
        BasicDBObject queryObject = new BasicDBObject("name", "88").append("date", "jia");
        DBObject obj = dbCollection.findOne(queryObject);

        Assert.assertEquals(obj.get("name"), "88");
    }

    @Test
    public void mongodbFindMultiTest() {
        DB db = mongoClient.getDB("test");
        DBCollection dbCollection = db.getCollection("testjia");
        BasicDBObject queryObject = new BasicDBObject("name", "88");
        List<DBObject> result = dbCollection.find(queryObject).toArray();
        for (DBObject temp : result) {
            Assert.assertEquals(temp.get("date"), "jia");
        }
    }

    @Test
    public void POSTGREQueryTestInfo() {
        SqlSession session = sqlSessionFactory.openSession();
        UserMapper mapper = session.getMapper(UserMapper.class);
        List<UserInfo> infolist = mapper.getTestInfo("poland", 4);
        for (UserInfo info : infolist) {
            System.out.println("Get Data from postgreSql " + info.getScore() + ",id is " + info.getId());
            Assert.assertEquals(infolist.size(), 3);
        }

    }

    @AfterClass
    public void teardown() throws Exception {

        System.out.println("stop");
        //Runtime.getRuntime().exec("taskkill /F /IM python.exe");

    }

    //Get Token before Auto Test.
    private void getToken() throws IOException, InterruptedException {
        while(true){
            webClient.getOptions().setThrowExceptionOnScriptError(false);
            webClient.getOptions().setCssEnabled(false);
            webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
            HtmlPage htmlPage = webClient.getPage("https://sag-chen0.accounts.ondemand.com");
            if (htmlPage != null) {
                HtmlElement name = (HtmlElement) htmlPage.getElementById("j_username");
                HtmlElement password = (HtmlElement) htmlPage.getElementById("j_password");
                HtmlElement button = (HtmlElement) htmlPage.getElementById("logOnFormSubmit");
                if (name != null && password != null && button != null) {
                    name.type("nanzhang@siemens.com");
                    password.type("Lsff0uosm$");
                    HtmlPage page = button.click();
                    Thread.sleep(10000);
                }
                HtmlPage tokenPage = webClient.getPage("https://sag-chen0.appsdev.mindsphere.io/apps/schaeffler/getToken");
                Thread.sleep(5000);
                if(tokenPage!= null && tokenPage.getBody()!=null){
                    PostResult2File.flush(tokenFile);
                    String token = tokenPage.getBody().asText();
                    System.out.println("token: " + token);
                    PostResult2File.writedata("Token, " + token, tokenFile);
                    break;
                }
            }
    }
    }
    //UTC Time Format Convert to TimeStamp
    public long timeStampChange(String UTCTime) {

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        long unixTimeStamp = 0;
        try {
            Date date = df.parse(UTCTime);
            unixTimeStamp = date.getTime()/1000; //unit: second
            System.out.println(unixTimeStamp);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return unixTimeStamp;
    }


    @Test
    public void timeStampChangeTest() {

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        SimpleDateFormat sdf1 = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long unixTimeStamp = 0;
        Date now=new Date();
        String newformat = df.format(now);
        System.out.println(newformat);
        try {
//            String dfString = df.parse("2017-10-09T02:07:18.102Z").toString();
//            //String dfString = df.parse(UTCTime).toString();
//            System.out.println(dfString);
//            String sdf2String = sdf2.format(sdf1.parse(dfString));
//            Date date = sdf2.parse(sdf2String);
//            unixTimeStamp = date.getTime();
//            System.out.println(unixTimeStamp);
            Date date = df.parse("2017-11-06T09:02:29.870Z");
            System.out.print(date.toString());
            System.out.println(date.getTime());
            System.out.println(date.getTime()/1000);

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


}

