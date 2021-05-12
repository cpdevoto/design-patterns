-----------------
-- TEST TABLES --
-----------------

INSERT INTO test1_tbl (id, name) VALUES
  (1, 'One'),
	(2, 'Two');

ALTER SEQUENCE test1_tbl_id_seq RESTART WITH 3;    

INSERT INTO test2_tbl (id, name) VALUES
  (1, 'One'),
	(2, 'Two');
	
ALTER SEQUENCE test2_tbl_id_seq RESTART WITH 3;    

INSERT INTO test3_tbl (test1_id, test2_id, name) VALUES
  (1, 1, 'One - One'),
  (1, 2, 'One - Two'),
  (2, 1, 'Two - One'),
  (2, 2, 'Two - Two');

INSERT INTO test4_tbl (id, test1_id, parent_id, name) VALUES
  (1, 1, NULL, 'One - One'),
  (2, 2, 1, 'Two - Two'),
  (3, 2, 2, 'Three - Two'),
  (4, 2, NULL, 'Four - Two'),
  (5, 2, 4, 'Five - Two'),
  (6, 1, 5, 'Six - One');

ALTER SEQUENCE test4_tbl_id_seq RESTART WITH 7;    

INSERT INTO test4_closure_tbl (parent_id, child_id, depth) VALUES
  (1, 1, 0),
  (1, 2, 1),
  (1, 3, 2),
  (2, 2, 0),
  (2, 3, 1),
  (3, 3, 0),
  (4, 4, 0),
  (4, 5, 1),
  (4, 6, 2),
  (5, 5, 0),
  (5, 6, 1),
  (6, 6, 0);

INSERT INTO test5_tbl (id, name) VALUES
  (1, 'Customer1'),
  (2, 'Customer2');

ALTER SEQUENCE test5_tbl_id_seq RESTART WITH 3;    

INSERT INTO test6_tbl (id, test5_id, name) VALUES
  (1, 1, 'Node1'),
  (2, 2, 'Node2');

ALTER SEQUENCE test6_tbl_id_seq RESTART WITH 3;    

INSERT INTO test7_tbl (id, test6_id, name) VALUES
  (1, 1, 'NodeTag1'),
  (2, 2, 'NodeTag2');

ALTER SEQUENCE test7_tbl_id_seq RESTART WITH 3;    

INSERT INTO test8_tbl (id, test5_id, test6_id, name) VALUES
  (1, 1, 1, 'Customer1 - Node1 Notification'),
  (2, 1, NULL, 'Customer1 Notification'),
  (3, 2, 2, 'Customer2 Node2 Notification');

ALTER SEQUENCE test8_tbl_id_seq RESTART WITH 4;    

INSERT INTO test9_tbl (id, test8_id, name) VALUES
  (1, 1, 'Customer1 - Node1 Notification Settings'),
  (2, 2, 'Customer1 Notification Settings'),
  (3, 3, 'Customer2 Node2 Notification Setting');

ALTER SEQUENCE test9_tbl_id_seq RESTART WITH 4;    

INSERT INTO clone_test_tbl (int_array_field, text_array_field, character_varying_array_field, user_defined_field, bigint_field, boolean_field, character_varying_field, date_field, double_precision_field, inet_field, integer_field, json_field, money_field, numeric_field, numeric_field2, smallint_field, text_field, time_without_timezone_field, timestamp_without_timezone_field, uuid_field, null_field) VALUES
  ('{1, 2, 3}', '{"dog", "cat", "mouse"}', '{"dog", "cat", "mouse"}', 'ONLINE', 1234567, TRUE, 'Character Varying Field', '2017-03-14', 123.4567, '192.168.2.1', 6789, '{"email": "thom22@gmail.com", "country": "US"}', 345.67, 1234, 234.56, 12, 'Text Field', '02:03:04', '03/03/2014 02:03:04', '4b36afc8-5205-49c1-af16-4dc6f96db982', NULL);


INSERT INTO test11_tbl (id1, id2, name, description) VALUES
  (3905, 7, 'Record 1', 'Description 1'),
  (3907, 7, 'Record 2', 'Description 2'),
  (3908, 7, 'Record 3', 'Description 3'),
  (3911, 7, 'Record 4', 'Description 4');

ALTER SEQUENCE test11_tbl_id1_seq RESTART WITH 3912;    
ALTER SEQUENCE test11_tbl_id2_seq RESTART WITH 8;    

INSERT INTO test12_tbl (test11_id1, test11_id2, name) VALUES
  (3905, 7, 'Record 1'),
  (3907, 7, 'Record 2'),
  (3908, 7, 'Record 3'),
  (3911, 7, 'Record 4');


