-----------------
-- REAL TABLES --
-----------------

INSERT INTO online_distributors (id, parent_id, name, referral_agent_id) VALUES (2, 1, 'Online Distributor', 1);

ALTER SEQUENCE distributor_tbl_id_seq RESTART WITH 3;    

INSERT INTO customers (id, distributor_id, name) VALUES 
   (1, 2, 'Studebaker');

ALTER SEQUENCE customer_tbl_id_seq RESTART WITH 2;    

INSERT INTO components (id, customer_id, component_type_id) VALUES (1, 1, 1);	 

ALTER SEQUENCE component_tbl_id_seq RESTART WITH 2;

INSERT INTO portfolios (id, customer_id, name) VALUES (1, 1, 'Studebaker');

INSERT INTO buildings (id, customer_id, parent_id, name, ruby_timezone_id) VALUES 
  (2, 1, 1, 'Building1', 43),
  (12, 1, 1, 'Building2', 43),
  (1012, 1, 1, 'Building3', 43);

INSERT INTO floors (id, customer_id, parent_id, name) VALUES 
  (3, 1, 2, 'Floor1'),
  (13, 1, 12, 'Floor2');

INSERT INTO equipment (id, customer_id, parent_id, name) VALUES 
  (4, 1, 3, 'HVAC1'),
  (14, 1, 13, 'HVAC2');

INSERT INTO raw_points (id, customer_id, component_id, metric_id, unit_type, point_type, range) VALUES 
  (1, 1, 1, 'Drivers/NiagaraNetwork/Building1/Floor1/HVAC1/ZoneTempDev', 'fahrenheit', 'NumericPoint', ''),
  (10, 1, 1, 'Drivers/NiagaraNetwork/Building2/Floor2/HVAC2/ZoneTempDev', 'fahrenheit', 'NumericPoint', '');

INSERT INTO mappable_points (id, customer_id, parent_id, name, data_type_id, metric_id, raw_point_id) VALUES 
  (5, 1, 4, 'ZoneTempDev1', 1, 'Drivers/NiagaraNetwork/Building1/Floor1/HVAC1/ZoneTempDev', 1),
  (15, 1, 14, 'ZoneTempDev2', 1, 'Drivers/NiagaraNetwork/Building2/Floor2/HVAC2/ZoneTempDev', 10);

ALTER SEQUENCE node_tbl_id_seq RESTART WITH 16;
ALTER SEQUENCE raw_point_tbl_id_seq RESTART WITH 11;

INSERT INTO customer_users (id, role_id, customer_id, email, encrypted_password, first_name, last_name, invitation_sent_at, invitation_accepted_at, last_sign_in_at) VALUES 
  (1, 1, 1, 'kdmiller@mclaren.com', 'password', 'Keith', 'Miller', '2016-05-13T09:58:24.738', '2016-05-16T09:38:39.688', '2016-05-16T09:38:39.688');

INSERT INTO distributor_users (id, role_id, distributor_id, email, encrypted_password, first_name, last_name) VALUES 
  (2, 5, 2, 'cdevoto@maddogtehcnology.com', 'passw0rd', 'Carlos', 'Devoto');

ALTER SEQUENCE user_tbl_id_seq RESTART WITH 3;
  
INSERT INTO user_setting_tbl ( user_id, ruby_timezone_id ) VALUES
	(1, 43),
	(2, 43);
	
INSERT INTO standard_perspectives (id, customer_id, name, display_name, description, template, user_visible, hide_summary) VALUES
  (1, 1, 'Perspective 1', 'My First Perspective', 'A simple perspective', false, true, false);

ALTER SEQUENCE perspective_tbl_id_seq RESTART WITH 2;

INSERT INTO standard_perspective_customers (perspective_id, customer_id, ordinal, visible_to_all) VALUES
  (1, 1, 1, false);

INSERT INTO standard_perspective_customer_nodes (perspective_id, node_id) VALUES
  (1, 2);  -- Building1
  
INSERT INTO w_kpis (id, customer_id, template, name, history_aggregator_id, point_id, precision, unit, prepend, append, low_threshold, high_threshold) VALUES 
   (1, 1, false, 'kWh per Col', 1, 5, 4, 'kWh', 'Test Prepend', 'Test Append', 5, 10);
  
INSERT INTO w_datablocks (id, customer_id, template, name, point_id, precision, prepend, append, background_color, text_color, humanize) VALUES 
   (2, 1, false, 'kWh per Hour', 15, 4, 'Test Prepend', 'Test Append', 'red', 'blue', 'cardinal8deg'); 

INSERT INTO w_gauges (id, customer_id, template, name, display_name, description, colwidth, help_text, min, max) VALUES 
  (3, 1, false, 'Test Gauge', 'Test Gauge', 'Test Description', 1, 'Test Help Text', 0, 50); 

ALTER SEQUENCE widget_tbl_id_seq RESTART WITH 4;

INSERT INTO w_gauge_ranges (id, widget_id, start, "end", color, label, short_label) VALUES
  (1, 3, 0, 50, 'green', 'Test Label', 'Test Short Label');

INSERT INTO w_gauge_points(id, widget_id, point_id, widget_point_type_id) VALUES
  (1, 3, 5, 5),
  (2, 3, 15, 6);

ALTER SEQUENCE w_gauge_range_tbl_id_seq RESTART WITH 2;
ALTER SEQUENCE w_gauge_point_tbl_id_seq RESTART WITH 3;

INSERT INTO perspective_widgets(perspective_id, widget_id, row_num, col_num, ordinal) VALUES
  (1, 1, 1, 1, 1),
  (1, 2, 1, 1, 2);

INSERT INTO distributor_email_notifications (id, type, distributor_id) VALUES (1, 'GRACE_PERIOD_WARNING', 2);  
INSERT INTO customer_email_notifications (id, type, customer_id) VALUES (2, 'GRACE_PERIOD_WARNING', 1); 
INSERT INTO building_email_notifications (id, type, building_id) VALUES 
  (3, 'GRACE_PERIOD_WARNING', 2),
  (4, 'GRACE_PERIOD_WARNING', 12);

ALTER SEQUENCE email_notification_tbl_id_seq RESTART WITH 5;

INSERT INTO distributor_client_credentials (id, client_id, client_secret, distributor_id) VALUES (1, 'client_id_1', 'xyzpdq', 2);    
INSERT INTO customer_client_credentials (id, client_id, client_secret, customer_id) VALUES (2, 'client_id_2', 'xyzpdq', 1);    

ALTER SEQUENCE client_credential_tbl_id_seq RESTART WITH 3;

INSERT INTO export_jobs (id, customer_id, export_job_status_id, export_job_type_id, creator_id, creator_email, name, config, attempts, url) VALUES
  (1, 1, 3, 1, 1, 'kdmiller@mclaren.com', 'Export Chart Data: Tue, 14 Jul 2020 13:13', '{"metrics":[{"metricId":"weather/Station/KHYX/OatCurrent","aggregator":"avg","pointId":118023,"pointTypeId":3}],"start":1590724800,"end":1593489599,"downSamplingInterval":"1d","timezone":"America/New_York","unifyOutputFiles":true}', 1, 'https://s3.amazonaws.com/resolute-prod/export/export-job-3629-2020-07-14_13-13-42-648.zip'),
  (2, 1, 3, 1, 2, 'cdevoto@maddogtehcnology.com', 'Export Chart Data: Tue, 14 Jul 2020 13:02', '{"metrics":[{"metricId":"weather/Station/KHYX/OatCurrent","aggregator":"avg","pointId":118023,"pointTypeId":3}],"start":1590724800,"end":1593575999,"downSamplingInterval":"1d","timezone":"America/New_York","unifyOutputFiles":true}', 1, 'https://s3.amazonaws.com/resolute-prod/export/export-job-3628-2020-07-14_13-03-04-573.zip');

ALTER SEQUENCE export_job_tbl_id_seq RESTART WITH 3;

INSERT INTO ac_customer_tags (id, name, customer_id) VALUES (513350000, 'Customer Tag 1', 1);  
INSERT INTO ac_global_tags (id, name) VALUES (513350001, 'Global Tag 1');   