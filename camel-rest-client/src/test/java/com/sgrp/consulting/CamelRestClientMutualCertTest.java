package com.sgrp.consulting;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.Response;

import org.junit.BeforeClass;
import org.junit.Test;

import com.sgrp.consulting.util.RestClientCertificateManager;

public class CamelRestClientMutualCertTest {
	private static String SSL_MUT_CERT_PROP = "sslMutCertUrl";
		
	private static String SSL_MUTUAL_CERT_URL;
	private static String EXPECTED_REST_RESPONSE = "Hello REST World with Mutual Cert";

    @BeforeClass
    public static void beforeClass() {
    	CamelRestClientMutualCertTest.SSL_MUTUAL_CERT_URL = 
    			System.getProperty(CamelRestClientMutualCertTest.SSL_MUT_CERT_PROP);
    }
    
    @Test
    public void testSslWithMutualCert() throws Exception {
        System.out.println("===============================================");
        System.out.println("URL: " + CamelRestClientMutualCertTest.SSL_MUTUAL_CERT_URL);

    	Client restClient = RestClientCertificateManager.getRestClientWithCertificateValidationDisabled();
    	
    	Response response = restClient.target(CamelRestClientMutualCertTest.SSL_MUTUAL_CERT_URL).request().get();
    	
    	if (response.getStatus() != 200) {
            throw new RuntimeException("Failed request with HTTP status: " + response.getStatus());
        }
    	
    	
    	String retVal = response.readEntity(String.class); 
        System.out.println("\n*** Response from Server ***\n");
        System.out.println("Response entity => " + retVal);

    	assertEquals(EXPECTED_REST_RESPONSE, retVal);
        System.out.println("\n===============================================");
    }
}
