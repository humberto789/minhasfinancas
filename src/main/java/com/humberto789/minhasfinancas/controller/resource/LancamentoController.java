package com.humberto789.minhasfinancas.controller.resource;

import com.humberto789.minhasfinancas.controller.dto.AtualizaStatusDTO;
import com.humberto789.minhasfinancas.controller.dto.LancamentoDTO;
import com.humberto789.minhasfinancas.exception.RegraNegocioException;
import com.humberto789.minhasfinancas.model.entity.Lancamento;
import com.humberto789.minhasfinancas.model.entity.Usuario;
import com.humberto789.minhasfinancas.model.enums.StatusLancamento;
import com.humberto789.minhasfinancas.model.enums.TipoLancamento;
import com.humberto789.minhasfinancas.service.LancamentoService;
import com.humberto789.minhasfinancas.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/lancamentos")
@RequiredArgsConstructor
public class LancamentoController {

    private final LancamentoService service;
    private final UsuarioService usuarioService;

    @PostMapping
    public ResponseEntity salvar(@RequestBody LancamentoDTO lancamentoDto) {

        try {
            Lancamento lancamento = converter(lancamentoDto);
            lancamento = service.salvar(lancamento);
            return new ResponseEntity(lancamento, HttpStatus.CREATED);
        } catch(RegraNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity atualizar(@PathVariable("id") Long id, @RequestBody LancamentoDTO lancamentoDto){
        return service.obterPorId(id).map( entity -> {

            try {
                Lancamento lancamento = converter(lancamentoDto);
                lancamento.setId(entity.getId());
                service.atualizar(lancamento);

                return ResponseEntity.ok(lancamento);
            }catch (RegraNegocioException e){
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }).orElseGet( () -> new ResponseEntity("Lançamento não encontrado na base de dados.", HttpStatus.BAD_REQUEST));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deletar(@PathVariable("id") Long id) {
        return service.obterPorId(id).map(entidade -> {
           service.deletar(entidade);
           return new ResponseEntity(HttpStatus.NO_CONTENT);
        }).orElseGet( () -> new ResponseEntity("Lançamento não encontrado na base de dados.", HttpStatus.BAD_REQUEST));
    }

    @GetMapping
    public ResponseEntity buscar(
            @RequestParam(name="descricao", required = false) String descricao,
            @RequestParam(name="mes", required = false) Integer mes,
            @RequestParam(name="ano", required = false) Integer ano,
            @RequestParam("usuario") Long idUsuario) {

        Lancamento lancamentoFiltro = Lancamento.builder()
                .descricao(descricao)
                .mes(mes)
                .ano(ano).build();

        Optional<Usuario> usuario = usuarioService.obterPorId(idUsuario);

        if(usuario.isEmpty()) {
            return ResponseEntity.badRequest().body("Não foi possível realizar a consulta. Usuário não encontrado para o Id informado.");
        }

        lancamentoFiltro.setUsuario(usuario.get());

        List<Lancamento> lancamentos = service.buscar(lancamentoFiltro);

        return ResponseEntity.ok(lancamentos);
    }

    @PutMapping("/{id}/atualiza-status")
    public ResponseEntity atualizarStatus( @PathVariable("id") Long id ,@RequestBody AtualizaStatusDTO dto) {
        return service.obterPorId(id).map(entity -> {
            StatusLancamento statusSelecionado = StatusLancamento.valueOf(dto.getStatus());
            if(statusSelecionado == null) {
                return ResponseEntity.badRequest().body("Não foi possível atualizar o status de lançamento, envie um status válido");
            }

            try {
                entity.setStatus(statusSelecionado);
                service.atualizar(entity);
                return ResponseEntity.ok(entity);
            }catch (RegraNegocioException e){
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }).orElseGet( () -> new ResponseEntity("Lançamento não encontrado na base de dados.", HttpStatus.BAD_REQUEST) );
    }

    private Lancamento converter(LancamentoDTO lancamentoDto) {
        Lancamento lancamento = Lancamento.builder()
                .id(lancamentoDto.getId())
                .descricao(lancamentoDto.getDescricao())
                .ano(lancamentoDto.getAno())
                .mes(lancamentoDto.getMes())
                .valor(lancamentoDto.getValor())
                .build();

        Usuario usuario = usuarioService
                .obterPorId(lancamentoDto.getUsuario())
                .orElseThrow(() -> new RegraNegocioException("Usuario não encontrado para Id informado."));

        lancamento.setUsuario(usuario);

        if(lancamentoDto.getTipo() != null) {
            lancamento.setTipo(TipoLancamento.valueOf(lancamentoDto.getTipo()));
        }

        if(lancamentoDto.getStatus() != null) {
            lancamento.setStatus(StatusLancamento.valueOf(lancamentoDto.getStatus()));
        }

        return lancamento;
    }
}
