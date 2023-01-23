import java.text.NumberFormat;
import java.util.Locale;

public class Account {
    private NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.US);

    private Customer owner;
    private double money;
    private String name;

    public Account(String name, Customer owner) {
        this.owner=owner;
        this.name=name;
        money=0;
    }

//    public Account(Customer owner, String name, int money) {
//        this.owner=owner;
//        this.name=name;
//        this.money=money;
//    }

    public double getMoney() {return money;}
    public String getName() {return name;}

    public String getFormattedMoney() {
        return formatter.format(getMoney());
    }

    public boolean addMoney(double amount) {
        if (amount>0) {
            money+=amount;
            return true;
        } else {
            return false;
        }
    }

    public boolean removeMoney(double amount) {
        if (amount<=money && amount>0) {
            money-=amount;
            return true;
        } else {
            return false;
        }
    }

    public boolean transferMoney(double amount, Account toAccount) {
        if ( (amount<=money && amount>0) && toAccount.addMoney(amount)) {
            money-=amount;
            return true;
        } else {
            return false;
        }
    }
}
