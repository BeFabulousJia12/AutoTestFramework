package base;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;

import org.eclipse.jetty.client.util.BytesContentProvider;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.util.ssl.SslContextFactory;

import java.util.HashMap;


public class HTTPClientRequest {



    public static HashMap RequestToPlatform(String method, String requestBody, String URL) throws Exception{

        String token = PostResult2File.ReadFile("src/test/resources/Readtoken.dat");
        String[] tokenValue = token.split(",");
        HashMap<String,Object> responseMap = new HashMap<>();
        SslContextFactory sslContextFactory = new SslContextFactory();
        sslContextFactory.setTrustAll(false);
        System.setProperty("javax.net.ssl.trustStore","src/test/resources/mg.store");
        HttpClient httpclient = new HttpClient(sslContextFactory);
        //httpclient.setFollowRedirects(false);
        httpclient.setRequestBufferSize(2*4096);
        httpclient.start();
        ContentResponse response = null;
        System.out.println("====Sending Http Request====");
        //add token into header
        //exchange.addRequestHeader("Authorization",tokenValue[1]);
        if (method.toLowerCase().equals("post") || method.toLowerCase().equals("put")){
            response = httpclient.newRequest(URL).header("Authorization",tokenValue[1]).method(HttpMethod.POST).content(new BytesContentProvider(requestBody.getBytes("UTF-8")),"application/json;charset=UTF-8").send();
            //response = httpclient.newRequest(URL).method(HttpMethod.POST).content(new BytesContentProvider(requestBody.getBytes("UTF-8")),"application/json;charset=UTF-8").send();
            System.out.println(response.getHeaders());

        }
        if (method.toLowerCase().equals("get")){
            response = httpclient.newRequest(URL).header("Authorization",tokenValue[1]).method(HttpMethod.GET).send();
        }
        if(response!=null){
            responseMap.put("ResponseStatus",response.getStatus());
            responseMap.put("ResponseBody", response.getContentAsString());
        }

        return responseMap;
    }

}
