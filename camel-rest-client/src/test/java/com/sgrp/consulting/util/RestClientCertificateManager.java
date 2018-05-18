package com.sgrp.consulting.util;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import org.apache.http.ssl.SSLContexts;
import org.glassfish.jersey.client.ClientConfig;

public class RestClientCertificateManager {

    /**
    * Returns a http client that will not do any certificate validation
    * required as we are using self-signed certificates
    * =================== DO NOT USE IN PRODUCTION ====================
     * @throws Exception 
    */
    public static Client getRestClientWithCertificateValidationDisabled() throws Exception {
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[] { 
          new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() { 
              return new X509Certificate[0]; 
            }
            
            public void checkClientTrusted(X509Certificate[] certs, String authType) {}
            public void checkServerTrusted(X509Certificate[] certs, String authType) {}
        }};

        // Ignore differences between given hostname and certificate hostname
        HostnameVerifier hv = new HostnameVerifier() {
          public boolean verify(String hostname, SSLSession session) { return true; }
        };

        KeyStore keyStore;
		try {
			keyStore = KeyStore.getInstance("PKCS12");
	        keyStore.load(new FileInputStream(System.getProperty("trustStore")), System.getProperty("trustStorePassword").toCharArray());
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		
		KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
        kmf.init(keyStore, System.getProperty("trustStorePassword").toCharArray());
        // Install the all-trusting trust manager
        SSLContext sc;
        try {
        	sc = SSLContexts.custom().loadKeyMaterial(keyStore, "secretwf".toCharArray()).build();
        	//sc = SSLContext.getInstance("SSL");
        	sc.init(kmf.getKeyManagers(), trustAllCerts, new SecureRandom());
        	HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        	HttpsURLConnection.setDefaultHostnameVerifier(hv);
        } catch (Exception e) {
        	e.printStackTrace();
        	return null;
        }
        
        ClientConfig config = new ClientConfig();
        
        Client restClient = ClientBuilder.newBuilder().sslContext(sc).hostnameVerifier(hv).withConfig(config).build();
        
        return restClient; 
    }
}
