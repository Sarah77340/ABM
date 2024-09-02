package bankingSystem.transactions;

import bankingSystem.core.Account;

public class Transfer extends Transaction {
    private String recipientAccount;

    public Transfer(double amount, String currency, String recipientAccount) {
        super(amount, currency);
        this.recipientAccount = recipientAccount;
    }

    public Transfer() {
		// TODO Auto-generated constructor stub
	}

	public String getRecipientAccount() {
        return recipientAccount;
    }

    public void setRecipientAccount(String recipientAccount) {
        this.recipientAccount = recipientAccount;
    }

	@Override
	public void updateBalance(double amount, String currency) {
	}
	
    public boolean handleExecution(double amount, String currency, Account account) {
        System.out.println("Executing transfer transaction...");
        return account.getBankAccount(0).subtractBalance(amount, currency, true);
    }
}
