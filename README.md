# Spider-SlimAPP

SlimAPP 是我个人制作的一些爬虫小工具

基于[jsoup](https://mvnrepository.com/artifact/org.jsoup/jsoup/1.11.3)[(GitHub)](https://github.com/jhy/jsoup) 和我另一个项目[Spider-tools](https://github.com/50Death/Spider-Tools) 开发而成

## 工具列表
1. [百度搜索结果爬虫](https://github.com/50Death/Spider-SlimAPP/blob/master/SlimApp/src/main/java/com/lyc/spider/slimapp/BaiduResult.java)
2. [谷歌搜索结果爬虫](https://github.com/50Death/Spider-SlimAPP/blob/master/SlimApp/src/main/java/com/lyc/spider/slimapp/GoogleResult.java)
3. To Publish: 知乎问题推荐爬虫
4. TODOs: Need Advice

## 使用方式

### 开发环境
java version "1.8.0_191"

Java(TM) SE Runtime Environment (build 1.8.0_191-b12)

Java HotSpot(TM) 64-Bit Server VM (build 25.191-b12, mixed mode)

### IDE
IntelliJ IDEA

## 免责声明
本仓库所涉及一切程序, 代码等元素均为学习所用, 程序设计者即本人对使用者在使用过程中造成的一切后果不承担任何责任, 下载或查阅或参考任何代码或文件即表示您同意一切后果由使用者承担

I do not take any responsibility of the trouble you may faced. Using, even seeing my code means all the consequences is up to you

## 使用安全
### 进行大范围爬取会使您的计算机访问到风险网站， 您可能成为XSS攻击等被动攻击方式的潜在受害者
### You may become the victim of XSS attacks

建议在虚拟机下运行，并使用代理保护隐私安全

## 百度搜索爬虫
#### BaiduResult.java
爬取百度搜索结果

目前已经实现：
1. 自定义搜索内容
2. 自定义线程数
3. 自定义代理设置
4. 自定义HTTP请求头
5. 自定义失败重连次数
6. 支持验证是否爬到最大页数之外！

暂不支持：
1. 获取搜索结果标题，摘要(原因在下文)
2. 输出至txt文件->内容不多没必要

#### 爬虫逻辑
百度对搜索结果的链接均进行了更改，直接爬取会获得baidu自己的跳转链接，需要再次连接跳转后获得最终连接

访问到最大连接以外时会跳转至第一页，故使用爬重复计数器来解决超出页数

多线程类1 -> 爬取搜索结果中间链接 -> 爬取结束 -> 多线程类2 -> 访问中间连接获取实际连接

因为百度的HTML写的比较混乱所以考虑到制作成本暂不制作爬取标题和摘要

## 谷歌搜索爬虫
#### GoogleResult.java
[English](https://github.com/50Death/Spider-SlimAPP/blob/master/README.md#english)

爬取谷歌搜索结果

目前除了不能爬取js加载的视频以外都可以爬到

最终可获得 标题-URL-摘要

支持代理，ShadowSocks 使用方法：setProxy("127.0.0.1",1080);

暂不支持验证爬完与否，请勿设置超过100页，不然会被封IP

可直接输出URL，方便部署Google Hack

##### English:
Crawl spider for google search result

So far you can not crawl vedios loaded by js

Finally we can get titles, urls and summaries of each search result

Sadly we temporarily not support judging out of bounds. So you must not crawl numbers of pages no larger than 100

Or you will get banned

Class is able to print all urls which can be a tool for google hacks

## 使用到的Maven依赖
```xml
<!-- https://mvnrepository.com/artifact/org.jsoup/jsoup -->
<dependency>
    <groupId>org.jsoup</groupId>
    <artifactId>jsoup</artifactId>
    <version>1.11.3</version>
</dependency>

<!-- 我的另一个Reponsitory, 见 Spider-Tools -->
 <dependency>
    <groupId>com.lyc.spider.tools</groupId>
    <artifactId>url-tools</artifactId>
    <version>1.0</version>
</dependency>
```
## 请捐赠打赏投食！！！
![图片加载失败](https://github.com/50Death/CipheredSocketChat/blob/master/Pictures/%E6%94%AF%E4%BB%98%E5%AE%9D%E7%BA%A2%E5%8C%85.jpg)

## Support Me on Patreon
(https://www.patreon.com/user?u=16747470)

## 更新记录

