package org.wallet.services;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.wallet.commons.exceptions.BusinessException;
import org.wallet.domain.Operation;
import org.wallet.domain.OperationType;
import org.wallet.domain.Wallet;
import org.wallet.dto.OperationDTO;
import org.wallet.repositories.OperationRepository;
import org.wallet.repositories.WalletRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OperationService {

    private final WalletRepository walletRepository;
    private final OperationRepository operationRepository;

    private final String DEPOSIT = "DEPOSIT";
    private final String WITHDRAW = "WITHDRAW";
    private final String TRANSFER = "TRANSFER";

    public OperationService(WalletRepository walletRepository, OperationRepository operationRepository) {
        this.walletRepository = walletRepository;
        this.operationRepository = operationRepository;
    }

    @Transactional
    public OperationDTO createOperation(OperationDTO operationDTO){
        Operation operation;
        switch (operationDTO.getType()){
            case DEPOSIT:
                operation = this.deposit(operationDTO.getUserName(), operationDTO.getAmount());
                break;
            case WITHDRAW:
                operation = this.withdraw(operationDTO.getUserName(), operationDTO.getAmount());
                break;
            case TRANSFER:
                operation = this.transfer(operationDTO.getUserName(), operationDTO.getDestinationUsername(), operationDTO.getAmount());
                break;
            default: throw new IllegalArgumentException("Unknown operation type: " + operationDTO.getType());
        }
        return OperationDTO.from(operation);
    }

    /**
     * Save a deposit operation
     * @param username
     * @param amount
     * @return
     */
    private Operation deposit(String username, double amount) {
        Wallet wallet = walletRepository.findByUsername(username);
        if (wallet != null && amount > 0) {
            wallet.setBalance(wallet.getBalance() + amount);
            Operation operation = new Operation();
            operation.setDate(LocalDateTime.now());
            operation.setType(OperationType.DEPOSIT);
            operation.setAmount(amount);
            operation.setWallet(wallet);
            operationRepository.save(operation);
            return operation;
        }
        throw new BusinessException("Error in deposit.");
    }

    /**
     * Save a withdraw operation
     * @param username
     * @param amount
     * @return
     */
    private Operation withdraw(String username, double amount) {
        Wallet wallet = walletRepository.findByUsername(username);
        if (wallet != null && amount > 0 && wallet.getBalance() >= amount) {
            wallet.setBalance(wallet.getBalance() - amount);
            Operation operation = new Operation();
            operation.setDate(LocalDateTime.now());
            operation.setType(OperationType.WITHDRAW);
            operation.setAmount(amount);
            operation.setWallet(wallet);
            operationRepository.save(operation);
            return operation;
        }
        throw new BusinessException("Error in withdrawal.");
    }

    /**
     * Save a transfer operation
     * @param sourceUsername
     * @param destinationUsername
     * @param amount
     * @return
     */
     private Operation transfer(String sourceUsername, String destinationUsername, double amount) {
        Wallet sourceWallet = walletRepository.findByUsername(sourceUsername);
        Wallet destinationWallet = walletRepository.findByUsername(destinationUsername);
        if (sourceWallet != null && destinationWallet != null && amount > 0 && sourceWallet.getBalance() >= amount) {
            sourceWallet.setBalance(sourceWallet.getBalance() - amount);
            destinationWallet.setBalance(destinationWallet.getBalance() + amount);
            Operation operation = new Operation();
            operation.setDate(LocalDateTime.now());
            operation.setType(OperationType.TRANSFER);
            operation.setAmount(amount);
            operation.setWallet(sourceWallet);
            operation.setDestinationWallet(destinationUsername);
            operationRepository.save(operation);
            return operation;
        }
        throw new BusinessException("Error in transfer.");
    }

    public List<Operation> findAllByWalletAndDateBefore(Wallet wallet, LocalDateTime date){
        return operationRepository.findAllByWalletAndDateBefore(wallet, date);
    };
}

