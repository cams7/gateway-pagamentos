package br.com.cams7.app.itau;

public class ItauParametro {
	private String codEmp;
	private String pedido;
	private String valor;
	private String tipPag;
	private String sitPag;
	private String dtPag;
	private String codAut;
	private String numId;
	private String compVend;
	private String tipCard;

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "[codEmp = " + codEmp + ", pedido = " + pedido + ", valor = " + valor
				+ ", tipPag = " + tipPag + ", sitPag = " + sitPag + ", dtPag = " + dtPag + ", codAut = " + codAut
				+ ", numId = " + numId + ", compVend = " + compVend + ", tipCard = " + tipCard + "]";
	}

	public String getCodEmp() {
		return codEmp;
	}

	public void setCodEmp(String codEmp) {
		this.codEmp = codEmp;
	}

	public String getPedido() {
		return pedido;
	}

	public void setPedido(String pedido) {
		this.pedido = pedido;
	}

	public String getValor() {
		return valor;
	}

	public void setValor(String valor) {
		this.valor = valor;
	}

	public String getTipPag() {
		return tipPag;
	}

	public void setTipPag(String tipPag) {
		this.tipPag = tipPag;
	}

	public String getSitPag() {
		return sitPag;
	}

	public void setSitPag(String sitPag) {
		this.sitPag = sitPag;
	}

	public String getDtPag() {
		return dtPag;
	}

	public void setDtPag(String dtPag) {
		this.dtPag = dtPag;
	}

	public String getCodAut() {
		return codAut;
	}

	public void setCodAut(String codAut) {
		this.codAut = codAut;
	}

	public String getNumId() {
		return numId;
	}

	public void setNumId(String numId) {
		this.numId = numId;
	}

	public String getCompVend() {
		return compVend;
	}

	public void setCompVend(String compVend) {
		this.compVend = compVend;
	}

	public String getTipCard() {
		return tipCard;
	}

	public void setTipCard(String tipCard) {
		this.tipCard = tipCard;
	}

}
