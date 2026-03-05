package com.b9.json.jsonplatform.wallet.application;

import com.b9.json.jsonplatform.wallet.domain.Wallet;

import java.math.BigDecimal;
import java.util.UUID;

public interface WalletService {
    Wallet createWallet(UUID userId);
    Wallet getWalletByUserId(UUID userId);
    Wallet topUp(UUID userId, BigDecimal amount);
    Wallet withdraw(UUID userId, BigDecimal amount);
}