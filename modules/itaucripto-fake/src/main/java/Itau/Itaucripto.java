/**
 * 
 */
package Itau;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * @author César Magalhães
 *
 */
@SuppressWarnings("restriction")
public final class Itaucripto {

	private final String SEPARADOR = ";";
	private final String ALGORITHM = "PBEWithMD5AndDES";
	private final byte[] SALT = { (byte) 0xde, (byte) 0x33, (byte) 0x10, (byte) 0x12, (byte) 0xde, (byte) 0x33,
			(byte) 0x10, (byte) 0x12, };

	public String numbers;

	private Shopline shopline;

	/**
	 * @param codigoEmpresa:
	 *            Código da Empresa (Código do Site) - Alfanumérico com exatas 26
	 *            posições (enviar o código todo em maiúsculas)
	 * @param numeroPedido:
	 *            Número do Pedido - Numérico com o máximo de 08 posições (99999999)
	 *            ( * )
	 * @param valorPagamento:
	 *            Valor Total do Pagamento - Números inteiros de até 8 posições
	 *            (99999999,99). Os centavos deverão ser enviados com 2 casas
	 *            decimais (não obrigatório), utilizando a vírgula como separador
	 * @param observacao:
	 *            Espaço disponível para enviar uma linha de mensagem única por
	 *            pedido ou um parâmetro indicando qual Mensagem Adicional se quer
	 *            apresentar - Alfanumérico com o máximo de 40 posições
	 * 
	 *            Enviar: "uma linha de mensagem" ou "1" para apresentar a "Mensagem
	 *            Adicional 1" ou "2" para apresentar a "Mensagem Adicional 2" ou
	 *            "3" para apresentar as "3 linhas de Mensagem Adicional" enviadas
	 *            nos campos "ObsAdicional1", "ObsAdicional2" e "ObsAdicional3"
	 * @param chaveCriptografia:
	 *            Chave de Criptografia - Alfanumérico com exatas 16 posições
	 *            (enviar a chave toda em maiúsculas)
	 * @param nomeSacado:
	 *            Nome do Sacado - Alfanumérico com o máximo de 30 posições
	 * @param codigoInscricao:
	 *            Código de inscrição do sacado - Numérico com exatamente 02
	 *            posições (01 para CPF, 02 para CNPJ)
	 * @param numeroInscricao:
	 *            Número de inscrição do sacado - Numérico com 14 posições
	 *            (99999999999999)
	 * @param enderecoSacado:
	 *            Endereço do sacado - Alfanumérico com o máximo de 40 posições
	 * @param bairroSacado:
	 *            Bairro do sacado - Alfanumérico com o máximo de 15 posições
	 * @param cepSacado:
	 *            CEP do sacado - Numérico com exatamente 08 posições (99999999)
	 * @param cidadeSacado:
	 *            Cidade do sacado - Alfanumérico com o máximo de 15 posições
	 * @param estadoSacado:
	 *            Estado do sacado - Alfanumérico com exatamente 02 posições.
	 *            Somente serão aceitos Estados brasileiros
	 * @param dataVencimento:
	 *            Data de vencimento do título - Numérico com exatamente 08 posições
	 *            Utilizar o formato "ddmmaaaa"
	 * @param urlRetorna:
	 *            Parte final da URL de Retorno Completa - Alfanumérico com o máximo
	 *            de 60 posições
	 * @param obsAdicional1:
	 *            Espaço disponível para enviar uma linha de mensagem única por
	 *            pedido, que só será exibida se o campo Observação contiver o texto
	 *            "3" - Alfanumérico com o máximo de 60 posições
	 * @param obsAdicional2:
	 *            Espaço disponível para enviar uma linha de mensagem única por
	 *            pedido, que só será exibida se o campo Observação contiver o texto
	 *            "3" - Alfanumérico com o máximo de 60 posições
	 * @param obsAdicional3:
	 *            Espaço disponível para enviar uma linha de mensagem única por
	 *            pedido, que só será exibida se o campo Observação contiver o texto
	 *            "3" - Alfanumérico com o máximo de 60 posições
	 * @return Dados criptografados
	 */
	public String geraDados(final String codigoEmpresa, final String numeroPedido, final String valorPagamento,
			final String observacao, final String chaveCriptografia, final String nomeSacado,
			final String codigoInscricao, final String numeroInscricao, final String enderecoSacado,
			final String bairroSacado, final String cepSacado, final String cidadeSacado, final String estadoSacado,
			final String dataVencimento, final String urlRetorna, final String obsAdicional1,
			final String obsAdicional2, final String obsAdicional3) {

		final String DADOS = getValue(codigoEmpresa) + SEPARADOR + getValue(numeroPedido) + SEPARADOR
				+ getValue(valorPagamento) + SEPARADOR + getValue(dataVencimento) + SEPARADOR + getValue(urlRetorna)
				+ SEPARADOR + getValue(codigoInscricao) + SEPARADOR + getValue(numeroInscricao) + SEPARADOR
				+ getValue(nomeSacado) + SEPARADOR + getValue(enderecoSacado) + SEPARADOR + getValue(bairroSacado)
				+ SEPARADOR + getValue(cepSacado) + SEPARADOR + getValue(cidadeSacado) + SEPARADOR
				+ getValue(estadoSacado) + SEPARADOR + getValue(observacao) + SEPARADOR + getValue(obsAdicional1)
				+ SEPARADOR + getValue(obsAdicional2) + SEPARADOR + getValue(obsAdicional3);

		return criptografa(chaveCriptografia, DADOS);
	}

	/**
	 * @param string1
	 * @param string2
	 * @param string3
	 * @return
	 */
	public String geraDadosGenerico(final String string1, final String string2, final String string3) {
		return null;
	}

	/**
	 * @param codigoEmpresa:
	 *            Código da Empresa (Código do Site) - Alfanumérico com exatas 26
	 *            posições (enviar o código todo em maiúsculas)
	 * @param numeroPedido:
	 *            Número do Pedido - Numérico com o máximo de 08 posições (99999999)
	 *            ( * )
	 * @param formatoConsulta:
	 *            Formato do retorno da consulta - Numérico com 01 posição (0 para
	 *            formato de página HTML para consulta visual, 1 para formato XML)
	 * @param chaveCriptografia:
	 *            Chave de Criptografia - Alfanumérico com exatas 16 posições
	 *            (enviar a chave toda em maiúsculas)
	 * @return Dados criptografados
	 */
	public String geraConsulta(final String codigoEmpresa, final String numeroPedido, final String formatoConsulta,
			final String chaveCriptografia) {
		final String DADOS = getValue(codigoEmpresa) + SEPARADOR + getValue(numeroPedido) + SEPARADOR
				+ getValue(formatoConsulta);

		return criptografa(chaveCriptografia, DADOS);
	}

	/**
	 * @param string1
	 * @param string2
	 * @param string3
	 * @return
	 */
	public String geraCripto(final String string1, final String string2, final String string3) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @param dadosCriptografados:
	 *            Dados criptografados
	 * @param chaveCriptografia:
	 *            Chave de Criptografia - Alfanumérico com exatas 16 posições
	 *            (enviar a chave toda em maiúsculas)
	 * @return Dados descriptografados
	 */
	public String decripto(final String dadosCriptografados, final String chaveCriptografia) {
		final String DADOS_DESCRIPTOGRAFADOS = descriptografa(chaveCriptografia, dadosCriptografados);

		final String[] DADOS = DADOS_DESCRIPTOGRAFADOS.split(SEPARADOR);

		switch (DADOS.length) {
		case 3:
			shopline = new Shopline(DADOS[0], DADOS[1], DADOS[2]);
			break;
		case 17:
			shopline = new Shopline(DADOS[0], DADOS[1], DADOS[2], DADOS[3], DADOS[4], DADOS[5], DADOS[6], DADOS[7],
					DADOS[8], DADOS[9], DADOS[10], DADOS[11], DADOS[12], DADOS[13], DADOS[14], DADOS[15], DADOS[16]);
			break;
		default:
			throw new ShoplineException("Os dados descriptografados estão incorretos");
		}

		return DADOS_DESCRIPTOGRAFADOS;
	}

	/**
	 * @return Código da Empresa (Código do Site) - Alfanumérico com exatas 26
	 *         posições
	 */
	public String retornaCodEmp() {
		return shopline.getCodigoEmpresa();
	}

	/**
	 * @return Tipo de pagamento escolhido pelo comprador - Numérico com 02
	 *         posições:
	 * 
	 *         00 para pagamento ainda não escolhido
	 * 
	 *         01 para pagamento à vista (TEF e CDC)
	 * 
	 *         02 para boleto
	 * 
	 *         03 para cartão de crédito
	 */
	public String retornaTipPag() {
		return null;
	}

	/**
	 * @return Número do Pedido - Numérico com o máximo de 08 posições (99999999)
	 */
	public String retornaPedido() {
		return shopline.getNumeroPedido();
	}

	private String getValue(String value) {
		if (value == null || value.isEmpty())
			return "";

		return value.trim();
	}

	/**
	 * Criptografa os dados
	 * 
	 * @param chaveCriptografia
	 *            Chave de criptografia
	 * @param dados
	 *            Dados que seram criptografados
	 * @return Dados criptografados
	 */
	private String criptografa(String chaveCriptografia, String dados) {
		try {
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM);
			SecretKey key = keyFactory.generateSecret(new PBEKeySpec(chaveCriptografia.toCharArray()));
			Cipher pbeCipher = Cipher.getInstance(ALGORITHM);
			pbeCipher.init(Cipher.ENCRYPT_MODE, key, new PBEParameterSpec(SALT, 20));
			return base64Encode(pbeCipher.doFinal(dados.getBytes("UTF-8")));
		} catch (GeneralSecurityException | UnsupportedEncodingException e) {
			throw new ShoplineException(e);
		}

	}

	/**
	 * @param bytes
	 * @return
	 */
	private String base64Encode(byte[] bytes) {
		return new BASE64Encoder().encode(bytes);
	}

	/**
	 * @param chaveCriptografia
	 *            Chave de criptografia
	 * @param dadosCriptografados
	 *            Dados que seram secriptografados
	 * @return Dados descriptografados
	 */
	private String descriptografa(String chaveCriptografia, String dadosCriptografados) {
		try {
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM);
			SecretKey key = keyFactory.generateSecret(new PBEKeySpec(chaveCriptografia.toCharArray()));
			Cipher pbeCipher = Cipher.getInstance(ALGORITHM);
			pbeCipher.init(Cipher.DECRYPT_MODE, key, new PBEParameterSpec(SALT, 20));
			return new String(pbeCipher.doFinal(base64Decode(dadosCriptografados)), "UTF-8");
		} catch (GeneralSecurityException | IOException e) {
			throw new ShoplineException(e);
		}
	}

	/**
	 * @param property
	 * @return
	 * @throws IOException
	 */
	private byte[] base64Decode(String property) throws IOException {
		return new BASE64Decoder().decodeBuffer(property);
	}

	public Shopline getShopline() {
		return shopline;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		final String CHAVE_CRIPTOGRAFIA = "A1B2C3D4E5F6G7H8";

		final String CODIGO_EMPRESA = "A1B2C3D4E5F6G7H8I9J0L1K2M3";
		final String NUMERO_PEDIDO = "99999999";
		final String VALOR_PAGAMENTO = "99999999,99";
		final String DATA_VENCIMENTO = "11092017";

		final String URL_RETORNA = "https://www.minhaloja.com.br/final/retorno.aspx";

		final String CODIGO_INSCRICAO = "01";
		final String NUMERO_INSCRICAO = "88378658058";

		final String NOME_SACADO = "Arthur Benjamin Cavalcanti";

		final String ENDERECO_SACADO = "Rua Monsenhor Nicodemus dos Santos";
		final String BAIRRO_SACADO = "Piratininga";
		final String CEP_SACADO = "99999999";
		final String CIDADE_SACADO = "Niterói";
		final String ESTADO_SACADO = "RJ";

		final String OBSERVACAO = "3";
		final String OBS_ADICIONAL1 = "Essa é minha primeira observação adicional";
		final String OBS_ADICIONAL2 = "Essa é minha segunda observação adicional";
		final String OBS_ADICIONAL3 = "Essa é minha terceira observação adicional";

		final String FORMATO_CONSULTA = "1";

		Itaucripto itaucripto = new Itaucripto();
		String dadosCriptografados = itaucripto.geraDados(CODIGO_EMPRESA, NUMERO_PEDIDO, VALOR_PAGAMENTO, OBSERVACAO,
				CHAVE_CRIPTOGRAFIA, NOME_SACADO, CODIGO_INSCRICAO, NUMERO_INSCRICAO, ENDERECO_SACADO, BAIRRO_SACADO,
				CEP_SACADO, CIDADE_SACADO, ESTADO_SACADO, DATA_VENCIMENTO, URL_RETORNA, OBS_ADICIONAL1, OBS_ADICIONAL2,
				OBS_ADICIONAL3);

		System.out.println(String.format("Dados criptografados: %s", dadosCriptografados));

		itaucripto = new Itaucripto();
		String dados = itaucripto.decripto(dadosCriptografados, CHAVE_CRIPTOGRAFIA);

		System.out.println(String.format("Dados descriptografados: %s", dados));
		System.out.println(String.format("Dados: %s", itaucripto.getShopline()));

		itaucripto = new Itaucripto();
		dadosCriptografados = itaucripto.geraConsulta(CODIGO_EMPRESA, NUMERO_PEDIDO, FORMATO_CONSULTA,
				CHAVE_CRIPTOGRAFIA);

		System.out.println(String.format("Dados criptografados: %s", dadosCriptografados));

		itaucripto = new Itaucripto();
		dados = itaucripto.decripto(dadosCriptografados, CHAVE_CRIPTOGRAFIA);

		System.out.println(String.format("Dados descriptografados: %s", dados));
		System.out.println(String.format("Dados: %s", itaucripto.getShopline()));

		System.out.println(String.format("Código da empresa: %s", itaucripto.retornaCodEmp()));
		System.out.println(String.format("Número do pedido: %s", itaucripto.retornaPedido()));
		System.out.println(String.format("Tipo de pagamento: %s", itaucripto.retornaTipPag()));

	}

}
