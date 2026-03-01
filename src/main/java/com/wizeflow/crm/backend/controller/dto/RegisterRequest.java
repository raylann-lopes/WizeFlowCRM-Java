package com.wizeflow.crm.backend.controller.dto;

import com.wizeflow.crm.backend.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "Nome é obrigatório")
    private String name;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ser válido")
    private String email;

    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 8, message = "Senha deve ter no mínimo 8 caracteres")
    private String password;

    @NotNull(message = "Role é obrigatória")
    private Role role;

    @NotNull(message = "ID da empresa é obrigatório")
    private Long companyId;

    private String phone;
    private String cpf;
    private String jobTitle;
    private String department;
}
