package bankingSystem.core;

import java.util.HashMap;
import java.util.Map;

public class BankAccount {
	private double balance;
	private String currency;
	
	public BankAccount() {
	}
	
	public BankAccount(double balance, String currency) {
	    this.balance = balance;
	    this.currency = currency;
	}
	
	public double getBalance() {
		return balance;
	}
	
	public void setBalance(double balance) {
		this.balance = balance;
	}
	
	public String getCurrency() {
		return currency;
	}
	
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	
	public boolean subtractBalance(double newBalance, String newCurrency, boolean overdraftPermitted) {
	    double convertedBalance = (newCurrency.equals(currency)) ? newBalance : convertCurrency(newBalance, newCurrency, currency);
	    
	    if (balance - convertedBalance < 0 && !overdraftPermitted) 
	    	return false;
	    balance -= convertedBalance;
	    currency = newCurrency;
//	    System.out.println(this);
	    return true;
	}
	
	public boolean addBalance(double newBalance, String newCurrency, double balanceLimit) {
	    double convertedBalance = (newCurrency.equals(currency)) ? newBalance : convertCurrency(newBalance, newCurrency, currency);
	    
	    if (balance + convertedBalance > balanceLimit) 
	    	return false;
	    balance += convertedBalance;
	    currency = newCurrency;
//	    System.out.println(this);
	    return true;
	}

	public static double convertCurrency(double amount, String fromCurrency, String toCurrency) {
	    Map<String, Double> exchangeRates = new HashMap<>();
	    exchangeRates.put("EUR_TO_USD", 1.18);
	    exchangeRates.put("EUR_TO_CAD", 1.47);
	    exchangeRates.put("CAD_TO_USD", 0.80);
	    return amount / exchangeRates.computeIfAbsent(fromCurrency + "_TO_EUR", k -> 1.0) *
	            exchangeRates.computeIfAbsent("EUR_TO_" + toCurrency, k -> 1.0);
	}
	
	@Override
	public String toString() {
	    return "Balance: " + balance + " " + currency;
	}

}
