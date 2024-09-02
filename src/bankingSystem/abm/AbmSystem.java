package bankingSystem.abm;

import bankingSystem.core.*;
import bankingSystem.transactions.*;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class AbmSystem {
	private double amountAvailable;
    private CreditCard creditCard;
    private List<Account> accounts;
    private Transaction transaction;
    private Account account;
    
    public AbmSystem() {
    	amountAvailable = 10000;
    }
    
    public void blockCard() {
    	creditCard.setCardStatus("blocked");
    	// add to change on json file
    }

    public static CreditCard readCreditCardInfoFromFile(String fileName) throws IOException,
            InvalidCreditCardArgumentException, InvalidSecurityCodeException, InvalidExpirationDateException {
        StringBuilder jsonContent = new StringBuilder();
        try (FileReader reader = new FileReader(fileName)) {
            int character;
            while ((character = reader.read()) != -1) {
                jsonContent.append((char) character);
            }
        }
        String jsonString = jsonContent.toString();
        JSONArray jsonArray = new JSONArray(jsonString);

        JSONObject jsonObject = jsonArray.getJSONObject(0);
        String cardNumber = jsonObject.getString("cardNumber");
        String codeNIP = jsonObject.getString("codeNIP");
        String expirationDate = jsonObject.getString("expirationDate");
        String securityCode = jsonObject.getString("securityCode");
        String cardStatus = jsonObject.getString("cardStatus");

        return new CreditCard(cardNumber, codeNIP, expirationDate, securityCode, cardStatus);
    }

	public CreditCard getCreditCard() {
		return creditCard;
	}

	public void setCreditCard(CreditCard creditCard) {
		this.creditCard = creditCard;
	}

	public List<Account> getAccounts() {
		return accounts;
	}

	public void setAccounts(List<Account> accounts) {
		this.accounts = accounts;
	}

	public boolean inputTransaction(String transactionSelected) {
	    switch(transactionSelected) {
	        case "Withdrawal": 
	            return createAndCheckTransaction(new Withdrawal());
	        case "Transfer":
	            return createAndCheckTransaction(new Transfer());
	        case "Bill Payment":
	            return createAndCheckTransaction(new BillPayment());
	        case "Deposit":
	            return createAndCheckTransaction(new Deposit());
	        case "Exchange Money":
	            return createAndCheckTransaction(new Exchange());
	        default:
	            return false;
	    }
	}

	private boolean createAndCheckTransaction(Transaction transaction) {
//		System.out.println(transaction);
	    this.transaction = transaction.transactionAllowed(getAccount()) ? transaction : null;
	    return this.transaction != null;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}
	
	public Transaction getTransaction() {
		return transaction;
	}

	public boolean handleTransaction(double amount, String currency) {
		return transaction.handleExecution(amount, currency, account);
	}
	
	public double handleTransactionWithoutAccount(double amount, String currentCurrency, String desiredCurrency) {
		return transaction.handleExecution(amount, currentCurrency, desiredCurrency);
	}
	
    public Object[][] getDataFromAccounts() {
        Object[][] data = new Object[accounts.size()][3];
        
        for (int i = 0; i < accounts.size(); i++) {
            Account account = accounts.get(i);
            data[i][0] = account.getName();
            data[i][1] = account.getCustomerType();
            data[i][2] = account.getId();
        }
        return data;
    }
	
	public void cleanAbmSystem() {
		
	}
}
