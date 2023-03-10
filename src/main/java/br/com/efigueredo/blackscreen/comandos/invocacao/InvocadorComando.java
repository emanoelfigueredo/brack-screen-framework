package br.com.efigueredo.blackscreen.comandos.invocacao;

import java.lang.reflect.Method;
import java.util.List;

import br.com.efigueredo.blackscreen.comandos.invocacao.exception.InvocacaoComandoInterrompidaException;
import br.com.efigueredo.container.exception.ContainerIocException;

/**
 * <h4>Classe responsável por invocar os métodos de comando.</h4>
 * 
 * @author Emanoel
 * @since 1.0.0
 */
public class InvocadorComando {

	/** Objeto responsável por obter uma instância do controlador do método. */
	private InstanciadorControlador intanciadorControlador;

	/** Objeto responsável por invocar o método de comando. */
	private InvocadorMetodo invocadorMetodo;

	/**
	 * Construtor.
	 *
	 * @throws ContainerIocException Erro no container Ioc.
	 */
	public InvocadorComando(String pacoteRaiz) throws ContainerIocException {
		this.intanciadorControlador = new InstanciadorControlador(pacoteRaiz);
		this.invocadorMetodo = new InvocadorMetodo();
	}

	/**
	 * Invoque o método de comando.
	 * 
	 * Seu funcionamento consiste em delegar as tarefas necessárias para a invocação
	 * do método passado por parâmetro. Os objetos responsáveis irão intânciar o
	 * controlador atual com todas as suas dependências e invocar o método com todos
	 * os parâmetros.
	 *
	 * @param controlador   Objeto {@linkplain Class} que represente a classe
	 *                      controladora ao qual o método pertence.
	 * @param metodoComando Objeto {@linkplain Method} que represente o método para
	 *                      ser invocado.
	 * @param valores       Lista de valores para ser passada como parâmetro na
	 *                      invocação do método.
	 * @throws InvocacaoComandoInterrompidaException Ocorrerá se houver alguma falha
	 *                                               na invocação do comando. A
	 *                                               cuasa estára indicada na stack
	 *                                               trace.
	 * @throws ContainerIocException                 Erro no container Ioc.
	 */
	public void invocarComando(Class<?> controlador, Method metodoComando, List<String> valores)
			throws InvocacaoComandoInterrompidaException, ContainerIocException {
		Object objetoControlador = this.intanciadorControlador.intanciarControlador(controlador);
		this.invocadorMetodo.invocar(objetoControlador, metodoComando, valores);
	}

}
