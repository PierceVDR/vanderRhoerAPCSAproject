public class Customer {
    String name;
    String PIN;

    Account savings;
    Account checking;

    public Customer(String name, String PIN) {
        this.name=name;
        this.PIN=PIN;

        savings = new Account(name + "'s Savings Account");
        checking = new Account(name + "'s Checking Account");
    }

    public Account getSavings() {return savings;}
    public Account getChecking() {return checking;}

    public void setPIN(String newPIN) {
        PIN=newPIN;
    }

    public boolean checkPIN(String enteredPIN) {return PIN.equals(enteredPIN);}
}
