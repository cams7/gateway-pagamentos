
insert into cliente (cliente_id, cliente_nome) values (1, 'Maria Oliveira Silva') 
update cliente_seq set next_val = 2 where next_val=1;
insert into cliente (cliente_id, cliente_nome) values (2, 'Carlos Silva Freitas')
update cliente_seq set next_val = 3 where next_val=2;
--insert into cliente (cliente_id, cliente_nome) values (cliente_seq.nextval, 'Maria Oliveira Silva') 
--insert into cliente (cliente_id, cliente_nome) values (cliente_seq.nextval, 'Carlos Silva Freitas')
