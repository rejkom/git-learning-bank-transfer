package com.bankservice.service;

import com.bankservice.model.Account;
import com.bankservice.model.Transfer;
import com.bankservice.model.TransferStatus;
import com.bankservice.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * TransferServiceBuggyTest - Testy z CELOWYMI BŁĘDAMI do naprawy.
 * <p>
 * Ten plik zawiera błędy w kodzie testów. Zadanie:
 * 1. Przejrzyj każdy test
 * 2. Znajdź błąd (w kodzie lub w asercjach)
 * 3. Napraw błąd
 * 4. Commit i push do repozytorium
 * <p>
 * Każdy test ma jeden prosty błąd - nie są to zaawansowane błędy programistyczne,
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Transfer Service Buggy Tests - NAPRAW BŁĘDY!")
class TransferServiceBuggyTest {

    @Mock
    private AccountRepository accountRepository;

    private TransferService transferService;
    private Account fromAccount;
    private Account toAccount;

    private static final String FROM_IBAN = "PL61109010140000071219812874";
    private static final String TO_IBAN = "PL61109010140000071219812875";

    @BeforeEach
    void setUp() {
        transferService = new TransferService(accountRepository);

        fromAccount = Account.builder()
                .id("1")
                .iban(FROM_IBAN)
                .accountHolder("Jan Kowalski")
                .balance(new BigDecimal("5000.00"))
                .dailyTransferLimit(new BigDecimal("10000.00"))
                .dailyTransfersSum(new BigDecimal("0.00"))
                .active(true)
                .build();

        toAccount = Account.builder()
                .id("2")
                .iban(TO_IBAN)
                .accountHolder("Maria Nowak")
                .balance(new BigDecimal("3000.00"))
                .dailyTransferLimit(new BigDecimal("10000.00"))
                .dailyTransfersSum(new BigDecimal("0.00"))
                .active(true)
                .build();
    }

    // ====== BŁĄD 1: Zła Wartość Kwoty ==================================================
    @Test
    @DisplayName("BUG #1: Transfer z kwotą zero powinien być FAILED")
    void testBugZeroAmountValue() {
        // Arrange
        // ❌ BUG: Kwota jest ustawiona na 100, ale powinno być ZERO!
        BigDecimal transferAmount = new BigDecimal(100);
        // Act
        Transfer result = transferService.processTransfer(
                FROM_IBAN, TO_IBAN, transferAmount, "Test transfer");

        // Assert
        // ❌ BUG: Test oczekuje że transfer z ZERO będzie FAILED
        assertThat(result.getStatus()).isEqualTo(TransferStatus.FAILED);
        assertThat(result.getFailureReason()).contains("nieprawidłowe dane");
    }

    // ====== BŁĄD 2: Zła Asercja - Niewłaściwe Porównanie ===============================
    @Test
    @DisplayName("BUG #2: Transfer z pustym IBAN powinien być FAILED")
    void testBugEmptyIbanAssertion() {
        // Arrange
        BigDecimal transferAmount = new BigDecimal("100.00");
        String emptyIban = "";  // ← Pusty IBAN (tak ma pozostać)

        // Act
        Transfer result = transferService.processTransfer(
                emptyIban, TO_IBAN, transferAmount, "Test transfer");

        // Assert
        // ❌ BUG: Asercja sprawdza COMPLETED, ale powinna sprawdzać FAILED!
        assertThat(result.getStatus()).isEqualTo(TransferStatus.COMPLETED);
    }

    // ====== BŁĄD 3: Logika Warunkowa jest Odwrócona ====================================
    @Test
    @DisplayName("BUG #3: Transfer do tego samego konta powinien być FAILED")
    void testBugSameAccountTransfer() {
        // Arrange
        BigDecimal transferAmount = new BigDecimal("100.00");

        // Act
        Transfer result = transferService.processTransfer(
                FROM_IBAN, FROM_IBAN,  // ← TO SAMO KONTO!
                transferAmount, "Test transfer"
        );

        // Assert
        // ❌ BUG: Asercja sprawdza COMPLETED, ale powinna być FAILED
        assertThat(result.getStatus()).isEqualTo(TransferStatus.COMPLETED);
    }

    // ====== DODATKOWY TEST: Poprawny - Jako Reference Point =============================
    @Test
    @DisplayName("✅ TEST POPRAWNY: Prawidłowy transfer (bez błędów)")
    void testCorrectSuccessfulTransfer() {
        // Arrange
        BigDecimal transferAmount = new BigDecimal("100.00");
        when(accountRepository.findByIban(FROM_IBAN)).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findByIban(TO_IBAN)).thenReturn(Optional.of(toAccount));

        // Act
        Transfer result = transferService.processTransfer(
                FROM_IBAN, TO_IBAN, transferAmount, "Test transfer"
        );

        // Assert
        assertThat(result.getStatus()).isEqualTo(TransferStatus.COMPLETED);
        assertThat(result.getFailureReason()).isNull();
    }
}
