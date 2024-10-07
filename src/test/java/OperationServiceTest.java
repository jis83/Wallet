import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.wallet.commons.exceptions.BusinessException;
import org.wallet.domain.Operation;
import org.wallet.domain.OperationType;
import org.wallet.domain.Wallet;
import org.wallet.dto.OperationDTO;
import org.wallet.repositories.OperationRepository;
import org.wallet.repositories.WalletRepository;
import org.wallet.services.OperationService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class OperationServiceTest {

    @InjectMocks
    private OperationService operationService;

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private OperationRepository operationRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateOperation_Deposit() {
        Wallet wallet = new Wallet();
        wallet.setUsername("Juan");
        wallet.setBalance(100.0);

        when(walletRepository.findByUsername("Juan")).thenReturn(wallet);

        OperationDTO operationDTO = new OperationDTO();
        operationDTO.setUserName("Juan");
        operationDTO.setType("DEPOSIT");
        operationDTO.setAmount(50.0);

        OperationDTO operationResponseDTO = operationService.createOperation(operationDTO);

        assertEquals(OperationType.DEPOSIT.name(), operationResponseDTO.getType());
        assertEquals(150.0, wallet.getBalance());
        verify(operationRepository, times(1)).save(any(Operation.class));
    }

    @Test
    void testCreateOperation_Withdraw_Success() {
        Wallet wallet = new Wallet();
        wallet.setUsername("Juan");
        wallet.setBalance(100.0);

        when(walletRepository.findByUsername("Juan")).thenReturn(wallet);

        OperationDTO operationDTO = new OperationDTO();
        operationDTO.setUserName("Juan");
        operationDTO.setType("WITHDRAW");
        operationDTO.setAmount(50.0);

        OperationDTO operationResponseDTO = operationService.createOperation(operationDTO);

        assertEquals(OperationType.WITHDRAW.name(), operationResponseDTO.getType());
        assertEquals(50.0, wallet.getBalance());
        verify(operationRepository, times(1)).save(any(Operation.class));
    }

    @Test
    void testCreateOperation_Withdraw_Failure() {
        Wallet wallet = new Wallet();
        wallet.setUsername("Juan");
        wallet.setBalance(30.0);

        when(walletRepository.findByUsername("Juan")).thenReturn(wallet);

        OperationDTO operationDTO = new OperationDTO();
        operationDTO.setUserName("Juan");
        operationDTO.setType("WITHDRAW");
        operationDTO.setAmount(50.0);

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            operationService.createOperation(operationDTO);
        });

        assertEquals("Error in withdrawal.", exception.getMessage());
        assertEquals(30.0, wallet.getBalance());
    }

    @Test
    void testCreateOperation_Transfer_Success() {
        Wallet sourceWallet = new Wallet();
        sourceWallet.setUsername("Juan");
        sourceWallet.setBalance(100.0);

        Wallet destinationWallet = new Wallet();
        destinationWallet.setUsername("Pedro");
        destinationWallet.setBalance(50.0);

        when(walletRepository.findByUsername("Juan")).thenReturn(sourceWallet);
        when(walletRepository.findByUsername("Pedro")).thenReturn(destinationWallet);

        OperationDTO operationDTO = new OperationDTO();
        operationDTO.setUserName("Juan");
        operationDTO.setDestinationUsername("Pedro");
        operationDTO.setType("TRANSFER");
        operationDTO.setAmount(30.0);

        OperationDTO operationResponseDTO = operationService.createOperation(operationDTO);

        assertEquals(OperationType.TRANSFER.name(), operationResponseDTO.getType());
        assertEquals(70.0, sourceWallet.getBalance());
        assertEquals(80.0, destinationWallet.getBalance());
        verify(operationRepository, times(1)).save(any(Operation.class));
    }

    @Test
    void testCreateOperation_Transfer_Failure() {
        Wallet sourceWallet = new Wallet();
        sourceWallet.setUsername("Juan");
        sourceWallet.setBalance(20.0);

        Wallet destinationWallet = new Wallet();
        destinationWallet.setUsername("Pedro");
        destinationWallet.setBalance(50.0);

        when(walletRepository.findByUsername("Juan")).thenReturn(sourceWallet);
        when(walletRepository.findByUsername("Pedro")).thenReturn(destinationWallet);

        OperationDTO operationDTO = new OperationDTO();
        operationDTO.setUserName("Juan");
        operationDTO.setDestinationUsername("Pedro");
        operationDTO.setType("TRANSFER");
        operationDTO.setAmount(30.0);

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            operationService.createOperation(operationDTO);
        });

        assertEquals("Error in transfer.", exception.getMessage());
        assertEquals(20.0, sourceWallet.getBalance());
        assertEquals(50.0, destinationWallet.getBalance());
    }
}
