/**
 * 
 */
package Itau;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import br.com.cams7.utils.Cep;
import br.com.cams7.utils.Cnpj;
import br.com.cams7.utils.Cpf;

/**
 * @author César Magalhães
 *
 */
public class ShoplineValida {

	/**
	 * @param value
	 * @return
	 */
	private static String validaNotEmpty(String value) {
		if (value == null || value.isEmpty())
			throw new ShoplineException("O valor informado não pode ser nulo ou vazio");

		return value;
	}

	/**
	 * @param codigoEmpresa:
	 *            Código da Empresa (Código do Site) - Alfanumérico com exatas 26
	 *            posições (enviar o código todo em maiúsculas)
	 * @return codigoEmpresa
	 */
	public static String validaCodigoEmpresa(String codigoEmpresa) {
		codigoEmpresa = validaNotEmpty(codigoEmpresa);

		if (!codigoEmpresa.matches("^[A-Z0-9]{26}$"))
			throw new ShoplineException("O \"código da empresa\" tem que ter 26 caracteres alfanuméricos e maiusculo");

		return codigoEmpresa;
	}

	/**
	 * @param numeroPedido:
	 *            Número do Pedido - Numérico com o máximo de 08 posições (99999999)
	 *            ( * )
	 * @return numeroPedido
	 */
	public static String validaNumeroPedido(String numeroPedido) {
		numeroPedido = validaNotEmpty(numeroPedido);

		if (!numeroPedido.matches("^[0-9]{1,8}$"))
			throw new ShoplineException("O \"número do pedido\" tem que ser inteiros de até 8 posições");

		return numeroPedido;
	}

	/**
	 * @param valorPagamento:
	 *            Valor Total do Pagamento - Números inteiros de até 8 posições
	 *            (99999999,99). Os centavos deverão ser enviados com 2 casas
	 *            decimais (não obrigatório), utilizando a vírgula como separador
	 * @return valorPagamento
	 */
	public static String validaValorPagamento(String valorPagamento) {
		valorPagamento = validaNotEmpty(valorPagamento);

		if (!valorPagamento.matches("^[0-9]{1,8}(\\,([0-9]{1,2}))?$"))
			throw new ShoplineException(
					"O \"valor do pagamento\" tem que ser inteiros de até 8 posições ou ter 2 casas decimais (separados por virgulas)");

		return valorPagamento;
	}

	/**
	 * @param chaveCriptografia:
	 *            Chave de Criptografia - Alfanumérico com exatas 16 posições
	 *            (enviar a chave toda em maiúsculas)
	 * @return chaveCriptografia
	 */
	public static String validaChaveCriptografia(String chaveCriptografia) {
		chaveCriptografia = validaNotEmpty(chaveCriptografia);

		if (!chaveCriptografia.matches("^[A-Z0-9]{16}$"))
			throw new ShoplineException(
					"A \"chave de criptografia\" tem que ter 16 caracteres alfanuméricos e maiusculo");

		return chaveCriptografia;
	}

	/**
	 * @param nomeSacado:
	 *            Nome do Sacado - Alfanumérico com o máximo de 30 posições
	 * @return nomeSacado
	 */
	public static String validaNomeSacado(String nomeSacado) {
		nomeSacado = validaNotEmpty(nomeSacado);

		if (nomeSacado.length() > 30)
			throw new ShoplineException("O \"nome sacado\" não pode ter mais de 30 caracteres");

		return nomeSacado;
	}

	/**
	 * @param codigoInscricao:
	 *            Código de inscrição do sacado - Numérico com exatamente 02
	 *            posições (01 para CPF, 02 para CNPJ)
	 * @return codigoInscricao
	 */
	public static String validaCodigoInscricao(String codigoInscricao) {
		codigoInscricao = validaNotEmpty(codigoInscricao);

		if (!Arrays.asList("01", "02").contains(codigoInscricao))
			throw new ShoplineException("O \"código de incrição\" tem que ser 01 ou 02");

		return codigoInscricao;
	}

	/**
	 * @param codigoInscricao:
	 *            Código de inscrição do sacado - Numérico com exatamente 02
	 *            posições (01 para CPF, 02 para CNPJ)
	 * @param numeroInscricao:
	 *            Número de inscrição do sacado - Numérico com 14 posições
	 *            (99999999999999)
	 * @return numeroInscricao
	 */
	public static String validaNumeroInscricao(String codigoInscricao, String numeroInscricao) {
		codigoInscricao = validaCodigoInscricao(codigoInscricao);

		numeroInscricao = validaNotEmpty(numeroInscricao);

		if ("01".equals(codigoInscricao) && !Cpf.isValido(numeroInscricao))
			throw new ShoplineException("O \"número de incrição\" tem que ser um CPF válido");

		if ("02".equals(codigoInscricao) && !Cnpj.isValido(numeroInscricao))
			throw new ShoplineException("O \"número de incrição\" tem que ser um CNPJ válido");

		return numeroInscricao;
	}

	/**
	 * @param enderecoSacado:
	 *            Endereço do sacado - Alfanumérico com o máximo de 40 posições
	 * @return enderecoSacado
	 */
	public static String validaEnderecoSacado(String enderecoSacado) {
		enderecoSacado = validaNotEmpty(enderecoSacado);

		if (enderecoSacado.length() > 40)
			throw new ShoplineException("O \"endereço sacado\" não pode ter mais de 40 caracteres");

		return enderecoSacado;
	}

	/**
	 * @param bairroSacado:
	 *            Bairro do sacado - Alfanumérico com o máximo de 15 posições
	 * @return bairroSacado
	 */
	public static String validaBairroSacado(String bairroSacado) {
		bairroSacado = validaNotEmpty(bairroSacado);

		if (bairroSacado.length() > 15)
			throw new ShoplineException("O \"bairro sacado\" não pode ter mais de 15 caracteres");

		return bairroSacado;
	}

	/**
	 * @param cepSacado:
	 *            CEP do sacado - Numérico com exatamente 08 posições (99999999)
	 * @return cepSacado
	 */
	public static String validaCepSacado(String cepSacado) {
		cepSacado = validaNotEmpty(cepSacado);

		if (!Cep.isValido(cepSacado))
			throw new ShoplineException("O \"cep sacado\" não é válido");

		return cepSacado;
	}

	/**
	 * @param cidadeSacado:
	 *            Cidade do sacado - Alfanumérico com o máximo de 15 posições
	 * @return cidadeSacado
	 */
	public static String validaCidadeSacado(String cidadeSacado) {
		cidadeSacado = validaNotEmpty(cidadeSacado);

		if (cidadeSacado.length() > 15)
			throw new ShoplineException("A \"cidade sacado\" não pode ter mais de 15 caracteres");

		return cidadeSacado;
	}

	/**
	 * @param estadoSacado:
	 *            Estado do sacado - Alfanumérico com exatamente 02 posições.
	 *            Somente serão aceitos Estados brasileiros
	 * @return estadoSacado
	 */
	public static String validaEstadoSacado(String estadoSacado) {
		estadoSacado = validaNotEmpty(estadoSacado);

		if (!Arrays
				.asList("AC", "AL", "AP", "AM", "BA", "CE", "DF", "ES", "GO", "MA", "MT", "MS", "MG", "PA", "PB", "PR",
						"PE", "PI", "RJ", "RN", "RS", "RO", "RR", "SC", "SP", "SE", "TO")
				.contains(estadoSacado.toUpperCase()))
			throw new ShoplineException("O \"estado sacado\" não é válido");

		return estadoSacado;
	}

	/**
	 * @param dataVencimento:
	 *            Data de vencimento do título - Numérico com exatamente 08 posições
	 *            Utilizar o formato "ddmmaaaa"
	 * @return dataVencimento
	 */
	public static String validaDataVencimento(String dataVencimento) {
		dataVencimento = validaNotEmpty(dataVencimento);

		if (!dataVencimento.matches(
				"(^(((0[1-9]|1[0-9]|2[0-8])(0[1-9]|1[012]))|((29|30|31)(0[13578]|1[02]))|((29|30)(0[4,6,9]|11)))(19|[2-9][0-9])\\d\\d$)|(^2902(19|[2-9][0-9])(00|04|08|12|16|20|24|28|32|36|40|44|48|52|56|60|64|68|72|76|80|84|88|92|96)$)"))
			throw new ShoplineException("A \"data de vencimento\" não é válida");

		return dataVencimento;
	}

	/**
	 * @param urlRetorna:
	 *            Parte final da URL de Retorno Completa - Alfanumérico com o máximo
	 *            de 60 posições
	 * @return urlRetorna
	 */
	public static String validaUrlRetorna(String urlRetorna) {
		urlRetorna = validaNotEmpty(urlRetorna);

		if (!urlRetorna.matches(
				"(https?:\\/\\/(?:www\\.|(?!www))[a-zA-Z0-9][a-zA-Z0-9-]+[a-zA-Z0-9]\\.[^\\s]{2,}|www\\.[a-zA-Z0-9][a-zA-Z0-9-]+[a-zA-Z0-9]\\.[^\\s]{2,}|https?:\\/\\/(?:www\\.|(?!www))[a-zA-Z0-9]\\.[^\\s]{2,}|www\\.[a-zA-Z0-9]\\.[^\\s]{2,}|http:\\/\\/\\w+(\\.\\w+)*(:[0-9]+)?\\/?(\\/[.\\w-]*)*)"))
			throw new ShoplineException("A \"url de retorno\" não é válida");

		return urlRetorna;
	}

	/**
	 * @param observacao:
	 *            Espaço disponível para enviar uma linha de mensagem única por
	 *            pedido ou um parâmetro indicando qual Mensagem Adicional se quer
	 *            apresentar - Alfanumérico com o máximo de 40 posições
	 * 
	 *            Enviar: "uma linha de mensagem" ou "1" para apresentar a "Mensagem
	 *            Adicional 1" ou "2" para apresentar a "Mensagem Adicional 2" ou
	 *            "3" para apresentar as "3 linhas de Mensagem Adicional" enviadas
	 *            nos campos "ObsAdicional1", "ObsAdicional2" e "ObsAdicional3"
	 * @return observacao
	 */
	public static String validaObservacao(String observacao) {
		if (observacao != null && observacao.length() > 40)
			throw new ShoplineException("A \"observação\" não pode ter mais de 40 caracteres");

		return observacao;
	}

	/**
	 * @param observacao:
	 *            Observação
	 * @param obsAdicional:
	 *            Observação adicional
	 * @param codigo
	 * @return Observação adicional
	 */
	private static String validaObsAdicional(String observacao, String obsAdicional, String codigo) {
		observacao = validaObservacao(observacao);

		if (observacao == null || observacao.isEmpty() || !Arrays.asList("1", "2", "3").contains(observacao.trim())
				|| Byte.valueOf(observacao.trim()) < Byte.valueOf(codigo))
			return null;

		obsAdicional = validaNotEmpty(obsAdicional);

		if (obsAdicional.length() > 60)
			throw new ShoplineException(
					String.format("A \"observação adicional %s\" não pode ter mais de 60 caracteres", codigo));

		return obsAdicional;
	}

	/**
	 * @param observacao:
	 *            Espaço disponível para enviar uma linha de mensagem única por
	 *            pedido ou um parâmetro indicando qual Mensagem Adicional se quer
	 *            apresentar - Alfanumérico com o máximo de 40 posições
	 * 
	 *            Enviar: "uma linha de mensagem" ou "1" para apresentar a "Mensagem
	 *            Adicional 1" ou "2" para apresentar a "Mensagem Adicional 2" ou
	 *            "3" para apresentar as "3 linhas de Mensagem Adicional" enviadas
	 *            nos campos "ObsAdicional1", "ObsAdicional2" e "ObsAdicional3"
	 * @param obsAdicional1:
	 *            Espaço disponível para enviar uma linha de mensagem única por
	 *            pedido, que só será exibida se o campo Observação contiver o texto
	 *            "3" - Alfanumérico com o máximo de 60 posições
	 * @return obsAdicional1
	 */
	public static String validaObsAdicional1(String observacao, String obsAdicional1) {
		return validaObsAdicional(observacao, obsAdicional1, "1");
	}

	/**
	 * @param observacao:
	 *            Espaço disponível para enviar uma linha de mensagem única por
	 *            pedido ou um parâmetro indicando qual Mensagem Adicional se quer
	 *            apresentar - Alfanumérico com o máximo de 40 posições
	 * 
	 *            Enviar: "uma linha de mensagem" ou "1" para apresentar a "Mensagem
	 *            Adicional 1" ou "2" para apresentar a "Mensagem Adicional 2" ou
	 *            "3" para apresentar as "3 linhas de Mensagem Adicional" enviadas
	 *            nos campos "ObsAdicional1", "ObsAdicional2" e "ObsAdicional3"
	 * @param obsAdicional2:
	 *            Espaço disponível para enviar uma linha de mensagem única por
	 *            pedido, que só será exibida se o campo Observação contiver o texto
	 *            "3" - Alfanumérico com o máximo de 60 posições
	 * @return obsAdicional1
	 */
	public static String validaObsAdicional2(String observacao, String obsAdicional2) {
		return validaObsAdicional(observacao, obsAdicional2, "2");
	}

	/**
	 * @param observacao:
	 *            Espaço disponível para enviar uma linha de mensagem única por
	 *            pedido ou um parâmetro indicando qual Mensagem Adicional se quer
	 *            apresentar - Alfanumérico com o máximo de 40 posições
	 * 
	 *            Enviar: "uma linha de mensagem" ou "1" para apresentar a "Mensagem
	 *            Adicional 1" ou "2" para apresentar a "Mensagem Adicional 2" ou
	 *            "3" para apresentar as "3 linhas de Mensagem Adicional" enviadas
	 *            nos campos "ObsAdicional1", "ObsAdicional2" e "ObsAdicional3"
	 * @param obsAdicional3:
	 *            Espaço disponível para enviar uma linha de mensagem única por
	 *            pedido, que só será exibida se o campo Observação contiver o texto
	 *            "3" - Alfanumérico com o máximo de 60 posições
	 * @return obsAdicional1
	 */
	public static String validaObsAdicional3(String observacao, String obsAdicional3) {
		return validaObsAdicional(observacao, obsAdicional3, "3");
	}

	/**
	 * @param formatoConsulta:
	 *            Formato do retorno da consulta - Numérico com 01 posição (0 para
	 *            formato de página HTML para consulta visual, 1 para formato XML)
	 * @return formatoConsulta
	 */
	public static String validaFormatoConsulta(String formatoConsulta) {
		formatoConsulta = validaNotEmpty(formatoConsulta);

		if (!Arrays.asList("0", "1").contains(formatoConsulta))
			throw new ShoplineException("");

		return formatoConsulta;
	}

	public static void main(String[] args) {
		String codigoEmpresa = validaCodigoEmpresa("A1B2C3D4E5F6G7H8I9J0L1K2M3");
		System.out.println(String.format("Codigo da empresa: %s", codigoEmpresa));

		String numeroPedido = validaNumeroPedido("99999999");
		System.out.println(String.format("Número do pedido: %s", numeroPedido));

		String valorPagamento = validaValorPagamento("99999999,99");
		System.out.println(String.format("Valor do pagamento: %s", valorPagamento));

		String chaveCriptografia = validaChaveCriptografia("A1B2C3D4E5F6G7H8");
		System.out.println(String.format("Chave de criptografia: %s", chaveCriptografia));

		String nomeSacado = validaNomeSacado("Arthur Benjamin Cavalcanti");
		System.out.println(String.format("Nome sacado: %s", nomeSacado));

		String numeroInscricao = validaNumeroInscricao("01", "88378658058");
		System.out.println(String.format("Número de inscrição: %s", Cpf.formata(numeroInscricao)));
		numeroInscricao = validaNumeroInscricao("02", "93124995000183");
		System.out.println(String.format("Número de inscrição: %s", Cnpj.formata(numeroInscricao)));

		String enderecoSacado = validaEnderecoSacado("Rua Monsenhor Nicodemus dos Santos");
		System.out.println(String.format("Endereço sacado: %s", enderecoSacado));

		String bairroSacado = validaBairroSacado("Piratininga");
		System.out.println(String.format("Bairro sacado: %s", bairroSacado));

		String cepSacado = validaCepSacado("99999999");
		System.out.println(String.format("Cep sacado: %s", cepSacado));

		String cidadeSacado = validaCidadeSacado("Niterói");
		System.out.println(String.format("Cidade sacado: %s", cidadeSacado));

		String estadoSacado = validaEstadoSacado("RJ");
		System.out.println(String.format("Estado sacado: %s", estadoSacado));

		String dataVencimento = validaDataVencimento("07092017");
		try {
			final Date DATA_VENCIMENTO = new SimpleDateFormat("ddmmyyyy").parse(dataVencimento);
			dataVencimento = new SimpleDateFormat("dd/mm/yyyy").format(DATA_VENCIMENTO);
			System.out.println(String.format("Data de vencimento: %s", dataVencimento));
		} catch (ParseException e) {
			System.err.println(e);
		}

		String urlRetorna = validaUrlRetorna("https://www.minhaloja.com.br/final/retorno.aspx");
		System.out.println(String.format("Url de retorno: %s", urlRetorna));
		urlRetorna = validaUrlRetorna("http://localhost:8080/gateway-pagamentos/retorno");
		System.out.println(String.format("Url de retorno: %s", urlRetorna));

		String observacao = validaObservacao("Essa é minha observação");
		System.out.println(String.format("Observação: %s", observacao));

		for (String codigo : new String[] { "1", "2", "3" }) {
			String obsAdicional1 = validaObsAdicional1(codigo, "Essa é minha primeira observação adicional");
			System.out.println(String.format("Observação adicional 1: %s", obsAdicional1));

			String obsAdicional2 = validaObsAdicional2(codigo, "Essa é minha segunda observação adicional");
			System.out.println(String.format("Observação adicional 2: %s", obsAdicional2));

			String obsAdicional3 = validaObsAdicional3(codigo, "Essa é minha terceira observação adicional");
			System.out.println(String.format("Observação adicional 3: %s", obsAdicional3));
		}

		String formatoConsulta = validaFormatoConsulta("1");
		System.out.println(String.format("Formato da consulta: %s", formatoConsulta));

	}

}
