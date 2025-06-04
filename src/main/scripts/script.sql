-- Produtos
INSERT INTO item_cardapio (nome, preco, categoria) VALUES ('Batata Frita - M', 15.00, 'LANCHE');
INSERT INTO item_cardapio (nome, preco, categoria) VALUES ('Batata Frita - P', 10.00, 'LANCHE');
INSERT INTO item_cardapio (nome, preco, categoria) VALUES ('Coca-Cola', 8.50, 'BEBIDA');
INSERT INTO item_cardapio (nome, preco, categoria) VALUES ('Suco de Laranja', 5.00, 'BEBIDA');
INSERT INTO item_cardapio (nome, preco, categoria) VALUES ('Hambúrguer Simples', 18.00, 'LANCHE');
INSERT INTO item_cardapio (nome, preco, categoria) VALUES ('Hambúrguer Duplo', 25.00, 'LANCHE');
INSERT INTO item_cardapio (nome, preco, categoria) VALUES ('X-Bacon', 22.00, 'LANCHE');
INSERT INTO item_cardapio (nome, preco, categoria) VALUES ('X-Egg', 20.00, 'LANCHE');
INSERT INTO item_cardapio (nome, preco, categoria) VALUES ('Refrigerante 2L', 12.00, 'BEBIDA');
INSERT INTO item_cardapio (nome, preco, categoria) VALUES ('Água Mineral', 3.50, 'BEBIDA');
INSERT INTO item_cardapio (nome, preco, categoria) VALUES ('Chá Gelado', 6.00, 'BEBIDA');
INSERT INTO item_cardapio (nome, preco, categoria) VALUES ('Milkshake Chocolate', 12.50, 'BEBIDA');
INSERT INTO item_cardapio (nome, preco, categoria) VALUES ('Sorvete 2 bolas', 8.00, 'SOBREMESA');
INSERT INTO item_cardapio (nome, preco, categoria) VALUES ('Petit Gâteau', 14.00, 'SOBREMESA');
INSERT INTO item_cardapio (nome, preco, categoria) VALUES ('Brownie', 9.00, 'SOBREMESA');
INSERT INTO item_cardapio (nome, preco, categoria) VALUES ('Pudim de Leite', 7.00, 'SOBREMESA');

-- Pedidos
INSERT INTO pedido (cliente, data, valor_total) VALUES ('Carlos', '2025-05-21', 23.50);
INSERT INTO pedido (cliente, data, valor_total) VALUES ('Maria', '2025-05-21', 18.00);
INSERT INTO pedido (cliente, data, valor_total) VALUES ('João', '2025-05-21', 33.50);
INSERT INTO pedido (cliente, data, valor_total) VALUES ('Fernanda', '2025-05-21', 46.00);
INSERT INTO pedido (cliente, data, valor_total) VALUES ('Eduardo', '2025-05-21', 10.00);
INSERT INTO pedido (cliente, data, valor_total) VALUES ('Rafaela', '2025-05-22', 8.00);
INSERT INTO pedido (cliente, data, valor_total) VALUES ('Lucas', '2025-05-22', 42.00);
INSERT INTO pedido (cliente, data, valor_total) VALUES ('Bruna', '2025-05-22', 47.50);
INSERT INTO pedido (cliente, data, valor_total) VALUES ('Otávio', '2025-05-22', 25.00);
INSERT INTO pedido (cliente, data, valor_total) VALUES ('Carla', '2025-05-22', 21.00);
INSERT INTO pedido (cliente, data, valor_total) VALUES ('Henrique', '2025-05-22', 14.00);
INSERT INTO pedido (cliente, data, valor_total) VALUES ('Aline', '2025-05-23', 26.50);
INSERT INTO pedido (cliente, data, valor_total) VALUES ('Fabiana', '2025-05-23', 32.00);
INSERT INTO pedido (cliente, data, valor_total) VALUES ('Mateus', '2025-05-23', 33.00);
INSERT INTO pedido (cliente, data, valor_total) VALUES ('Giovana', '2025-05-23', 42.50);

-- Pedido_Itens (relacionando os pedidos aos itens do cardápio)
INSERT INTO pedido_itens (pedido_id, item_id) VALUES (1, 1); -- Batata Frita - M
INSERT INTO pedido_itens (pedido_id, item_id) VALUES (1, 3); -- Coca-Cola
INSERT INTO pedido_itens (pedido_id, item_id) VALUES (2, 5); -- Hambúrguer Simples
INSERT INTO pedido_itens (pedido_id, item_id) VALUES (3, 6); -- Hambúrguer Duplo
INSERT INTO pedido_itens (pedido_id, item_id) VALUES (3, 3); -- Coca-Cola
INSERT INTO pedido_itens (pedido_id, item_id) VALUES (4, 6); -- Hambúrguer Duplo
INSERT INTO pedido_itens (pedido_id, item_id) VALUES (4, 3); -- Coca-Cola
INSERT INTO pedido_itens (pedido_id, item_id) VALUES (4, 12); -- Milkshake
INSERT INTO pedido_itens (pedido_id, item_id) VALUES (5, 2); -- Batata Frita - P
INSERT INTO pedido_itens (pedido_id, item_id) VALUES (6, 13); -- Sorvete
INSERT INTO pedido_itens (pedido_id, item_id) VALUES (7, 7); -- X-Bacon
INSERT INTO pedido_itens (pedido_id, item_id) VALUES (7, 8); -- X-Egg
INSERT INTO pedido_itens (pedido_id, item_id) VALUES (8, 6); -- Hambúrguer Duplo
INSERT INTO pedido_itens (pedido_id, item_id) VALUES (8, 3); -- Coca-Cola
INSERT INTO pedido_itens (pedido_id, item_id) VALUES (8, 14); -- Petit Gâteau
INSERT INTO pedido_itens (pedido_id, item_id) VALUES (9, 6); -- Hambúrguer Duplo
INSERT INTO pedido_itens (pedido_id, item_id) VALUES (10, 1); -- Batata Frita - M
INSERT INTO pedido_itens (pedido_id, item_id) VALUES (10, 11); -- Chá Gelado
INSERT INTO pedido_itens (pedido_id, item_id) VALUES (11, 14); -- Petit Gâteau
INSERT INTO pedido_itens (pedido_id, item_id) VALUES (12, 5); -- Hambúrguer Simples
INSERT INTO pedido_itens (pedido_id, item_id) VALUES (12, 3); -- Coca-Cola
INSERT INTO pedido_itens (pedido_id, item_id) VALUES (13, 8); -- X-Egg
INSERT INTO pedido_itens (pedido_id, item_id) VALUES (13, 9); -- Refrigerante 2L
INSERT INTO pedido_itens (pedido_id, item_id) VALUES (14, 5); -- Hambúrguer Simples
INSERT INTO pedido_itens (pedido_id, item_id) VALUES (14, 1); -- Batata Frita - M
INSERT INTO pedido_itens (pedido_id, item_id) VALUES (15, 6); -- Hambúrguer Duplo
INSERT INTO pedido_itens (pedido_id, item_id) VALUES (15, 3); -- Coca-Cola
INSERT INTO pedido_itens (pedido_id, item_id) VALUES (15, 15); -- Brownie

-- Usuários
INSERT INTO usuario (username, senha, role) VALUES ('admin', '$2y$10$LU3jGs9BFPgavdi.m1VSpOpaJjFTrHckIg6.iVKtMc0WMtwSjQr6.', 'ADMIN');
INSERT INTO usuario (username, senha, role) VALUES ('user', '$2y$10$pkIYByhfCiPndxSLovQCYet9xqmloWRXf2FnzGZ8cKxaqosMemdEC', 'ATENDENTE');
