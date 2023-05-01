package com.humberto789.minhasfinancas.service;

import com.humberto789.minhasfinancas.exception.ErroAutenticacao;
import com.humberto789.minhasfinancas.exception.RegraNegocioException;
import com.humberto789.minhasfinancas.model.entity.Usuario;
import com.humberto789.minhasfinancas.model.repository.UsuarioRepository;
import com.humberto789.minhasfinancas.service.impl.UsuarioServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class UsuarioServiceTest {

    @SpyBean
    UsuarioServiceImpl service;

    @MockBean
    UsuarioRepository repository;

    @Test
    public void deveValidarEmail() {

        Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(false);

        Assertions.assertDoesNotThrow(() -> {
            service.validarEmail("email@email.com");
        });
    }

    @Test
    public void deveLancarExcecaoAoValidarEmailQuandoExistirEmailCadastrado() {

        Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(true);

        Assertions.assertThrowsExactly(RegraNegocioException.class, () -> {
           service.validarEmail("usuario@email.com");
        });
    }

    @Test
    public void deveAutenticarUmUsuarioComSucesso() {
        String email = "email@email.com";
        String senha= "senha";

        Usuario usuario = Usuario.builder().email(email).senha(senha).id(1l).build();
        Mockito.when(repository.findByEmail(email)).thenReturn(Optional.of(usuario));

        Usuario result = service.autenticar(email, senha);

        Assertions.assertNotNull(result);
    }

    @Test
    public void deveLancarExcecaoQuandoNaoEncontrarUsuarioCadastradoComEmailInformado() {
        Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());


        Throwable exception = Assertions.assertThrowsExactly(ErroAutenticacao.class, () -> {
            service.autenticar("usuario@email.com", "senha");
        });

        Assertions.assertInstanceOf(ErroAutenticacao.class, exception, "Usuário não encontrado para o email informado.");
    }

    @Test
    public void deveLancarExcecaoQuandoSenhaNaoForCorreta(){
        String senha = "senha";
        Usuario usuario = Usuario.builder().email("usuario@email.com").senha(senha).build();
        Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(usuario));

        Throwable exception = Assertions.assertThrowsExactly(ErroAutenticacao.class, () -> {
            service.autenticar("usuario@email.com", "senha incorreta");
        });

        Assertions.assertInstanceOf(ErroAutenticacao.class, exception, "Senha inválida.");
    }

    @Test
    public void deveSalvarUmUsuario(){
        Mockito.doNothing().when(service).validarEmail(Mockito.anyString());
        Usuario usuario = Usuario.builder()
                .id(1l)
                .nome("nome")
                .email("email@email.com")
                .senha("senha")
                .build();

        Mockito.when(repository.save(Mockito.any(Usuario.class))).thenReturn(usuario);

        Usuario usuarioSalvo = service.salvarUsuario(usuario);

        Assertions.assertNotNull(usuarioSalvo);
        Assertions.assertEquals(usuarioSalvo.getId(), usuario.getId());
        Assertions.assertEquals(usuarioSalvo.getNome(), usuario.getNome());
        Assertions.assertEquals(usuarioSalvo.getEmail(), usuario.getEmail());
        Assertions.assertEquals(usuarioSalvo.getSenha(), usuario.getSenha());
    }

    @Test
    public void naoDeveSalvarUsuarioComEmailJaCadastrado() {
        String email = "usuario@email.com";
        Usuario usuario = Usuario.builder().email(email).build();
        Mockito.doThrow(RegraNegocioException.class).when(service).validarEmail(email);

        Assertions.assertThrowsExactly(RegraNegocioException.class, () -> {
            service.salvarUsuario(usuario);
        });

        Mockito.verify(repository, Mockito.never()).save(usuario);

    }
}
