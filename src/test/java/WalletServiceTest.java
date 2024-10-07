import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.wallet.commons.exceptions.BusinessException;
import org.wallet.domain.Operation;
import org.wallet.domain.OperationType;
import org.wallet.domain.Wallet;
import org.wallet.repositories.WalletRepository;
import org.wallet.services.OperationService;
import org.wallet.services.WalletService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class WalletServiceTest {

    @InjectMocks
    private WalletService walletService;

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private OperationService operationService;

    private Wallet wallet;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        wallet = new Wallet();
        wallet.setId(1L);
        wallet.setUsername("testUser");
        wallet.setBalance(100.0);
    }

    @Test
    void create() {
        Wallet wallet = new Wallet();
        wallet.setUsername("testUser");
        wallet.setBalance(0.0);
        when(walletRepository.save(any(Wallet.class))).thenReturn(wallet);

        Wallet createdWallet = walletService.create("testUser");

        assertNotNull(createdWallet);
        assertEquals("testUser", createdWallet.getUsername());
        assertEquals(0.0, createdWallet.getBalance());
    }

    @Test
    void retrieveBalance() {
        Wallet wallet = new Wallet();
        wallet.setId(2L);
        wallet.setUsername("testUser");
        wallet.setBalance(50.0);

        when(walletRepository.findByUsername("testUser")).thenReturn(wallet);

        double balance = walletService.retrieveBalance("testUser");

        assertEquals(50, balance);
    }

    @Test
    void retrieveBalanceNotFound() {
        when(walletRepository.findByUsername("unknownUser")).thenReturn(null);

        Exception exception = assertThrows(BusinessException.class, () -> {
            walletService.retrieveBalance("unknownUser");
        });

        assertEquals("Wallet not found for username: unknownUser", exception.getMessage());
    }

    @Test
    void testRetrieveHistoricalBalance_Success() {
        Wallet otherWallet = new Wallet();
        otherWallet.setId(2L);
        otherWallet.setUsername("otherUser");
        otherWallet.setBalance(300.0);

        LocalDateTime date = LocalDateTime.now();
        when(walletRepository.findByUsername("testUser")).thenReturn(wallet);

        Operation deposit = new Operation();
        deposit.setType(OperationType.DEPOSIT);
        deposit.setAmount(100.0);
        deposit.setWallet(wallet);

        Operation withdrawal = new Operation();
        withdrawal.setType(OperationType.WITHDRAW);
        withdrawal.setAmount(50.0);
        withdrawal.setWallet(wallet);

        Operation transferOut = new Operation();
        transferOut.setType(OperationType.TRANSFER);
        transferOut.setAmount(30.0);
        transferOut.setWallet(wallet);
        transferOut.setDestinationWallet("otherUser");

        Operation transferIn = new Operation();
        transferIn.setType(OperationType.TRANSFER);
        transferIn.setAmount(20.0);
        transferIn.setWallet(otherWallet);
        transferIn.setDestinationWallet(wallet.getUsername());

        List<Operation> operations = Arrays.asList(deposit, withdrawal, transferOut, transferIn);
        when(operationService.findAllByWalletAndDateBefore(wallet, date)).thenReturn(operations);

        double result = walletService.retrieveHistoricalBalance("testUser", date);

        assertEquals(40, result);
        verify(walletRepository).findByUsername("testUser");
        verify(operationService).findAllByWalletAndDateBefore(wallet, date);
    }

    @Test
    void testRetrieveHistoricalBalance_WalletNotFound() {
        when(walletRepository.findByUsername("testUser")).thenReturn(null);

        Exception exception = assertThrows(BusinessException.class, () -> {
            walletService.retrieveHistoricalBalance("testUser", LocalDateTime.now());
        });

        assertEquals("Wallet not found for username: testUser", exception.getMessage());
    }

}
