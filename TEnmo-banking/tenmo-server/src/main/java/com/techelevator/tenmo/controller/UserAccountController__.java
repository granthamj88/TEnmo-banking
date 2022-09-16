package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.UserAccountDao__;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Transfer__;
import com.techelevator.tenmo.model.User;
import com.techelevator.tenmo.model.UserAccount__;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping()
//@PreAuthorize("isAuthenticated()")
public class UserAccountController__ {

    //control class made to call methods from the jdbc with rest methods

    @Autowired
    private UserAccountDao__ userAccountDao;
    @Autowired
    private UserDao userDao;


    public UserAccountController__(UserAccountDao__ userAccountDao, UserDao userDao) {
        this.userAccountDao = userAccountDao;
        this.userDao = userDao;
    }

    @GetMapping(path = "balance/{id}")
    public BigDecimal getBalance(@PathVariable int id){
        System.out.println("made it to controller!!!");
        BigDecimal balance = userAccountDao.getBalance(id);
        return balance;
    }

    @GetMapping(path = "listusers")
    public List<User> listUsers() {
        List<User> users = userDao.findAll();
        return users;
    }

    @GetMapping(path = "user/{id}/account")
    public UserAccount__ findAccountIdByUserId(@PathVariable int id) {

        System.out.println("endpoint: " + id);
        UserAccount__ accountId = userAccountDao.findUserById(id);
        return accountId;
    }

    @PutMapping(path = "newtransfer/approved")
    public void transferMoney (@RequestBody Transfer__ transfer) {
        userAccountDao.transferFunds(transfer);
    }
}
