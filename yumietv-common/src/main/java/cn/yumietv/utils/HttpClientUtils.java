package cn.yumietv.utils;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.net.URI;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpClientUtils {

    private static String ip = "121.61.0.44";
    private static Integer port = 9999;
    private static String scheme = "http";

    public static String getDom(String url) {
        // 通过httpClient获取网页响应,将返回的响应解析为纯文本
        HttpGet httpGet = new HttpGet(url);
        httpGet = (HttpGet) moniliulanqi(httpGet);
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;

        String responseStr = "";
        try {
            httpClient = HttpClientBuilder.create().build();
            HttpClientContext context = HttpClientContext.create();
            response = httpClient.execute(httpGet, context);
            int state = response.getStatusLine().getStatusCode();
            if (state != 200)
                responseStr = "";
            HttpEntity entity = response.getEntity();
            if (entity != null)
                responseStr = EntityUtils.toString(entity, "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (response != null)
                    response.close();
                if (httpClient != null)
                    httpClient.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        if (responseStr == null) {
            return null;
        }
        return responseStr;
    }

    /*
     * 传uri
     * */
    public static String getDom(URI uri) {
        // 通过httpClient获取网页响应,将返回的响应解析为纯文本
        HttpGet httpGet = new HttpGet(uri);
        HttpHost proxy = new HttpHost("119.101.115.126", 9999);
        RequestConfig requestConfig = RequestConfig.custom().setProxy(proxy).build();
        httpGet.setConfig(requestConfig);

        //httpGet.setConfig(RequestConfig.custom().setSocketTimeout(30000).setConnectTimeout(30000).build());
        httpGet.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        httpGet.setHeader("Accept-Encoding", "gzip, deflate, sdch");
        httpGet.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
        httpGet.setHeader("User-Agent",
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.99 Safari/537.36");
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;

        String responseStr = "";
        try {
            httpClient = HttpClientBuilder.create().build();
            HttpClientContext context = HttpClientContext.create();
            response = httpClient.execute(httpGet, context);
            int state = response.getStatusLine().getStatusCode();
            if (state != 200)
                responseStr = "";
            HttpEntity entity = response.getEntity();
            if (entity != null)
                responseStr = EntityUtils.toString(entity, "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (response != null)
                    response.close();
                if (httpClient != null)
                    httpClient.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        if (responseStr == null) {
            return null;
        }
        return responseStr;
    }

    /*
     * 校验图片地址是否存在
     *
     */
    public static Boolean img_urlIsExist(String imgUrl) {
        // 通过httpClient获取网页响应,将返回的响应解析为纯文本
        HttpGet httpGet = new HttpGet(imgUrl);
        httpGet.setConfig(RequestConfig.custom().setSocketTimeout(30000).setConnectTimeout(30000).build());
        httpGet.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        httpGet.setHeader("Accept-Encoding", "gzip, deflate, sdch");
        httpGet.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
        httpGet.setHeader("User-Agent",
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.99 Safari/537.36");
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;

        try {
            httpClient = HttpClientBuilder.create().build();
            HttpClientContext context = HttpClientContext.create();
            response = httpClient.execute(httpGet, context);
            int state = response.getStatusLine().getStatusCode();
            if (state != 200) {
                return false;
            } else {
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (response != null)
                    response.close();
                if (httpClient != null)
                    httpClient.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return false;
    }

    public static String getHalihaliDom(String ip, int port, String scheme, String referer, String url) {
        // 通过httpClient获取网页响应,将返回的响应解析为纯文本
        HttpGet httpGet = new HttpGet(url);
        HttpHost proxy = new HttpHost(ip, port, scheme);
        RequestConfig requestConfig = RequestConfig.custom().setProxy(proxy).build();
        httpGet.setConfig(requestConfig);
        httpGet.setConfig(RequestConfig.custom().setSocketTimeout(30000).setConnectTimeout(30000).build());
        httpGet.setHeader("Referer", referer);
        httpGet.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        httpGet.setHeader("Accept-Encoding", "gzip, deflate, sdch");
        httpGet.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
        httpGet.setHeader("User-Agent",
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.99 Safari/537.36");
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;

        String responseStr = "";
        try {
            httpClient = HttpClientBuilder.create().build();
            HttpClientContext context = HttpClientContext.create();
            response = httpClient.execute(httpGet, context);
            int state = response.getStatusLine().getStatusCode();
            if (state != 200)
                responseStr = "";
            HttpEntity entity = response.getEntity();
            if (entity != null)
                responseStr = EntityUtils.toString(entity, "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (response != null)
                    response.close();
                if (httpClient != null)
                    httpClient.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        if (responseStr == null) {
            return null;
        }
        return responseStr;
    }

    public static Boolean getHalihaliDomIs200(String ip, int port, String scheme, String referer, String url) throws Exception {
        // 通过httpClient获取网页响应,将返回的响应解析为纯文本
        HttpGet httpGet = new HttpGet(url);
        HttpHost proxy = new HttpHost(ip, port, scheme);
        RequestConfig requestConfig = RequestConfig.custom().setProxy(proxy).build();
        httpGet.setConfig(requestConfig);
        httpGet.setConfig(RequestConfig.custom().setSocketTimeout(30000).setConnectTimeout(30000).build());
        httpGet.setHeader("Referer", referer);
        httpGet.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        httpGet.setHeader("Accept-Encoding", "gzip, deflate, sdch");
        httpGet.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
        httpGet.setHeader("User-Agent",
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.99 Safari/537.36");
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;

        httpClient = HttpClientBuilder.create().build();
        HttpClientContext context = HttpClientContext.create();
        response = httpClient.execute(httpGet, context);
        int state = response.getStatusLine().getStatusCode();
        if (state != 200) {
            return false;
        } else {
            return true;
        }

    }

    public static String getPHPSESSID(String ip, int port, String scheme) {
        // 通过httpClient获取网页响应,将返回的响应解析为纯文本
        HttpGet httpGet = new HttpGet("https://www.halihali.tv/user/login.html");
        HttpHost proxy = new HttpHost(ip, port, scheme);
        RequestConfig requestConfig = RequestConfig.custom().setProxy(proxy).build();
        httpGet.setConfig(requestConfig);
        httpGet.setConfig(RequestConfig.custom().setSocketTimeout(30000).setConnectTimeout(30000).build());
        httpGet.setHeader("Referer", "https://www.halihali.tv");
        httpGet.setHeader("User-Agent",
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.99 Safari/537.36");
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;

        String responseStr = "";
        try {
            httpClient = HttpClientBuilder.create().build();
            HttpClientContext context = HttpClientContext.create();
            response = httpClient.execute(httpGet, context);
            int state = response.getStatusLine().getStatusCode();
            if (state != 200)
                responseStr = "";
            Header[] cookies = response.getHeaders("Set-Cookie");
            String cookie = Arrays.toString(cookies);
            String regex = "PHPSESSID\\S*?;";
            Pattern pattern = Pattern.compile(regex);
            Matcher m = pattern.matcher(cookie);
            while (m.find()) {
                return (m.group().replace(";", ""));
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (response != null)
                    response.close();
                if (httpClient != null)
                    httpClient.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        if (responseStr == null) {
            return null;
        }
        return responseStr;
    }

    public static void halihaliLogin(String ip, int port, String scheme, String phpsessid) throws Exception {
        //创建POST请求对象
        HttpPost httpPost = new HttpPost("https://www.halihali.tv/index.php?s=user-login-index");
        //创建代理对象
        HttpHost proxy = new HttpHost(ip, port, scheme);
        RequestConfig requestConfig = RequestConfig.custom().setProxy(proxy).build();
        httpPost.setConfig(requestConfig);
        //设置超时时间
        httpPost.setConfig(RequestConfig.custom().setSocketTimeout(30000).setConnectTimeout(30000).build());

        //创建请求参数
        List<NameValuePair> list = new LinkedList<>();
        //不需要更改的固定参数
        list.add(new BasicNameValuePair("username", "a1084460097"));
        list.add(new BasicNameValuePair("password", "yumie123"));
        list.add(new BasicNameValuePair("jump", "1"));
        list.add(new BasicNameValuePair("cookietime", "2592000"));
        list.add(new BasicNameValuePair("dosubmit", "1"));
        //使用URL实体转换工具,进行UrlEnCode编码
        UrlEncodedFormEntity entityParam = new UrlEncodedFormEntity(list, "UTF-8");
        //设置参数
        httpPost.setEntity(entityParam);

        //添加请求头信息
        httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        httpPost.setHeader("Referer", "https://www.halihali.tv/user/login.html");
        //模仿浏览器
        httpPost.setHeader("User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36");
        //必须携带的Cookie
        httpPost.setHeader("Cookie", phpsessid);

        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;

        httpClient = HttpClientBuilder.create().build();
        HttpClientContext context = HttpClientContext.create();
        response = httpClient.execute(httpPost, context);
    }

    public static HttpRequestBase moniliulanqi(HttpRequestBase httpRequestBase) {
        HttpHost proxy = new HttpHost(ip, port, scheme);
        RequestConfig requestConfig = RequestConfig.custom().setProxy(proxy).build();
        httpRequestBase.setConfig(requestConfig);
        httpRequestBase.setConfig(RequestConfig.custom().setSocketTimeout(30000).setConnectTimeout(30000).build());
        httpRequestBase.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        httpRequestBase.setHeader("Accept-Encoding", "gzip, deflate, sdch");
        httpRequestBase.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
        httpRequestBase.setHeader("User-Agent",
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.99 Safari/537.36");
        return httpRequestBase;
    }

}
