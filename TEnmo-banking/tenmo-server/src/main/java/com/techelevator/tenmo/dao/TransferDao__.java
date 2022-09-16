package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.*;
import org.springframework.stereotype.Component;


import java.math.BigDecimal;
import java.util.List;

@Component
public interface TransferDao__ {
    List<Transfer__> getAllTransfers(int userId);
    Transfer__ getTransferById(int transactionId);
    Transfer__ sendTransfer(int userFrom, int UserTo, BigDecimal amount);
    Transfer__ requestTransfer(int userId);

    //must also have option to reject and conform transfer
}
