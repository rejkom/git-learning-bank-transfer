package com.bankservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Account - Reprezentuje konto bankowe
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Account {
    
    private String id;
    private String iban;
    private String accountHolder;
    private BigDecimal balance;
    private BigDecimal dailyTransferLimit;
    private BigDecimal dailyTransfersSum;
    private boolean active;
    
    /**
     * Sprawdza czy konto ma wystarczające środki do transferu
     */
    public boolean hasSufficientFunds(BigDecimal amount) {
        return balance != null && balance.compareTo(amount) >= 0;
    }
    
    /**
     * Sprawdza czy transfer nie przekracza dziennego limitu
     */
    public boolean canTransferToday(BigDecimal amount) {
        BigDecimal availableToday = dailyTransferLimit.subtract(dailyTransfersSum);
        return amount.compareTo(availableToday) <= 0;
    }
    
    /**
     * Walidacja IBAN - prosta walidacja - abc
     */
    public boolean isValidIban() {
        if (iban == null || iban.isBlank()) {
            return false;
        }
        // IBAN powinien mieć minimum 15 znaków i maksimum 34
        return iban.length() >= 15 && iban.length() <= 34;
    }
}
