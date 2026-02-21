package com.wizeflow.crm_backend.enums;

import lombok.Getter;

@Getter
public enum Role {
    ADMIN("Administrador do Sistema"),
    USER("Colaborador"),
    MANAGER("Gerente");

    private final String descricao;

    Role(String descricao) {
        this.descricao = descricao;
    }
}


