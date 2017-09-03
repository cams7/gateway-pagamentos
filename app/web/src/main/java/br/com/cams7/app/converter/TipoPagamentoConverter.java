/**
 * 
 */
package br.com.cams7.app.converter;

import javax.faces.convert.EnumConverter;
import javax.faces.convert.FacesConverter;

import br.com.cams7.app.itau.Pagamento.TipoPagamento;

/**
 * @author cesaram
 *
 */
@FacesConverter("tipoPagamentoConverter")
public class TipoPagamentoConverter extends EnumConverter {
	public TipoPagamentoConverter() {
		super(TipoPagamento.class);
	}

}
