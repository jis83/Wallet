package org.wallet.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.wallet.domain.Operation;
import org.wallet.domain.Wallet;

import java.time.LocalDateTime;
import java.util.List;

public interface OperationRepository extends JpaRepository<Operation, Long> {
    List<Operation> findAllByWalletAndDateBefore(Wallet wallet, LocalDateTime date);
}
