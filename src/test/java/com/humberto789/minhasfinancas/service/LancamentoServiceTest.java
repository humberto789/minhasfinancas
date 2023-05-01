package com.humberto789.minhasfinancas.service;

import com.humberto789.minhasfinancas.exception.ErroAutenticacao;
import com.humberto789.minhasfinancas.exception.RegraNegocioException;
import com.humberto789.minhasfinancas.model.entity.Lancamento;
import com.humberto789.minhasfinancas.model.entity.Usuario;
import com.humberto789.minhasfinancas.model.enums.StatusLancamento;
import com.humberto789.minhasfinancas.model.enums.TipoLancamento;
import com.humberto789.minhasfinancas.model.repository.LancamentoRepository;
import com.humberto789.minhasfinancas.model.repository.LancamentoRepositoryTest;
import com.humberto789.minhasfinancas.service.impl.LancamentoServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Example;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class LancamentoServiceTest {

    @SpyBean
    LancamentoServiceImpl service;

    @MockBean
    LancamentoRepository repository;

    @Test
    public void deveSalvarLancamento() {
        Lancamento lancamentoASalvar = LancamentoRepositoryTest.criarLancamento();
        Mockito.doNothing().when(service).validar(lancamentoASalvar);

        Lancamento lancamentoSalvo = LancamentoRepositoryTest.criarLancamento();
        lancamentoSalvo.setId(1l);
        lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);
        Mockito.when(repository.save(lancamentoASalvar)).thenReturn(lancamentoSalvo);

        Lancamento lancamento = service.salvar(lancamentoASalvar);

        Assertions.assertEquals(lancamento.getId(), lancamentoSalvo.getId());
        Assertions.assertEquals(lancamento.getStatus(), StatusLancamento.PENDENTE);
    }

    @Test
    public void naoDeveSalvarLancamentoQuandoHouverErroDeValidacao(){
        Lancamento lancamentoASalvar = LancamentoRepositoryTest.criarLancamento();
        Mockito.doThrow(RegraNegocioException.class).when(service).validar(lancamentoASalvar);

        Assertions.assertThrowsExactly(RegraNegocioException.class, () -> {
            service.salvar(lancamentoASalvar);
        });

        Mockito.verify(repository, Mockito.never()).save(lancamentoASalvar);
    }

    @Test
    public void deveAtualizarLancamento() {
        Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
        lancamento.setId(1l);
        lancamento.setStatus(StatusLancamento.PENDENTE);
        Mockito.doNothing().when(service).validar(lancamento);

        Mockito.when(repository.save(lancamento)).thenReturn(lancamento);

        service.atualizar(lancamento);

        Mockito.verify(repository, Mockito.times(1)).save(lancamento);
    }

    @Test
    public void deveLancarErroAoTentarAtualizarLancamentoQueAindaNaoFoiSalvo(){
        Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();

        Assertions.assertThrowsExactly(NullPointerException.class, () -> {
            service.atualizar(lancamento);
        });

        Mockito.verify(repository, Mockito.never()).save(lancamento);
    }

    @Test
    public void naoDeveAtualizarLancamentoQuandoHouverErroDeValidacao(){
        Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
        lancamento.setId(1l);

        Mockito.doThrow(RegraNegocioException.class).when(service).validar(lancamento);

        Assertions.assertThrowsExactly(RegraNegocioException.class, () -> {
            service.atualizar(lancamento);
        });

        Mockito.verify(repository, Mockito.never()).save(lancamento);
    }

    @Test
    public void deveDeletarUmLancamento(){
        Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
        lancamento.setId(1l);

        service.deletar(lancamento);

        Mockito.verify(repository, Mockito.times(1)).delete(lancamento);
    }

    @Test
    public void deveLancarErroAoTentarDeletarLancamentoQueAindaNaoFoiSalvo(){
        Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();

        Assertions.assertThrowsExactly(NullPointerException.class, () -> {
            service.deletar(lancamento);
        });

        Mockito.verify(repository, Mockito.never()).delete(lancamento);
    }

    @Test
    public void deveFiltrarLancamento(){
        Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
        lancamento.setId(1l);

        List<Lancamento> lista = Arrays.asList(lancamento);

        Mockito.when(repository.findAll(Mockito.any(Example.class))).thenReturn(lista);

        List<Lancamento> resultado = service.buscar(lancamento);

        Assertions.assertFalse(resultado.isEmpty());
        Assertions.assertArrayEquals(resultado.toArray(), lista.toArray());
        Assertions.assertEquals(resultado.size(), lista.size());
    }

    @Test
    public void deveAtualizarStatusLancamento(){
        Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
        lancamento.setId(1l);
        lancamento.setStatus(StatusLancamento.PENDENTE);

        StatusLancamento novoStatus = StatusLancamento.EFETIVADO;


        Mockito.doReturn(lancamento).when(service).atualizar(lancamento);

        service.atualizarStatus(lancamento, novoStatus);

        Assertions.assertEquals(lancamento.getStatus(), novoStatus);
        Mockito.verify(service).atualizar(lancamento);

    }

    @Test
    public void deveObterLancamentoPorId(){
        Long id = 1l;

        Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
        lancamento.setId(id);

        Mockito.when(repository.findById(id)).thenReturn(Optional.of(lancamento));

        Optional<Lancamento> resultado = service.obterPorId(id);

        Assertions.assertTrue(resultado.isPresent());
    }

    @Test
    public void deveRetornarVazioQuandoLancamentoNaoExiste(){
        Long id = 1l;

        Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
        lancamento.setId(id);

        Mockito.when(repository.findById(id)).thenReturn(Optional.empty());

        Optional<Lancamento> resultado = service.obterPorId(id);

        Assertions.assertTrue(resultado.isEmpty());
    }

    @Test
    public void deveLancarErroAoValidarUmLancamento(){
        Lancamento lancamento = new Lancamento();
        Throwable exception;

        exception = Assertions.assertThrowsExactly(RegraNegocioException.class, () -> {
            service.validar(lancamento);
        });

        Assertions.assertInstanceOf(RegraNegocioException.class, exception, "Informe uma descrição válida.");

        lancamento.setDescricao("");

        exception = Assertions.assertThrowsExactly(RegraNegocioException.class, () -> {
            service.validar(lancamento);
        });

        Assertions.assertInstanceOf(RegraNegocioException.class, exception, "Informe uma descrição válida.");

        lancamento.setDescricao("   ");

        exception = Assertions.assertThrowsExactly(RegraNegocioException.class, () -> {
            service.validar(lancamento);
        });

        Assertions.assertInstanceOf(RegraNegocioException.class, exception, "Informe uma descrição válida.");

        lancamento.setDescricao("descricao");

        exception = Assertions.assertThrowsExactly(RegraNegocioException.class, () -> {
            service.validar(lancamento);
        });

        Assertions.assertInstanceOf(RegraNegocioException.class, exception, "Informe um mês válido.");

        lancamento.setMes(0);

        exception = Assertions.assertThrowsExactly(RegraNegocioException.class, () -> {
            service.validar(lancamento);
        });

        Assertions.assertInstanceOf(RegraNegocioException.class, exception, "Informe um mês válido.");

        lancamento.setMes(13);

        exception = Assertions.assertThrowsExactly(RegraNegocioException.class, () -> {
            service.validar(lancamento);
        });

        Assertions.assertInstanceOf(RegraNegocioException.class, exception, "Informe um mês válido.");

        lancamento.setMes(1);

        exception = Assertions.assertThrowsExactly(RegraNegocioException.class, () -> {
            service.validar(lancamento);
        });

        Assertions.assertInstanceOf(RegraNegocioException.class, exception, "Informe um ano válido.");

        lancamento.setAno(10);

        exception = Assertions.assertThrowsExactly(RegraNegocioException.class, () -> {
            service.validar(lancamento);
        });

        Assertions.assertInstanceOf(RegraNegocioException.class, exception, "Informe um ano válido.");

        lancamento.setAno(2023);

        exception = Assertions.assertThrowsExactly(RegraNegocioException.class, () -> {
            service.validar(lancamento);
        });

        Assertions.assertInstanceOf(RegraNegocioException.class, exception, "Informe um usuário.");

        lancamento.setUsuario(new Usuario());

        exception = Assertions.assertThrowsExactly(RegraNegocioException.class, () -> {
            service.validar(lancamento);
        });

        Assertions.assertInstanceOf(RegraNegocioException.class, exception, "Informe um usuário.");

        lancamento.getUsuario().setId(1l);

        exception = Assertions.assertThrowsExactly(RegraNegocioException.class, () -> {
            service.validar(lancamento);
        });

        Assertions.assertInstanceOf(RegraNegocioException.class, exception, "Informe um valor válido.");

        lancamento.setValor(BigDecimal.valueOf(-1l));
        exception = Assertions.assertThrowsExactly(RegraNegocioException.class, () -> {
            service.validar(lancamento);
        });

        Assertions.assertInstanceOf(RegraNegocioException.class, exception, "Informe um valor válido.");

        lancamento.setValor(BigDecimal.valueOf(100l));

        exception = Assertions.assertThrowsExactly(RegraNegocioException.class, () -> {
            service.validar(lancamento);
        });

        Assertions.assertInstanceOf(RegraNegocioException.class, exception, "Informe um tipo de lançamento.");

        lancamento.setTipo(TipoLancamento.RECEITA);

        Assertions.assertDoesNotThrow(() -> {
            service.validar(lancamento);
        });
    }
}
