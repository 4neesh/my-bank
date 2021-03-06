package com.abc.util;

import com.abc.entity.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;


/**
 * Annual interest calculator utility to calculate the interest earned by each account on an annual basis
 * @author aneesh
 */
public class AnnualInterestCalculator {

    /**
     * calculate the interest an account will earn in one year given it's current balance
     * @param account account to assess the interest earned upon
     * @return the interest that would be earned by the account in one year
     */
    public static BigDecimal interestEarnedByAccount(Account account) {
        BigDecimal accountHolding = account.calculateBalance();
        BigDecimal interest;
        switch(account.getAccountType()){
            case SAVINGS:
                interest = calculateSavingsInterest(accountHolding);
                break;
            case MAXI_SAVINGS:
                interest = calculateMaxiSavingsInterest(accountHolding);
                break;
            case MAXI_SAVINGS_ADD:
                interest = calculateMaxiSavingInterestAdditionalFeature(accountHolding, account);
                break;
            default:
                interest = accountHolding.multiply(AccountType.CURRENT.getFlatRate());
        }
        return interest.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calculate the annual interest a bank will pay as an accumulation of all it's customers
     * @param bank the bank to calculate total interest paid for
     * @return the total interest a bank will have to pay in 1 year for all the customers it has.
     */
    public static BigDecimal totalInterestPaidByBank(Bank bank) {
        BigDecimal total = new BigDecimal("0");
        for(Customer customer : bank.getCustomers())
            total = total.add(AnnualInterestCalculator.interestEarnedByCustomer(customer));
        return total.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * Calculate the annual interest a bank will pay a customer for all their account balances.
     * @param customer the customer to whom owns the accounts
     * @return the amount of interest they would earn on an annual basis for their current account holding.
     */
    public static BigDecimal interestEarnedByCustomer(Customer customer) {
        BigDecimal total = new BigDecimal(0);
        for (Account account : customer.getAccounts().values()) {
            total = total.add(AnnualInterestCalculator.interestEarnedByAccount(account));
        }
        return total;
    }

    static BigDecimal calculateMaxiSavingInterestAdditionalFeature(BigDecimal accountHolding, Account account) {

        if(accountNotMadeWithdrawalsInDays(10, account)){
            return accountHolding.multiply(AccountType.MAXI_SAVINGS_ADD.getSecondRate());
        }
        else{
            return accountHolding.multiply(AccountType.MAXI_SAVINGS_ADD.getFlatRate());
        }
    }

    static BigDecimal calculateMaxiSavingsInterest(BigDecimal accountHolding) {
        if (accountHolding.intValue()  <= 1000) {
            return accountHolding.multiply(AccountType.MAXI_SAVINGS.getFlatRate());
        }
        else if(accountHolding.intValue() <= 2000){
            BigDecimal balanceForSecondRate = accountHolding.subtract(new BigDecimal("1000.00"));
            BigDecimal interestFromSecondRate = new BigDecimal(
                    String.valueOf(balanceForSecondRate.multiply(AccountType.MAXI_SAVINGS.getSecondRate()))
            );
            return interestFromSecondRate.add(AccountType.MAXI_SAVINGS.getFlatRate().multiply(new BigDecimal("1000")));
        }
        else{
            BigDecimal interest1 = AccountType.MAXI_SAVINGS.getFlatRate().multiply(new BigDecimal("1000"));
            BigDecimal interest2 = AccountType.MAXI_SAVINGS.getSecondRate().multiply(new BigDecimal("1000"));

            BigDecimal balanceForThirdRate = accountHolding.subtract(new BigDecimal("2000"));

            BigDecimal interestFromThirdRate = new BigDecimal(
                    String.valueOf(balanceForThirdRate.multiply(AccountType.MAXI_SAVINGS.getThirdRate()))
            );
            return interest1.add(interest2).add(interestFromThirdRate);
        }
    }

    static BigDecimal calculateSavingsInterest(BigDecimal accountHolding) {
        if(accountHolding.intValue() <= 1000) {
            return accountHolding.multiply(AccountType.SAVINGS.getFlatRate());
        }
        else{
            BigDecimal remainingBalance = getRemaining(accountHolding, "1000");

            BigDecimal remainingInterest = new BigDecimal(
                    String.valueOf(remainingBalance.multiply(AccountType.SAVINGS.getSecondRate()))
            );
            return remainingInterest.add(AccountType.SAVINGS.getFlatRate().multiply(new BigDecimal("1000")));

        }
    }


    private static boolean accountNotMadeWithdrawalsInDays(int daysSinceToday, Account account) {

        for(Transaction transaction : account.getTransactions()){

            if(transaction.getAmount().doubleValue() < 0 &&
                    transaction.getTransactionDate().isAfter(LocalDateTime.now().minusDays(daysSinceToday))){
                return false;
            }
        }
        return true;
    }

    private static BigDecimal getRemaining(BigDecimal accountHolding, String i) {
        return accountHolding.subtract(new BigDecimal(Integer.parseInt(i)));
    }


}
