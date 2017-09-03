/**
 * 
 */
package br.com.cams7.app.converter;

import javax.faces.convert.EnumConverter;
import javax.faces.convert.FacesConverter;

import br.com.cams7.app.itau.Pagamento.SituacaoPagamento;

/**
 * @author cesaram
 *
 */
@FacesConverter("situacaoPagamentoConverter")
public class SituacaoPagamentoConverter extends EnumConverter {
	public SituacaoPagamentoConverter() {
		super(SituacaoPagamento.class);
	}

}
