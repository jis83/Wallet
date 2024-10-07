package org.wallet.services;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.wallet.commons.exceptions.BusinessException;
import org.wallet.domain.Operation;
import org.wallet.domain.OperationType;
import org.wallet.domain.Wallet;
import org.wallet.repositories.WalletRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class WalletService {

    private final WalletRepository walletRepository;
    private final OperationService operationService;

    public WalletService(WalletRepository walletRepository, OperationService operationService) {
        this.walletRepository = walletRepository;
        this.operationService = operationService;
    }

    /**
     * Create a Wallet
     * @param username
     * @return
     */
    @Transactional
    public Wallet create(String username){
        Wallet wallet = new Wallet();
        wallet.setUsername(username);
        wallet.setBalance(0.0);
        return walletRepository.save(wallet);
    }

    /**
     * Retrieve the Balance
     * @param username
     * @return
     */
    public double retrieveBalance(String username) {
        Wallet wallet = walletRepository.findByUsername(username);
        if (wallet == null) {
            throw new BusinessException("Wallet not found for username: " + username);
        }
        return wallet.getBalance();
    }

    /**
     * Retrieve the balance at a specific date
     * @param username
     * @param date
     * @return
     */
    public double retrieveHistoricalBalance(String username, LocalDateTime date) {
        Wallet wallet = walletRepository.findByUsername(username);
        if (wallet == null) {
            throw new BusinessException("Wallet not found for username: " + username);
        }
        List<Operation> operations = operationService.findAllByWalletAndDateBefore(wallet, date);
        return getHistoricalBalance(operations, wallet);
    }

    private double getHistoricalBalance(List<Operation> operations, Wallet wallet){
        double historicalBalance = 0;
        for (Operation operation : operations) {
            if (operation.getType() == OperationType.DEPOSIT) {
                historicalBalance += operation.getAmount();
            } else if (operation.getType() == OperationType.WITHDRAW) {
                historicalBalance -= operation.getAmount();
            } else if (operation.getType() == OperationType.TRANSFER && operation.getDestinationWallet() != null) {
                if (operation.getWallet().equals(wallet)) {
                    historicalBalance -= operation.getAmount();
                } else {
                    historicalBalance += operation.getAmount();
                }
            }
        }
        return historicalBalance;
    }

}

