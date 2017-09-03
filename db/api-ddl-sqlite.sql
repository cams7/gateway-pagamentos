--DDL - Data Definition Language (Linguagem de Definição de Dados)

DROP TABLE IF EXISTS PEDIDO;

CREATE TABLE PEDIDO(
	COD_PEDIDO VARCHAR(15) NOT NULL, --N�mero do pedido
	COD_EMP VARCHAR(30) NOT NULL, --C�digo da empresa	
	VALOR NUMERIC(13,4) NOT NULL, --Valor do pedido
	TIP_PAG CHAR(2) NOT NULL, --Tipo de pagamento
	SIT_PAG CHAR(2) NOT NULL, --Situa��o do pagamento
	DT_PAG CHAR(8) NOT NULL, --Data do pagamento
	COD_AUT CHAR(10) NULL,
	NUM_ID CHAR(10) NULL,
	COMP_VEND CHAR(10) NULL,
	TIP_CARD CHAR(10) NULL,
	PRIMARY KEY (COD_PEDIDO)	
);

CREATE INDEX IDX_PEDIDO ON PEDIDO(COD_PEDIDO);