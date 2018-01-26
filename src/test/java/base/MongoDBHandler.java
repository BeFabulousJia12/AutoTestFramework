package base;

import com.alibaba.fastjson.JSONObject;
import com.mongodb.*;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @Author You Jia
 * @Date 10/19/2017 1:20 PM
 */
public class MongoDBHandler {
    public static MongoClient mongoclient = null;
    public String collectionName = "Collections";


    public static MongoClient connectMongoDB(String Address, int port) {
        try {
            mongoclient = new MongoClient(new ServerAddress(Address, port));

        } catch (MongoException e) {
            e.printStackTrace();
        }
        return mongoclient;
    }
    public static void Close(MongoClient mongoclient)
    {

        mongoclient.close();

    }
}
