package entity;

import enums.AccountStatus;
import org.bson.types.ObjectId;

import java.util.Date;

public class User {

    private ObjectId id;
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private String email;
    private String settings;
    private int credits;
    private String ip;
    private Date lastSeen;
    private AccountStatus accountStatus;

    public User(
            ObjectId id,
            String firstName,
            String lastName,
            String username,
            String password,
            String email,
            String settings,
            int credits,
            String ip,
            Date lastSeen,
            AccountStatus accountStatus
    ){
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.email = email;
        this.settings = settings;
        this.credits = credits;
        this.ip = ip;
        this.lastSeen = lastSeen;
        this.accountStatus = accountStatus;
    }

    public User(){

    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
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

    public AccountStatus getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(AccountStatus accountStatus) {
        this.accountStatus = accountStatus;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSettings() {
        return settings;
    }

    public void setSettings(String settings) {
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

}
