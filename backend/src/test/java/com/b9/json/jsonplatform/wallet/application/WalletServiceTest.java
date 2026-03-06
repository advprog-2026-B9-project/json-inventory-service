package com.b9.json.jsonplatform.wallet.application;

import com.b9.json.jsonplatform.wallet.domain.Wallet;
import com.b9.json.jsonplatform.wallet.domain.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WalletServiceTest {

    @Mock
    private WalletRepository walletRepository;

    @InjectMocks
    private WalletServiceImpl walletService;

    private UUID userId;
    private Wallet wallet;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        wallet = new Wallet(userId);
    }

    @Test
    void testCreateWallet() {
        when(walletRepository.save(any(Wallet.class))).thenReturn(wallet);
        Wallet result = walletService.createWallet(userId);

        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals(BigDecimal.ZERO, result.getBalance());
    }

    @Test
    void testGetWalletByUserId_Exist() {
        when(walletRepository.findByUserId(userId)).thenReturn(Optional.of(wallet));
        Wallet result = walletService.getWalletByUserId(userId);

        assertNotNull(result);
        assertEquals(userId, result.getUserId());
    }

    @Test
    void testGetWalletByUserId_NotExist() {
        when(walletRepository.findByUserId(userId)).thenReturn(Optional.empty());
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> walletService.getWalletByUserId(userId)
        );

        assertEquals("Wallet not found", exception.getMessage());
    }

    @Test
    void testTopUp_Valid() {
        BigDecimal amount = new BigDecimal("100.00");
        when(walletRepository.findByUserId(userId)).thenReturn(Optional.of(wallet));
        when(walletRepository.save(wallet)).thenReturn(wallet);
        Wallet result = walletService.topUp(userId, amount);

        assertEquals(new BigDecimal("100.00"), result.getBalance());
    }

    @Test
    void testTopUp_Invalid_Zero() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> walletService.topUp(userId, BigDecimal.ZERO)
        );

        assertEquals("Amount must be greater than zero", ex.getMessage());
    }

    @Test
    void testTopUp_Invalid_Negative() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> walletService.topUp(userId, new BigDecimal("-50.00"))
        );

        assertEquals("Amount must be greater than zero", exception.getMessage());
    }

    @Test
    void testWithdraw_Valid() {
        wallet.setBalance(new BigDecimal("200.00"));
        BigDecimal amount = new BigDecimal("75.00");
        when(walletRepository.findByUserId(userId)).thenReturn(Optional.of(wallet));
        when(walletRepository.save(wallet)).thenReturn(wallet);
        Wallet result = walletService.withdraw(userId, amount);

        assertEquals(new BigDecimal("125.00"), result.getBalance());
    }

    @Test
    void testWithdraw_Invalid_Zero() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> walletService.withdraw(userId, BigDecimal.ZERO)
        );

        assertEquals("Amount must be greater than zero", exception.getMessage());
    }

    @Test
    void testWithdraw_Invalid_Negative() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> walletService.withdraw(userId, new BigDecimal("-10.00"))
        );

        assertEquals("Amount must be greater than zero", ex.getMessage());
    }

    @Test
    void testWithdraw_Invalid_InsufficientBalance() {
        wallet.setBalance(new BigDecimal("50.00"));
        when(walletRepository.findByUserId(userId)).thenReturn(Optional.of(wallet));
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> walletService.withdraw(userId, new BigDecimal("100.00"))
        );

        assertEquals("Insufficient balance", exception.getMessage());
    }
}
