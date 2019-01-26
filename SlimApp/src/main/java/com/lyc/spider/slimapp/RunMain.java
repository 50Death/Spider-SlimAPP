package com.lyc.spider.slimapp;

import com.lyc.spider.tools.HttpURL;
import com.lyc.spider.tools.WebPage;

import java.util.Scanner;

public class RunMain {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("　　　　◢████◤");
        System.out.println("　　⊙███████◤     欢迎使用本爬虫:)");
        System.out.println("　●████████◤      Welcome to ues");
        System.out.println("　　▼～◥███◤");
        System.out.println("　　 ▲▃▃◢███　 ●　　●　　●　　●　　●　　●　");
        System.out.println("　　　　　　███／█　／█　／█　／█　／█　／█　　　◢◤");
        System.out.println("　　　　　　█████████████████████████████████◤");

        System.out.println("\n\n1. Baidu\n2. Google");
        int option = Integer.parseInt(sc.nextLine());

        switch (option) {

            case 1:

                System.out.print("搜索内容: ");
                BaiduResult baidu = new BaiduResult(sc.nextLine());

                System.out.print("代理设置：(没有请输0)\nHost: ");
                String host = sc.nextLine();
                System.out.print("Port: ");
                int port = Integer.parseInt(sc.nextLine());
                if (!host.equals("0") && port != 0) {
                    baidu.setProxy(host, port);
                }

                System.out.print("请输入爬取搜索结果页数，最大100页:");
                baidu.setPages(Integer.parseInt(sc.nextLine()));

                System.out.print("请输入线程数(推荐小于等于页数): ");
                baidu.setThread(Integer.parseInt(sc.nextLine()));

                System.out.println("请输入连接超时时长(单位毫秒, 推荐1000): ");
                baidu.setTimeout(Integer.parseInt(sc.nextLine()));

                System.out.print("请输入超时重试次数(使用代理推荐为3以上): ");
                baidu.setRetry(Integer.parseInt(sc.nextLine()));

                System.out.println("\n0. 不使用Http头部\n1. 使用Google Chrome头部\n2. 使用随机头部\n(爬取失败请更换头部)");
                int mode = Integer.parseInt(sc.nextLine());
                if (mode == 1) {
                    baidu.setDefaultHeader();
                } else if (mode == 2) {
                    baidu.setRandomHeader();
                }
                System.err.println("请耐心等待，忽略报错...按回车键继续");
                sc.nextLine();
                HttpURL urls = baidu.getUrls();
                System.out.println("\n\n\n\n\n\n\n");
                System.err.println("以下是结果:)");
                for (String s : urls.getAllURL()) {
                    System.out.println(s);
                }

                break;
                //System.err.println("多谢使用");

            case 2:
                System.out.print("搜索内容: ");
                GoogleResult google = new GoogleResult(sc.nextLine());

                System.out.print("代理设置：(没有请输0)\nHost: ");
                String host2 = sc.nextLine();
                System.out.print("Port: ");
                int port2 = Integer.parseInt(sc.nextLine());
                if (!host2.equals("0") && port2 != 0) {
                    google.setProxy(host2, port2);
                }

                System.out.print("请输入爬取搜索结果页数，最大100页:");
                google.setPages(Integer.parseInt(sc.nextLine()));

                System.out.print("请输入线程数(推荐小于等于页数): ");
                google.setThread(Integer.parseInt(sc.nextLine()));

                System.out.println("请输入连接超时时长(单位毫秒, 推荐3000): ");
                google.setTimeout(Integer.parseInt(sc.nextLine()));

                System.out.print("请输入超时重试次数(使用代理推荐为3以上): ");
                google.setRetry(Integer.parseInt(sc.nextLine()));

                System.out.println("\n0. 不使用Http头部\n1. 使用Google Chrome头部\n2. 使用随机头部\n(爬取失败请更换头部)");
                int mode2 = Integer.parseInt(sc.nextLine());
                if (mode2 == 1) {
                    google.setDefaultHeaders();
                } else if (mode2 == 2) {
                    google.setRandomHeaders();
                }
                System.err.println("请耐心等待，忽略报错...按回车键继续");
                sc.nextLine();
                WebPage page = google.getWebPage();
                System.out.println("\n\n\n\n\n\n\n");
                System.err.println("以下是结果:)");
                for (String[] s : page.getAllPage()) {
                    System.out.println("[Title]\n"+s[0]);
                    System.out.println("[URL]\n"+s[1]);
                    System.out.println("[Summary]\n"+s[2]);
                    System.out.println("--------------------------------------------------------");
                }

                break;
        }

    }
}
