package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer__;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcTransferDao implements TransferDao__{

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private UserAccountDao__ accountDao;

    public JdbcTransferDao(JdbcTemplate jdbcTemplate) { this.jdbcTemplate = jdbcTemplate;}

    @Override
    public List<Transfer__> getAllTransfers(int userId) {
        System.out.println(userId);
        List<Transfer__> transferList = new ArrayList<>();
        String sql = "SELECT t.*, user1.username AS userFrom, user2.username AS userTo FROM transfer t \n" +
                "JOIN account a ON t.account_from = a.account_id \n" +
                "JOIN account b ON t.account_from = b.account_id \n" +
                "JOIN tenmo_user user1 ON a.user_id = user1.user_id \n" +
                "JOIN tenmo_user user2 ON a.user_id = user2.user_id \n" +
                "WHERE a.user_id = ? OR b.user_id = ?;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId, userId);
        while (results.next()) {
            Transfer__ transfer = mapRowToTransfer(results);
            transferList.add(transfer);
        }
        return transferList;
    }

    @Override
    public Transfer__ getTransferById(int transactionId) {
        Transfer__ transfer = new Transfer__();
                String sql = "SELECT t.*, user1.username AS userFrom, user2.username \n" +
                        "AS userTo, ts.transfer_status_desc, tt.transfer_type_desc \n" +
                        "FROM transfer t \n" +
                        "JOIN account a ON t.account_from = a.account_id \n" +
                        "JOIN account b ON t.account_to = b.account_id\n" +
                        "JOIN tenmo_user user1 ON a.user_id = user1.user_id\n" +
                        "JOIN tenmo_user user2 ON b.user_id = user2.user_id\n" +
                        "JOIN transfer_status ts ON t.transfer_status_id = ts.transfer_status_id\n" +
                        "JOIN transfer_type tt ON t.transfer_type_id = tt.transfer_type_id\n" +
                        "WHERE t.transfer_id = ?;";
                SqlRowSet results = jdbcTemplate.queryForRowSet(sql, transactionId);
                if (results.next()) {
                    transfer = mapRowToTransfer(results);
                } else {
                    throw new RuntimeException("Transfer not found.");
                }
                return transfer;
    }

    //edit later to allow for rejection and pending of requests
    @Override
    public Transfer__ sendTransfer(int InsertFrom, int InsertTo, BigDecimal amount) {
        String sql = "INSERT INTO transfer \n" +
                "(transfer_type_id, transfer_status_id, \n" +
                "account_from, account_to, amount) VALUES(2, 2, ?, ?, ?);\n" +
                "\n" +
                //where money is being taken from
                "UPDATE account \n" +
                "SET balance = balance - ?\n" +
                "WHERE account_id = ?;\n" +
                "\n" +
                //where money is being sent
                "UPDATE account \n" +
                "SET balance = balance + ?\n" +
                "WHERE account_id = ?;\n" +
                "\n" +
                "COMMIT;";
        jdbcTemplate.update(sql, InsertFrom, InsertTo, amount, amount, InsertFrom, amount, InsertTo );
        Transfer__ transfer = new Transfer__(InsertFrom, InsertTo, amount);

        return transfer;
    }

    @Override
    public Transfer__ requestTransfer(int userId) {
        return null;
    }

    private Transfer__ mapRowToTransfer(SqlRowSet results) {
        Transfer__ transfer = new Transfer__();
        transfer.setTransferId(results.getInt("transfer_id"));
        transfer.setTransferTypeId(results.getInt("transfer_type_id"));
        transfer.setTransferStatusId(results.getInt("transfer_status_id"));
        transfer.setAccountFrom(results.getInt("account_From"));
        transfer.setAccountTo(results.getInt("account_to"));
        transfer.setAmount(results.getBigDecimal("amount"));
        try {
            transfer.setUserFrom(results.getString("userFrom"));
            transfer.setUserTo(results.getString("UserTo"));
        } catch (Exception e) {
            System.out.println("Can't set user from/to values.");
        }
        try {
            transfer.setTransferType(results.getString("transfer_type_desc"));
            transfer.setTransferStatus(results.getString("transfer_status_desc"));
        } catch (Exception e) {
            System.out.println("Can't set transfer type/status.");
        }
        return transfer;
    }
}
