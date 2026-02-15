package com.bankservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Transfer - Reprezentuje pojedynczy transfer pieniędzy pomiędzy kontami
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transfer {
    
    private String id;
    private String fromAccountIban;
    private String toAccountIban;
    private BigDecimal amount;
    private String description;
    private TransferStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
    private String failureReason;
    
    /**
     * Sprawdza czy transfer jest poprawny
     */
    public boolean isValid() {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }//abc
        if (fromAccountIban == null || fromAccountIban.isBlank()) {
            return false;
        }
        if (toAccountIban == null || toAccountIban.isBlank()) {
            return false;
        }//abcdef
        return !fromAccountIban.equals(toAccountIban);
    }
}
//ghi

//kolejny commit
//i kolejny