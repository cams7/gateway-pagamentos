/**
 * 
 */
package Itau;

/**
 * @author César Magalhães
 *
 */
public class Shopline {

	private String codigoEmpresa;
	private String numeroPedido;
	private String valorPagamento;
	private String dataVencimento;

	private String urlRetorna;

	private String codigoInscricao;
	private String numeroInscricao;

	private String nomeSacado;
	private String enderecoSacado;
	private String bairroSacado;
	private String cepSacado;
	private String cidadeSacado;
	private String estadoSacado;

	private String observacao;
	private String obsAdicional1;
	private String obsAdicional2;
	private String obsAdicional3;

	private String formatoConsulta;

	/**
	 * 
	 */
	public Shopline() {
		super();
	}

	/**
	 * @param codigoEmpresa
	 * @param numeroPedido
	 */
	public Shopline(String codigoEmpresa, String numeroPedido, String formatoConsulta) {
		this();

		this.codigoEmpresa = ShoplineValida.validaCodigoEmpresa(codigoEmpresa);
		this.numeroPedido = ShoplineValida.validaNumeroPedido(numeroPedido);
		if (formatoConsulta != null)
			this.formatoConsulta = ShoplineValida.validaFormatoConsulta(formatoConsulta);
	}

	/**
	 * @param codigoEmpresa
	 * @param numeroPedido
	 * @param valorPagamento
	 * @param dataVencimento
	 * @param urlRetorna
	 * @param codigoInscricao
	 * @param numeroInscricao
	 * @param nomeSacado
	 * @param enderecoSacado
	 * @param bairroSacado
	 * @param cepSacado
	 * @param cidadeSacado
	 * @param estadoSacado
	 * @param observacao
	 * @param obsAdicional1
	 * @param obsAdicional2
	 * @param obsAdicional3
	 */
	public Shopline(String codigoEmpresa, String numeroPedido, String valorPagamento, String dataVencimento,
			String urlRetorna, String codigoInscricao, String numeroInscricao, String nomeSacado, String enderecoSacado,
			String bairroSacado, String cepSacado, String cidadeSacado, String estadoSacado, String observacao,
			String obsAdicional1, String obsAdicional2, String obsAdicional3) {

		this(codigoEmpresa, numeroPedido, null);

		this.valorPagamento = ShoplineValida.validaValorPagamento(valorPagamento);
		this.dataVencimento = ShoplineValida.validaDataVencimento(dataVencimento);
		this.urlRetorna = ShoplineValida.validaUrlRetorna(urlRetorna);
		this.codigoInscricao = ShoplineValida.validaCodigoInscricao(codigoInscricao);
		this.numeroInscricao = ShoplineValida.validaNumeroInscricao(codigoInscricao, numeroInscricao);
		this.nomeSacado = ShoplineValida.validaNomeSacado(nomeSacado);
		this.enderecoSacado = ShoplineValida.validaEnderecoSacado(enderecoSacado);
		this.bairroSacado = ShoplineValida.validaBairroSacado(bairroSacado);
		this.cepSacado = ShoplineValida.validaCepSacado(cepSacado);
		this.cidadeSacado = ShoplineValida.validaCidadeSacado(cidadeSacado);
		this.estadoSacado = ShoplineValida.validaEstadoSacado(estadoSacado);
		this.observacao = ShoplineValida.validaObservacao(observacao);
		this.obsAdicional1 = ShoplineValida.validaObsAdicional1(observacao, obsAdicional1);
		this.obsAdicional2 = ShoplineValida.validaObsAdicional2(observacao, obsAdicional2);
		this.obsAdicional3 = ShoplineValida.validaObsAdicional3(observacao, obsAdicional3);
	}

	@Override
	public String toString() {
		return String.format(
				"%s {codigoEmpresa: %s, numeroPedido: %s, valorPagamento: %s, dataVencimento: %s, urlRetorna: %s, codigoInscricao: %s, numeroInscricao: %s, nomeSacado: %s, enderecoSacado: %s, bairroSacado: %s, cepSacado: %s, cidadeSacado: %s, estadoSacado: %s, observacao: %s, obsAdicional1: %s, obsAdicional2: %s, obsAdicional3: %s, formatoConsulta: %s}",
				this.getClass().getSimpleName(), codigoEmpresa, numeroPedido, valorPagamento, dataVencimento,
				urlRetorna, codigoInscricao, numeroInscricao, nomeSacado, enderecoSacado, bairroSacado, cepSacado,
				cidadeSacado, estadoSacado, observacao, obsAdicional1, obsAdicional2, obsAdicional3, formatoConsulta);

	}

	public String getCodigoEmpresa() {
		return codigoEmpresa;
	}

	public String getNumeroPedido() {
		return numeroPedido;
	}

	public String getValorPagamento() {
		return valorPagamento;
	}

	public String getDataVencimento() {
		return dataVencimento;
	}

	public String getUrlRetorna() {
		return urlRetorna;
	}

	public String getCodigoInscricao() {
		return codigoInscricao;
	}

	public String getNumeroInscricao() {
		return numeroInscricao;
	}

	public String getNomeSacado() {
		return nomeSacado;
	}

	public String getEnderecoSacado() {
		return enderecoSacado;
	}

	public String getBairroSacado() {
		return bairroSacado;
	}

	public String getCepSacado() {
		return cepSacado;
	}

	public String getCidadeSacado() {
		return cidadeSacado;
	}

	public String getEstadoSacado() {
		return estadoSacado;
	}

	public String getObservacao() {
		return observacao;
	}

	public String getObsAdicional1() {
		return obsAdicional1;
	}

	public String getObsAdicional2() {
		return obsAdicional2;
	}

	public String getObsAdicional3() {
		return obsAdicional3;
	}

	public String getFormatoConsulta() {
		return formatoConsulta;
	}

}
