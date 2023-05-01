package com.humberto789.minhasfinancas.model.repository;

import com.humberto789.minhasfinancas.model.entity.Lancamento;
import com.humberto789.minhasfinancas.model.enums.StatusLancamento;
import com.humberto789.minhasfinancas.model.enums.TipoLancamento;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class LancamentoRepositoryTest {

    @Autowired
    LancamentoRepository repository;

    @Autowired
    TestEntityManager entityManager;

    @Test
    public void deveSalvarUmLancamento(){
        Lancamento lancamento = criarLancamento();

        Lancamento lancamentoSalvo = repository.save(lancamento);

        Assertions.assertNotNull(lancamentoSalvo.getId());
        Assertions.assertEquals(lancamentoSalvo.getAno(), lancamento.getAno());
        Assertions.assertEquals(lancamentoSalvo.getMes(), lancamento.getMes());
        Assertions.assertEquals(lancamentoSalvo.getDescricao(), lancamento.getDescricao());
        Assertions.assertEquals(lancamentoSalvo.getValor(), lancamento.getValor());
        Assertions.assertEquals(lancamentoSalvo.getTipo(), lancamento.getTipo());
        Assertions.assertEquals(lancamentoSalvo.getStatus(), lancamento.getStatus());
        Assertions.assertEquals(lancamentoSalvo.getDataCadastro(), lancamento.getDataCadastro());
    }

    @Test
    public void deveDeletarUmLancamento(){
        Lancamento lancamento = criarPersistirLancamento();

        lancamento = entityManager.find(Lancamento.class, lancamento.getId());

        repository.delete(lancamento);

        Lancamento lancamentoInexistente = entityManager.find(Lancamento.class, lancamento.getId());

        Assertions.assertNull(lancamentoInexistente);
    }


    @Test
    public void deveAtualizarUmLancamento(){
        Lancamento lancamento= criarPersistirLancamento();
        lancamento.setAno(2022);
        lancamento.setDescricao("Atualizada");
        lancamento.setStatus(StatusLancamento.CANCELADO);

        repository.save(lancamento);

        Lancamento lancamentoAtualizado = entityManager.find(Lancamento.class, lancamento.getId());

        Assertions.assertEquals(lancamentoAtualizado.getAno(), 2022);
        Assertions.assertEquals(lancamentoAtualizado.getDescricao(), "Atualizada");
        Assertions.assertEquals(lancamentoAtualizado.getStatus(), StatusLancamento.CANCELADO);
    }

    @Test
    public void deveBuscarUmLancamentoPorId(){
        Lancamento lancamento = criarPersistirLancamento();

        Optional<Lancamento> lancamentoEncontrado = repository.findById(lancamento.getId());

        Assertions.assertTrue(lancamentoEncontrado.isPresent());

    }

    public Lancamento criarPersistirLancamento() {
        Lancamento lancamento = criarLancamento();
        entityManager.persist(lancamento);
        return lancamento;
    }
    public static Lancamento criarLancamento() {
        return Lancamento.builder()
                .ano(2023)
                .mes(1)
                .descricao("lancamento")
                .valor(BigDecimal.valueOf(10))
                .tipo(TipoLancamento.RECEITA)
                .status(StatusLancamento.PENDENTE)
                .dataCadastro(LocalDate.now())
                .build();
    }
}
