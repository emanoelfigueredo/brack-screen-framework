package br.com.efigueredo.blackscreen.userinput.verificacao;

import br.com.efigueredo.blackscreen.userinput.exception.ExpressaoInvalidaException;
import br.com.efigueredo.blackscreen.userinput.expressao.ExpressaoUsuario;

/**
 * <h4>A classe abstrata {@code VerificacaoExpressaoUsuario} é uma interface
 * para às verificações necessárias para o prosseguimento da expressão do
 * usuário no sistema.</h4>
 * 
 * Seu design permite que seja aplicado o design pattern Chain of Resposability
 * na classe reponsável por sua instância.
 * 
 * @author Emanoel
 * @since 1.0.0
 */
public abstract class VerificacaoExpressaoUsuario {

	/** A implementação da próxima verificação, caso a atual não seja aplicada. */
	private VerificacaoExpressaoUsuario proximo;

	/**
	 * Construtor.
	 *
	 * @param proximo Objeto do tipo {@linkplain VerificacaoExpressaoUsuario}, a
	 *                implementação da próxima verificação.
	 */
	public VerificacaoExpressaoUsuario(VerificacaoExpressaoUsuario proximo) {
		this.proximo = proximo;
	}

	/**
	 * Método abstrato que especifica a expressão que verifica se a característica
	 * da expressão inserida pelo usuário foi atendida.
	 *
	 * @param entrada Objeto do tipo {@linkplain EntradaUsuario} para forncecer os
	 *                recursos necessários para a verificação.
	 * @return <b>true</b>, se a característica <em>não foi atentida</em> na
	 *         expressão.<br>
	 *         <b>false</b>, se a característica <em>foi atentida</em> na expressão.
	 */
	protected abstract boolean verificacao(ExpressaoUsuario expressao);

	/**
	 * Método abstrato responsável por escpecificar que uma exceção do tipo
	 * {@linkplain EntradaUsuarioInvalidaException} que deve ser atribuida no corpo
	 * do método pela sua implementação.
	 * 
	 * @throws EntradaUsuarioInvalidaException Exceção do tipo
	 *                                         {@linkplain EntradaUsuarioInvalidaException}
	 *                                         que será lançada caso o método
	 *                                         {@code verificacao} retorne o valor
	 *                                         true.
	 */
	protected abstract void jogarExcecao() throws ExpressaoInvalidaException;

	/**
	 * Método responsável por usar dos métodos especificados {@code verificacao}
	 * quando os mesmos forem implementados e {@code jogarExcecao} utilizando o
	 * design pattern Template Method.
	 *
	 * @param entrada Objeto do tipo {@linkplain EntradaUsuario}
	 * @throws EntradaUsuarioInvalidaException Exceção do tipo
	 *                                         {@linkplain EntradaUsuarioInvalidaException}
	 *                                         que será lançada caso o método
	 *                                         {@code verificacao} retorne o valor
	 *                                         true.
	 */
	public void verificar(ExpressaoUsuario expressao) throws ExpressaoInvalidaException {
		if (this.verificacao(expressao)) {
			this.jogarExcecao();
			return;
		}
		this.proximo.verificar(expressao);
	}

}