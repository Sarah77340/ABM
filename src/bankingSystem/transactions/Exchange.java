package bankingSystem.transactions;

import bankingSystem.core.BankAccount;

public class Exchange extends Transaction {
    private String targetCurrency;

    public Exchange(double amount, String sourceCurrency, String targetCurrency) {
        super(amount, sourceCurrency);
        this.targetCurrency = targetCurrency;
    }

    public Exchange() {
		// TODO Auto-generated constructor stub
	}

	public String getTargetCurrency() {
        return targetCurrency;
    }

    public void setTargetCurrency(String targetCurrency) {
        this.targetCurrency = targetCurrency;
    }

	@Override
	public void updateBalance(double amount, String currency) {
	}
	
	public double handleExecution(double amount, String currency, String targetCurrency) {
		System.out.println("Executing exchange transaction...");
		return (targetCurrency.equals(currency)) ? amount : BankAccount.convertCurrency(amount, targetCurrency, currency);
	}
}

