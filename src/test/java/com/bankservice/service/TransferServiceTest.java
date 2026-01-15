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

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * TransferServiceTest - Testy jednostkowe dla TransferService
 * Wykorzystuje JUnit 5 i Mockito
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Transfer Service Tests")
class TransferServiceTest {

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

        // Przygotuj testowe konta
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

    @Test
    @DisplayName("Powinien pomyślnie wykonać transfer pomiędzy kontami")
    void testSuccessfulTransfer() {
        // Arrange
        BigDecimal transferAmount = new BigDecimal("100.00");
        when(accountRepository.findByIban(FROM_IBAN)).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findByIban(TO_IBAN)).thenReturn(Optional.of(toAccount));
        when(accountRepository.save(any(Account.class))).thenReturn(null);

        // Act
        Transfer result = transferService.processTransfer(
                FROM_IBAN, TO_IBAN, transferAmount, "Test transfer"
        );

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(TransferStatus.COMPLETED);
        assertThat(result.getAmount()).isEqualTo(transferAmount);
        assertThat(result.getFailureReason()).isNull();
        verify(accountRepository, times(2)).save(any(Account.class));
    }

    @Test
    @DisplayName("Powinien zwrócić FAILED gdy brak wystarczających środków")
    void testTransferWithInsufficientFunds() {
        // Arrange
        fromAccount.setBalance(new BigDecimal("50.00"));
        BigDecimal transferAmount = new BigDecimal("100.00");
        when(accountRepository.findByIban(FROM_IBAN)).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findByIban(TO_IBAN)).thenReturn(Optional.of(toAccount));

        // Act
        Transfer result = transferService.processTransfer(
                FROM_IBAN, TO_IBAN, transferAmount, "Test transfer"
        );

        // Assert
        assertThat(result.getStatus()).isEqualTo(TransferStatus.FAILED);
        assertThat(result.getFailureReason()).contains("Niewystarczające środki");
        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    @DisplayName("Powinien zwrócić FAILED gdy konto źródłowe nie istnieje")
    void testTransferFromNonExistentAccount() {
        // Arrange
        BigDecimal transferAmount = new BigDecimal("100.00");
        when(accountRepository.findByIban(FROM_IBAN)).thenReturn(Optional.empty());

        // Act
        Transfer result = transferService.processTransfer(
                FROM_IBAN, TO_IBAN, transferAmount, "Test transfer"
        );

        // Assert
        assertThat(result.getStatus()).isEqualTo(TransferStatus.FAILED);
        assertThat(result.getFailureReason()).contains("Konto źródłowe nie znalezione");
    }

    @Test
    @DisplayName("Powinien zwrócić FAILED gdy konto docelowe nie istnieje")
    void testTransferToNonExistentAccount() {
        // Arrange
        BigDecimal transferAmount = new BigDecimal("100.00");
        when(accountRepository.findByIban(FROM_IBAN)).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findByIban(TO_IBAN)).thenReturn(Optional.empty());

        // Act
        Transfer result = transferService.processTransfer(
                FROM_IBAN, TO_IBAN, transferAmount, "Test transfer"
        );

        // Assert
        assertThat(result.getStatus()).isEqualTo(TransferStatus.FAILED);
        assertThat(result.getFailureReason()).contains("Konto docelowe nie znalezione");
    }

    @Test
    @DisplayName("Powinien zwrócić FAILED gdy IBAN źródłowy jest nieprawidłowy")
    void testTransferWithInvalidFromIban() {
        // Arrange
        String invalidIban = "INVALID";
        BigDecimal transferAmount = new BigDecimal("100.00");

        // Act
        Transfer result = transferService.processTransfer(
                invalidIban, TO_IBAN, transferAmount, "Test transfer"
        );

        // Assert
        assertThat(result.getStatus()).isEqualTo(TransferStatus.FAILED);
        assertThat(result.getFailureReason()).isNotBlank();
    }

    @Test
    @DisplayName("Powinien zwrócić FAILED gdy IBAN docelowy jest nieprawidłowy")
    void testTransferWithInvalidToIban() {
        // Arrange
        String invalidIban = "SHORT";
        BigDecimal transferAmount = new BigDecimal("100.00");

        // Act
        Transfer result = transferService.processTransfer(
                FROM_IBAN, invalidIban, transferAmount, "Test transfer"
        );

        // Assert
        assertThat(result.getStatus()).isEqualTo(TransferStatus.FAILED);
        assertThat(result.getFailureReason()).isNotBlank();
    }

    @Test
    @DisplayName("Powinien zwrócić FAILED gdy kwota transferu jest ujemna")
    void testTransferWithNegativeAmount() {
        // Arrange
        BigDecimal negativeAmount = new BigDecimal("-100.00");

        // Act
        Transfer result = transferService.processTransfer(
                FROM_IBAN, TO_IBAN, negativeAmount, "Test transfer"
        );

        // Assert
        assertThat(result.getStatus()).isEqualTo(TransferStatus.FAILED);
    }

    @Test
    @DisplayName("Powinien zwrócić FAILED gdy kwota transferu jest zero")
    void testTransferWithZeroAmount() {
        // Arrange
        BigDecimal zeroAmount = BigDecimal.ZERO;

        // Act
        Transfer result = transferService.processTransfer(
                FROM_IBAN, TO_IBAN, zeroAmount, "Test transfer"
        );

        // Assert
        assertThat(result.getStatus()).isEqualTo(TransferStatus.FAILED);
    }

    @Test
    @DisplayName("Powinien zwrócić FAILED gdy transfer jest do tego samego konta")
    void testTransferToTheSameAccount() {
        // Arrange
        BigDecimal transferAmount = new BigDecimal("100.00");

        // Act
        Transfer result = transferService.processTransfer(
                FROM_IBAN, FROM_IBAN, transferAmount, "Test transfer"
        );

        // Assert
        assertThat(result.getStatus()).isEqualTo(TransferStatus.FAILED);
    }

    @Test
    @DisplayName("Powinien zwrócić FAILED gdy konto źródłowe nie jest aktywne")
    void testTransferFromInactiveAccount() {
        // Arrange
        fromAccount.setActive(false);
        BigDecimal transferAmount = new BigDecimal("100.00");
        when(accountRepository.findByIban(FROM_IBAN)).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findByIban(TO_IBAN)).thenReturn(Optional.of(toAccount));

        // Act
        Transfer result = transferService.processTransfer(
                FROM_IBAN, TO_IBAN, transferAmount, "Test transfer"
        );

        // Assert
        assertThat(result.getStatus()).isEqualTo(TransferStatus.FAILED);
        assertThat(result.getFailureReason()).contains("nie jest aktywne");
    }

    @Test
    @DisplayName("Powinien zwrócić FAILED gdy transfer przekracza dzienny limit")
    void testTransferExceedingDailyLimit() {
        // Arrange
        fromAccount.setDailyTransferLimit(new BigDecimal("200.00"));
        fromAccount.setDailyTransfersSum(new BigDecimal("150.00"));
        BigDecimal transferAmount = new BigDecimal("100.00");
        when(accountRepository.findByIban(FROM_IBAN)).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findByIban(TO_IBAN)).thenReturn(Optional.of(toAccount));

        // Act
        Transfer result = transferService.processTransfer(
                FROM_IBAN, TO_IBAN, transferAmount, "Test transfer"
        );

        // Assert
        assertThat(result.getStatus()).isEqualTo(TransferStatus.FAILED);
        assertThat(result.getFailureReason()).contains("dzienny limit");
    }

    @Test
    @DisplayName("Powinien zwrócić FAILED gdy IBAN jest pusty (pusty string)")
    void testTransferWithEmptyIban() {
        // Arrange
        BigDecimal transferAmount = new BigDecimal("100.00");

        // Act
        Transfer result = transferService.processTransfer(
                "", TO_IBAN, transferAmount, "Test transfer"
        );

        // Assert
        assertThat(result.getStatus()).isEqualTo(TransferStatus.FAILED);
        assertThat(result.getFailureReason()).isNotBlank();
    }

    @Test
    @DisplayName("Transfer powinien mieć unikalny ID")
    void testTransferHasUniqueId() {
        // Arrange
        BigDecimal transferAmount = new BigDecimal("100.00");
        when(accountRepository.findByIban(FROM_IBAN)).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findByIban(TO_IBAN)).thenReturn(Optional.of(toAccount));
        when(accountRepository.save(any(Account.class))).thenReturn(null);

        // Act
        Transfer transfer1 = transferService.processTransfer(
                FROM_IBAN, TO_IBAN, transferAmount, "Test 1"
        );
        Transfer transfer2 = transferService.processTransfer(
                FROM_IBAN, TO_IBAN, transferAmount, "Test 2"
        );

        // Assert
        assertThat(transfer1.getId()).isNotEqualTo(transfer2.getId());
    }
}
