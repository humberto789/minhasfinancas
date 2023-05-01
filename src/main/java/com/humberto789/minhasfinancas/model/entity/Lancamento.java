package com.humberto789.minhasfinancas.model.entity;

import com.humberto789.minhasfinancas.model.enums.StatusLancamento;
import com.humberto789.minhasfinancas.model.enums.TipoLancamento;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity(name = "lancamento")
@Table(name = "lancamento", schema = "financas")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Lancamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String descricao;

    @Column
    private Integer mes;

    @Column
    private Integer ano;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @Column
    private BigDecimal valor;

    @Column
    @Convert(converter = Jsr310JpaConverters.LocalDateConverter.class)
    private LocalDate dataCadastro;

    @Column
    @Enumerated(EnumType.STRING)
    private TipoLancamento tipo;

    @Column
    @Enumerated(EnumType.STRING)
    private StatusLancamento status;
}
