package bankingSystem.transactions;

import bankingSystem.core.Account;
import bankingSystem.core.BankAccount;

public abstract class Transaction {
    protected double amount;
    protected String currency;

    public Transaction() {
    	
    }
    
    public Transaction(double amount, String currency) {
        this.amount = amount;
        this.currency = currency;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
    
    public boolean transactionAllowed(Account account) {
    	return true;
    }
    
    @Override
    public String toString() {
        return "Type: " + getClass().getSimpleName() + ", Amount: " + amount + ", Currency: " + currency;
    }

	public static Transaction fromString(String transactionSelected) {
		return null;
	}
	
    public abstract void updateBalance(double amount, String currency);

    public boolean handleExecution() {
    	return true;
    }
    
	public boolean handleExecution(double amount, String currency, Account account) {
		return true;
	}
	
	public double handleExecution(double amount, String currency) {
		return 0;
	}
	
	public double handleExecution(double amount, String currency, String targetCurrency) {
		return 0;
	}

    
}
