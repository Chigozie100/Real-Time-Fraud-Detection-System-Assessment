package com.Fraud.Detection.System.RealTime.Fraud.Detection.System.service;

import com.Fraud.Detection.System.RealTime.Fraud.Detection.System.entities.Transaction;

import java.util.List;


public interface TransactionService {

    void processTransaction(List<Transaction> transaction);
}
