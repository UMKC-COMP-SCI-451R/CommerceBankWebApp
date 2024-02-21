package com.commercebank.accounts;

import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UuidGenerator;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Accounts")
public class Accounts {

    @Id
    @SequenceGenerator(name="accid_gen", initialValue = 11331734)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "accid_gen")
    private Integer AccID;

    @Column(nullable = false, unique = true, length = 45)
    private String email;
    @Column(nullable = false, length = 45)
    private String password;
    @Column(nullable = false, length = 45)
    private String firstName;
    @Column(nullable = false, length = 45)
    private String lastName;

    @Column(columnDefinition = "double default 0.00")
    private double balance;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Transactions> transactionsList = new ArrayList<>();

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private  List<ExternalAccounts> externalAccountsList = new ArrayList<>();

    @Column(nullable = false,length = 45)
    private String address;

    @Column(nullable = false,length = 15)
    private String phoneNumber;

    @Column(nullable = false,length = 16)
    private long cardNumber;
    @Column()
    private boolean multifactorAuth = false;
    @Column()
    private boolean paperless = false;
    @Column()
    private boolean emailAlert = false;
    @Column()
    private boolean textAlert = false;


    public List<Transactions> getTransactionsList() {
        return transactionsList;
    }

    public List<ExternalAccounts> getExternalAccountsList() {
        return externalAccountsList;
    }


    public double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public Integer getAccID() {
        return AccID;
    }

    public void setAccID(Integer accID) {
        AccID = accID;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public long getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(long cardNumber) {
        this.cardNumber = cardNumber;
    }

    public boolean isMultifactorAuth() {
        return multifactorAuth;
    }

    public void setMultifactorAuth(boolean multifactorAuth) {
        this.multifactorAuth = multifactorAuth;
    }

    public boolean isPaperless() {
        return paperless;
    }

    public void setPaperless(boolean paperless) {
        this.paperless = paperless;
    }

    public boolean isEmailAlert() {
        return emailAlert;
    }

    public void setEmailAlert(boolean emailAlert) {
        this.emailAlert = emailAlert;
    }

    public boolean isTextAlert() {
        return textAlert;
    }

    public void setTextAlert(boolean textAlert) {
        this.textAlert = textAlert;
    }



    @Override
    public String toString() {
        StringBuilder content = new StringBuilder("Accounts{" +
                "AccID=" + AccID +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", balance=" + balance +
                ", isPaperless= " + paperless +
                ", isMulti= "+multifactorAuth+
                ", istext= "+ textAlert+
                ", isEmailAlert= " + emailAlert+
                '}');
        for(Transactions trans : this.transactionsList){
            content.append("\n").append(trans.toString());
        }
        for(ExternalAccounts exAcc : this.externalAccountsList){
            content.append("\n").append(exAcc.toString());
        }
        return content.toString();
    }
}
