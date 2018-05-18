package com.sgrp.consulting;

import static org.junit.Assert.assertEquals;

import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.ClientConfig;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * JUnit4 Test class which makes a request to the RESTful rest web service.
 */
public class CamelRestClientTest {
	/**
	 * Authentication details for sending the header
	 */
	String username="demo_user";
    String password="P@ssw0rd";
    String usernameAndPassword = username + ":" + password;
    String authorizationHeaderName = "Authorization";
    String authorizationHeaderValue = "Basic " + java.util.Base64.getEncoder().encodeToString( usernameAndPassword.getBytes() );
	
    /**
     * Request URLs pulled from system properties in pom.xml
     */
    private static String SSL_URL;
    private static String SSL_WITH_AUTH_URL;

    /**
     * Property names used to pull values from system properties in pom.xml
     */
    private static final String SSL_URL_PROPERTY = "sslUrl";
    private static final String SSL_WITH_AUTH_PROPERTY = "sslWithAuthUrl";

    /**
     * Responses of the RESTful web service
     */
    private static final String SSL_ONLY_RESPONSE = "Hello REST World";
    private static final String SSL_WITH_AUTH_RESPONSE = "Hello SECURE REST World";
   
    /**
    * Returns a http client that will not do any certificate validation
    * required as we are using self-signed certificates
    * =================== DO NOT USE IN PRODUCTION ====================
    */
    private static Client getRestClientWithCertificateValidationDisabled() {
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

        // Install the all-trusting trust manager
        SSLContext sc;
        try {
        	sc = SSLContext.getInstance("SSL");
        	sc.init(null, trustAllCerts, new SecureRandom());
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
    
    /**
     * Method executes BEFORE the test method. Values are read from system properties that can be modified in the pom.xml.
     */
    @BeforeClass
    public static void beforeClass() {
    	CamelRestClientTest.SSL_URL = System.getProperty(CamelRestClientTest.SSL_URL_PROPERTY);
    	CamelRestClientTest.SSL_WITH_AUTH_URL = System.getProperty(CamelRestClientTest.SSL_WITH_AUTH_PROPERTY);
    }

    /**
     * Test method which executes the runRequest method that calls the RESTful service over https
     */
    @Test
    public void testSsl() {
        assertEquals("XML Response", CamelRestClientTest.SSL_ONLY_RESPONSE,
            this.runRequest(CamelRestClientTest.SSL_URL, MediaType.TEXT_PLAIN_TYPE));

    }
    
    /**
     * Test method which executes the runRequest method that calls the RESTful service over https with Auth
     */
    @Test
    public void testSslWithAuth() {
        assertEquals("JSON Response", CamelRestClientTest.SSL_WITH_AUTH_RESPONSE,
                this.runRequest(CamelRestClientTest.SSL_WITH_AUTH_URL, MediaType.TEXT_PLAIN_TYPE));    	
    }

    /**
     * The purpose of this method is to run the external REST request.
     *
     * @param url The url of the RESTful service
     * @param mediaType The mediatype of the RESTful service
     */
    private String runRequest(String url, MediaType mediaType) {
        String result = null;

        System.out.println("===============================================");
        System.out.println("URL: " + url);
        System.out.println("MediaType: " + mediaType.toString());

        try {
        	// Using the RESTEasy libraries, initiate a client request
            // using the url as a parameter
        	Client restClient = getRestClientWithCertificateValidationDisabled();
        	
        	WebTarget target = restClient.target(url);
        	
        	Response response;
        	if (CamelRestClientTest.SSL_WITH_AUTH_URL.equals(url)) {
                response = target.request().header(authorizationHeaderName, authorizationHeaderValue).get();        		
        	} else {
        		response = target.request().get();
        	}
            
            // Check the HTTP status of the request
            // HTTP 200 indicates the request is OK
            if (response.getStatus() != 200) {
                throw new RuntimeException("Failed request with HTTP status: " + response.getStatus());
            }
            
            // Loop over the br in order to print out the contents
            System.out.println("\n*** Response from Server ***\n");
            result = response.readEntity(String.class);
            System.out.println("Response entity => " + result);

        } catch (Exception e) {
        	e.printStackTrace();
            System.err.println(e);
        }

        System.out.println("\n===============================================");

        return result;
    }

}
