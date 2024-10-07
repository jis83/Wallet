package org.wallet.controllers;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import org.wallet.domain.Wallet;
import org.wallet.services.WalletService;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/wallet")
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @PostMapping("/create")
    public Wallet createWallet(@RequestParam("username") String username) {
        return walletService.create(username);
    }

    @GetMapping("/balance")
    public double retrieveBalance(@RequestParam("username") String username) {
        return walletService.retrieveBalance(username);
    }

    @GetMapping("/historical-balance")
    public double retrieveHistoricalBalance(@RequestParam("username") String username, @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date) {
        return walletService.retrieveHistoricalBalance(username, date);
    }

}
