package RequestHandler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import static java.net.HttpURLConnection.HTTP_OK;

public class RequestHandler {

    public RequestHandler(){

    }
    public String GET(String url) throws IOException {
        String res = "";
        URL url1 = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) url1.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(27000);
        connection.setReadTimeout(27000);

        int code = connection.getResponseCode();
        if( code == HTTP_OK){
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while((line = br.readLine()) != null){
                res += line;
            }
            br.close();
        }else{
            res = "";
        }

        return res;
    }
    public String POST(String url, HashMap<String, String> param) throws IOException {
        String res = "";
        URL url1 = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) url1.openConnection();
        connection.setRequestMethod("POST");
        connection.addRequestProperty("Content-Type","application/x-www-form-urlencoded; charset=UTF-8");
        connection.addRequestProperty("Accept","*/*");
        connection.setReadTimeout(27000);
        connection.setConnectTimeout(27000);
        connection.setDoOutput(true);
        connection.setDoInput(true);

        OutputStream os = connection.getOutputStream();
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
        bw.write(getParam(param));
        bw.flush();
        bw.close();
        os.close();

        if( connection.getResponseCode() == HTTP_OK ){
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while((line = br.readLine()) != null){
                res += line;
            }
        }else{
            res = "";
        }
        return res;
    }
    public String getParam(HashMap<String, String> param){
        StringBuilder sb = new StringBuilder();
        boolean status = true;
        try{
            for(Map.Entry<String, String> map : param.entrySet()){
                if(status)
                    status = false;
                else
                    sb.append("&");
                sb.append(URLEncoder.encode(map.getKey(), "UTF-8"));
                sb.append("=");
                sb.append(URLEncoder.encode(map.getValue(), "UTF-8"));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return sb.toString();
    }
}
