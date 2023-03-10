package br.com.efigueredo.blackscreen.sistema;

import java.lang.reflect.Method;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;

import br.com.efigueredo.blackscreen.anotacoes.Comando;
import br.com.efigueredo.blackscreen.comandos.invocacao.InvocadorComando;
import br.com.efigueredo.blackscreen.comandos.invocacao.exception.InvocacaoComandoInterrompidaException;
import br.com.efigueredo.blackscreen.comandos.metodos.GerenciadorComandoControlador;
import br.com.efigueredo.blackscreen.comandos.metodos.exception.NomeComandoInexistenteException;
import br.com.efigueredo.blackscreen.comandos.metodos.exception.ParametroDeComandoInexistenteException;
import br.com.efigueredo.blackscreen.comandos.metodos.exception.SolicitacaoDeMetodoComandoInexistenteException;
import br.com.efigueredo.blackscreen.comandos.metodos.exception.ValoresIncoerentesComOsComandosExistentesException;
import br.com.efigueredo.blackscreen.sistema.configuracoes.respostas.RespostasSistema;
import br.com.efigueredo.blackscreen.sistema.configuracoes.respostas.RespostasSistemaFactory;
import br.com.efigueredo.blackscreen.sistema.configuracoes.respostas.exception.ConfiguracaoRespostaSistemaException;
import br.com.efigueredo.blackscreen.sistema.exception.ControladorAtualInexistenteException;
import br.com.efigueredo.blackscreen.userinput.EntradaUsuario;
import br.com.efigueredo.blackscreen.userinput.GerenciadorEntradaUsuario;
import br.com.efigueredo.blackscreen.userinput.exception.EntradaUsuarioInvalidaException;
import br.com.efigueredo.container.ContainerIoc;
import br.com.efigueredo.container.exception.ContainerIocException;

/**
 * <h4>Classe que representa o sistema. Sua função é disponibilizar o método que
 * inicia o sistema.</h4>
 * 
 * @author Emanoel
 * @since 1.0.0
 */
public class AplicacaoBlackScreen {

	/**
	 * Objeto {@linkplain Class} estático que representa a classe controladora
	 * atual. Nela deve ter ao menos um método anotado com {@linkplain Comando}.
	 */
	private static Class<?> controladorAtual;

	/**
	 * Objeto {@linkplain ContainerIoc} estático responsável pela inversão de
	 * controle e injeção de dependências. Deve ser acessado pelas outras classes.
	 */
	private static ContainerIoc containerIoc;

	/**
	 * Objeto responsável por gerenciar todos os procedimentos necessários para
	 * obtermos a entrada do usuário.
	 */
	private GerenciadorEntradaUsuario gerenteEntrada;

	/**
	 * Objeto responsável pro gerenciar os comandos da classe constroladora atual.
	 */
	private GerenciadorComandoControlador gerenteMetodos;

	/** Objeto responsável por invocar os métodos de comandos. */
	private InvocadorComando invocadorComandos;

	/** Objeto responsável pelas respostas do sistema. */
	private static RespostasSistema respostasSistema;

	/** O pacote raiz do projeto. */
	private String pacoteRaizProjeto;

	/**
	 * Construtor.
	 * 
	 * Inicializa os objetos funcionais para a classe.
	 *
	 * @param controladorInicial Objeto {@linkplain Class} que represente a classe
	 *                           constroladora inicial.
	 * @param pacoteRaizProjeto  Pacote raiz do projeto. Ou o de maior hierarquia
	 *                           possível.
	 * @throws ControladorAtualInexistenteException Ocorrerá se o paramâmetro
	 *                                              controladorInicial for
	 *                                              preenchido com valor null.
	 * @throws ContainerIocException                Erro no container Ioc.
	 * @throws ConfiguracaoRespostaSistemaException Ocorrerá se houver algum erro na
	 *                                              configuração de respostas do
	 *                                              sistema.
	 */
	public AplicacaoBlackScreen(Class<?> controladorInicial, String pacoteRaizProjeto)
			throws ControladorAtualInexistenteException, ContainerIocException, ConfiguracaoRespostaSistemaException {
		if (controladorInicial == null) {
			throw new ControladorAtualInexistenteException("Não existe classe controladora setada no sistema.");
		}
		this.pacoteRaizProjeto = pacoteRaizProjeto;
		Reflections reflections = new Reflections(this.pacoteRaizProjeto, new SubTypesScanner(false),
				new TypeAnnotationsScanner());
		AplicacaoBlackScreen.controladorAtual = controladorInicial;
		this.gerenteEntrada = new GerenciadorEntradaUsuario(reflections, this.pacoteRaizProjeto);
		this.gerenteMetodos = new GerenciadorComandoControlador();
		this.invocadorComandos = new InvocadorComando(this.pacoteRaizProjeto);
		AplicacaoBlackScreen.respostasSistema = new RespostasSistemaFactory().getRespostasSistema(reflections,
				this.pacoteRaizProjeto);
	}

	/**
	 * Executar o sistema.
	 * 
	 * Primeiramente, recebendo a entrada do usuário. Caso seja nula, o laço será
	 * recomeçado. Usando a resposta do usuário, podemos obter o método
	 * correspondente ao comando desejado, com seus parâmetros de comando e seus
	 * valores. Se não valer nulo, então o método encontrado será invocado. Assim
	 * executando o comando desejado.
	 *
	 * @param habilitarComandoSair true, comando para sair padrão do sistema estará
	 *                             habilitado.
	 * @throws ContainerIocException Erro no Container IoC.
	 */
	public void executar(boolean habilitarComandoSair) throws ContainerIocException {
		respostasSistema.imprimirBanner();
		while (true) {
			EntradaUsuario entradaUsuario = this.receberEntrada();
			if (entradaUsuario == null) {
				continue;
			}
			if (entradaUsuario.getComando().equals("")) {
				continue;
			}
			this.verificarComandoSair(habilitarComandoSair, entradaUsuario);
			Method metodoComando = this.obterMetodoComando(entradaUsuario);
			if (metodoComando == null) {
				continue;
			}
			this.invocarMetodoComando(entradaUsuario, metodoComando);
		}
	}

	/**
	 * Método privado auxiliar para executar a verificação da entrada usuário para
	 * sair do sistema.
	 * 
	 * @param habilitarComandoSair Se for true, então a verificação pode ser
	 *                             efetuada.
	 * @param entradaUsuario       Entrada usuario com o comando inserido.
	 */
	private void verificarComandoSair(boolean habilitarComandoSair, EntradaUsuario entradaUsuario) {
		if (habilitarComandoSair
				&& (entradaUsuario.getComando().equals("sair") || entradaUsuario.getComando().equals("exit"))) {
			respostasSistema.imprimirMensagem("Encerrando sistema");
			System.exit(0);
		}
	}

	/**
	 * Método auxiliar privado responsável por tratar o procedimento de recebimento
	 * da entrada.
	 * 
	 * Caso ocorra alguma exceção, a mesma será tratada e impressa no console o
	 * erro.
	 *
	 * @return Objeto {@linkplain EntradaUsuario} que encapsule todos as partes da
	 *         entrada expressão inserida.<br>
	 *         null, caso uma exceção seja lançada.
	 */
	private EntradaUsuario receberEntrada() {
		EntradaUsuario entradaUsuario = null;
		try {
			entradaUsuario = this.gerenteEntrada.buildEntradaUsuario();
		} catch (EntradaUsuarioInvalidaException e) {
			respostasSistema.imprimirMensagemErro("Insira uma expressão válida");
		}
		return entradaUsuario;
	}

	/**
	 * Método privado auxiliar responsável por tratar do procedimento de obter o
	 * método de comando adequado.
	 *
	 * @param entradaUsuario Objeto {@linkplain EntradaUsuario} que encapsule todos
	 *                       as partes da entrada expressão inserida.
	 * @return Objeto {@linkplain Method} caso exista um método correspondente.<br>
	 *         null, caso não exista.
	 */
	private Method obterMetodoComando(EntradaUsuario entradaUsuario) {
		Method metodoComando = null;
		try {
			metodoComando = this.gerenteMetodos.getMetodoComando(entradaUsuario);
		} catch (NomeComandoInexistenteException e) {
			respostasSistema.imprimirMensagemErro("O comando " + entradaUsuario.getComando() + " não existe");
		} catch (ParametroDeComandoInexistenteException e) {
			respostasSistema.imprimirMensagemErro("O parâmetro de comando " + entradaUsuario.getParametros().get(0)
					+ " não existe para o comando " + entradaUsuario.getComando());
		} catch (ValoresIncoerentesComOsComandosExistentesException e) {
			respostasSistema.imprimirMensagemErro("Os valores inseridos não podem ser aceitos pelo comando");
		} catch (SolicitacaoDeMetodoComandoInexistenteException e) {
			respostasSistema.imprimirMensagemErro("Não existe comando que corresponda ao formato inserido.");
		}
		return metodoComando;
	}

	/**
	 * Método privado auxiliar responsável por tratar do procedimento de invocar o
	 * método de comando correspondente.
	 *
	 * @param entradaUsuario Objeto {@linkplain EntradaUsuario} que encapsule todos
	 *                       as partes da entrada expressão inserida.
	 * @param metodoComando  Objeto {@linkplain Method} que represente o método de
	 *                       comando.
	 * @throws ContainerIocException Erro no Container IoC.
	 */
	private void invocarMetodoComando(EntradaUsuario entradaUsuario, Method metodoComando)
			throws ContainerIocException {
		try {
			this.invocadorComandos.invocarComando(controladorAtual, metodoComando, entradaUsuario.getValores());
		} catch (InvocacaoComandoInterrompidaException e) {
			respostasSistema.imprimirMensagemErro("A invocação do comando foi interrompida");
			e.printStackTrace();
		}
	}

	/**
	 * Obter o objeto {@linkplain Class} da classe controladora atual do sistema.
	 *
	 * @return Objeto {@linkplain Class} da classe controladora atual.
	 */
	public static Class<?> getControladorAtual() {
		return AplicacaoBlackScreen.controladorAtual;
	}

	/**
	 * Obter o objeto {@linkplain ContainerIoc} responsável pela inversão de
	 * controle e injeção de dependências.
	 *
	 * @return {@linkplain ContainerIoc}
	 */
	public static ContainerIoc getContainerIoc() {
		return AplicacaoBlackScreen.containerIoc;
	}

	/**
	 * Alterar a classe controladora atual do sistema.
	 *
	 * @param classe Objeto {@linkplain Class} que represete a nova classe
	 *               controladora atual do sistema.
	 */
	public static void setControladorAtual(Class<?> classe) {
		AplicacaoBlackScreen.controladorAtual = classe;
	}

	/**
	 * Obtenha o objeto responsável pelas respostas do sistema.
	 * 
	 * @return Objeto responsável pelas respostas do sistema.
	 */
	public static RespostasSistema getRespostasSistema() {
		return AplicacaoBlackScreen.respostasSistema;
	}

}
