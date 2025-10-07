package org.example.tripbuddy.domain.user.domain;

public enum RoleType {

    USER("ROLE_USER", "일반 등급"),
    SILVER_USER("ROLE_SILVER_USER", "실버 등급"),
    GOLD_USER("ROLE_GOLD_USER", "골드 등급"),
    ADMIN("ROLE_ADMIN", "관리자");

    private final String roleName;
    private final String description;

    RoleType(String roleName, String description) {
        this.roleName = roleName;
        this.description = description;
    }

    public String getRoleName() {
        return roleName;
    }

    public String getDescription() {
        return description;
    }
}
