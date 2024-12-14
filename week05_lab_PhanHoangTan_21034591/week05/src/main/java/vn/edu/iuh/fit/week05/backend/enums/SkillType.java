package vn.edu.iuh.fit.week05.backend.enums;

public enum SkillType {
    UNSPECIFIC("UNSPECIFIC"),
    TECHNICAL_SKILL("TECHNICAL_SKILL"),
    SOFT_SKILL("SOFT_SKILL");

    private final String value;

    SkillType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }

    public static SkillType fromCode(int code) {
        switch (code) {
            case 0:
                return UNSPECIFIC;
            case 1:
                return TECHNICAL_SKILL;
            case 2:
                return SOFT_SKILL;
            default:
                throw new IllegalArgumentException("Unknown code: " + code);
        }
    }
}
