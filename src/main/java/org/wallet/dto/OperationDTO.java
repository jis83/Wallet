package org.wallet.dto;

import org.wallet.domain.Operation;

import java.time.LocalDateTime;

public class OperationDTO {
    private LocalDateTime date;
    private String type;
    private double amount;
    private String userName;
    private String destinationUsername;

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDestinationUsername() {
        return destinationUsername;
    }

    public void setDestinationUsername(String destinationUsername) {
        this.destinationUsername = destinationUsername;
    }

    public static OperationDTO from(Operation operation){
        OperationDTO operationDTO = new OperationDTO();
        operationDTO.setAmount(operation.getAmount());
        operationDTO.setDate(operation.getDate());
        operationDTO.setUserName(operation.getWallet().getUsername());
        operationDTO.setType(operation.getType().name());
        operationDTO.setDestinationUsername(operation.getDestinationWallet() == null ? null : operation.getDestinationWallet());
        return operationDTO;
    }
}
