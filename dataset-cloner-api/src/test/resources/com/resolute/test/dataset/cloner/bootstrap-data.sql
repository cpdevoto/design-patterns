INSERT INTO distributors (id, parent_id, name) VALUES (2, 1, 'Distributor1');

INSERT INTO customers (id, distributor_id, uuid, name, resolute_start_date, fiscal_year_start) VALUES 
  (1, 1, 'b558664d-fcf7-48e8-9807-e7a7614f22bc', 'McLaren', '2014-05-01T00:00:00.000', 3),
  (2, 2, '95ee18c1-305a-4074-bea2-04d261e8800b', 'Demo', '2014-05-01T00:00:00.000', 1);
  
ALTER SEQUENCE customer_tbl_id_seq RESTART WITH 3;
  
INSERT INTO customer_timezones (customer_id, timezones ) VALUES
  ( 1, '{Pacific Time (US & Canada), Eastern Time (US & Canada)}'),
  ( 2, '{Pacific Time (US & Canada), Eastern Time (US & Canada)}');
  
INSERT INTO customer_utilities (customer_id, utility_id) VALUES
  (1, 1),
  (1, 2),
  (1, 3);
  
INSERT INTO components (id, customer_id, component_type_id) VALUES (1, 1, 1);

ALTER SEQUENCE component_tbl_id_seq RESTART WITH 2;
  
INSERT INTO portfolios (id, customer_id, name) VALUES (1, 1, 'McLaren');

INSERT INTO portfolios (id, customer_id, name) VALUES (14, 2, 'Demo');

INSERT INTO sites (id, customer_id, parent_id, name) VALUES (2, 1, 1, 'Site1');

INSERT INTO sites (id, customer_id, parent_id, name) VALUES (8, 1, 1, 'Site2');

INSERT INTO buildings (id, customer_id, parent_id, name, timezone) VALUES (3, 1, 2, 'Building1', 'Eastern Time (US & Canada)');
INSERT INTO buildings (id, customer_id, parent_id, name, timezone) VALUES (2020, 1, 2, '0123', 'Eastern Time (US & Canada)');

INSERT INTO floors (id, customer_id, parent_id, name) VALUES (50, 1, 3, 'Floor1');
INSERT INTO floors (id, customer_id, parent_id, name) VALUES (51, 1, 3, 'FloorFoo');
INSERT INTO floors (id, customer_id, parent_id, name) VALUES (52, 1, 3, 'FloorBar');
INSERT INTO floors (id, customer_id, parent_id, name) VALUES (512, 1, 3, 'SweettttFloor');
INSERT INTO floors (id, customer_id, parent_id, name) VALUES (53, 1, 2020, 'abcd');

INSERT INTO floors (id, customer_id, parent_id, name) VALUES (56, 1, 3, 'floor with mapped point as only child');

INSERT INTO floors (id, customer_id, parent_id, name) VALUES (57, 1, 3, 'floor with async point as only child');
INSERT INTO floors (id, customer_id, parent_id, name) VALUES (58, 1, 3, 'floor with sync point as only child');
INSERT INTO floors (id, customer_id, parent_id, name) VALUES (59, 1, 3, 'floor with mapped point as only child but tagged');


INSERT INTO buildings (id, customer_id, parent_id, name, timezone) VALUES (9, 1, 2, 'Building2', 'Eastern Time (US & Canada)');

INSERT INTO buildings (id, customer_id, parent_id, name, timezone) VALUES (10, 1, 8, 'Building3', 'Eastern Time (US & Canada)');

INSERT INTO buildings (id, customer_id, parent_id, name, timezone) VALUES (11, 1, 8, 'Building4', 'Eastern Time (US & Canada)');

INSERT INTO buildings (id, customer_id, parent_id, name, timezone) VALUES (12, 1, 1, 'Building5', 'Eastern Time (US & Canada)');

INSERT INTO buildings (id, customer_id, parent_id, name, timezone) VALUES (13, 1, 1, 'Building6', 'Eastern Time (US & Canada)');

INSERT INTO buildings (id, customer_id, parent_id, name, timezone) VALUES (15, 2, 14, 'Building7', 'Eastern Time (US & Canada)');

insert into equipment (id, parent_id, customer_id, name) values (132234, 3, 1, 'Equipment1');

insert into equipment (id, parent_id, customer_id, name) values (132235, 132234, 1, 'Equipment2');

insert into equipment (id, parent_id, customer_id, parent_equipment_id, name) values (132236, 3, 1, 132234, 'Equipment3');

update equipment SET parent_equipment_id = null WHERE id = 132236;
update equipment SET parent_equipment_id = 132236 WHERE id = 132234;
update equipment SET parent_id = 132236 WHERE id = 132235;

insert into node_tags (node_id, tag_id) values (132234, 43);


insert into meters (id, parent_id, name, display_name, customer_id) values ( 54, 9, 'Building Meter', 'Building Meter', 1);

INSERT INTO raw_points (id, customer_id, component_id, metric_id, unit_type, point_type, "value", value_timestamp) VALUES
  (1, 1, 1, '/Drivers/NiagaraNetwork/APPB/appb5810/points/ElecMeters/BldgMeter/kWH_IntHour', 'kWh', 'NumericPoint', 98.35546875, '2016-05-13T09:58:24.738'),
  (2, 1, 1, '/Drivers/NiagaraNetwork/ColsSteel/points/ElecMeters/Slitter72/kWH_Int5', 'kWh', 'NumericPoint', 60.0, '2016-05-13T09:58:24.738'),
  (3, 1, 1, '/Drivers/NiagaraNetwork/ColsSteel/points/ElecMeters/Slitter72/coilsOut', '#coils', 'NumericPoint', 20.0, '2016-05-13T09:58:24.738'),
  (4, 1, 1, '/Drivers/NiagaraNetwork/ColsSteel/points/ElecMeters/Slitter72/kWH_Hour', 'kWh', 'NumericPoint', 40.0, '2016-05-13T09:58:24.738'),
  (5, 1, 1, '/Drivers/NiagaraNetwork/A/B', 'kWh', 'NumericPoint', 40.0, '2016-05-13T09:58:24.738'),
  (6, 1, 1, 'A','kWh', 'NumericPoint', 40.0, '2016-05-13T09:58:24.738'),
  (7, 1, 1, 'B','kWh', 'NumericPoint', 40.0, '2016-05-13T09:58:24.738'),
  (8, 1, 1, 'D','kWh', 'NumericPoint', 40.0, '2016-05-13T09:58:24.738'),
  (9, 1, 1, 'E','kWh', 'NumericPoint', 40.0, '2016-05-13T09:58:24.738'),
  (10, 1, 1, 'F','kWh', 'NumericPoint', 40.0, '2016-05-13T09:58:24.738'),
  (11, 1, 1, 'CC','kWh', 'NumericPoint', 40.0, '2016-05-13T09:58:24.738'),
  (12, 1, 1, 'BB','kWh', 'NumericPoint', 40.0, '2016-05-13T09:58:24.738'),
  (13, 1, 1, 'AA','kWh', 'NumericPoint', 40.0, '2016-05-13T09:58:24.738'),
  (14, 1, 1, 'BBB','kWh', 'NumericPoint', 40.0, '2016-05-13T09:58:24.738'),
  (15, 1, 1, 'XXX','kWh', 'NumericPoint', 40.0, '2016-05-13T09:58:24.738'),
  (16, 1, 1, 'XXXX','kWh', 'NumericPoint', 40.0, '2016-05-13T09:58:24.738'),
  (17, 1, 1, 'XXXXX','kWh', 'NumericPoint', 40.0, '2016-05-13T09:58:24.738');

ALTER SEQUENCE raw_point_tbl_id_seq RESTART WITH 18;

INSERT INTO mappable_points (id, customer_id, parent_id, name, data_type_id, metric_id, unit_type, raw_point_id) VALUES 
  (16, 1, 3, 'kW per Hour', 1, '/Drivers/NiagaraNetwork/APPB/appb5810/points/ElecMeters/BldgMeter/kWH_IntHour', 'kWh', 1),
  (17, 1, 3, 'kW per 5 Minutes', 1, '/Drivers/NiagaraNetwork/ColsSteel/points/ElecMeters/Slitter72/kWH_Int5', 'kWh', 2),
  (18, 1, 3, 'Coils Out', 1, '/Drivers/NiagaraNetwork/ColsSteel/points/ElecMeters/Slitter72/coilsOut', '#coils', 3),
  (21, 1, 9, 'kWh per Hour', 1, '/Drivers/NiagaraNetwork/ColsSteel/points/ElecMeters/Slitter72/kWH_Hour', 'kWh', 4),
  (22, 1, 9, 'A', 1, '/Drivers/NiagaraNetwork/A/B', 'kWh', 5),
  (23, 1, 9, 'B', 1, 'foo', 'A', 6),
  (24, 1, 9, 'C', 1, '/Drivers/foo', 'C', 7),
  (25, 1, 9, 'D', 1, '/Drivers/bar', 'D', 8),
  (26, 1, 9, 'E', 1, '/Drivers/bar2', 'C', 9),
  (27, 1, 9, 'F', 1, '/Drivers/bar3', 'C', 10),
  (28, 1, 56, 'F', 1, '/Drivers/bar4', 'C', 15),
  (29, 1, 59, 'F', 1, '/Drivers/bar5', 'C', 16),
  (31, 1, 9, 'G', 1, '/Drivers/bar6', 'C', 17),
  (502, 1, 13, 'Unmapped Point', 1, '/Drivers/Unmapped', '', null);


INSERT INTO sync_computed_points (id, customer_id, parent_id, name, data_type_id, metric_id, unit_type, join_function_id, "value", value_timestamp) VALUES 
  (19, 1, 3, 'kWh per Coil', 1, '/Drivers/NiagaraNetwork/ColsSteel/points/ElecMeters/Slitter72/kWhPerCoil', 'kWh/coil', 5, 1234.5678, '2016-05-13T09:58:24.738'),
  (430495, 1, 3, 'kWh per Coilbar', 1, '/Drivers/NiagaraNetwork/ColsSteel/points/ElecMeters/Slitter72/kWhPerCoil1', 'kWh/coil', 5, 1234.5678, '2016-05-13T09:58:24.738'),
  (500, 1, 58, 'kWh per Coilfoo', 1, '/Drivers/NiagaraNetwork/ColsSteel/points/ElecMeters/Slitter72/kWhPerCoil2', 'kWh/coil', 5, 1234.5678, '2016-05-13T09:58:24.738');


-- TODO why is the ordinal in both this and the sync_computed_point_input_transformers
INSERT INTO sync_computed_point_inputs(id, sync_computed_point_id, input_point_id, aggregator_id, ordinal) VALUES 
  (1, 19, 17, 6, 1),
  (2, 19, 18, 6, 2),
  (3, 500, 27, 6, 1),
  (4, 500, 28, 6, 2);

INSERT INTO sync_computed_point_input_transformers(id, sync_computed_point_input_id, transformer_function_id, operand, ordinal) VALUES 
  (1, 1, 3, 10.0, 1),
  (2, 1, 3, 1.0, 2),
  (3, 3, 3, 10.0, 1),
  (4, 4, 3, 1.0, 2);

INSERT INTO async_computed_points (id, customer_id, parent_id, name, data_type_id, metric_id, unit_type, "value", value_timestamp) VALUES 
  (20, 1, 9, 'kW per 5 Minutes', 1, '/Drivers/NiagaraNetwork/ColsSteel/points/ElecMeters/Slitter72/kWH_Delta5', 'kWh', 25.0, '2016-05-13T09:58:24.738'),
  (400, 1, 57, 'kW per 5 Minutes', 1, '/Drivers/NiagaraNetwork/ColsSteel/points/ElecMeters/Slitter72/kWH_Delta6', 'kWh', 25.0, '2016-05-13T09:58:24.738'),
  (401, 1, 512, 'kW per 6 Minutes', 1, '/Drivers/NiagaraNetwork/ColsSteel/points/ElecMeters/Slitter72/kWH_Delta7', 'kWh', 25.0, '2016-05-13T09:58:24.738'),
  (402, 1, 132234, 'foobarfoo template test', 1, '/foo/bar/foo/template/test', 'kWh', 25.0, '2016-05-13T09:58:24.738');

INSERT INTO async_computed_points (
  id,   name, display_name, customer_id, parent_id, data_type_id, metric_id, unit_type, configurable, timezone_based_rollups,
   "value", value_timestamp
  ) VALUES 
  (30, 'TotalkWhPerDay', 'Total kWh per Day', 1, 9,  1, '/Async/Building2/kWH_DeltaDay', 'kWh', TRUE, TRUE, 25.0, '2016-05-13T09:58:24.738');
  
INSERT INTO async_computed_point_configs ( id, computation_interval ) VALUES 
  ( 30, '1dc');

INSERT INTO temporal_async_computed_point_configs ( id, async_computed_point_config_id, effective_date, formula) VALUES 
( 1, 30, '2016-01-01', '(Variable_1*2+Variable_2)/Variable_3'),
( 2, 30, '2017-01-01', '(Variable_1*2.5+Variable_2)/Variable_3');

INSERT INTO temporal_async_computed_point_vars ( temporal_async_computed_point_config_id, point_id, fill_policy_id, variable_name) VALUES 
  ( 1, 16, 1, 'Variable_1'),
  ( 1, 17, 2, 'Variable_2'),
  ( 1, 18, 2, 'Variable_3'),
  ( 2, 16, 1, 'Variable_1'),
  ( 2, 17, 2, 'Variable_2'),
  ( 2, 18, 2, 'Variable_3');

INSERT INTO scheduled_async_computed_points (id, customer_id, parent_id, name, display_name, data_type_id, metric_id, unit_type, range, scheduled_event_type_id) 
  VALUES (41, 1, 3, 'Occupancy', 'Occupancy', 2, 'Drivers/NiagaraNetwork/McLaren/Macomb/Occupancy', '', '{"trueText":"Occupied","falseText":"Unoccupied"}', 1);
   
INSERT INTO scheduled_events (id, point_id, recurrence_type, start_date, end_date, start_time, end_time) VALUES
  (1, 41, 2, '2019-03-19', null, '13:00:00', '14:00:00');

INSERT INTO recurrence_rules (id, repeats, every_x, every_month, days_of_week, on_x, on_day_qualifier, on_nonday_qualifier) VALUES
  (1, 2, 2, null, '{1, 3, 7}', null, null, null);
  
INSERT INTO recurrence_rule_exceptions (id, recurrence_rule_id, exception_type, start_date, end_date, start_time, end_time) VALUES
  (1, 1, 2, '2019-03-20', null, '14:00:01', '15:00:00');

ALTER SEQUENCE scheduled_event_tbl_id_seq RESTART WITH 1000;
ALTER SEQUENCE recurrence_rule_exception_tbl_id_seq RESTART WITH 1000;  
  
INSERT INTO customer_users (id, role_id, customer_id, email, encrypted_password, first_name, last_name, invitation_sent_at, invitation_accepted_at, last_sign_in_at) VALUES 
  (1, 1, 1, 'kdmiller@mclaren.com', 'password', 'Keith', 'Miller', '2016-05-13T09:58:24.738', '2016-05-16T09:38:39.688', '2016-05-16T09:38:39.688'),
  (2, 2, 1, 'jsmith@mclaren.com', 'p@$$w0rd', 'John', 'Smith', '2016-05-13T09:58:24.738', '2016-05-16T09:38:39.688', '2016-05-16T09:38:39.688'),
  (3, 3, 1, 'smcgee@mclaren.com', 'p@$$w0rd', 'Slappy', 'McGee', '2016-05-13T09:58:24.738', '2016-05-16T09:38:39.688', '2016-05-16T09:38:39.688'),
  (4, 4, 1, 'sbozo@mclaren.com', 'p@$$w0rd', 'Spanish', 'Bozo', '2016-05-13T09:58:24.738', '2016-05-16T09:38:39.688', '2016-05-16T09:38:39.688'),
  (7, 3, 1, 'hsanders@mclaren.com', 'p@$$w0rd', 'Harland', 'Sanders', '2016-05-13T09:58:24.738', '2016-05-16T09:38:39.688', '2016-05-16T09:38:39.688'),
  (8, 3, 1, 'deleteMe@mclaren.com', 'p@$$w0rd', 'Delete', 'MyAccount', '2016-05-13T09:58:24.738', '2016-05-16T09:38:39.688', '2016-05-16T09:38:39.688'),
  (9, 3, 1, 'deleteMeAlso@mclaren.com', 'p@$$w0rd', 'Delete', 'MyOtherAccount', '2016-05-13T09:58:24.738', '2016-05-16T09:38:39.688', '2016-05-16T09:38:39.688'),
  (10, 4, 1, 'larryn@mclaren.com', 'p@$$w0rd', 'Lysa', 'Arryn', '2016-05-13T09:58:24.738', '2016-05-16T09:38:39.688', '2016-05-16T09:38:39.688'),
  (11, 4, 1, 'etully@mclaren.com', 'p@$$w0rd', 'Edmure', 'Tully', '2016-05-13T09:58:24.738', '2016-05-16T09:38:39.688', '2016-05-16T09:38:39.688'),
  (100, 1, 1, 'sadams@mclaren.com', 'p@$$w0rd', 'Samuel', 'Adams', '2016-05-13T09:58:24.738', '2016-05-16T09:38:39.688', '2016-05-16T09:38:39.688');

INSERT INTO distributor_users (id, role_id, distributor_id, email, encrypted_password, first_name, last_name) VALUES 
  (5, 6, 1, 'cdevoto@maddogtehcnology.com', 'passw0rd', 'Carlos', 'Devoto'),
  (6, 5, 1, 'challendy@resolutebi.com', 'p@$$w0rd', 'Chris', 'Hallendy'),
  (101, 5, 2, 'jblow@subcontractor.com', 'p@$$w0rd', 'Joe', 'Blow');
  
INSERT INTO distributor_users (id, role_id, distributor_id, first_name, last_name, email, uuid, encrypted_password, last_sign_in_at, invitation_created_at, invitation_sent_at, invitation_accepted_at) VALUES
  (104, 6, 2, 'distributor', 'user4', 'distributor104@email.com', '5771bc43-6c01-4b7f-9c33-becf8074b1d2', 'p@$$w0rd', '2016-05-13T09:58:24.738', '2016-05-13T09:58:24.738', '2016-05-13T09:58:24.738', '2016-05-13T09:58:24.738'),
  (105, 6, 2, 'update', 'integrator', 'distributor105@email.com', '5771bc43-6c01-4b7f-9c33-becf8074b1d3', 'p@$$w0rd', '2016-05-13T09:58:24.738', '2016-05-13T09:58:24.738', '2016-05-13T09:58:24.738', '2016-05-13T09:58:24.738'),
  (106, 6, 2, 'delete', 'integrator', 'distributor106@email.com', '5771bc43-6c01-4b7f-9c33-becf8074b1d4', 'p@$$w0rd', '2016-05-13T09:58:24.738', '2016-05-13T09:58:24.738', '2016-05-13T09:58:24.738', '2016-05-13T09:58:24.738'),
  (107, 6, 2, 'updatenames', 'integrator', 'distributor107@email.com', '5771bc43-6c01-4b7f-9c33-becf8074b1d5', 'p@$$w0rd', '2016-05-13T09:58:24.738', '2016-05-13T09:58:24.738', '2016-05-13T09:58:24.738', '2016-05-13T09:58:24.738'),
  (108, 6, 2, 'updatetimezone', 'integrator', 'distributor108@email.com', '5771bc43-6c01-4b7f-9c33-becf8074b1d6', 'p@$$w0rd', '2016-05-13T09:58:24.738', '2016-05-13T09:58:24.738', '2016-05-13T09:58:24.738', '2016-05-13T09:58:24.738'),
  (109, 6, 2, 'updaterole', 'integrator', 'distributor109@email.com', '5771bc43-6c01-4b7f-9c33-becf8074b1d7', 'p@$$w0rd', '2016-05-13T09:58:24.738', '2016-05-13T09:58:24.738', '2016-05-13T09:58:24.738', '2016-05-13T09:58:24.738');
  
INSERT INTO user_setting_tbl ( user_id, timezone ) VALUES
  (1, 'Pacific Time (US & Canada)'),
  (2, 'Pacific Time (US & Canada)'),
  (3, 'Pacific Time (US & Canada)'),
  (4, 'Pacific Time (US & Canada)'),
  (7, 'Pacific Time (US & Canada)'),
  (8, 'Pacific Time (US & Canada)'),
  (9, 'Pacific Time (US & Canada)'),
  (10, 'Pacific Time (US & Canada)'),
  (11, 'Pacific Time (US & Canada)'),
  (100, 'Pacific Time (US & Canada)'),
  (5, 'Pacific Time (US & Canada)'),
  (6, 'Pacific Time (US & Canada)'),
  (101, 'Pacific Time (US & Canada)'),
  (104, 'Pacific Time (US & Canada)'),
  (105, 'Eastern Time (US & Canada)'),
  (106, 'Eastern Time (US & Canada)'),
  (107, 'Eastern Time (US & Canada)'),
  (108, 'Eastern Time (US & Canada)'),
  (109, 'Eastern Time (US & Canada)');
  
INSERT INTO user_features (user_id, feature_id) VALUES
    (3, 10),
    (3, 33),
    (3, 4),
    (4, 10),
    (8, 10),
    (9, 10),
    (10, 10),
    (11,10);
    
INSERT INTO customer_features (customer_id, feature_id) VALUES
    (1, 1),
    (1, 4),
    (1, 9),
    (1, 10),
    (1, 30),
    (1, 32),
    (1, 33),
    (1, 34),
    (1, 35),
    (1, 36),
    (1, 37),
    (2, 32);

INSERT INTO user_nodes (user_id, node_id) VALUES
  (3, 3), -- Building 1
  (3, 8), -- Site 2
  (4, 2), -- Site 1
  (4, 13), -- Building 6
  (7, 2), -- Site 2
  (8, 2), -- Site 2
  (9, 2), -- Site 2
  (10, 2), -- Site 1
  (11, 8)  -- Site 1
  ;
  
INSERT INTO export_jobs (id, customer_id, export_job_status_id, export_job_type_id, creator_id, creator_email, name, config, attempts, url ) 
  VALUES (100, 1, 1, 1, 4, 'cdevoto@maddogtechnology.com', 'Export-Job: Fri, 06 Jan 2017 13:12', '{"metrics":[{"metricId":"4587834f-9607-4542-a58a-72d6848e09e9./Drivers/NiagaraNetwork/Demo/CustMeter/kWH_Int15","aggregator":"sum","pointId":100,"pointTypeId":200}],"start":0,"end":1480535989,"downSamplingInterval":"1d","timezone":"US/Eastern","unifyOutputFiles":true}', 1, 'https://s3.amazonaws.com/resolute-dev/export/export.zip');

-- user 2 should get back reports 1, 2, 3, 4, and 5
-- user 3 should get back report 1 and 4
-- user 4 should get back reports 1, 3, and 5  

INSERT INTO weather_stations (id, code, city, state) VALUES
  (1, 'KDTW', 'Detroit', 'MI'),
  (2, 'KPHN', 'Port Huron', 'MI'),
  (3, 'KFNT', 'Flint', 'MI'),
  (4, 'KLAN', 'Lansing', 'MI'),
  (5, 'KHYX', 'Sagniaw', 'MI'),
  (6, 'KPTK', 'Pontiac', 'MI'),
  (7, 'KMOP', 'Mount Pleasant', 'MI'),
  (8, 'KMGN', 'Petosky', 'MI');
  
INSERT INTO temporal_billing_locations (id, billing_location_id, effective_date, weather_station_id, sqft, energy_star_rating, kbtu_cost_factor) VALUES 
  (1, 3, '2017-10-17', 4, 1500, 85, 1.25);
  
ALTER SEQUENCE temporal_billing_location_tbl_id_seq RESTART WITH 2;

INSERT INTO temporal_billing_location_utilities (temporal_billing_location_id, utility_id, formula, computation_interval_id, utility_rate) VALUES 
  (1, 1, E'IF (WEEK_DAY)\n      IF(AVG_DAILY_TEMP < 51.76)\n        42070.53 - 115.43 * (51.76 - AVG_DAILY_TEMP)\n      ELSE\n        42070.53 + 576.01 * (AVG_DAILY_TEMP - 51.76)\nELSE\n      IF(AVG_DAILY_TEMP < 50.92)\n        39660.37 - 111.91 * (50.92 - AVG_DAILY_TEMP)\n      ELSE\n        39660.37 + 564.63 * (AVG_DAILY_TEMP - 50.92)', 1, 5.0),
  (1, 2, '51.76 - AVG_MONTHLY_TEMP', 2, 10.0),
  (1, 3, 'SAME_MONTH_LAST_YEAR_TOTAL', 2, 20.0);
  
INSERT INTO node_tags ( tag_id, node_id ) VALUES
  ( 13, 22 ), ( 14, 22), (15, 22), (16, 22), (57, 22), (33, 16), (34, 17), (35, 18), (15, 29);

ALTER SEQUENCE node_tbl_id_seq RESTART WITH 200;
SELECT SETVAL('sync_computed_point_input_tbl_id_seq', (SELECT MAX(id) FROM sync_computed_point_inputs) + 1);
SELECT SETVAL('sync_computed_point_input_transformer_tbl_id_seq', (SELECT MAX(id) FROM sync_computed_point_input_transformers) + 1);
SELECT SETVAL('temporal_async_computed_point_config_tbl_id_seq', (SELECT MAX(id) FROM temporal_async_computed_point_configs) + 1);

INSERT INTO point_point_templates(node_id, node_template_id) VALUES (402, 139);