package org.wallet.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Operation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime date;
    @Enumerated(EnumType.STRING)
    private OperationType type;
    private double amount;

    @ManyToOne
    @JoinColumn(name = "wallet_id")
    private Wallet wallet;

    /*
    NOTE: This field is used only in Transfer operations. The Operation class could have been modeled as an abstract
    class, with each type of operation inheriting from it, moving this property to the Transfer subclass.
    In this case, it is kept this way for simplicity due to time constraints.
    */
    private String destinationWallet;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public OperationType getType() {
        return type;
    }

    public void setType(OperationType type) {
        this.type = type;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Wallet getWallet() {
        return wallet;
    }

    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }

    public String getDestinationWallet() {
        return destinationWallet;
    }

    public void setDestinationWallet(String destinationWallet) {
        this.destinationWallet = destinationWallet;
    }
}
