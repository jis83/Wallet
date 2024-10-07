package org.wallet.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.wallet.domain.Wallet;

public interface WalletRepository extends JpaRepository<Wallet, Long> {
    Wallet findByUsername(String username);
}



