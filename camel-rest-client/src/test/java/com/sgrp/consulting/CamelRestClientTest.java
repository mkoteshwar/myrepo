package com.sgrp.consulting;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.BeforeClass;
import org.junit.Test;

import com.sgrp.consulting.util.RestClientCertificateManager;

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
        assertEquals(CamelRestClientTest.SSL_ONLY_RESPONSE,
            this.runRequest(CamelRestClientTest.SSL_URL, MediaType.TEXT_PLAIN_TYPE));

    }
    
    /**
     * Test method which executes the runRequest method that calls the RESTful service over https with Auth
     */
    @Test
    public void testSslWithAuth() {
        assertEquals(CamelRestClientTest.SSL_WITH_AUTH_RESPONSE,
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
        	Client restClient = RestClientCertificateManager.getRestClientWithCertificateValidationDisabled();
        	
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
