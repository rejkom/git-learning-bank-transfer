package com.bankservice.repository;

import com.bankservice.model.Account;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * AccountRepository - Mock'owana implementacja do przechowywania kont
 * W rzeczywistej aplikacji byłaby to baza danych
 */
@Repository
public class AccountRepository {
    
    // Symulacja bazy danych (w pamięci)
    private final Map<String, Account> accounts = new HashMap<>();
    
    public AccountRepository() {
        // Inicjalizuj z testowymi danymi
        initializeTestData();
    }
    
    /**
     * Inicjalizuje testowe konta
     */
    private void initializeTestData() {
        Account account1 = Account.builder()
                .id("1")
                .iban("PL61109010140000071219812874")
                .accountHolder("Jan Kowalski")
                .balance(new BigDecimal("5000.00"))
                .dailyTransferLimit(new BigDecimal("10000.00"))
                .dailyTransfersSum(new BigDecimal("0.00"))
                .active(true)
                .build();
        
        Account account2 = Account.builder()
                .id("2")
                .iban("PL61109010140000071219812875")
                .accountHolder("Maria Nowak")
                .balance(new BigDecimal("3000.00"))
                .dailyTransferLimit(new BigDecimal("10000.00"))
                .dailyTransfersSum(new BigDecimal("0.00"))
                .active(true)
                .build();
        
        accounts.put(account1.getIban(), account1);
        accounts.put(account2.getIban(), account2);
    }
    
    /**
     * Znajdź konto po IBAN
     */
    public Optional<Account> findByIban(String iban) {
        return Optional.ofNullable(accounts.get(iban));
    }
    
    /**
     * Zapisz konto
     */
    public Account save(Account account) {
        accounts.put(account.getIban(), account);
        return account;
    }
    
    /**
     * Sprawdź czy konto istnieje
     */
    public boolean existsByIban(String iban) {
        return accounts.containsKey(iban);
    }
    
    /**
     * Usuń konto
     */
    public void deleteByIban(String iban) {
        accounts.remove(iban);
    }
}
