DROP TABLE IF EXISTS temp_1234_test1_tbl;
DROP TABLE IF EXISTS temp_1234_node_tbl2;
DELETE FROM node_tbl2;
UPDATE customers SET status = 'DELETED';
DELETE FROM customers;
ALTER SEQUENCE node_tbl2_id_seq RESTART WITH 1;  