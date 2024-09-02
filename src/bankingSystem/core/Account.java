package bankingSystem.core;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Account {
    private String name;
    private String customerType;
    private String id;
    private Date dateOfBirth;
    private List<BankAccount> bankAccounts;

    public Account(String name, String customerType, String id, Date dateOfBirth) {
        this.name = name;
        this.customerType = customerType;
        this.id = id;
        this.dateOfBirth = dateOfBirth;
        this.bankAccounts = new ArrayList<>();
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getCustomerType() {
        return customerType;
    }

    public String getId() {
        return id;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setCustomerType(String customerType) {
        this.customerType = customerType;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
    
    // Others
    public void addBankAccount(double balance, String currency) throws IllegalArgumentException {
        if (balance < 0) {
            throw new IllegalArgumentException("Balance cannot be negative");
        }
        bankAccounts.add(new BankAccount(balance, currency));
    }

    public BankAccount getBankAccount(int index) throws IndexOutOfBoundsException {
        if (index < 0 || index >= bankAccounts.size()) {
            throw new IndexOutOfBoundsException("Invalid bank account index");
        }
        return bankAccounts.get(index);
    }
    
    public int calculateAge() {
        LocalDate birthDate = dateOfBirth.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate currentDate = LocalDate.now();
        Period period = Period.between(birthDate, currentDate);
        return period.getYears();
    }
    
    @Override
    public String toString() {
        return "Account{" +
                "name='" + name + '\'' +
                ", customerType='" + customerType + '\'' +
                ", id='" + id + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", bankAccounts=" + bankAccounts +
                '}';
    }
}
