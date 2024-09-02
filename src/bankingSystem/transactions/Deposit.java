package bankingSystem.transactions;

import bankingSystem.core.Account;

public class Deposit extends Transaction {
	private String typeofDeposit;
    public Deposit(double amount, String currency, String typeofDeposit) {
        super(amount, currency);
        this.setTypeofDeposit(typeofDeposit);
    }

    public Deposit() {

	}

	@Override
	public void updateBalance(double amount, String currency) {
		
	}

	public String getTypeofDeposit() {
		return typeofDeposit;
	}

	public void setTypeofDeposit(String typeofDeposit) {
		this.typeofDeposit = typeofDeposit;
	}
	
	@Override
	public boolean handleExecution() {
		return true;
	}
	
    public boolean handleExecution(double amount, String currency, Account account) {
        System.out.println("Executing deposit transaction...");
        return account.getBankAccount(0).addBalance(amount, currency, 10000);
    }
}

