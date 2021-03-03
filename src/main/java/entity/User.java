package entity;

import enums.AccountType;
import enums.SubscriptionStatus;
import org.bson.types.ObjectId;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class User {
    private ObjectId id;
    private String username;
    private String firstname;
    private String lastname;
    private String email;
    private String password;
    private Settings settings = new Settings();
    private int credits = 0;
    private String ip = "";
    private Date lastSeen;
    private AccountType accountType = AccountType.NOT_VERIFIED;
    private SubscriptionStatus subscriptionStatus = SubscriptionStatus.BASIC;
    private String authToken;
    public List<String> imageFactoryName = new LinkedList<String>();

    public User(
            ObjectId id,
            String firstname,
            String lastname,
            String username,
            String password,
            String email,
            Settings settings,
            int credits,
            AccountType accountType,
            SubscriptionStatus subscriptionStatus,
            String authToken
    ){
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.username = username;
        this.password = password;
        this.email = email;
        this.settings = settings;
        this.credits = credits;
        this.accountType = accountType;
        this.subscriptionStatus = subscriptionStatus;
        this.authToken = authToken;
    }

    public User(){
        this.setSubscriptionStatus(SubscriptionStatus.BASIC);
        this.setAccountType(AccountType.NOT_VERIFIED);
        this.setCredits(0);
        this.setSettings(new Settings());
    }

    public List<String> getImageFactoryName() {
        return imageFactoryName;
    }

    public void setImageFactoryName(List<String> imageFactoryName) {
        this.imageFactoryName = imageFactoryName;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Settings getSettings() {
        return settings;
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Date getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(Date lastSeen) {
        this.lastSeen = lastSeen;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public SubscriptionStatus getSubscriptionStatus() {
        return subscriptionStatus;
    }

    public void setSubscriptionStatus(SubscriptionStatus subscriptionStatus) {
        this.subscriptionStatus = subscriptionStatus;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
}
