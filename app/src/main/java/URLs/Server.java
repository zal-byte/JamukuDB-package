package URLs;

import android.os.Environment;
import android.widget.Toast;

import com.qiva.jamuku.MainActivity;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class Server {
    public String url;
    public String sign_ui;
    public String img_product;
    public String product_request;
    public String profile_request;
    public String img_profile;

    public Server(){
        try{
            this.url = getFromFile();
        }catch(Exception e){
            e.printStackTrace();
        }
        if(url != null){
            this.sign_ui = url+"/index.php";
            this.img_product = url+"/lib/img/produk/";
            this.profile_request = url+"/";
            this.img_profile = url+"/lib/img/profilepengguna/";
            this.product_request = url+"/index.php";
        }else{
            //Default url
            this.url = "http://192.168.173.1";
        }
    }
    String getFromFile() throws IOException {
        String finalRes = "";
        String path = Environment.getExternalStorageDirectory().getPath().toString()+ "/Jamuku/";
        File file = new File(path);
        if(!file.exists()){
            file.mkdir();
        }
        String filename = "config.txt";
        try{
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(path+filename));
            byte data;
            while((data = (byte) bis.read()) != -1){
                finalRes += (char) data;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return finalRes;
    }
}
