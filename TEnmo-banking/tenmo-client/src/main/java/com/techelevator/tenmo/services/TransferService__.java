package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.Transfer__;
import com.techelevator.tenmo.model.User;
import com.techelevator.tenmo.model.UserAccount__;
import org.springframework.http.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Scanner;

public class TransferService__{

    private String BASE_URL;
    private AuthenticatedUser currentUser;
    private RestTemplate restTemplate = new RestTemplate();
    private UserAccount__ accountTo;
    private UserAccount__ accountFrom;
    private UserAccountService__ y;

    public TransferService__() {
    }

    public TransferService__(String url, AuthenticatedUser currentUser) {
        this.BASE_URL = url;
        this.currentUser = currentUser;
    }

    public Transfer__[] transferList(){
        Transfer__[] results = null;

        try{
            results = restTemplate.exchange(BASE_URL + "account/transfers/" + currentUser.getUser().getId(), HttpMethod.GET, makeAuthEntity(), Transfer__[].class).getBody();
            System.out.println("-------------------------------------------\n" +
                    "Transfers\n" +
                    "ID          From/To                 Amount\n" +
                    "-------------------------------------------\n");
            String toOrFrom = "";
            String name = "";

            for(Transfer__ transfer : results){
                if(currentUser.getUser().getId() == transfer.getAccountTo()){
                    toOrFrom = "To: ";
                    name = transfer.getUserFrom();
                } else {
                    toOrFrom = "From: ";
                    name = transfer.getUserTo();
                }
                System.out.println(transfer.getTransferId() + "\t\t" + toOrFrom + name + "\t\t\t" + "$" + transfer.getAmount());
            }

            System.out.println("-------------------------------------------\n" +
                    "Please enter transfer ID to view details (0 to cancel): ");
            //in progress from here
            Scanner scanner = new Scanner(System.in);
            String input = scanner.nextLine();
            //take more time to understand
            if(Integer.parseInt((input) )!= 0){
                boolean foundTransferId = false;
                for(Transfer__ transfer : results){
                    if (Integer.parseInt(input) == transfer.getTransferId()){
                        //System.out.println("rest reached");
                        Transfer__ tempTransfer = restTemplate.exchange(BASE_URL + "transfers/" +transfer.getTransferId(), HttpMethod.GET, makeAuthEntity(), Transfer__.class).getBody();
                        foundTransferId = true;
                        System.out.println("rest processed");
                        System.out.println("--------------------------------------------\n" +
                                "Transfer Details\n" +
                                "--------------------------------------------\n" +
                                " ID: " + tempTransfer.getTransferId() + "\n" +
                                " From: " + tempTransfer.getUserFrom() + "\n" +
                                " To: " + tempTransfer.getUserTo() + "\n" +
                                " Type: " + tempTransfer.getTransferType() + "\n" +
                                " Status: " + tempTransfer.getTransferStatus() + "\n" +
                                " Amount: $" + tempTransfer.getAmount()
                                  );
                    }
                }
                if (!foundTransferId) {
                    System.out.println("Invalid transfer ID!");
                }
            }
        }catch (Exception e){
            System.out.println("Oh no! All the TEnmo gnomes took your money!");
        }
        return results;
    }

    public void sendBucks(){
        User[] users;

        ResponseEntity<UserAccount__> response_accountTo;
        ResponseEntity<UserAccount__> response_accountFrom;

        try{ //compiles user list
            users = restTemplate.exchange(BASE_URL + "listusers", HttpMethod.GET, makeAuthEntity(), User[].class).getBody();
            System.out.println("-------------------------------------------\n" +
                    "Users\n" +
                    "ID\t\t\tName\n" +
                    "-------------------------------------------");
            for (User user : users){
                if(user.getId() != currentUser.getUser().getId()){
                    System.out.println(user.getId() + "\t\t" + user.getUsername());
                }
            }
        } catch (Exception e){
            System.out.println("Bad input.");
        }
        // user to transfer to select here
        try {
            Scanner scanner = new Scanner(System.in);
            Transfer__ transfer = new Transfer__();
            System.out.println("-------------------------------------------\n" +
                    "Enter ID of user you are sending to (0 to cancel): ");

            int accountFromInt = scanner.nextInt();

            response_accountTo = restTemplate.exchange(BASE_URL + "user/" + accountFromInt + "/account", HttpMethod.GET, makeAuthEntity(), UserAccount__.class);
            response_accountFrom = restTemplate.exchange(BASE_URL + "user/" + currentUser.getUser().getId() + "/account", HttpMethod.GET, makeAuthEntity(), UserAccount__.class);

            this.accountTo = response_accountTo.getBody();
            this.accountFrom = response_accountFrom.getBody();

            if (accountFromInt == 0) {
                System.out.println("Cancelled.");
            }
            if (accountFromInt != 0) {
                transfer.setAccountTo(accountTo.getAccountId());
                transfer.setAccountFrom(accountFrom.getAccountId());
            }
            if (accountFromInt != 0 && (transfer.getAccountFrom() == transfer.getAccountTo())) {
                System.out.println("You can't send money to yourself!");
            }

            if (transfer.getAccountFrom() != transfer.getAccountTo()) {
                if (transfer.getAccountTo() != 0) {
                    Double userService = new UserAccountService__(BASE_URL, currentUser).getBalance();

                    //System.out.println("your available balance is - " + userService + "\n");

                    System.out.println("Enter amount to send: ");

                    try {
                        double amountD = scanner.nextDouble();
                        if(amountD>userService || amountD < -1){
                            System.out.println("\ninvalid entry, please try again \n");
                            sendBucks();
                        }else {
                            transfer.setAmount(new BigDecimal(amountD));
                            restTemplate.exchange(BASE_URL + "transfer", HttpMethod.POST, makeTransferEntity(transfer), Transfer__.class).getBody();
                            restTemplate.exchange(BASE_URL + "newtransfer/approved", HttpMethod.PUT, makeTransferEntity(transfer), Transfer__.class).getBody();
                            System.out.println("Transfer Successful!!");
                        }

                    } catch (NumberFormatException e) {
                        System.out.println("Error when entering amount.\n");
                    }
//                    restTemplate.exchange(BASE_URL + "transfer", HttpMethod.POST, makeTransferEntity(transfer), Transfer__.class).getBody();
//                    restTemplate.exchange(BASE_URL + "newtransfer/approved", HttpMethod.PUT, makeTransferEntity(transfer), Transfer__.class).getBody();
//                    System.out.println("Transfer Successful!!");
                }
            }

        }catch (RestClientResponseException ex) {
            //look up these exceptions
            System.out.println(ex.getRawStatusCode() + " : " + ex.getResponseBodyAsString());
        } catch (RestClientException e) {
            System.out.println("Bad input");
        }
    }

    private HttpEntity<Transfer__> makeTransferEntity(Transfer__ transfer) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(currentUser.getToken());
        HttpEntity<Transfer__> entity = new HttpEntity<>(transfer, headers);
        return entity;
    }

    private HttpEntity makeAuthEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(currentUser.getToken());
        HttpEntity entity = new HttpEntity<>(headers);
        return entity;
    }

    private class InsufficientBalanceException extends Exception {
    }
}
