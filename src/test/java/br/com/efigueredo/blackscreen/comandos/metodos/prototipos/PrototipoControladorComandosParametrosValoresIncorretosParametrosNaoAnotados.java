package br.com.efigueredo.blackscreen.comandos.metodos.prototipos;

import java.util.List;

import br.com.efigueredo.blackscreen.anotacoes.Comando;
import br.com.efigueredo.blackscreen.anotacoes.Parametro;

public class PrototipoControladorComandosParametrosValoresIncorretosParametrosNaoAnotados {

	@Comando(nome = "adicionar")
	public void metodo4(@Parametro("--param1") List<String> args1, @Parametro("--param2") List<String> args2,
			List<String> args3) {

	}

}