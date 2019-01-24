package baiduSpider;

import com.lyc.spider.slimapp.BaiduResult;
import com.lyc.spider.tools.HttpURL;

public class Demo {
    public static void main(String[] args){


        BaiduResult baidu = new BaiduResult("春晚");
        baidu.setDefaultHeader();
        baidu.setPages(80);
        baidu.setThread(50);
        //baidu.setProxy("127.0.0.1",1080);
        baidu.setTimeout(20000);
        baidu.setRetry(3);
        HttpURL urls = baidu.getUrls();
        for(String s: urls.getAllURL()){
            System.out.println(s);
        }
        /*
        try{
            Thread.currentThread().join();
        }catch (InterruptedException e){
            e.printStackTrace();
        }*/

    }
}
