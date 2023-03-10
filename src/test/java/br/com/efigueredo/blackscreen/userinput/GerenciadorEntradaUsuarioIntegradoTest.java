package br.com.efigueredo.blackscreen.userinput;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;

import br.com.efigueredo.blackscreen.sistema.configuracoes.respostas.exception.ConfiguracaoRespostaSistemaException;
import br.com.efigueredo.blackscreen.userinput.exception.EntradaUsuarioInvalidaException;
import br.com.efigueredo.blackscreen.userinput.exception.MaisDeUmParametroNaExpressaoException;
import br.com.efigueredo.container.exception.ContainerIocException;

@Tag("integrado")
public class GerenciadorEntradaUsuarioIntegradoTest {

	private GerenciadorEntradaUsuario gerenciador;
	
	@BeforeEach
	public void setup() throws ContainerIocException, ConfiguracaoRespostaSistemaException  {
		String pacoteRaiz = "br.com.efigueredo.blackscreen.prototipo_configuracao_resposta.correta.unica";
		Reflections reflections = new Reflections(pacoteRaiz, new SubTypesScanner(false), new TypeAnnotationsScanner());
		this.gerenciador = new GerenciadorEntradaUsuario(reflections, pacoteRaiz);
	}
	
	@Test
	public void deveriaRetornarObjetoEntradaUsuarioIncorreto_DadosExpressaoIncorreta_QuandoManipularExpressao() throws MaisDeUmParametroNaExpressaoException {
		String expressao = "--comando --parametro1 --parametro2 valor";
		EntradaUsuario entradaUsuarioObj = this.gerenciador.manipularExpressao(expressao);
		assertEquals("--comando", entradaUsuarioObj.getComando());
		assertEquals(Arrays.asList("--parametro1", "--parametro2"), entradaUsuarioObj.getParametros());
		assertEquals(Arrays.asList("valor"), entradaUsuarioObj.getValores());
	}
	
	@Test
	public void deveriaRetornarObjetoEntradaUsuarioCorreto_DadosExpressaoCorreta_QuandoManipularExpressao() throws MaisDeUmParametroNaExpressaoException {
		String expressao = "comando --parametro1 valor";
		EntradaUsuario entradaUsuarioObj = this.gerenciador.manipularExpressao(expressao);
		assertEquals("comando", entradaUsuarioObj.getComando());
		assertEquals(Arrays.asList("--parametro1"), entradaUsuarioObj.getParametros());
		assertEquals(Arrays.asList("valor"), entradaUsuarioObj.getValores());
	}
	
	@Test
	public void deveriaRetornarObjetoEntradaUsuario_DadosExpressao_QuandoManipularExpressao() throws MaisDeUmParametroNaExpressaoException {
		String expressao = "comando --parametro1 --parametro2 valor";
		EntradaUsuario entradaUsuarioObj = this.gerenciador.manipularExpressao(expressao);
		assertEquals("comando", entradaUsuarioObj.getComando());
		assertEquals(Arrays.asList("--parametro1", "--parametro2"), entradaUsuarioObj.getParametros());
		assertEquals(Arrays.asList("valor"), entradaUsuarioObj.getValores());
	}
	
	@Test
	public void naoDeveriaJogarExcecao_QuandoVerificarExpressao_DadoEntradaUsuarioCorreto() {
		EntradaUsuario entradaUsuario = new EntradaUsuario("comando", Arrays.asList("--param1"), Arrays.asList("valor"));
		assertDoesNotThrow(() -> this.gerenciador.executarVerificacoesExpressao(entradaUsuario));;
	}
	
	@Test
	public void deveriaJogarExcecao_QuandoVerificarExpressao_DadoEntradaUsuarioIncorretoComando() {
		EntradaUsuario entradaUsuario = new EntradaUsuario("--comando", Arrays.asList("--param1"), Arrays.asList("valor"));
		assertThrows(EntradaUsuarioInvalidaException.class, () -> this.gerenciador.executarVerificacoesExpressao(entradaUsuario));
	}
	
	@Test
	public void deveriaJogarExcecao_QuandoVerificarExpressao_DadoEntradaUsuarioIncorretoParametro() {
		EntradaUsuario entradaUsuario = new EntradaUsuario("comando", Arrays.asList("--param1", "--param2"), Arrays.asList("valor"));
		assertThrows(EntradaUsuarioInvalidaException.class, () -> this.gerenciador.executarVerificacoesExpressao(entradaUsuario));
	}
	
	@Test
	public void deveriaJogarExcecao_QuandoVerificarExpressao_DadoEntradaUsuarioIncorretoComandoParametro() {
		EntradaUsuario entradaUsuario = new EntradaUsuario("--comando", Arrays.asList("--param1", "--param2"), Arrays.asList("valor"));
		assertThrows(EntradaUsuarioInvalidaException.class, () -> this.gerenciador.executarVerificacoesExpressao(entradaUsuario));
	}
	
}
