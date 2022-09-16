package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer__;
import com.techelevator.tenmo.model.UserAccount__;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public interface UserAccountDao__ {

    String getMyUserName (int userId); //will get current user name
    String getMyPassword (int userId); //will get current user password, can also offer to change it
    BigDecimal getBalance (int userId);
    UserAccount__ findUserById(int userId);
    UserAccount__ findAccountById(int id);
    void transferFunds (Transfer__ transfer);
}
