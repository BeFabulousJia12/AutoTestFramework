package base;

import com.alibaba.fastjson.JSONObject;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;

/**
 * @Author You Jia
 * @Date 10/25/2017 11:49 AM
 */
public class FeedRate extends HttpServlet {
    private static final long serialVersionUID = -4012838481920999524L;
    private static String contentEncode="";
    private static String afterEncode="";
    //Start mongodb
    MongoClient mongoClient = MongoDBHandler.connectMongoDB("127.0.0.1",27017);
    DB db = mongoClient.getDB("schaefflerTest");
    private DBCollection dbCollection;
    /**
     * POST Request Processing
     */
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        BufferedReader bodyContent = request.getReader();
        String str, theWholeStr="";
        while((str = bodyContent.readLine())!=null)
        {
            theWholeStr +=str;
        }
        System.out.println("FeedRate requestMsg: " + theWholeStr);
        if (theWholeStr!=null && theWholeStr!=""){
        JSONObject jb = JSONObject.parseObject(theWholeStr);
        String mtId = jb.get("mtId").toString();
        Double feedRate = jb.getDouble("feedRate");
        long timeStamp = jb.getLongValue("timestamp");
        //Insert Actual Notify Data into MongoDB
        dbCollection = db.getCollection("postFeedRateActual");
        BasicDBObject doc = new BasicDBObject();
        doc.put("timestamp", mtId + timeStamp);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("mtId", mtId);
        jsonObject.put("feedRate", feedRate);
        jsonObject.put("timestamp", timeStamp);
        System.out.println(jsonObject.toJSONString());
        doc.put("actualContent", jsonObject);
        dbCollection.insert(doc);
        //String result = "{\"codeMsg\":\"Success\"}";
        String result = "Success";
        ResultToClient.printToJson(result, response);
    }else {
            System.out.println("FeedRate requestMsg: is null");
        }
    }

    /**
     * GET Request Processing
     */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String query = request.getParameter("query");
        String result = "welcome to my server. It's a GET request.";
        if (null != query && !query.trim().equals("")) {
            result = query + ", " + result;
            System.out.println(result);
        }
        ResultToClient.printToJson(result, response);
    }
}
