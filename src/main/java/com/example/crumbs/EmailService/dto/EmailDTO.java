package com.example.crumbs.EmailService.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailDTO {
    @NotNull
    @Email
    private String email;
    @NotNull
    @NotEmpty
    private String token;
    private String name;
}
