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
    private Integer routingNumber;
    @Column(nullable = false)
    private Integer accountNumber;
    @Column(nullable = false,length = 45)
    private String bankName;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(nullable = false)
    private Accounts account;

    public Integer getExternalAccId() {
        return ExternalAccId;
    }

    public void setExternalAccId(Integer externalAccId) {
        ExternalAccId = externalAccId;
    }

    public Integer getRoutingNumber() {
        return routingNumber;
    }

    public void setRoutingNumber(Integer routingNumber) {
        this.routingNumber = routingNumber;
    }

    public Integer getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(Integer accountNumber) {
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
