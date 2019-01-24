package baiduSpider;

import com.lyc.spider.tools.DefaultHeaders;
import com.lyc.spider.tools.HttpURL;

import java.util.HashMap;
import java.util.Map;

public class GoogleResult {

    private static final String STANDARD_URL = "https://www.google.com/search?&q=";//谷歌搜索URL前缀
    private static final int MAX_PAGES = 100;//最大搜索页数
    private static final String LINK_REGEX = "";//TODO

    private String text;//搜索内容
    private String host;//代理host
    private int port;//代理port
    private int pages = 5;//搜索页数，默认为5
    private int threadNumber = 5;//爬取线程数，默认为5
    private int timeout = 15000;//超时设置，默认15秒

    private Map<String, String> headers = new HashMap<String, String>();//储存Http头部

    private HttpURL toUrls = new HttpURL();//待爬队列
    private HttpURL urls = new HttpURL();//储存结果

    private int overflowCounter = 0;

    /**
     * 一系列构造函数
     * @param text
     */
    public GoogleResult(String text){this.text = text;}

    public GoogleResult(String text, int pages){
        this.text = text;
        if(pages>MAX_PAGES){
            throw new IllegalStateException();
        }
        this.pages = pages;
    }

    public GoogleResult(String text, String host, int port){
        this.text = text;
        this.host = host;
        this.port = port;
    }

    /**
     * 设置头部
     * @param headers
     */
    public void setHeaders(Map<String,String> headers){
        this.headers=headers;
    }

    /**
     * 设置默认头部
     */
    public void setDefaultHeaders(){
        this.headers.putAll(DefaultHeaders.getHeaders());
    }

    /**
     * 设置随机头部
     */
    public void setRandomHeaders(){
        this.headers.putAll(DefaultHeaders.getRandomHeaders());
    }




}
