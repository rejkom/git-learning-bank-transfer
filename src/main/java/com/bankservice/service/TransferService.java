package com.bankservice.service;

import com.bankservice.exception.AccountNotFoundException;
import com.bankservice.exception.InsufficientFundsException;
import com.bankservice.exception.InvalidIbanException;
import com.bankservice.model.Account;
import com.bankservice.model.Transfer;
import com.bankservice.model.TransferStatus;
import com.bankservice.repository.AccountRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * TransferService - Logika biznesowa dla transferów pieniędzy
 * Obsługuje walidacje, limity i przetwarzanie transferów
 */
@Service
public class TransferService {
    
    private final AccountRepository accountRepository;
    
    public TransferService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }
    
    /**
     * Przetwarza transfer pieniędzy między dwoma kontami
     *
     * @param fromIban IBAN konta źródłowego
     * @param toIban IBAN konta docelowego
     * @param amount Kwota do transferu
     * @param description Opis transferu
     * @return Transfer z statusem COMPLETED lub FAILED
     */
    public Transfer processTransfer(String fromIban, String toIban, BigDecimal amount, String description) {
        Transfer transfer = Transfer.builder()
                .id(UUID.randomUUID().toString())
                .fromAccountIban(fromIban)
                .toAccountIban(toIban)
                .amount(amount)
                .description(description)
                .status(TransferStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();
        
        try {
            // Walidacja danych transferu
            validateTransfer(transfer);
            
            // Pobranie kont
            Account fromAccount = accountRepository.findByIban(fromIban)
                    .orElseThrow(() -> new AccountNotFoundException("Konto źródłowe nie znalezione: " + fromIban));
            
            Account toAccount = accountRepository.findByIban(toIban)
                    .orElseThrow(() -> new AccountNotFoundException("Konto docelowe nie znalezione: " + toIban));
            
            // Sprawdzenie czy konto źródłowe jest aktywne
            if (!fromAccount.isActive()) {
                throw new IllegalStateException("Konto źródłowe nie jest aktywne");
            }
            
            // Sprawdzenie czy konto docelowe jest aktywne
            if (!toAccount.isActive()) {
                throw new IllegalStateException("Konto docelowe nie jest aktywne");
            }
            
            // Sprawdzenie wystarczających środków
            if (!fromAccount.hasSufficientFunds(amount)) {
                throw new InsufficientFundsException(
                        "Niewystarczające środki. Dostępne: " + fromAccount.getBalance() + ", wymagane: " + amount
                );
            }
            
            // Sprawdzenie limitu dziennego
            if (!fromAccount.canTransferToday(amount)) {
                BigDecimal availableToday = fromAccount.getDailyTransferLimit()
                        .subtract(fromAccount.getDailyTransfersSum());
                throw new IllegalStateException(
                        "Transfer przekracza dzienny limit. Dostępne dzisiaj: " + availableToday
                );
            }
            
            // Wykonanie transferu
            fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
            fromAccount.setDailyTransfersSum(fromAccount.getDailyTransfersSum().add(amount));
            
            toAccount.setBalance(toAccount.getBalance().add(amount));
            
            // Zapisanie zmian
            accountRepository.save(fromAccount);
            accountRepository.save(toAccount);
            
            // Aktualizacja statusu transferu
            transfer.setStatus(TransferStatus.COMPLETED);
            transfer.setCompletedAt(LocalDateTime.now());
            
            return transfer;
            
        } catch (Exception e) {
            transfer.setStatus(TransferStatus.FAILED);
            transfer.setFailureReason(e.getMessage());
            return transfer;
        }
    }
    
    /**
     * Walidacja danych transferu
     */
    private void validateTransfer(Transfer transfer) {
        if (!transfer.isValid()) {
            throw new IllegalArgumentException("Transfer zawiera nieprawidłowe dane");
        }
        
        // Walidacja IBAN
        if (!isValidIban(transfer.getFromAccountIban())) {
            throw new InvalidIbanException("Nieprawidłowy IBAN konta źródłowego: " + transfer.getFromAccountIban());
        }
        
        if (!isValidIban(transfer.getToAccountIban())) {
            throw new InvalidIbanException("Nieprawidłowy IBAN konta docelowego: " + transfer.getToAccountIban());
        }
        
        // Sprawdzenie minimum kwoty
        if (transfer.getAmount().compareTo(new BigDecimal("0.01")) < 0) {
            throw new IllegalArgumentException("Minimalna kwota transferu to 0.01");
        }
    }
    
    /**
     * Prosta walidacja IBAN
     */
    private boolean isValidIban(String iban) {
        if (iban == null || iban.isBlank()) {
            return false;
        }
        // IBAN powinien mieć minimum 15 znaków i maksimum 34
        return iban.length() >= 15 && iban.length() <= 34;
    }
}
