
import java.util.Scanner;
import java.text.NumberFormat;
import java.util.Locale;

public class ATM {
    private Scanner scan = new Scanner(System.in);
    private NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.US);

    private static int lastTransactionID = 10000;

    private Customer customer;

    public ATM(){
        customer=null;
    }

    private static String generateTransactionInfo() {
        lastTransactionID++;
        return "\nTransaction ID: #" + lastTransactionID;
    }

    public void start() {
        System.out.println("\nWelcome, new user!\nFollow the steps below to create a new account.");
        String name = getInputString("Enter your name");
        String PIN = getInputPIN("Enter a PIN");

        customer = new Customer(name,PIN);
        System.out.println("\nAccount created!");

        returnToMenu();
    }

    private void menu(){
        System.out.println("\n\n----------MENU-----------");
        System.out.println("Actions:\n" +
                " 1. Withdraw money\n" +
                " 2. Deposit money\n" +
                " 3. Transfer money between accounts\n" +
                " 4. Get account balances\n" +
                " 5. Change PIN\n" +
                " 6. Exit\n");
        int action= getInput("Enter the number of what action you would like to perform",6);

        if (action==1) {
            withdraw();
        } else if (action==2) {
            deposit();
        } else if (action==3) {
            transfer();
        } else if (action==4) {
            getBalances();
        } else if (action==5) {
            changePIN();
        } else {
            exit();
        }
    }

    private void withdraw() {
        String transactionInfo = generateTransactionInfo(); // Gives the transaction a unique ID for the receipt

        // Choosing account:
        System.out.println("\n\nAccounts:\n" +
                " 1. Savings\n" +
                " 2. Checking\n");
        int action = getInput("Enter the number correlating to the account you would like to withdraw from",2);
        Account account;
        if (action==1) {
            account=customer.getSavings();
        } else {
            account=customer.getChecking();
        }
        String accountName = account.getName();

        System.out.println("\n");
        // Getting amount:
        double amount;
        while (true) {
            amount = getPositiveDouble("Enter the amount of money you would like to withdraw");
            if (amount>account.getMoney()) {
                System.out.println("Insufficient funds in " + accountName + "; money not withdrawn!" + transactionInfo);
                returnToMenu(); // Stop early
                return;
            } else if (amount==0) {
                System.out.println("No money withdrawn from " + accountName + "!" + transactionInfo);
                returnToMenu(); // Stop early
                return;
            } else if (amount%5!=0) {
                System.out.println("! Unable to dispense that exact quantity; you may only withdraw multiples of 5 !");
            } else {
                break;
            }
        }

        System.out.println("\n");
        // Getting # of $5 & $20 bills:
        int max = ((int) amount/20);
        int twentyDollarBills;

        System.out.println("Money will be dispensed in $5 and/or $20 bills.");
        if (max>0) { // Don't need to ask if amount is less than 20
            String prompt = "Enter how many $20 bills you would like to receive";
            prompt += "\n At most " + max + " $20 bills can be dispensed";
            twentyDollarBills = getIntInRange(prompt, 0, max);
        } else {
            twentyDollarBills=0;
        }

        int fiveDollarBills=((int) amount - 20*twentyDollarBills)/5;

        System.out.println("\n");
        // Dispensing money:
        if (account.removeMoney(amount)) {
            dispense(fiveDollarBills, twentyDollarBills);
            // While not necessary here, in a real ATM dispensing money would probably be its own method
            // Here, though, it just prints part of the receipt
            System.out.println(formatter.format(amount) + " successfully withdrawn from " + accountName + "!");
            System.out.println(" Remaining balance: " + account.getFormattedMoney() + transactionInfo);
        } else { // This side of the if/else statement will never actually be called, but
            // realistically transactions aren't correctly processed 100% of the time

            // The other transaction methods have a similar error message too
            System.out.println("!? Error occurred; money not withdrawn !?" + transactionInfo);
        }

        returnToMenu();
    }

    private void dispense(int fiveDollarBills, int twentyDollarBills) {
        int total = fiveDollarBills*5 + twentyDollarBills*20;
        String message="$" + total + " dispensed!";
        if (fiveDollarBills!=0) {message+="\n " + fiveDollarBills + " $5 bills";}
        if (twentyDollarBills!=0) {message+= "\n " + twentyDollarBills + " $20 bills";}

        System.out.println(message);
    }
    private void deposit() {
        String transactionInfo = generateTransactionInfo(); // Again, this gives the transaction an ID for the receipt

        // Choosing account:
        System.out.println("\n\nAccounts:\n" +
                " 1. Savings\n" +
                " 2. Checking\n");
        int action = getInput("Enter the number correlating to the account you would like to deposit money into",2);
        Account account;
        if (action==1) {
            account=customer.getSavings();
        } else {
            account=customer.getChecking();
        }
        String accountName = account.getName();

        System.out.println("\n");
        // Getting amount:
        double amount = getPositiveDouble("Enter the amount of money you would like to deposit");

        System.out.println("\n");
        // Adding money to account:
        if (amount==0) {
             System.out.println("No money deposited into " + accountName + "!" + transactionInfo);
             returnToMenu();
             return; // Stop early
        } else if (account.addMoney(amount)) {
            System.out.println(formatter.format(amount) + " successfully deposited into " + accountName + "!");
            System.out.println(" " + accountName + " balance: " + account.getFormattedMoney() + transactionInfo);
        } else {
            System.out.println("!? Error occurred; money not deposited into " + accountName + " !?" + transactionInfo);
        }

        returnToMenu();
    }

    private void transfer() {
        String transactionInfo = generateTransactionInfo();

        // Choosing account:
        System.out.println("\n\nAccounts:\n" +
                " 1. Savings\n" +
                " 2. Checking\n");
        int action = getInput("Enter the number correlating to the account you would like to transfer money FROM",2);

        Account savingsAccount=customer.getSavings(); // Declaring & initializing these 2 variables
        Account checkingAccount=customer.getChecking(); // is convenient for the receipt later on

        Account fromAccount;
        Account toAccount;
        if (action==1) {
            fromAccount=savingsAccount;
            toAccount=checkingAccount;
        } else {
            fromAccount=checkingAccount;
            toAccount=savingsAccount;
        }
        String fromAccountName = fromAccount.getName();
        String toAccountName = toAccount.getName();

        System.out.println("\n");
        // Getting amount:
        double amount;
        while (true) {
            amount = getPositiveDouble("Enter the amount of money you would like to transfer");

            if (amount>fromAccount.getMoney()) {
                System.out.println("Insufficient funds in " + fromAccountName + "; money not transferred!" + transactionInfo);
                returnToMenu(); // Stop early
                return;
            } else if (amount==0) {
                System.out.println("No money transferred from " + fromAccountName + " to " + toAccountName + "!" + transactionInfo);
                returnToMenu(); // Stop early
                return;
            } else {
                break;
            }
        }

        System.out.println("\n");
        // Transferring money to account:
        if (fromAccount.transferMoney(amount,toAccount)) {
            System.out.println(formatter.format(amount) + " successfully transferred from " + fromAccountName + " to " + toAccountName+ "!");
            // We want to preserve the order (1) Savings and (2) Checking
            System.out.println(" " + savingsAccount.getName() + " balance: " + savingsAccount.getFormattedMoney());
            System.out.println(" " + checkingAccount.getName() + " balance: " + checkingAccount.getFormattedMoney() + transactionInfo);

        } else {
            System.out.println("!? Error occurred; money not transferred from " + fromAccountName + " to " + toAccountName +  " !?" + transactionInfo);
        }

        returnToMenu();
    }

    private void getBalances() {
        System.out.println("\n\nYour account balances: ");
        System.out.println(" Savings Account: " + customer.getSavings().getFormattedMoney());
        System.out.println(" Checking Account: " + customer.getChecking().getFormattedMoney());

        returnToMenu();
    }

    private void changePIN() {
        System.out.print("\n\n");
        customer.setPIN(getInputPIN("Enter the new PIN"));

        System.out.println("\nPIN successfully changed!");

        returnToMenu();
    }

    private void returnToMenu() {
        System.out.println("\nOptions:" +
                "\n 1. Continue, return to menu" +
                "\n 2. I'm done");
        int action = getInput("Enter whether there's anything else you'd like to do",2);

        if (action==1) {
            int attempts = 4;
            while (attempts>0) {
                String PIN = getInputPIN("Re-enter your PIN to continue");
                if (customer.checkPIN(PIN)) {
                    menu();
                    return; // While loop has code after it
                    // It will continue if you don't tell the method to stop
                } else {
                    attempts--;
                    System.out.println("! PIN incorrect, " + attempts + " attempt(s) remaining !");
                }
            }
            System.out.println("Incorrect PIN entered too many times, exiting program!");
            exit();
        } else {
            exit();
        }
    }

    private void exit() {
        customer=null;
        System.out.println("\nThank you for your business, come again soon!");
    }

    // Helper method for getting the integer input
    private int getInput(String prompt, int maxNum) { // For a list of actions
        double action=0;
        while (true) {
            System.out.println(prompt);
            System.out.print(" > ");
            action = Double.parseDouble(scan.nextLine());
            if (action<1 || maxNum<action || action%1!=0) { // If invalid..
                System.out.println("! You must enter a valid option !");
            } else { // If valid...
                break;
            }
        }

        return (int) action;
    }

    private double getPositiveDouble(String prompt) { // Get positive (or zero) number with decimals
        // This method's useful for getting quantities of money
        double n;
        while (true) {
            System.out.println(prompt);
            System.out.print(" > $");
            n = Double.parseDouble(scan.nextLine());
            if (n>=0) { // If valid...
                break;
            } else { // If invalid...
                System.out.println("! You must enter a number greater than zero !");
            }
        }

        return n;
    }

    private int getIntInRange(String prompt, int min, int max) { // Like getInput, but specifically for getting quantities that are integer numbers instead of a number correlating to an action.
        double n;
        while (true) {
            System.out.println(prompt);
            System.out.print(" > ");
            n = Double.parseDouble(scan.nextLine());
            if (n%1!=0) {
                System.out.println("! You must enter a whole number !");
            } else if (n<min || n>max) {
                System.out.println("! Number must be at least " + min + " and at most " + max + " !");
            } else {
                break;
            }
        }

        return (int) n;
    }

    private String getInputString(String prompt) {
        System.out.println(prompt);
        System.out.print(" > ");
        return scan.nextLine();
    }

    /* Right now, this method is no different from getInputString, but
     * it exists to make adding requirements for a PIN (ex: Must be 4 digits)
     * easier if that ever needed to be done
     */
    private String getInputPIN(String prompt) {return getInputString(prompt);}

}
