package com.jeancoder.trade.ready.unipay

import javax.net.ssl.SSLContext;
import java.io.FileInputStream;
import java.security.KeyStore;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts; // For Apache HttpClient 4.5.x
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class WxRefundRequestSender {

    public static String sendRefundRequest(String url, String xml, String certPath, String certPassword) throws Exception {
        // Step 1: Load PKCS12 Certificate
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        FileInputStream instream = null;
        try {
            instream = new FileInputStream(certPath);
            keyStore.load(instream, certPassword.toCharArray());
        } finally {
            if (instream != null) {
                try {
                    instream.close();
                } catch (Exception ignore) {}
            }
        }

        // Step 2: Build SSL Context with Certificate
        SSLContext sslContext = SSLContexts.custom()
                .loadKeyMaterial(keyStore, certPassword.toCharArray())
                .build();

        // Step 3: Create HTTPS Client with SSL Context
        SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(sslContext);
        CloseableHttpClient httpClient = null;

        try {
            httpClient = HttpClients.custom()
                    .setSSLSocketFactory(sslSocketFactory)
                    .build();

            // Step 4: Build HTTP POST Request
            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader("Content-Type", "text/xml");
            httpPost.setEntity(new StringEntity(xml, "UTF-8"));

            // Step 5: Execute and Return Response
            HttpResponse response = httpClient.execute(httpPost);
            return EntityUtils.toString(response.getEntity(), "UTF-8");

        } finally {
            if (httpClient != null) {
                try {
                    httpClient.close();
                } catch (Exception ignore) {}
            }
        }
    }
}