--DML - Data Manipulation Language (Linguagem de Manipulação de Dados)

select p.pedido_id, p.cliente_id, p.data_pagamento, p.data_pedido, p.forma_pagamento, p.situacao_pagamento, p.valor_pago, p.valor_pedido from pedido p 
where p.pedido_id=(select min(p2.pedido_id) from pedido p2 where (p2.forma_pagamento is null) and (p2.situacao_pagamento is null));

select p.pedido_id, p.cliente_id, p.data_pagamento, p.data_pedido, p.forma_pagamento, p.situacao_pagamento, p.valor_pago, p.valor_pedido from pedido p 
where p.pedido_id=(select min(p2.pedido_id) from pedido p2 where p2.forma_pagamento=1 and (p2.situacao_pagamento=1 or p2.situacao_pagamento=2));


select distinct p.pedido_id from pedido p where (p.forma_pagamento is null) and (p.situacao_pagamento is null) order by p.pedido_id asc;