package br.com.efigueredo.blackscreen.comandos.invocacao;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.efigueredo.blackscreen.comandos.invocacao.prototipo.PrototipoControlador;
import br.com.efigueredo.blackscreen.comandos.invocacao.prototipo.PrototipoControladorComDependencia;
import br.com.efigueredo.blackscreen.comandos.invocacao.prototipo.PrototipoControladorDependenciaInvalida;
import br.com.efigueredo.blackscreen.comandos.invocacao.prototipo.PrototipoControladorDuploConstrutorAnotado;
import br.com.efigueredo.blackscreen.comandos.invocacao.prototipo.PrototipoControladorSemConstrutorAdequando;
import br.com.efigueredo.container.ContainerIoc;
import br.com.efigueredo.container.construtor.exception.InversaoDeControleInvalidaException;
import br.com.efigueredo.container.exception.ContainerIocException;
import br.com.efigueredo.container.objetos.exception.ClasseIlegalParaIntanciaException;

class InstanciadorControladorUnitarioTest {

	@Mock
	private ContainerIoc containerIoc;
	
	@InjectMocks
	private InstanciadorControlador instanciador;

	@BeforeEach
	void setUp() throws Exception {
		this.instanciador = new InstanciadorControlador("br.com.efigueredo.blackscreen.comandos");
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void deveriaRetornarUmaInstanciaDaClasseControladoraSemDependencias() throws ContainerIocException {
		Class<?> controlador = PrototipoControlador.class;
		when(this.containerIoc.getInstancia(controlador)).thenReturn(new PrototipoControlador());
		Object instanciaControlador = this.instanciador.intanciarControlador(controlador);
		assertTrue(instanciaControlador instanceof PrototipoControlador);
	}
	
	@Test
	void deveriaRetornarUmaInstanciaDaClasseControladoraComDependencias() throws ContainerIocException {
		Class<?> controlador = PrototipoControladorComDependencia.class;
		when(this.containerIoc.getInstancia(controlador)).thenReturn(new PrototipoControladorComDependencia(new PrototipoControlador()));
		PrototipoControladorComDependencia instanciaControlador = (PrototipoControladorComDependencia) this.instanciador.intanciarControlador(controlador);
		assertTrue(instanciaControlador instanceof PrototipoControladorComDependencia);
		PrototipoControlador controller = instanciaControlador.getController();
		assertTrue(controller instanceof PrototipoControlador);
	}
	
	@Test
	public void deveriaJogarExcecao_SeHouverMaisDeUmContrutorAnotado_ComArrobaInjecaoNoControlador() throws ContainerIocException {
		Class<?> controlador = PrototipoControladorDuploConstrutorAnotado.class;
		when(this.containerIoc.getInstancia(controlador)).thenThrow(InversaoDeControleInvalidaException.class);
		assertThrows(InversaoDeControleInvalidaException.class, () -> this.instanciador.intanciarControlador(controlador));
	}
	
	@Test
	public void deveriaJogarExcecao_SeNaoHouverConstrutorPadraoENaoHouverConstrutorAnotado_ComArrobaInjecaoNoControlador() throws ContainerIocException {
		Class<?> controlador = PrototipoControladorSemConstrutorAdequando.class;
		when(this.containerIoc.getInstancia(controlador)).thenThrow(InversaoDeControleInvalidaException.class);
		assertThrows(InversaoDeControleInvalidaException.class, () -> this.instanciador.intanciarControlador(controlador));
	}
	
	@Test
	public void deveriaJogarExcecao_SeADependenciaInterfaceNaoEstiverConfigurada() throws ContainerIocException {
		Class<?> controlador = PrototipoControladorDependenciaInvalida.class;
		when(this.containerIoc.getInstancia(controlador)).thenThrow(ClasseIlegalParaIntanciaException.class);
		assertThrows(ClasseIlegalParaIntanciaException.class, () -> this.instanciador.intanciarControlador(controlador));
	}

}
