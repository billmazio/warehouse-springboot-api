package gr.clothesmanager.core.enums;

public enum OrderStatus {
    PENDING, 
    PROCESSING, 
    COMPLETED, 
    CANCELLED;
    
    @Override
    public String toString() {
        switch (this) {
            case PENDING: return "ΕΚΚΡΕΜΕΙ";
            case PROCESSING: return "ΣΕ ΕΠΕΞΕΡΓΑΣΙΑ";
            case COMPLETED: return "ΟΛΟΚΛΗΡΩΜΕΝΗ";
            case CANCELLED: return "ΑΚΥΡΩΜΕΝΗ";
            default: return name();
        }
    }
    
    public boolean isActive() {
        return this == PENDING || this == PROCESSING;
    }
    
    public boolean isCompleted() {
        return this == COMPLETED;
    }
    
    public boolean isCancelled() {
        return this == CANCELLED;
    }
}