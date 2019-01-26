package com.lyc.spider.slimapp;

import com.lyc.spider.tools.DefaultHeaders;
import com.lyc.spider.tools.GetURLPage;
import com.lyc.spider.tools.HttpURL;
import com.lyc.spider.tools.URLFetch;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;


/**
 * 获取百度搜索结果的网址
 * 百度会将搜索结果以自己的网址写进html，需要进行二次访问让其跳转得到最终网址
 * TODO 爬取更丰富的搜索结果而不仅仅是网址（正则/cssQuery筛选）
 */
public class BaiduResult {

    private static final String STANDARD_URL = "https://www.baidu.com/s?ie=UTF-8&wd=";//百度搜索url前缀
    private static final int MAX_PAGES = 100;//最大搜索页数
    private static final String LINK_REGEX = "www.baidu.com/link?url=";

    private String text;//搜索内容
    private int pages = 5;//搜索页数，默认为5
    private int threadNumber = 5;//爬取线程数，默认为5
    private int timeout = 15000;//超时设置，默认15秒

    private String host;//设置代理服务器
    private int port;//设置代理端口
    private int retry = 0;//设置重连次数

    private Map<String, String> headers = new HashMap<String, String>();//储存Http头部

    private HttpURL toUrls = new HttpURL();//待爬队列
    private HttpURL middleURLs = new HttpURL();//中间队列
    private HttpURL urls = new HttpURL();//储存结果

    private int overflowCounter = 0;//重复记录器

    /**
     * 构造函数
     *
     * @param text 搜索内容
     */
    public BaiduResult(String text) {
        this.text = text;
    }

    /**
     * 构造函数
     *
     * @param text  搜索内容
     * @param pages 搜索页数
     */
    public BaiduResult(String text, int pages) {
        this.text = text;
        if (pages > MAX_PAGES) {
            throw new IllegalStateException();
        }
        this.pages = pages;
    }

    /**
     * 设置HTTP头部
     *
     * @param headers
     */
    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    /**
     * 快捷设置默认头部（Google Chrome）
     */
    public void setDefaultHeader() {
        this.headers.putAll(DefaultHeaders.getHeaders());
    }

    /**
     * 设置随机头部
     */
    public void setRandomHeader() {
        this.headers.putAll(DefaultHeaders.getRandomHeaders());
    }

    /**
     * 设置线程个数
     *
     * @param number
     */
    public void setThread(int number) {
        this.threadNumber = number;
    }

    /**
     * 设置结果页个数
     *
     * @param pages
     */
    public void setPages(int pages) {
        this.pages = pages;
    }

    /**
     * 设置超时时间
     *
     * @param timeout
     */
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    /**
     * 设置代理
     *
     * @param host 代理服务器地址
     * @param port 代理端口号
     */
    public void setProxy(String host, int port) {
        this.host = host;
        this.port = port;
    }

    /**
     * 设置失败重连次数
     *
     * @param retry
     */
    public void setRetry(int retry) {
        this.retry = retry;
    }

    /**
     * 获得结果
     * 目前已知百度搜索结果页不会超过100，所以设定最大爬取100页，如果遇到和第一页相同则停止爬取
     *
     * @return
     */
    public HttpURL getUrls() {
        //判断页数是否比线程数少，避免线程过多
        if (pages < threadNumber) {
            threadNumber = pages;
        }

        //初始化待爬序列 百度页面以pn=?0来区分，00第一页，10第二页...
        for (int i = 0; i < pages * 10; i += 10) {
            String link = STANDARD_URL + this.text + "&pn=" + i;
            toUrls.addURL(link);
        }

        //开启爬虫线程1
        Thread[] t1 = new Thread[threadNumber];
        for (int i = 0; i < threadNumber; i++) {
            t1[i] = new Thread(new BaiduResultScanner1());
            t1[i].start();
        }

        //等待第一个启动的线程爬完在开始下一步
        try {
            t1[0].join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //开启爬虫线程2
        Thread[] t2 = new Thread[threadNumber];
        for (int i = 0; i < threadNumber; i++) {
            t2[i] = new Thread(new BaiduResultScanner2());
            t2[i].start();
        }


        //等待爬虫执行完毕
        try {
            for (int i = 0; i < threadNumber; i++) {
                t2[i].join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return urls;
    }

    /**
     * 这个线程用来爬取搜索结果上所有的中间网址
     */
    private class BaiduResultScanner1 implements Runnable {
        public void run() {
            while (toUrls.getSize() != 0) {
                //创建得到页面实例
                URLFetch urlFetch = new URLFetch(toUrls.popURL(), headers);
                //设定返回连接类型
                urlFetch.setMode(URLFetch.Modes.links);
                //设定超时时长
                urlFetch.setTimeout(timeout);
                //判断是否使用代理
                if (host != null) {
                    urlFetch.setProxy(host,port);
                }
                //设置重连次数
                urlFetch.setRetry(retry);
                //获得返回的链接
                urlFetch.getUrlsVec();
                Vector<String> tempURL = urlFetch.getUrlsVec();//注意这里得到的URL不全是搜索结果,需要进一步筛选

                //判断是不是 “很抱歉，没有找到与xxx相关的网页”
                if (tempURL.contains("http://help.baidu.com/newadd?prod_id=1&category=4")) {
                    System.err.println("很抱歉，没有找到与\"" + text + "\"相关的网页");
                    break;
                }

                //去重
                Vector<String> middleURL = URLFetch.removeDuplicates(tempURL);

                //对链接进行过滤,然后储存
                for (String s : middleURL) {
                    if (s.contains(LINK_REGEX)) {

                        //判断该网址是不是被爬过了
                        if (middleURLs.getAllURL().contains(s)) {
                            overflowCounter++;//爬到重复计数器+1
                        } else {
                            System.err.print("FIRST");
                            System.out.println(s);
                            middleURLs.addURL(s);
                        }
                    }
                }

                //当爬到30个重复网站时退出
                if (overflowCounter > pages) {
                    break;
                }
            }//队列为空时直接结束

        }
    }

    /**
     * 这个线程用来获取中间网址经过跳转后的实际网址，需要打开中间网址让其跳转
     * 然后分析html内容
     */
    private class BaiduResultScanner2 implements Runnable {
        public void run() {
            while (middleURLs.getSize() != 0) {
                String finalURL = null;

                //TODO 此处存在脏读，但不影响结果 -> while判断和下面的popURL

                //失败重连循环
                for (int i = -1; i < retry; i++) {
                    try {
                        //创建链接实例，设置Jsoup链接的url,超时时长,头部
                        Connection connection = Jsoup.connect(middleURLs.popURL()).timeout(timeout).headers(headers);

                        //判断是否使用代理
                        if (host != null) {
                            connection.proxy(host, port).ignoreHttpErrors(true);
                        }
                        //链接，接收返回
                        Connection.Response response = connection.method(Connection.Method.GET).execute();

                        //提取返回地址URL
                        finalURL = response.url().toString();

                        //得到URL后退出重连循环
                        break;
                    } catch (IOException e) {
                        e.printStackTrace();
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e1) {
                            e.printStackTrace();
                        }
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }

                //添加进结果
                System.err.print("FINAL");

                if (finalURL == null) {
                    continue;
                }
                System.out.println(finalURL);
                urls.addURL(finalURL);

            }
        }
    }

    /**
     * 对1的更正, 百度搜索页HTML结构如下：
     * 每一个c-container
     * 有图的<h3>summary写在
     *
     * TODO 因为百度写的太乱了而暂时搁置
     * ps: 比谷歌乱太多了...
     */
    private class BaiduResultScanner3 implements Runnable{
        public void run() {
            //得到页面
            GetURLPage urlPage = new GetURLPage(toUrls.popURL(),headers);

            urlPage.setRetry(retry);

            urlPage.setProxy(host,port);

            urlPage.setTimeout(timeout);

            Document document = urlPage.getPage();

            //获取每个搜索结果
            Elements container = document.select("div[class~=.*c-container.*]");

            /**
             * TODO 进行筛选
             * 百度将搜索结果至少分为3类： 百度百科、 XXX的最新相关信息、 视频推荐
             * 每一种里面的构造都不尽相同，需要分开筛选
             * 链接和标题都在<h3></h3>里，但是摘要位置都不相同，存在不同位置标签相同的情况，三个类型必须完全分开筛选！
             */
            for(Element con: container){
                //处理h3
                Element h3 = con.selectFirst("h3");
                String url = h3.select("a[href]").attr("abs:href");
                String title = h3.select("a").text();

                //TODO
                //TODO
                //TODO
                //may not do forever....

            }
        }
    }

}
