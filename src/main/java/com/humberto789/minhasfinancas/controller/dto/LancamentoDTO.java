package com.humberto789.minhasfinancas.controller.dto;


import lombok.*;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LancamentoDTO {

    private Long id;
    private String descricao;
    private Integer mes;
    private Integer ano;
    private BigDecimal valor;
    private Long usuario;
    private String tipo;
    private String status;
}
