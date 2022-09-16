package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.User;
import org.springframework.http.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;


public class UserAccountService__ {

    private String API_BASE_URL = "http://localhost:8080/";
    private RestTemplate restTemplate = new RestTemplate();
    private AuthenticatedUser currentUser;
    public String AUTH_TOKEN = "";

    public UserAccountService__() {
    }

    public UserAccountService__(String url, AuthenticatedUser currentUser) {
        API_BASE_URL = url;
        this.currentUser = currentUser;
    }

    public User[] findUsers(){
        User[] user = null;

        ResponseEntity<User[]> response
                = restTemplate.exchange(API_BASE_URL + "user", HttpMethod.GET,  makeAuthEntity(), User[].class);
        user = response.getBody();
        return user;
    }

    public Double getBalance() {
        double balance;
        ResponseEntity<Double> response = null;
        try {
            response = restTemplate.exchange(API_BASE_URL + "balance/" + currentUser.getUser().getId(),
                    HttpMethod.GET, makeAuthEntity(), Double.class);
            System.out.format("\nYour current account balance is: $ %.2f", response.getBody());
            System.out.println(" ");
        } catch (RestClientException e) {
            System.out.println("\nError getting user balance???");
            System.out.println(" ");
        }
        balance = response.getBody();
        return balance;
    }

    private HttpEntity makeAuthEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(AuthenticatedUser.getToken());
        return new HttpEntity<>(headers);
    }


}
