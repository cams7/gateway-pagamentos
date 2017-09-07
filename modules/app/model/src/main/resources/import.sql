
insert into cliente (cliente_id, cliente_nome) values (1, 'Maria Oliveira Silva'); 
update cliente_seq set next_val = 2 where next_val=1;
insert into cliente (cliente_id, cliente_nome) values (2, 'Carlos Silva Freitas');
update cliente_seq set next_val = 3 where next_val=2;

-- Field Name 	Mandatory 	Allowed Values 	 Allowed Special Characters
-- Seconds 	    YES 	    0-59 	         , - * /
-- Minutes 	    YES 	    0-59 	         , - * /
-- Hours 	    YES 	    0-23 	         , - * /
-- Day of month YES 	    1-31 	         , - * ? / L W
-- Month 	    YES 	    1-12 or JAN-DEC  , - * /
-- Day of week 	YES 	    1-7 or SUN-SAT 	 , - * ? / L #
-- Year 	    NO 	        empty, 1970-2099 , - * /

-- Segundos, Minutos, Horas, Dia do Mês, Mês, Dia da Semana e Ano (Opcional)
-- Exemplo:
-- 0 0 12 ? * WED A tarefa será executada todas às quartas-feiras às 12:00pm
-- 0 0 8,12 * * * A tarefa será executada todos os dias, às 08:00am e 12:00pm

insert into tarefa (tarefa_id, carrega_cron, processa_cron) values(0, '0/35 * * * * ?', '0/5 * * * * ?');
insert into tarefa (tarefa_id, carrega_cron, processa_cron) values(1, '10 0/1 * * * ?', '0/10 * * * * ?');
insert into tarefa (tarefa_id, carrega_cron, processa_cron) values(2, '20 0/2 * * * ?', '0/20 * * * * ?');
insert into tarefa (tarefa_id, carrega_cron, processa_cron) values(3, '30 0/3 * * * ?', '0/30 * * * * ?');
insert into tarefa (tarefa_id, carrega_cron, processa_cron) values(4, '40 0/4 * * * ?', '0/40 * * * * ?');

--insert into cliente (cliente_id, cliente_nome) values (cliente_seq.nextval, 'Maria Oliveira Silva');
--insert into cliente (cliente_id, cliente_nome) values (cliente_seq.nextval, 'Carlos Silva Freitas');

--insert into tarefa (tarefa_id, carrega_cron, processa_cron) values(0, '0/30 * * * * ?', '0/11 * * * * ?');
--insert into tarefa (tarefa_id, carrega_cron, processa_cron) values(1, '12 0/1 * * * ?', '0/13 * * * * ?');
--insert into tarefa (tarefa_id, carrega_cron, processa_cron) values(2, '24 0/2 * * * ?', '0/25 * * * * ?');
--insert into tarefa (tarefa_id, carrega_cron, processa_cron) values(3, '36 0/3 * * * ?', '0/37 * * * * ?');
--insert into tarefa (tarefa_id, carrega_cron, processa_cron) values(4, '48 0/4 * * * ?', '0/49 * * * * ?');
