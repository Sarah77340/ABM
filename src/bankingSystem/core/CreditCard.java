package bankingSystem.core;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CreditCard {
	private String cardNumber;
	private String codeNIP;
	private Date expirationDate;
	private String securityCode;
	private String cardStatus;
	
	public CreditCard(String cardNumber, String codeNIP, String expirationDateStr, 
			String securityCode, String cardStatus) throws InvalidCreditCardArgumentException, 
			InvalidExpirationDateException, InvalidSecurityCodeException {
		
		if (cardNumber == null || cardNumber.isEmpty() ||
            codeNIP == null || codeNIP.isEmpty() ||
            securityCode == null || securityCode.isEmpty() ||
            cardStatus == null || cardStatus.isEmpty()) {
            throw new InvalidCreditCardArgumentException("All arguments are mandatory and cannot be empty");
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/yyyy");
        dateFormat.setLenient(false);
        try {
            expirationDate = dateFormat.parse(expirationDateStr);
        } catch (ParseException e) {
            throw new InvalidExpirationDateException("Invalid expiration date format. Use MM/yyyy format", e.getErrorOffset());
        }

        if (securityCode.length() != 3) {
            throw new InvalidSecurityCodeException("Security code must be composed of 3 digits");
        }

        this.cardNumber = cardNumber;
        this.codeNIP = codeNIP;
        this.securityCode = securityCode;
        this.cardStatus = cardStatus;
	}
	
	// Setters
    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public void setCodeNIP(String codeNIP) {
        this.codeNIP = codeNIP;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public void setSecurityCode(String securityCode) {
        this.securityCode = securityCode;
    }

    public void setCardStatus(String cardStatus) {
        this.cardStatus = cardStatus;
    }

    // Getters
    public String getCardNumber() {
        return cardNumber;
    }

    public String getCodeNIP() {
        return codeNIP;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public String getSecurityCode() {
        return securityCode;
    }

    public String getCardStatus() {
        return cardStatus;
    }

    // Others
    @Override
    public String toString() {
        return "CreditCard{" +
                "cardNumber='" + cardNumber + '\'' +
                ", codeNIP='" + codeNIP + '\'' +
                ", expirationDate=" + expirationDate +
                ", securityCode='" + securityCode + '\'' +
                ", cardStatus='" + cardStatus + '\'' +
                '}';
    }
}
