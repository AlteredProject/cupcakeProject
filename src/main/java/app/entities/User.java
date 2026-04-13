package app.entities;

public class User {
    private int userId;
    private String email;
    private String password;
    private double balance;
    private boolean isAdmin;

    public User(int userId, String email, String password, double balance, boolean isAdmin) {
        this.userId = userId;
        this.email = email;
        this.password = password;
        this.balance = balance;
        this.isAdmin = isAdmin;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }
}


