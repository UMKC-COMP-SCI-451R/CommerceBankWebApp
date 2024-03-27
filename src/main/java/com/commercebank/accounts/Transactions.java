package com.commercebank.accounts;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name="Transactions")
public class Transactions {
    @Id
    @SequenceGenerator(name="transid_gen", initialValue = 1804)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "transid_gen")
    private Integer transactionId;
    @Column(nullable = false)
    private String source;

    @Column(nullable = false)
    private String destination;

    @Column(nullable = false)
    private Double amount;

    @Column(nullable = false, name="account_email")
    private String accountEmail;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(nullable = false)
    private Accounts account;

    @Column(name = "transaction_date")
    @Temporal(TemporalType.DATE) // Specifies that this is a date without time
    private Date date;

    @Column()
    private String memo;

    @PrePersist
    protected void onCreate() {
        date = new Date(); // Set the current date when persisting new entity
    }

    public Date getDate() {
        return date;
    }

    public Integer getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Integer transactionId) {
        this.transactionId = transactionId;
    }

    public String getAccountEmail() {
        return accountEmail;
    }

    public void setAccountEmail(String accountEmail) {
        this.accountEmail = accountEmail;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Accounts getAccount() {
        return account;
    }

    public void setAccount(Accounts account) {
        this.account = account;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    @Override
    public String toString() {
        return "Transactions{" +
                "transactionId=" + transactionId +
                ", source='" + source + '\'' +
                ", destination='" + destination + '\'' +
                ", amount=" + amount +
                ", account=" + account.getAccID() +
                ", date= " + date+
                '}';
    }
}
