package cn.yumietv.utils;

import java.net.URI;
import java.net.URL;

public class StringToUrl {
    public static URI StringToUri(String strUrl) throws Exception {
        URL url = new URL(strUrl);
        URI uri = new URI(url.getProtocol(), url.getHost(), url.getPath(), url.getQuery(), null);
        return uri;
    }
}
