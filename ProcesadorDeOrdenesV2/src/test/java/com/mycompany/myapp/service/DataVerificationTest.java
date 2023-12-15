package com.mycompany.myapp.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class DataVerificationTest {

    String url = "http://192.168.194.254:8000";
    String token = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJzYW50aWFnb2NvcmlhIiwiYXV0aCI6IlJPTEVfVVNFUiIsImV4cCI6MTczMjYzMTcwM30.F1kI20s9p1kv8l2LhJcEcL-66_9X44zIybZw1piDV_ze2FiU3C7Th6iD6FRT7RFwuE9lWw1BCCJYr9hQYk8rEg";
    @Test
    void checkCliente() throws Exception{

        RestTemplate restTemplate = new RestTemplate();

        DataVerificationService dataVerificationService = new DataVerificationService(restTemplate);

        assertTrue(dataVerificationService.checkCliente(201225, url, token));

    }

    @Test
    void checkAccion() throws Exception{

        RestTemplate restTemplate = new RestTemplate();

        DataVerificationService dataVerificationService = new DataVerificationService(restTemplate);

        assertTrue(dataVerificationService.checkAccion(1, url, token));

    }

    @Test
    void checkClienteError() throws Exception{

        RestTemplate restTemplate = new RestTemplate();

        DataVerificationService dataVerificationService = new DataVerificationService(restTemplate);

        assertFalse(dataVerificationService.checkCliente(201131225, url, token));

    }

    @Test
    void checkAccionError() throws Exception{

        RestTemplate restTemplate = new RestTemplate();

        DataVerificationService dataVerificationService = new DataVerificationService(restTemplate);

        assertFalse(dataVerificationService.checkAccion(113131231, url, token));

    }

}
