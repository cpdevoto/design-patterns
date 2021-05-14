DROP TRIGGER IF EXISTS insert_test1_trigger ON test1_tbl;
DROP FUNCTION IF EXISTS insert_test1();

DELETE FROM test12_tbl;
DELETE FROM test11_tbl;
ALTER SEQUENCE test11_tbl_id1_seq RESTART WITH 1;    
ALTER SEQUENCE test11_tbl_id2_seq RESTART WITH 1;    

DELETE FROM test9_tbl;
ALTER SEQUENCE test9_tbl_id_seq RESTART WITH 1;    
DELETE FROM test8_tbl;
ALTER SEQUENCE test8_tbl_id_seq RESTART WITH 1;    
DELETE FROM test7_tbl;
ALTER SEQUENCE test7_tbl_id_seq RESTART WITH 1;    
DELETE FROM test6_tbl;
ALTER SEQUENCE test6_tbl_id_seq RESTART WITH 1;    
DELETE FROM test5_tbl;
ALTER SEQUENCE test5_tbl_id_seq RESTART WITH 1;    
DELETE FROM test4_closure_tbl;
DELETE FROM test4_tbl;
ALTER SEQUENCE test4_tbl_id_seq RESTART WITH 1;    
DELETE FROM test3_tbl;
DELETE FROM test2_tbl;
ALTER SEQUENCE test2_tbl_id_seq RESTART WITH 1;    
DELETE FROM test1_tbl;
ALTER SEQUENCE test1_tbl_id_seq RESTART WITH 1;    
DELETE FROM clone_test_tbl;
ALTER SEQUENCE clone_test_tbl_id_seq RESTART WITH 1;    