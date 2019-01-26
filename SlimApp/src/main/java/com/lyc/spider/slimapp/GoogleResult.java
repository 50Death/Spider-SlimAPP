package com.lyc.spider.slimapp;

import com.lyc.spider.tools.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.Map;

public class GoogleResult {

    private static final String STANDARD_URL = "https://www.google.com/search?&q=";//谷歌搜索URL前缀
    private static final int MAX_PAGES = 100;//最大搜索页数

    private String text;//搜索内容
    private String host;//代理host
    private int port;//代理port
    private int pages = 5;//搜索页数，默认为5
    private int threadNumber = 5;//爬取线程数，默认为5
    private int timeout = 15000;//超时设置，默认15秒
    private int retry = 0;//重试次数，因为谷歌需要翻墙，推荐为1次以上，默认不重试

    private Map<String, String> headers = new HashMap<String, String>();//储存Http头部

    private HttpURL toUrls = new HttpURL();//待爬队列
    private WebPage resPage = new WebPage();//储存结果
    private int pageCounter = 1;

    private int nullPointerCounter = 0;//用来记录出现了多少次什么也没爬到（临近结束时的脏读也会添加这个），用来避免爬到最大页数之外

    /**
     * 一系列构造函数
     *
     * @param text
     */
    public GoogleResult(String text) {
        this.text = text;
    }

    public GoogleResult(String text, int pages) {
        this.text = text;
        if (pages > MAX_PAGES) {
            throw new IllegalStateException();
        }
        this.pages = pages;
    }

    public GoogleResult(String text, String host, int port) {
        this.text = text;
        this.host = host;
        this.port = port;
    }

    /**
     * 设置头部
     *
     * @param headers
     */
    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    /**
     * 设置默认头部, 慎用！！！可能导致接收到的HTML乱码
     */
    public void setDefaultHeaders() {
        this.headers.putAll(DefaultHeaders.getHeaders());
    }

    /**
     * 设置随机头部
     */
    public void setRandomHeaders() {
        this.headers.putAll(DefaultHeaders.getRandomHeaders());
    }

    /**
     * 设置代理
     *
     * @param host 代理IP
     * @param port
     */
    public void setProxy(String host, int port) {
        this.host = host;
        this.port = port;
    }

    /**
     * 设置爬取页数
     *
     * @param pages
     */
    public void setPages(int pages) {
        this.pages = pages;
    }

    /**
     * 设置线程数
     *
     * @param thread
     */
    public void setThread(int thread) {
        this.threadNumber = thread;
    }

    /**
     * 设置连接失败重复次数
     *
     * @param retry
     */
    public void setRetry(int retry) {
        this.retry = retry;
    }

    /**
     * 设置超时时长
     *
     * @param timeout
     */
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    /**
     * 得到页面摘要
     *
     * @return WebPage类，存放网页摘要
     */
    private boolean getWebPageDone = false;//记录getWebPage是否被执行过

    public WebPage getWebPage() {
        //判断页数是否比线程数少，防止线程过多
        if (pages < threadNumber) {
            threadNumber = pages;
        }

        //初始化待爬队列 谷歌页面以start=?0 来区分页码
        for (int i = 0; i < pages * 10; i += 10) {
            String link = STANDARD_URL + this.text + "&start=" + i + "&filter=0";//filter=0用来取消谷歌24页限制
            toUrls.addURL(link);
        }

        //开启爬取线程
        Thread[] thread = new Thread[threadNumber];
        for (int i = 0; i < threadNumber; i++) {
            thread[i] = new Thread(new GoogleResultScanner());
            thread[i].start();
        }

        //等待爬虫运行结束
        for (int i = 0; i < threadNumber; i++) {
            try {
                thread[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //返回结果
        getWebPageDone = true;
        return resPage;
    }

    /**
     * 得到所有链接
     *
     * @return HttpURL
     */
    public HttpURL getUrls() {
        if (!getWebPageDone) {
            getWebPage();
        }
        HttpURL urls = new HttpURL();
        for (String[] s : resPage.getAllPage()) {
            urls.addURL(s[1]);
        }


        return urls;
    }


    /**
     * 爬虫主线程
     */
    class GoogleResultScanner implements Runnable {
        public void run() {
            while (toUrls.getSize() != 0) {
                try {

                    //创建得到页面实例，得到完整页面
                    GetURLPage urlPage = new GetURLPage(toUrls.popURL(), headers);
                    //设定超时时长
                    urlPage.setTimeout(timeout);
                    //判断是否使用代理, 有则设定
                    if (host != null) {
                        urlPage.setProxy(host, port);
                    }
                    //设定重连次数
                    urlPage.setRetry(retry);
                    //得到返回的页面
                    Document document = urlPage.getPage();

                    //对页面内容进行筛选
                    Elements elements = document.select("div[class=g]");//每个<div class="g"></div> 里存着一个搜索结果

                    for (Element e : elements) {
                        /**
                         * 下面的筛选逻辑设定自2019.Jan.24，谷歌可能会在以后更新中改变逻辑
                         * The following strategy is based on the rules at 2019.Jan.24. Google may update this in the future
                         */
                        String title = e.select("h3[class=LC20lb]").text();
                        String url = e.select("a[href]").attr("abs:href");
                        String summary = e.select("span[class=st]").text();

                        resPage.addPage(title, url, summary);
                        //System.out.println(title);
                    }

                    //判断是否爬到最大页数之外
                    if (nullPointerCounter > pages) {
                        break;
                    }

                    System.out.println(pageCounter);
                    pageCounter++;
                } catch (NullPointerException e) {
                    e.printStackTrace();
                    nullPointerCounter++;
                }
            }
        }
    }
}
