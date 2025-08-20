package gr.clothesmanager.core.enums;

public enum Status {
    INACTIVE, 
    ACTIVE;
    
    @Override
    public String toString() {
        return this == ACTIVE ? "ΕΝΕΡΓΟ" : "ΑΝΕΝΕΡΓΟ";
    }
    
    public String toFeminine() {
        return this == ACTIVE ? "ΕΝΕΡΓΗ" : "ΑΝΕΝΕΡΓΗ";
    }
    
    public String toMasculine() {
        return this == ACTIVE ? "ΕΝΕΡΓΟΣ" : "ΑΝΕΝΕΡΓΟΣ";
    }
    
    public boolean isActive() {
        return this == ACTIVE;
    }
}