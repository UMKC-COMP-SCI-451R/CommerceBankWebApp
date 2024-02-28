package com.commercebank.accounts;

import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;

@Entity
@Table(name="external_accounts")
public class ExternalAccounts {

    @Id
    @SequenceGenerator(name="ex_accid_gen", initialValue = 4564)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "ex_accid_gen")
    private Integer ExternalAccId;
    @Column(nullable = false)
    private Long routingNumber;
    @Column(nullable = false)
    private Long accountNumber;
    @Column(nullable = false,length = 45)
    private String bankName;

    @Column()
    private boolean isActive = false;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(nullable = false)
    private Accounts account;

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public Integer getExternalAccId() {
        return ExternalAccId;
    }

    public void setExternalAccId(Integer externalAccId) {
        ExternalAccId = externalAccId;
    }

    public Long getRoutingNumber() {
        return routingNumber;
    }

    public void setRoutingNumber(Long routingNumber) {
        this.routingNumber = routingNumber;
    }

    public Long getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(Long accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public Accounts getAccount() {
        return account;
    }

    public void setAccount(Accounts account) {
        this.account = account;
    }

    @Override
    public String toString() {
        return "ExternalAccounts{" +
                "id='" + getExternalAccId() + '\'' +
                "routingNumber='" + routingNumber + '\'' +
                ", accountNumber='" + accountNumber + '\'' +
                ", bankName='" + bankName + '\'' +
                ", account=" + account.getEmail() +
                '}';
    }
}
