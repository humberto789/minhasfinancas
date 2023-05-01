package com.humberto789.minhasfinancas.model.repository;

import com.humberto789.minhasfinancas.model.entity.Lancamento;
import com.humberto789.minhasfinancas.model.enums.TipoLancamento;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

public interface LancamentoRepository extends JpaRepository<Lancamento, Long> {

    @Transactional
    @Query(value = " select sum(l.valor) from lancamento l join l.usuario u " +
            "where u.id = :idUsuario and l.tipo = :tipo " +
            "group by u")
    BigDecimal obterSaldoPorTipoLancamentoEUsuario(@Param("idUsuario") Long idUsuario, @Param("tipo") TipoLancamento tipo);
}
