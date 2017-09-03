/**
 * 
 */
package br.com.cams7.app.model.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author César Magalhães
 *
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "tarefa")
public class Tarefa implements Serializable {

	@Id
	@Enumerated(EnumType.ORDINAL)
	@Column(name = "tarefa_id")
	private TarefaId id;

	@Column(name = "carrega_cron", length = 50, nullable = false)
	private String carregaCron;

	@Column(name = "processa_cron", length = 50, nullable = false)
	private String processaCron;

	public Tarefa() {
		super();
	}

	public Tarefa(TarefaId id) {
		this();
		this.id = id;
	}

	public TarefaId getId() {
		return id;
	}

	public void setId(TarefaId id) {
		this.id = id;
	}

	public String getCarregaCron() {
		return carregaCron;
	}

	public void setCarregaCron(String carregaCron) {
		this.carregaCron = carregaCron;
	}

	public String getProcessaCron() {
		return processaCron;
	}

	public void setProcessaCron(String processaCron) {
		this.processaCron = processaCron;
	}

	public enum TarefaId {
		PEDIDOS_NAO_VERIFICADOS("pedidos-nao-verificados", "pedidos não verificados"),

		PAGAMENTOS_NAO_ESCOLHIDOS("pagamentos-nao-escolhidos", "pagamentos não escolhidos"),

		PAGAMENTOS_A_VISTA("pagamentos-a-vista", "pagamentos à vista"),

		PAGAMENTOS_CARTOES_CREDITO("pagamentos-cartoes-credito", "pagamentos realizados por cartões de crédito"),

		PAGAMENTOS_BOLETOS("pagamento-boletos", "pagamentos realizados por boletos bancário");

		private String codigo;
		private String descricao;

		private TarefaId(String codigo, String descricao) {
			this.codigo = codigo;
			this.descricao = descricao;
		}

		public String getCodigo() {
			return codigo;
		}

		public String getDescricao() {
			return descricao;
		}

		public static TarefaId getTarefa(String codigo) {
			if (codigo == null)
				return null;

			for (TarefaId rotina : values())
				if (rotina.getCodigo().equals(codigo))
					return rotina;

			return null;
		}

	}

}
