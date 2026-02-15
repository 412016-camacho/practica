-- Mundo 1: Bosque Encantado
INSERT INTO worlds (id, name, description) VALUES (1, 'Bosque Encantado', 'Un bosque misterioso lleno de criaturas y tesoros ocultos');

-- Casillas del Mundo 1 (30 casillas: 0-29)
-- Casilla 0 y 29 siempre EMPTY
INSERT INTO cells (id, cell_index, type, cell_value, world_id) VALUES (1, 0, 'EMPTY', NULL, 1);
INSERT INTO cells (id, cell_index, type, cell_value, world_id) VALUES (2, 1, 'MONSTER', 0.5, 1);
INSERT INTO cells (id, cell_index, type, cell_value, world_id) VALUES (3, 2, 'EMPTY', NULL, 1);
INSERT INTO cells (id, cell_index, type, cell_value, world_id) VALUES (4, 3, 'BONUS', 1.0, 1);
INSERT INTO cells (id, cell_index, type, cell_value, world_id) VALUES (5, 4, 'MONSTER', 1.0, 1);
INSERT INTO cells (id, cell_index, type, cell_value, world_id) VALUES (6, 5, 'EMPTY', NULL, 1);
INSERT INTO cells (id, cell_index, type, cell_value, world_id) VALUES (7, 6, 'MONSTER', 0.5, 1);
INSERT INTO cells (id, cell_index, type, cell_value, world_id) VALUES (8, 7, 'BONUS', 0.5, 1);
INSERT INTO cells (id, cell_index, type, cell_value, world_id) VALUES (9, 8, 'EMPTY', NULL, 1);
INSERT INTO cells (id, cell_index, type, cell_value, world_id) VALUES (10, 9, 'MONSTER', 2.0, 1);
INSERT INTO cells (id, cell_index, type, cell_value, world_id) VALUES (11, 10, 'BONUS', 1.0, 1);
INSERT INTO cells (id, cell_index, type, cell_value, world_id) VALUES (12, 11, 'EMPTY', NULL, 1);
INSERT INTO cells (id, cell_index, type, cell_value, world_id) VALUES (13, 12, 'MONSTER', 1.0, 1);
INSERT INTO cells (id, cell_index, type, cell_value, world_id) VALUES (14, 13, 'EMPTY', NULL, 1);
INSERT INTO cells (id, cell_index, type, cell_value, world_id) VALUES (15, 14, 'BONUS', 2.0, 1);
INSERT INTO cells (id, cell_index, type, cell_value, world_id) VALUES (16, 15, 'MONSTER', 1.5, 1);
INSERT INTO cells (id, cell_index, type, cell_value, world_id) VALUES (17, 16, 'EMPTY', NULL, 1);
INSERT INTO cells (id, cell_index, type, cell_value, world_id) VALUES (18, 17, 'MONSTER', 0.5, 1);
INSERT INTO cells (id, cell_index, type, cell_value, world_id) VALUES (19, 18, 'BONUS', 1.0, 1);
INSERT INTO cells (id, cell_index, type, cell_value, world_id) VALUES (20, 19, 'EMPTY', NULL, 1);
INSERT INTO cells (id, cell_index, type, cell_value, world_id) VALUES (21, 20, 'MONSTER', 3.0, 1);
INSERT INTO cells (id, cell_index, type, cell_value, world_id) VALUES (22, 21, 'BONUS', 2.0, 1);
INSERT INTO cells (id, cell_index, type, cell_value, world_id) VALUES (23, 22, 'EMPTY', NULL, 1);
INSERT INTO cells (id, cell_index, type, cell_value, world_id) VALUES (24, 23, 'MONSTER', 1.0, 1);
INSERT INTO cells (id, cell_index, type, cell_value, world_id) VALUES (25, 24, 'EMPTY', NULL, 1);
INSERT INTO cells (id, cell_index, type, cell_value, world_id) VALUES (26, 25, 'BONUS', 3.0, 1);
INSERT INTO cells (id, cell_index, type, cell_value, world_id) VALUES (27, 26, 'MONSTER', 2.0, 1);
INSERT INTO cells (id, cell_index, type, cell_value, world_id) VALUES (28, 27, 'EMPTY', NULL, 1);
INSERT INTO cells (id, cell_index, type, cell_value, world_id) VALUES (29, 28, 'MONSTER', 1.0, 1);
INSERT INTO cells (id, cell_index, type, cell_value, world_id) VALUES (30, 29, 'EMPTY', NULL, 1);

-- Mundo 2: Caverna Oscura
INSERT INTO worlds (id, name, description) VALUES (2, 'Caverna Oscura', 'Una caverna peligrosa con muchos monstruos pero grandes recompensas');

-- Casillas del Mundo 2
INSERT INTO cells (id, cell_index, type, cell_value, world_id) VALUES (31, 0, 'EMPTY', NULL, 2);
INSERT INTO cells (id, cell_index, type, cell_value, world_id) VALUES (32, 1, 'MONSTER', 1.0, 2);
INSERT INTO cells (id, cell_index, type, cell_value, world_id) VALUES (33, 2, 'MONSTER', 1.0, 2);
INSERT INTO cells (id, cell_index, type, cell_value, world_id) VALUES (34, 3, 'BONUS', 2.0, 2);
INSERT INTO cells (id, cell_index, type, cell_value, world_id) VALUES (35, 4, 'EMPTY', NULL, 2);
INSERT INTO cells (id, cell_index, type, cell_value, world_id) VALUES (36, 5, 'MONSTER', 2.0, 2);
INSERT INTO cells (id, cell_index, type, cell_value, world_id) VALUES (37, 6, 'EMPTY', NULL, 2);
INSERT INTO cells (id, cell_index, type, cell_value, world_id) VALUES (38, 7, 'BONUS', 1.0, 2);
INSERT INTO cells (id, cell_index, type, cell_value, world_id) VALUES (39, 8, 'MONSTER', 1.5, 2);
INSERT INTO cells (id, cell_index, type, cell_value, world_id) VALUES (40, 9, 'EMPTY', NULL, 2);
INSERT INTO cells (id, cell_index, type, cell_value, world_id) VALUES (41, 10, 'MONSTER', 2.0, 2);
INSERT INTO cells (id, cell_index, type, cell_value, world_id) VALUES (42, 11, 'BONUS', 3.0, 2);
INSERT INTO cells (id, cell_index, type, cell_value, world_id) VALUES (43, 12, 'EMPTY', NULL, 2);
INSERT INTO cells (id, cell_index, type, cell_value, world_id) VALUES (44, 13, 'MONSTER', 1.0, 2);
INSERT INTO cells (id, cell_index, type, cell_value, world_id) VALUES (45, 14, 'MONSTER', 0.5, 2);
INSERT INTO cells (id, cell_index, type, cell_value, world_id) VALUES (46, 15, 'BONUS', 2.0, 2);
INSERT INTO cells (id, cell_index, type, cell_value, world_id) VALUES (47, 16, 'EMPTY', NULL, 2);
INSERT INTO cells (id, cell_index, type, cell_value, world_id) VALUES (48, 17, 'MONSTER', 3.0, 2);
INSERT INTO cells (id, cell_index, type, cell_value, world_id) VALUES (49, 18, 'BONUS', 2.0, 2);
INSERT INTO cells (id, cell_index, type, cell_value, world_id) VALUES (50, 19, 'EMPTY', NULL, 2);
INSERT INTO cells (id, cell_index, type, cell_value, world_id) VALUES (51, 20, 'MONSTER', 1.0, 2);
INSERT INTO cells (id, cell_index, type, cell_value, world_id) VALUES (52, 21, 'EMPTY', NULL, 2);
INSERT INTO cells (id, cell_index, type, cell_value, world_id) VALUES (53, 22, 'BONUS', 1.0, 2);
INSERT INTO cells (id, cell_index, type, cell_value, world_id) VALUES (54, 23, 'MONSTER', 2.0, 2);
INSERT INTO cells (id, cell_index, type, cell_value, world_id) VALUES (55, 24, 'BONUS', 3.0, 2);
INSERT INTO cells (id, cell_index, type, cell_value, world_id) VALUES (56, 25, 'EMPTY', NULL, 2);
INSERT INTO cells (id, cell_index, type, cell_value, world_id) VALUES (57, 26, 'MONSTER', 1.0, 2);
INSERT INTO cells (id, cell_index, type, cell_value, world_id) VALUES (58, 27, 'MONSTER', 0.5, 2);
INSERT INTO cells (id, cell_index, type, cell_value, world_id) VALUES (59, 28, 'BONUS', 1.0, 2);
INSERT INTO cells (id, cell_index, type, cell_value, world_id) VALUES (60, 29, 'EMPTY', NULL, 2);
