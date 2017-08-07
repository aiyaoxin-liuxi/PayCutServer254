package com.dhb.util;

import org.apache.http.HttpHost;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

public class HttpPollHelp {

	public static void main(String[] args) {
		PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
	    // 将最大连接数增加到200
	    cm.setMaxTotal(200);
	    // 将每个路由基础的连接增加到20
	    cm.setDefaultMaxPerRoute(20);
	    //将目标主机的最大连接数增加到50
	    HttpHost localhost = new HttpHost("www.yeetrack.com", 80);
	    cm.setMaxPerRoute(new HttpRoute(localhost), 50);

	    CloseableHttpClient httpClient = HttpClients.custom()
	            .setConnectionManager(cm)
	            .build();
	}
}
