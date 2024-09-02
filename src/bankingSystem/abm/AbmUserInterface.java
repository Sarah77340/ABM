package bankingSystem.abm;

import bankingSystem.core.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.ParseException;
import java.util.Objects;
import java.util.Stack;

public class AbmUserInterface extends JFrame {

    private final static String resourcesPath = System.getProperty("user.dir") + "\\resources\\";

    private int loginAttempts;
    private String language = "French";
    private boolean soundOption = false;
    private JTextField passwordField;
    private JButton loginButton;
    private JPanel mainPanel;
    private JPanel logoutPanel;
    private JButton logoutButton;
    private JLabel passwordLabel;
    private static AbmSystem abm;
    private Stack<JPanel> pageHistory = new Stack<>();

    public AbmUserInterface() {
        super("ABM");
        abm = new AbmSystem();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 400);
        setLocationRelativeTo(null);
        initUI();
    }

    public static void main(String[] args) throws ParseException {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                AbmUserInterface ui = new AbmUserInterface();
                try {
                    abm.setCreditCard(LoginManager.readCreditCardInfoFromFile(resourcesPath + "credit_card_info.json"));
                    abm.setAccounts(LoginManager.readAccountsFromFile(resourcesPath + "accounts.json"));
                    ui.setVisible(true);
                } catch (IOException | InvalidCreditCardArgumentException |
                        InvalidSecurityCodeException | ParseException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void login() throws IOException, InvalidExpirationDateException {
        String password = passwordField.getText();
        try {
        	abm.setAccount(LoginManager.verifyLogin(abm.getCreditCard(), password, abm.getAccounts()));
        	
        	if(abm.getCreditCard().getCardStatus().equals("blocked")) {
        		JOptionPane.showMessageDialog(this, "Your card is blocked.");
        	} else if (!Objects.isNull(abm.getAccount())) {
                showMainMenu();
            } else {
                JOptionPane.showMessageDialog(this, "Login failed! Please try again.");
                passwordField.setText("");
                loginAttempts++;

                if (loginAttempts >= 3) {
                    JOptionPane.showMessageDialog(this, "Too many unsuccessful login attempts. Card blocked.");
                    abm.blockCard();
                    loginAttempts = 0;
                    System.out.println(abm.getCreditCard().getCardStatus());
                }
            }
        } catch (InvalidCreditCardArgumentException |
                InvalidSecurityCodeException e) {
            e.printStackTrace();
        }
    }
    
    private void initUI() {
        mainPanel = createLoginPanel();
        add(mainPanel, BorderLayout.CENTER);
    }
    
    private void resetUI() {
    	remove(mainPanel);
        remove(logoutPanel);
        mainPanel = createLoginPanel();
        add(mainPanel, BorderLayout.CENTER);
        validate();
        repaint();
    }

    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        passwordLabel = new JLabel("Enter your PIN:");
        passwordField = new JPasswordField(4);
        loginButton = new JButton("Login");

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    login();
                } catch (InvalidExpirationDateException | IOException e1) {
                    e1.printStackTrace();
                }
            }
        });

        // Label
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.LINE_END; // Align the label to the right
        panel.add(passwordLabel, gbc);

        // Password field
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START; // Align the text field to the left
        panel.add(passwordField, gbc);

        // Login button
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(loginButton, gbc);

        return panel;
    }

    private void showMainMenu() {
        mainPanel.removeAll();
        mainPanel.setLayout(new GridLayout(6, 1));
        
        // Transactions buttons
        String[] transactionNames = {"Bill Payment", "Transfer", "Deposit", "Withdrawal", "Exchange Money"};
        JButton[] transactionButtons = new JButton[transactionNames.length];
  
        for (int i = 0; i < transactionNames.length; i++) {
            transactionButtons[i] = new JButton(transactionNames[i]);
            String transactionName = transactionNames[i];
            transactionButtons[i].addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if(abm.inputTransaction(transactionName)) {
                    	System.out.println("Transaction permission ok");
                    	showTransactionPage(transactionName);
                    } else {
                    	JOptionPane.showMessageDialog(AbmUserInterface.this, "Transaction permission denied");
                    }
                }
            });
            mainPanel.add(transactionButtons[i]);
        }

        // Logout button
        logoutButton = new JButton("Logout");
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(AbmUserInterface.this, "Thank you for using ABM. See you soon!");
                abm.cleanAbmSystem();
                resetUI();
            }
        });
        logoutPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        logoutPanel.add(logoutButton);
        add(logoutPanel, BorderLayout.NORTH);

        validate();
        repaint();
    }
    
    private void showTransactionPage(String transactionName) {
    	switch (transactionName) {
        case "Bill Payment":
            // Afficher la page pour le paiement de factures
            showBillPaymentPage();
            break;
        case "Transfer":
            // Afficher la page pour les transferts
            showTransferPage();
            break;
        case "Deposit":
            // Afficher la page pour les dépôts
            showDepositPage();
            break;
        case "Withdrawal":
            // Afficher la page pour les retraits
            showWithdrawalPage();
            break;
        case "Exchange Money":
            // Afficher la page pour l'échange de devises
            showExchangeMoneyPage();
            break;
        default:
            break;
    	}
    } 
    
    private void showBillPaymentPage() {
        pageHistory.push(mainPanel);
        mainPanel.removeAll();
        mainPanel.setLayout(new GridLayout(5, 1));

        // Title
        JLabel titleLabel = new JLabel("Bill Payment", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));

        // accountInfoPanel
        JPanel accountInfoPanel = new JPanel(new GridLayout(1, 2));
        JLabel balanceLabel = new JLabel("BankAccount balance:");
        JLabel currencyLabelInfo = new JLabel("Currency:");
        BankAccount bankAccount = abm.getAccount().getBankAccount(0);
        JLabel balanceValueLabel = new JLabel(Double.toString(bankAccount.getBalance()));
        JLabel currencyValueLabel = new JLabel(bankAccount.getCurrency());

        balanceValueLabel.setForeground(Color.BLUE);
        currencyValueLabel.setForeground(Color.BLUE);

        accountInfoPanel.add(balanceLabel);
        accountInfoPanel.add(balanceValueLabel);
        accountInfoPanel.add(currencyLabelInfo);
        accountInfoPanel.add(currencyValueLabel);

        // Accounts table
        Object[][] data = {
            {"Invoice 001", 100.00},
            {"Invoice 002", 150.00},
            {"Invoice 003", 200.00}
        };
        
        String[] columnNames = {"Invoice Name", "Amount"};
        JTable table = new JTable(data, columnNames);
        JScrollPane scrollPane = new JScrollPane(table);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Confirmation Button
        JPanel confirmButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton confirmButton = new JButton("Confirm");
        confirmButtonPanel.add(confirmButton);
        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	int selectedRow = table.getSelectedRow();
                double amount = (double) table.getValueAt(selectedRow, 1);
                String currency = bankAccount.getCurrency();
                System.out.println("amount:" + amount + " currency:" + currency);
                if (selectedRow != -1 && !abm.handleTransaction(amount, currency)) {
                    JOptionPane.showMessageDialog(AbmUserInterface.this, "Exceeds the balance limit.");
                } else {
                    System.out.println("reussite");
                    int choice = JOptionPane.showConfirmDialog(AbmUserInterface.this, "Transaction successful. Would you like to print a receipt?", "Print Receipt", JOptionPane.YES_NO_OPTION);
                    if (choice == JOptionPane.YES_OPTION) {
                        // Code to print receipt goes here
                        System.out.println("Printing receipt...");
                        printReceipt();
                    }
                    showMainMenu();
                }
            }
        });

     // Cancel
        JButton cancelButton = new JButton("Cancel");
        confirmButtonPanel.add(cancelButton);
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	showMainMenu();
            }
        });

        // Ajout des composants au mainPanel
        mainPanel.add(titleLabel);
        mainPanel.add(accountInfoPanel);
        mainPanel.add(scrollPane);
        mainPanel.add(confirmButtonPanel);
        validate();
        repaint();
    }

    private void showTransferPage() {
        pageHistory.push(mainPanel);
        mainPanel.removeAll();
        mainPanel.setLayout(new GridLayout(5, 1));

        // Title
        JLabel titleLabel = new JLabel("Transfer", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));

        // accountInfoPanel
        JPanel accountInfoPanel = new JPanel(new GridLayout(1, 2));
        JLabel balanceLabel = new JLabel("BankAccount balance:");
        JLabel currencyLabelInfo = new JLabel("Currency:");
        BankAccount bankAccount = abm.getAccount().getBankAccount(0);
        JLabel balanceValueLabel = new JLabel(Double.toString(bankAccount.getBalance()));
        JLabel currencyValueLabel = new JLabel(bankAccount.getCurrency());

        balanceValueLabel.setForeground(Color.BLUE);
        currencyValueLabel.setForeground(Color.BLUE);

        accountInfoPanel.add(balanceLabel);
        accountInfoPanel.add(balanceValueLabel);
        accountInfoPanel.add(currencyLabelInfo);
        accountInfoPanel.add(currencyValueLabel);

        // Accounts table
        String[] columnNames = {"Name", "Customer Type", "ID"};
        Object[][] data = abm.getDataFromAccounts();
        JTable table = new JTable(data, columnNames);
        JScrollPane tableScrollPane = new JScrollPane(table);

        // inputPanel
        JPanel inputPanel = new JPanel(new GridLayout(2, 2));
        JLabel amountLabel = new JLabel("Amount:");
        JTextField amountField = new JTextField("0");
        JLabel currencyLabel = new JLabel("Currency:");
        JComboBox<String> currencyComboBox = new JComboBox<>(new String[]{"CAD", "EUR", "USD"});

        inputPanel.add(amountLabel);
        inputPanel.add(amountField);
        inputPanel.add(currencyLabel);
        inputPanel.add(currencyComboBox);

        // Confirmation Button
        JPanel confirmButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton confirmButton = new JButton("Confirm");
        confirmButtonPanel.add(confirmButton);
        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                double amount = Double.parseDouble(amountField.getText());
                String currency = (String) currencyComboBox.getSelectedItem();
                if (!abm.handleTransaction(amount, currency)) {
                    JOptionPane.showMessageDialog(AbmUserInterface.this, "Exceeds the balance limit.");
                    amountField.setText("0");
                } else {
                    System.out.println("reussite");
                    int choice = JOptionPane.showConfirmDialog(AbmUserInterface.this, "Transaction successful. Would you like to print a receipt?", "Print Receipt", JOptionPane.YES_NO_OPTION);
                    if (choice == JOptionPane.YES_OPTION) {
                        // Code to print receipt goes here
                        System.out.println("Printing receipt...");
                        printReceipt();
                    }
                    showMainMenu();
                }
            }
        });

     // Cancel
        JButton cancelButton = new JButton("Cancel");
        confirmButtonPanel.add(cancelButton);
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	showMainMenu();
            }
        });

        // Ajout des composants au mainPanel
        mainPanel.add(titleLabel);
        mainPanel.add(accountInfoPanel);
        mainPanel.add(tableScrollPane);
        mainPanel.add(inputPanel);
        mainPanel.add(confirmButtonPanel);
        validate();
        repaint();
    }
    
    private void showDepositPage() {
        pageHistory.push(mainPanel);
        mainPanel.removeAll();
        mainPanel.setLayout(new GridLayout(5, 1));
        
        // Title
        JLabel titleLabel = new JLabel("Deposit", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));


        // accountInfoPanel
        JPanel accountInfoPanel = new JPanel(new GridLayout(1, 2));
        JLabel balanceLabel = new JLabel("BankAccount balance:");
        JLabel currencyLabelInfo = new JLabel("Currency:");
        BankAccount bankAccount = abm.getAccount().getBankAccount(0); 
        JLabel balanceValueLabel = new JLabel(Double.toString(bankAccount.getBalance())); 
        JLabel currencyValueLabel = new JLabel(bankAccount.getCurrency());

        balanceValueLabel.setForeground(Color.BLUE);
        currencyValueLabel.setForeground(Color.BLUE);

        accountInfoPanel.add(balanceLabel);
        accountInfoPanel.add(balanceValueLabel);
        accountInfoPanel.add(currencyLabelInfo);
        accountInfoPanel.add(currencyValueLabel);

        // inputPanel
        JPanel inputPanel = new JPanel(new GridLayout(2, 2));
        JLabel amountLabel = new JLabel("Amount:");
        JTextField amountField = new JTextField("0");
        JLabel currencyLabel = new JLabel("Currency:");
        JComboBox<String> currencyComboBox = new JComboBox<>(new String[]{"CAD", "EUR", "USD"});

        inputPanel.add(amountLabel);
        inputPanel.add(amountField);
        inputPanel.add(currencyLabel);
        inputPanel.add(currencyComboBox);

        // OptionPanel
        JPanel optionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JRadioButton option1 = new JRadioButton("Check");
        JRadioButton option2 = new JRadioButton("Cash");
        ButtonGroup optionGroup = new ButtonGroup();
        optionGroup.add(option1);
        optionGroup.add(option2);
        optionPanel.add(option1);
        optionPanel.add(option2);
        
        // Buttons
        JPanel confirmButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        // Confirmation
        JButton confirmButton = new JButton("Confirm");
        confirmButtonPanel.add(confirmButton);
        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                double amount = Double.parseDouble(amountField.getText());
                String currency = (String) currencyComboBox.getSelectedItem();
                JOptionPane.showMessageDialog(AbmUserInterface.this, "Please deposit your money");
                if (!abm.handleTransaction(amount, currency)) {
                    JOptionPane.showMessageDialog(AbmUserInterface.this, "Exceeds the balance limit.");
                    amountField.setText("0");
                } else {
                    System.out.println("reussite");
                    int choice = JOptionPane.showConfirmDialog(AbmUserInterface.this, "Transaction successful. Would you like to print a receipt?", "Print Receipt", JOptionPane.YES_NO_OPTION);
                    if (choice == JOptionPane.YES_OPTION) {
                        // Code to print receipt goes here
                        System.out.println("Printing receipt...");
                        printReceipt();
                    }
                    showMainMenu();
                }
            }
        });
        
        // Cancel
        JButton cancelButton = new JButton("Cancel");
        confirmButtonPanel.add(cancelButton);
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	showMainMenu();
            }
        });
        
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(accountInfoPanel);
        mainPanel.add(inputPanel);
        mainPanel.add(optionPanel);
        mainPanel.add(confirmButtonPanel);
        validate();
        repaint();
    }

    private void showExchangeMoneyPage() {
    	pageHistory.push(mainPanel);
        mainPanel.removeAll();
        mainPanel.setLayout(new GridLayout(8, 1));
        
        // Title
        JLabel titleLabel = new JLabel("Exchange", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));

        // inputPanel
        JPanel inputPanel = new JPanel(new GridLayout(1, 1));
        JLabel amountLabel = new JLabel("Amount:");
        JTextField amountField = new JTextField("0");
        JLabel currencyLabel = new JLabel("Current currency:");
        JComboBox<String> currentCurrencyComboBox = new JComboBox<>(new String[]{"CAD", "EUR", "USD"});
        JLabel desiredCurrencyLabel = new JLabel("Desired  currency:");
        JComboBox<String> desiredCurrentCurrencyComboBox = new JComboBox<>(new String[]{"CAD", "EUR", "USD"});

        inputPanel.add(amountLabel);
        inputPanel.add(amountField);
        inputPanel.add(currencyLabel);
        inputPanel.add(currentCurrencyComboBox);
        inputPanel.add(desiredCurrencyLabel);
        inputPanel.add(desiredCurrentCurrencyComboBox);

        // Confirmation
        JPanel confirmButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton confirmButton = new JButton("Confirm");
        confirmButtonPanel.add(confirmButton);
        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                double amount = Double.parseDouble(amountField.getText());
                String currentCurrency = (String) currentCurrencyComboBox.getSelectedItem();
                String desiredCurrency = (String) desiredCurrentCurrencyComboBox.getSelectedItem();
                double desiredAmount = abm.handleTransactionWithoutAccount(amount, currentCurrency, desiredCurrency);
                String message = "Please take your money: " + desiredAmount + " " + desiredCurrency;
                JOptionPane.showMessageDialog(AbmUserInterface.this, message);
                int choice = JOptionPane.showConfirmDialog(AbmUserInterface.this, "Exchange successful. Would you like to print a receipt?", "Print Receipt", JOptionPane.YES_NO_OPTION);
                if (choice == JOptionPane.YES_OPTION) {
                    // Code to print receipt goes here
                    System.out.println("Printing receipt...");
                    printReceipt();
                }
                showMainMenu();
            }
        });
        
        // Cancel
        JButton cancelButton = new JButton("Cancel");
        confirmButtonPanel.add(cancelButton);
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	showMainMenu();
            }
        });
        
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(inputPanel);
        mainPanel.add(confirmButtonPanel);
        validate();
        repaint();
    }
    
    private void showWithdrawalPage() {
    	pageHistory.push(mainPanel);
        mainPanel.removeAll();
        mainPanel.setLayout(new GridLayout(3, 1));
        
        // Title
        JLabel titleLabel = new JLabel("Withdrawal", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        
        // accountInfoPanel
        JPanel accountInfoPanel = new JPanel(new GridLayout(1, 2));
        JLabel balanceLabel = new JLabel("BankAccount balance:");
        JLabel currencyLabelInfo = new JLabel("Currency:");
        BankAccount bankAccount = abm.getAccount().getBankAccount(0); // Obtenez le compte bancaire actuellement sélectionné
        JLabel balanceValueLabel = new JLabel(Double.toString(bankAccount.getBalance())); // Récupérez le solde du compte
        JLabel currencyValueLabel = new JLabel(bankAccount.getCurrency()); // Récupérez la devise du compte

        balanceValueLabel.setForeground(Color.BLUE);
        currencyValueLabel.setForeground(Color.BLUE);

        accountInfoPanel.add(balanceLabel);
        accountInfoPanel.add(balanceValueLabel);
        accountInfoPanel.add(currencyLabelInfo);
        accountInfoPanel.add(currencyValueLabel);
        
        // inputPanel
        JPanel inputPanel = new JPanel(new GridLayout(2, 2));
        JLabel amountLabel = new JLabel("Amount:");
        JTextField amountField = new JTextField("0");
        JLabel currencyLabel = new JLabel("Currency:");
        JComboBox<String> currencyComboBox = new JComboBox<>(new String[]{"CAD", "EUR", "USD"});

        inputPanel.add(amountLabel);
        inputPanel.add(amountField);
        inputPanel.add(currencyLabel);
        inputPanel.add(currencyComboBox);

        // Validation
        JPanel confirmButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton confirmButton = new JButton("Confirm");
        confirmButtonPanel.add(confirmButton);
        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                double amount = Double.parseDouble(amountField.getText());
                String currency = (String) currencyComboBox.getSelectedItem();
                if(!abm.handleTransaction(amount, currency)) {
                	JOptionPane.showMessageDialog(AbmUserInterface.this, "Not enough funds available.");
                	amountField.setText("0");
                } else {
                	System.out.println("reussite");
                	int choice = JOptionPane.showConfirmDialog(AbmUserInterface.this, "Transaction successful. Would you like to print a receipt?", "Print Receipt", JOptionPane.YES_NO_OPTION);
                    if (choice == JOptionPane.YES_OPTION) {
                        // Code to print receipt goes here
                        System.out.println("Printing receipt...");
                        printReceipt();
                    }
                    showMainMenu();
                }
            }
        });
        
        // Cancel
        JButton cancelButton = new JButton("Cancel");
        confirmButtonPanel.add(cancelButton);
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	showMainMenu();
            }
        });
        
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(accountInfoPanel);
        mainPanel.add(inputPanel);
        mainPanel.add(confirmButtonPanel);
        validate();
        repaint();
    }
    
    private void printReceipt() {
        JDialog printingDialog = new JDialog();
        printingDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        printingDialog.setTitle("Receipt");
        JLabel printingLabel = new JLabel("Printing receipt...", SwingConstants.CENTER);
        printingDialog.add(printingLabel);
        printingDialog.setSize(200, 100);
        printingDialog.setLocationRelativeTo(null);
        printingDialog.setVisible(true);

        Timer timer = new Timer(3000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                printingDialog.dispose();
            }
        });
        timer.setRepeats(false);
        timer.start();
    }
    
}
