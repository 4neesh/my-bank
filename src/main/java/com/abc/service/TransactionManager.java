package com.abc.service;

import com.abc.entity.Account;
import com.abc.entity.Transaction;
import com.abc.exception.InvalidTransactionException;

import java.math.BigDecimal;
import java.math.BigInteger;

public class TransactionManager {

    public void deposit(Account account, BigDecimal amount) {
        if (amount.doubleValue() <= 0) {
            throw new InvalidTransactionException("deposit amount must be greater than zero");
        } else {
            account.getTransactions().add(new Transaction(amount));
        }
    }

    public void withdraw(Account account, BigDecimal amount) {
        if (amount.doubleValue() <= 0) {
            throw new InvalidTransactionException("withdrawal amount must be greater than zero");
        } else {
            account.getTransactions().add(new Transaction(amount.multiply(new BigDecimal(-1))));
        }
    }
    public static BigDecimal sumTransactions(Account account) {

        BigDecimal amount = new BigDecimal(0);
        for (Transaction t: account.getTransactions()) {
            amount = amount.add(t.amount);
        }
        return amount;

    }
}