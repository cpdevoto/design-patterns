DELETE FROM user_nodes;
DELETE FROM user_features;
DELETE FROM user_settings;
DELETE FROM distributor_users;
DELETE FROM customer_users;
DELETE FROM user_tbl;

DELETE FROM ad_rule_instances;
DELETE FROM ad_rule_template_tbl;

DELETE FROM point_point_templates;
DELETE FROM meters;
DELETE FROM node_tags;
DELETE FROM sync_computed_point_input_transformers;
DELETE FROM sync_computed_point_inputs;
DELETE FROM sync_computed_points;
DELETE FROM async_computed_points;
DELETE FROM mappable_points;
DELETE FROM raw_points;
DELETE FROM buildings;
DELETE FROM sites;
DELETE FROM portfolios;
DELETE FROM customers;
DELETE FROM distributors WHERE parent_id = 1; 

DELETE FROM weather_stations;
DELETE FROM temporal_billing_locations;
DELETE FROM temporal_billing_location_utilities;
