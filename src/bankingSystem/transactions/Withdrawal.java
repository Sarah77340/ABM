package bankingSystem.transactions;

import bankingSystem.core.Account;

public class Withdrawal extends Transaction {
	public Withdrawal() {
		super();
	}
	
    public Withdrawal(double amount, String currency) {
        super(amount, currency);
    }

	@Override
	public void updateBalance(double amount, String currency) {
	}
	
	@Override
	public boolean transactionAllowed(Account account) {
		return account.calculateAge() > 16;
    }

	@Override
	public boolean handleExecution() {
		return true;
	}
	
    public boolean handleExecution(double amount, String currency, Account account) {
        System.out.println("Executing withdrawal transaction...");
        return account.getBankAccount(0).subtractBalance(amount, currency, account.getCustomerType().equals("VIP"));
    }
}
