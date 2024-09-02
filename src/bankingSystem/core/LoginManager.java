package bankingSystem.core;

import java.util.Date;
import java.util.List;

import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

public class LoginManager {
    public static boolean verifyCardStatus(CreditCard creditCard) {
    	return creditCard.getCardStatus().equals("Active");
    }
    
    public static Account verifyLogin(CreditCard creditCard, String password, List<Account> accounts) {
        for (Account account : accounts) {
            if (account.getId().equals(creditCard.getCardNumber()) &&
            	password.equals(creditCard.getCodeNIP()) &&
                !creditCard.getExpirationDate().before(new Date())) {
                return account;
            }
        }
        return null;
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

    private static Account parseAccountFromJson(String jsonLine) throws ParseException {
        String[] parts = jsonLine.split(",");
        String name = getValueFromJsonPart(parts[0]);
        String customerType = getValueFromJsonPart(parts[1]);
        String id = getValueFromJsonPart(parts[2]);
        String dateOfBirthStr = getValueFromJsonPart(parts[3]);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date dateOfBirth = dateFormat.parse(dateOfBirthStr);

        return new Account(name, customerType, id, dateOfBirth);
    }

    private static String getValueFromJsonPart(String jsonPart) {
        String[] keyValue = jsonPart.split(":");
        return keyValue[1].replaceAll("\"", "").trim();
    }
    
    
    public static List<Account> readAccountsFromFile(String fileName) throws IOException, ParseException {
        List<Account> accounts = new ArrayList<>();
        StringBuilder jsonContent = new StringBuilder();
        try (FileReader fileReader = new FileReader(fileName)) {
            int character;
            while ((character = fileReader.read()) != -1) {
                jsonContent.append((char) character);
            }
        }

        String jsonString = jsonContent.toString();
        JSONArray jsonArray = new JSONArray(jsonString);

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String name = jsonObject.getString("name");
            String customerType = jsonObject.getString("customerType");
            String id = jsonObject.getString("id");
            
            // dateOfBirth
            String dateOfBirthStr = jsonObject.getString("dateOfBirth");
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date dateOfBirth = dateFormat.parse(dateOfBirthStr);
   
            // bankAccount
            JSONObject bankAccountJson = jsonObject.getJSONObject("bankAccount");
            double balance = bankAccountJson.getDouble("balance");
            String currency = bankAccountJson.getString("currency");

            Account account = new Account(name, customerType, id, dateOfBirth);
            account.addBankAccount(balance, currency);
            accounts.add(account);
        }
        System.out.println("accounts" + accounts);
        return accounts;
    }
}

