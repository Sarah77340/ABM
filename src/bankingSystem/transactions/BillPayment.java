package bankingSystem.transactions;

import bankingSystem.core.Account;

public class BillPayment extends Transaction {
    private String recipientAccount;

    public BillPayment(double amount, String currency, String recipientAccount) {
        super(amount, currency);
        this.recipientAccount = recipientAccount;
    }

    public BillPayment() {
		// TODO Auto-generated constructor stub
	}

	public String getRecipient() {
        return recipientAccount;
    }

    public void setRecipient(String recipient) {
        this.recipientAccount = recipient;
    }

	@Override
	public void updateBalance(double amount, String currency) {
		// TODO Auto-generated method stub
		
	}
	
    public boolean handleExecution(double amount, String currency, Account account) {
        System.out.println("Executing transfer transaction...");
        return account.getBankAccount(0).subtractBalance(amount, currency, true);
    }
}
