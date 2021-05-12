-----------------
-- TEST TABLES --
-----------------

DROP TABLE IF EXISTS test10_tbl;
DROP TABLE IF EXISTS test9_tbl;
DROP TABLE IF EXISTS test8_tbl;
DROP TABLE IF EXISTS test7_tbl;
DROP TABLE IF EXISTS test6_tbl;
DROP TABLE IF EXISTS test5_tbl;
DROP TABLE IF EXISTS test4_closure_tbl;
DROP TABLE IF EXISTS test4_tbl;
DROP TABLE IF EXISTS test3_tbl;
DROP TABLE IF EXISTS test2_tbl;
DROP TABLE IF EXISTS test1_tbl;
DROP TABLE IF EXISTS node_tbl2;

CREATE TABLE test1_tbl (
	id SERIAL PRIMARY KEY,
	name CHARACTER VARYING NOT NULL UNIQUE CHECK (name <> '')
);

CREATE TABLE test2_tbl (
	id SERIAL PRIMARY KEY,
	name CHARACTER VARYING NOT NULL UNIQUE CHECK (name <> '')
);

CREATE TABLE test3_tbl (
	test1_id INTEGER REFERENCES test1_tbl ON DELETE RESTRICT,
	test2_id INTEGER REFERENCES test2_tbl ON DELETE RESTRICT,
	name CHARACTER VARYING NOT NULL UNIQUE CHECK (name <> ''),
	PRIMARY KEY (test1_id, test2_id)
);

-- This table is used to test whether unary associations are properly handled!
CREATE TABLE test4_tbl (
	id SERIAL PRIMARY KEY,
	test1_id INTEGER REFERENCES test1_tbl ON DELETE RESTRICT,
	parent_id INTEGER REFERENCES test4_tbl ON DELETE RESTRICT,
	name CHARACTER VARYING NOT NULL UNIQUE CHECK (name <> '')
);

-- This table is used to test whether closure tables are properly handled!
CREATE TABLE test4_closure_tbl (
  parent_id INTEGER REFERENCES test4_tbl ON DELETE RESTRICT,
  child_id INTEGER REFERENCES test4_tbl ON DELETE RESTRICT,
  depth INTEGER,
  PRIMARY KEY (parent_id, child_id)
);


-- The following 5 tables are used to test the graph difference operator

CREATE TABLE test5_tbl (
  id SERIAL PRIMARY KEY,
  name CHARACTER VARYING NOT NULL UNIQUE CHECK (name <> '')
);

CREATE TABLE test6_tbl (
  id SERIAL PRIMARY KEY,
  test5_id INTEGER NOT NULL REFERENCES test5_tbl ON DELETE CASCADE,
  name CHARACTER VARYING NOT NULL UNIQUE CHECK (name <> '')
);

CREATE TABLE test7_tbl (
  id SERIAL PRIMARY KEY,
  test6_id INTEGER NOT NULL REFERENCES test6_tbl ON DELETE CASCADE,
  name CHARACTER VARYING NOT NULL UNIQUE CHECK (name <> '')
);


CREATE TABLE test8_tbl (
  id SERIAL PRIMARY KEY,
  test5_id INTEGER NOT NULL REFERENCES test5_tbl ON DELETE CASCADE,
  test6_id INTEGER REFERENCES test6_tbl ON DELETE CASCADE,
  name CHARACTER VARYING NOT NULL UNIQUE CHECK (name <> '')
);

CREATE TABLE test9_tbl (
  id SERIAL PRIMARY KEY,
  test8_id INTEGER NOT NULL REFERENCES test8_tbl ON DELETE CASCADE,
  name CHARACTER VARYING NOT NULL UNIQUE CHECK (name <> '')
);

-- these tables are used for additonal testing

CREATE TABLE test10_tbl (
  id SERIAL PRIMARY KEY,
  test4_id INTEGER NOT NULL REFERENCES test4_tbl ON DELETE CASCADE,
  name CHARACTER VARYING NOT NULL UNIQUE CHECK (name <> '')
);

CREATE TABLE node_tbl2 (
       id SERIAL PRIMARY KEY,
       uuid UUID NOT NULL UNIQUE DEFAULT gen_random_uuid(),
       customer_id INTEGER NOT NULL REFERENCES customer_tbl ON DELETE CASCADE,
       node_type_id INTEGER NOT NULL REFERENCES node_type_tbl ON DELETE RESTRICT,
       parent_id INTEGER REFERENCES node_tbl2 ON DELETE CASCADE,
       name CHARACTER VARYING NOT NULL CHECK (name <> ''),
       display_name CHARACTER VARYING NOT NULL DEFAULT ''::CHARACTER VARYING,
       created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
       updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
       CONSTRAINT node_tbl2_parent_id_name_idx UNIQUE(parent_id, name)
);

CREATE TABLE clone_test_tbl (
       id SERIAL PRIMARY KEY,
       int_array_field INTEGER [],
       text_array_field TEXT [],
       character_varying_array_field CHARACTER VARYING [],       
       user_defined_field building_payment_type,
       bigint_field BIGINT,
       boolean_field BOOL,
       character_varying_field CHARACTER VARYING,
       date_field DATE,
       double_precision_field DOUBLE PRECISION,
       inet_field INET,
       integer_field INTEGER,
       json_field JSON,
       money_field MONEY,
       numeric_field NUMERIC,
       numeric_field2 NUMERIC,
       smallint_field SMALLINT,
       text_field TEXT,
       time_without_timezone_field TIME WITHOUT TIME ZONE,
       timestamp_without_timezone_field TIMESTAMP WITHOUT TIME ZONE,
       uuid_field UUID,
       null_field INTEGER
);


