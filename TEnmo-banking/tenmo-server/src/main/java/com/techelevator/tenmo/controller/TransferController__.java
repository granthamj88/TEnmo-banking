package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.TransferDao__;
import com.techelevator.tenmo.model.Transfer__;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping
@PreAuthorize("isAuthenticated()")

public class TransferController__ {

    @Autowired
    private TransferDao__ transferDao;

    public TransferController__(TransferDao__ transferDao) {
        this.transferDao = transferDao;
    }

    // transfer dao incomplet
    @GetMapping(path = "account/transfers/{id}")
    public List<Transfer__> getAllTransfersById(@PathVariable int id) {
        //System.out.println("test reached controller");
        List<Transfer__> output = transferDao.getAllTransfers(id);
        return output;
    }

    @GetMapping(path = "transfers/{id}")
    public Transfer__ getSelectedTransfer(@PathVariable int id){
        Transfer__ transfer = transferDao.getTransferById(id);
        return transfer;
    }

    @PostMapping(path = "transfer")
    public Transfer__ sendTransferRequest(@RequestBody Transfer__ transfer){
        Transfer__ results = transferDao.sendTransfer(transfer.getAccountFrom(), transfer.getAccountTo(), transfer.getAmount());
        return results;
    }
}
