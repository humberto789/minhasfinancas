package com.humberto789.minhasfinancas.controller.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UsuarioDTO {

    private String email;
    private String nome;
    private String senha;
}
