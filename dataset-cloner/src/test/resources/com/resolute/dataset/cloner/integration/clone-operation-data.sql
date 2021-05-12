DROP TABLE IF EXISTS temp_1234_node_tbl2;
CREATE TABLE temp_1234_node_tbl2 (
  id INTEGER NOT NULL,
  PRIMARY KEY (id)
);

DROP TABLE IF EXISTS temp_1234_test1_tbl;
CREATE TABLE temp_1234_test1_tbl (
  id INTEGER NOT NULL,
  PRIMARY KEY (id)
);

INSERT INTO customers (id, distributor_id, name) VALUES 
   (1, 1, 'McLaren');

INSERT INTO node_tbl2 (id, uuid, customer_id, node_type_id, parent_id, name, display_name, created_at, updated_at) VALUES 
   (1, '4b36afc8-5205-49c1-af16-4dc6f96db982', 1, 1, NULL, 'Porfolio 1', 'Portfolio 1', '03/03/2021 02:03:04', '03/03/2021 02:03:04');

INSERT INTO node_tbl2 (id, uuid, customer_id, node_type_id, parent_id, name, display_name, created_at, updated_at) VALUES 
   (2, '4b36afc8-5205-49c1-af16-4dc6f96db983', 1, 3, 1, 'Building 1', 'Building 1', '03/03/2021 02:03:04', '03/03/2021 02:03:04'),
   (3, '4b36afc8-5205-49c1-af16-4dc6f96db984', 1, 3, 1, 'Building 2', 'Building 2', '03/03/2021 02:03:04', '03/03/2021 02:03:04');

INSERT INTO node_tbl2 (id, uuid, customer_id, node_type_id, parent_id, name, display_name, created_at, updated_at) VALUES 
   (4, '4b36afc8-5205-49c1-af16-4dc6f96db985', 1, 5, 2, 'Building 1 - Floor 1', 'Building 1 - Floor 1', '03/03/2021 02:03:04', '03/03/2021 02:03:04'),
   (5, '4b36afc8-5205-49c1-af16-4dc6f96db986', 1, 5, 2, 'Building 1 - Floor 2', 'Building 1 - Floor 2', '03/03/2021 02:03:04', '03/03/2021 02:03:04'),
   (6, '4b36afc8-5205-49c1-af16-4dc6f96db987', 1, 5, 3, 'Building 2 - Floor 1', 'Building 2 - Floor 1', '03/03/2021 02:03:04', '03/03/2021 02:03:04'),
   (7, '4b36afc8-5205-49c1-af16-4dc6f96db988', 1, 5, 3, 'Building 2 - Floor 2', 'Building 2 - Floor 2', '03/03/2021 02:03:04', '03/03/2021 02:03:04');

INSERT INTO node_tbl2 (id, uuid, customer_id, node_type_id, parent_id, name, display_name, created_at, updated_at) VALUES 
   (8,  '4b36afc8-5205-49c1-af16-4dc6f96db989', 1, 8, 4, 'Building 1 - Floor 1 - VAV 1', 'Building 1 - Floor 1 - VAV 1', '03/03/2021 02:03:04', '03/03/2021 02:03:04'),
   (9,  '4b36afc8-5205-49c1-af16-4dc6f96db98a', 1, 8, 4, 'Building 1 - Floor 1 - VAV 2', 'Building 1 - Floor 1 - VAV 2', '03/03/2021 02:03:04', '03/03/2021 02:03:04'),
   (10, '4b36afc8-5205-49c1-af16-4dc6f96db98b', 1, 8, 5, 'Building 1 - Floor 2 - VAV 1', 'Building 1 - Floor 2 - VAV 1', '03/03/2021 02:03:04', '03/03/2021 02:03:04'),
   (11, '4b36afc8-5205-49c1-af16-4dc6f96db98c', 1, 8, 5, 'Building 1 - Floor 2 - VAV 2', 'Building 1 - Floor 2 - VAV 2', '03/03/2021 02:03:04', '03/03/2021 02:03:04'),
   (12, '4b36afc8-5205-49c1-af16-4dc6f96db98d', 1, 8, 6, 'Building 2 - Floor 1 - VAV 1', 'Building 2 - Floor 1 - VAV 1', '03/03/2021 02:03:04', '03/03/2021 02:03:04'),
   (13, '4b36afc8-5205-49c1-af16-4dc6f96db98e', 1, 8, 6, 'Building 2 - Floor 1 - VAV 2', 'Building 2 - Floor 1 - VAV 2', '03/03/2021 02:03:04', '03/03/2021 02:03:04'),
   (14, '4b36afc8-5205-49c1-af16-4dc6f96db98f', 1, 8, 7, 'Building 2 - Floor 2 - VAV 1', 'Building 2 - Floor 2 - VAV 1', '03/03/2021 02:03:04', '03/03/2021 02:03:04'),
   (15, '4b36afc8-5205-49c1-af16-4dc6f96db990', 1, 8, 7, 'Building 2 - Floor 2 - VAV 2', 'Building 2 - Floor 2 - VAV 2', '03/03/2021 02:03:04', '03/03/2021 02:03:04');

INSERT INTO temp_1234_node_tbl2 (id) VALUES
  (2), (4), (5), (8), (9), (10), (11);

INSERT INTO temp_1234_test1_tbl (id) VALUES
  (1);

ALTER SEQUENCE node_tbl2_id_seq RESTART WITH 16;  