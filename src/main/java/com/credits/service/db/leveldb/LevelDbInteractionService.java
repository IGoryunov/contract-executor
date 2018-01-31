package com.credits.service.db.leveldb;

import com.credits.exception.ContractExecutorException;

import java.util.List;
import java.util.Map;

public interface LevelDbInteractionService {

    Map<String, Amount> getBalance(String address) throws ContractExecutorException;

    List<Transaction> getTransactions(String address, String currency) throws ContractExecutorException;

    TransactionInfo getTransactionInfo(String source, String destination, Amount amount, long timestamp, String currency) throws ContractExecutorException;
}
