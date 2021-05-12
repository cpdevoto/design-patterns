-----------------
-- TEST TABLES --
-----------------

INSERT INTO test1_tbl (id, name) VALUES
  (1, 'One'),
	(2, 'Two');

INSERT INTO test2_tbl (id, name) VALUES
  (1, 'One'),
	(2, 'Two');
	
INSERT INTO test3_tbl (test1_id, test2_id, name) VALUES
  (1, 1, 'One - One'),
  (1, 2, 'One - Two'),
  (2, 1, 'Two - One'),
  (2, 2, 'Two - Two');

INSERT INTO test4_tbl (id, test1_id, parent_id, name) VALUES
  (1, 1, NULL, 'One - One'),
  (2, 2, 1, 'Two - Two');

INSERT INTO test5_tbl (id, name) VALUES
  (1, 'Customer1'),
  (2, 'Customer2');

INSERT INTO test6_tbl (id, test5_id, name) VALUES
  (1, 1, 'Node1'),
  (2, 2, 'Node2');

INSERT INTO test7_tbl (id, test6_id, name) VALUES
  (1, 1, 'NodeTag1'),
  (2, 2, 'NodeTag2');

INSERT INTO test8_tbl (id, test5_id, test6_id, name) VALUES
  (1, 1, 1, 'Customer1 - Node1 Notification'),
  (2, 1, NULL, 'Customer1 Notification'),
  (3, 2, 2, 'Customer2 Node2 Notification');

INSERT INTO test9_tbl (id, test8_id, name) VALUES
  (1, 1, 'Customer1 - Node1 Notification Settings'),
  (2, 2, 'Customer1 Notification Settings'),
  (3, 3, 'Customer2 Node2 Notification Setting');
