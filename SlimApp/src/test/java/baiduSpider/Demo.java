package baiduSpider;

import com.lyc.spider.slimapp.GoogleResult;
import com.lyc.spider.tools.HttpURL;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.Vector;

public class Demo {
    public static void main(String[] args) {

        Connection connection = Jsoup.connect("http://space.bilibili.com/");
        connection.timeout(10000);

        try {
            Document doc = connection.get();
            System.out.println(doc.title());

        }catch (IOException e){
            e.printStackTrace();
        }

        /*
        Document document = null;
        for(int i =0;i<3;i++) {
            try {
                System.setProperty("proxyHost", "127.0.0.1");
                System.setProperty("proxyPort", "1080");
                document = Jsoup.parse(new URL("https://www.google.com/").openStream(), "UTF-8", "https://www.google.com/");
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        System.out.println(document);
        */
        /*
        GoogleResult googleResult = new GoogleResult("USA");
        //googleResult.setDefaultHeaders();
        googleResult.setPages(90);
        googleResult.setThread(45);
        googleResult.setProxy("127.0.0.1",1080);
        googleResult.setRetry(10);
        googleResult.setTimeout(30000);

        HttpURL urls = googleResult.getUrls();
        for(String s:urls.getAllURL()){
            System.out.println(s);
        }

*/
    }
}
