
insert into cliente (cliente_id, cliente_nome, cliente_email) values (1, 'Maria Oliveira Silva', 'maria@teste.com') 
update cliente_seq set next_val = 2 where next_val=1;
insert into cliente (cliente_id, cliente_nome, cliente_email) values (2, 'Carlos Silva Freitas', 'carlos@teste.com')
update cliente_seq set next_val = 3 where next_val=2;
