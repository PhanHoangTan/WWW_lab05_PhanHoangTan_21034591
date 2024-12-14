package vn.edu.iuh.fit.week05.backend.enums;

public enum SkillLevel {
    BEGINNER("Beginner"),
    INTERMEDIATE("INTERMEDIATE"),
    ADVANCED("ADVANCED"),
    PROFESSIONAL("PROFESSIONAL"),
    MASTER("MASTER");

    private final String value;

    SkillLevel(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }

    public static SkillLevel fromCode(int code) {
        switch (code) {
            case 0:
                return BEGINNER;
            case 1:
                return INTERMEDIATE;
            case 2:
                return ADVANCED;
            case 3:
                return PROFESSIONAL;
            case 4:
                return MASTER;

            default:
                throw new IllegalArgumentException("Unknown code: " + code);
        }
    }
}