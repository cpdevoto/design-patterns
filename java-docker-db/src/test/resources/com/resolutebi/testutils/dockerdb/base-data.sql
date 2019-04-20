INSERT INTO distributors (id, parent_id, name) VALUES (2, 1, 'Distributor1');

INSERT INTO customers (id, distributor_id, uuid, name, resolute_start_date) VALUES 
  (1, 1, 'b558664d-fcf7-48e8-9807-e7a7614f22bc', 'McLaren', '2014-05-01T00:00:00.000'),
  (2, 2, '95ee18c1-305a-4074-bea2-04d261e8800b', 'Demo', '2014-05-01T00:00:00.000');
