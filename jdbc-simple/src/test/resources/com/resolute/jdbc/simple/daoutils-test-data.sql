-----------------
-- TEST TABLES --
-----------------

DROP TABLE IF EXISTS test1_tbl;

CREATE TABLE foo_tbl (
  id SERIAL PRIMARY KEY,
  name CHARACTER VARYING NOT NULL UNIQUE CHECK (name <> '')
);

INSERT INTO foo_tbl (id, name) VALUES
  (1, 'Foo1');

