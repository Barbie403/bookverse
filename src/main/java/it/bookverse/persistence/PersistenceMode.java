package it.bookverse.persistence;

public enum PersistenceMode {

    IN_MEMORY("Demo - In Memory"),

    FILE_SYSTEM("File - Text Files");

    private final String displayName;

    PersistenceMode(
            String displayName
    ) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}