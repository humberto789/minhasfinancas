package com.humberto789.minhasfinancas.controller.resource;

import com.humberto789.minhasfinancas.controller.dto.UsuarioDTO;
import com.humberto789.minhasfinancas.exception.ErroAutenticacao;
import com.humberto789.minhasfinancas.exception.RegraNegocioException;
import com.humberto789.minhasfinancas.model.entity.Usuario;
import com.humberto789.minhasfinancas.service.LancamentoService;
import com.humberto789.minhasfinancas.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final LancamentoService lancamentoService;

    @PostMapping
    public ResponseEntity salvar( @RequestBody UsuarioDTO dto) {
        Usuario usuario = Usuario.builder()
                .nome(dto.getNome())
                .senha(dto.getSenha())
                .email(dto.getEmail())
                .build();
        try {
            Usuario usuarioSalvo = usuarioService.salvarUsuario(usuario);
            return new ResponseEntity(usuarioSalvo, HttpStatus.CREATED);
        }catch(RegraNegocioException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/autenticar")
    public ResponseEntity autenticar(@RequestBody UsuarioDTO dto) {
        try {
            Usuario usuarioAutenticado = usuarioService.autenticar(dto.getEmail(), dto.getSenha());
            return ResponseEntity.ok(usuarioAutenticado);
        }catch(ErroAutenticacao e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}/saldo")
    public ResponseEntity obterSaldo(@PathVariable("id") Long id){

        Optional<Usuario> usuario = usuarioService.obterPorId(id);

        if(usuario.isEmpty()){
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

        BigDecimal saldo = lancamentoService.obterSaldoPorUsuario(id);
        return ResponseEntity.ok(saldo);
    }

}
