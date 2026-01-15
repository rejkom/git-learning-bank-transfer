package com.bankservice.model;

/**
 * Status transferu - reprezentuje możliwe stany transferu
 */
public enum TransferStatus {
    PENDING,      // Transfer oczekuje na przetworzenie
    COMPLETED,    // Transfer został ukończony
    FAILED,       // Transfer nie powiódł się
    CANCELLED     // Transfer został anulowany
}
