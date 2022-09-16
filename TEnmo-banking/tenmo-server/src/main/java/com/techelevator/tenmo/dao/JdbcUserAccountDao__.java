package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer__;
import com.techelevator.tenmo.model.UserAccount__;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class JdbcUserAccountDao__ implements UserAccountDao__{

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public JdbcUserAccountDao__() {}

    public JdbcUserAccountDao__(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override //requires new selection option in user menu to explore users own account
    public String getMyUserName(int userId) {
        String sql = "SELECT username\n" +
                "FROM tenmo_user\n" +
                "WHERE user_id = ?;";
//returns current user name
        SqlRowSet result = null;
        String username = null;
        try{
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId);
            if (results.next()){
                username = result.getString("username");
            }
        }catch (DataAccessException e){
            System.out.println("Error accessing data of user balance.");
        }
        return username;
    }
//return current password
    @Override
    public String getMyPassword(int userId) {
        String sql = "SELECT password_hash\n" +
                "FROM tenmo_user\n" +
                "WHERE user_id = ?;";
        SqlRowSet result = null;
        String password = null;
        try{
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId);
            if (results.next()){
                password = result.getString("password_hash");
            }
        }catch (DataAccessException e){
            System.out.println("Error accessing data of user balance.");
        }
        return password;
    }

    @Override
    public BigDecimal getBalance(int userId) {
        String sql = "SELECT balance FROM account\n" +
                "JOIN tenmo_user USING (user_id) \n" +
                "WHERE user_id = ?;";
        SqlRowSet result;
        BigDecimal balance = null;
        //if error with get balance request, remove try catch
        try{
            result = jdbcTemplate.queryForRowSet(sql, userId);
        while(result.next()){

                balance = result.getBigDecimal("balance");
            System.out.println("holding something");
            }
        }catch (DataAccessException e){
            System.out.println("Error accessing data of user balance.");
        }
        return balance;
    }

    @Override
    public UserAccount__ findUserById(int userId) {
        String sql = "SELECT * FROM account WHERE user_id = ?;";

        UserAccount__ ua = new UserAccount__();
        try{
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId);
            if(results.next()){
                ua = mapToRowUserAccount(results);
            }
        }catch (DataAccessException e){
            System.out.println("Error accessing data with user Id.");
        }
        return ua;
    }

    @Override
    public UserAccount__ findAccountById(int accountId) {
        String sql = "SELECT * FROM account \n" +
                "WHERE account_id = ?;";
        UserAccount__ ua = null;
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, accountId);
        if(results.next()){
            ua = mapToRowUserAccount(results);
        }
        return ua;
    }

    @Override
    public void transferFunds(Transfer__ transfer) {
        System.out.println("method not finished");
    }

    private UserAccount__ mapToRowUserAccount(SqlRowSet results){
        UserAccount__ ua = new UserAccount__();
        double balance = results.getDouble("balance");
        ua.setBalance(new BigDecimal(balance));
        ua.setAccountId(results.getInt("account_id"));
        ua.setUserId(results.getInt("user_id"));
        return ua;
    }
}
