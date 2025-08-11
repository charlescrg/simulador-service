package br.com.charles.superapp.enums;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Perfil do usuário no sistema", example = "USER")
public enum UserRole {
    ADMIN("admin"),
    USER("user");

    private final String role;

    UserRole(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }

}