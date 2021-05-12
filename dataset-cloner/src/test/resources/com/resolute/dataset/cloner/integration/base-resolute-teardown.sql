DELETE FROM ac_global_tags WHERE id >= 513350000;
DELETE FROM ac_customer_tags;
DELETE FROM export_jobs;
DELETE FROM customer_client_credentials;
DELETE FROM distributor_client_credentials;
DELETE FROM building_email_notifications;
DELETE FROM customer_email_notifications;
DELETE FROM distributor_email_notifications;
DELETE FROM perspective_widgets;
DELETE FROM w_gauge_points;
DELETE FROM w_gauge_ranges;
DELETE FROM w_gauges;
DELETE FROM w_datablocks;
DELETE FROM w_kpis;
DELETE FROM standard_perspective_customer_nodes;
DELETE FROM standard_perspective_customers;
DELETE FROM standard_perspectives;
DELETE FROM user_setting_tbl;
DELETE FROM customer_users;
DELETE FROM distributor_users;
DELETE FROM mappable_points WHERE id IN (5, 15);
DELETE FROM raw_points WHERE id  IN (1, 10);
DELETE FROM equipment WHERE id  IN (4, 14);
DELETE FROM floors WHERE id  IN (3, 13);
UPDATE buildings SET pending_deletion = TRUE WHERE id  IN (2, 12) AND status = 'ACTIVE';
DELETE FROM buildings WHERE id  IN (2, 12);
DELETE FROM portfolios WHERE id = 1;
DELETE FROM components WHERE id = 1;
UPDATE customers SET status = 'DELETED' WHERE id = 1;
DELETE FROM customers WHERE id = 1;
UPDATE online_distributors SET status = 'CREATED' WHERE id = 2;
UPDATE online_distributors SET status = 'DELETED' WHERE id = 2;
DELETE FROM online_distributors WHERE id = 2;

ALTER SEQUENCE distributor_tbl_id_seq RESTART WITH 2;
ALTER SEQUENCE customer_tbl_id_seq RESTART WITH 1;
ALTER SEQUENCE component_tbl_id_seq RESTART WITH 1;
ALTER SEQUENCE node_tbl_id_seq RESTART WITH 1;
ALTER SEQUENCE raw_point_tbl_id_seq RESTART WITH 1;
ALTER SEQUENCE user_tbl_id_seq RESTART WITH 1;
ALTER SEQUENCE perspective_tbl_id_seq RESTART WITH 1;
ALTER SEQUENCE widget_tbl_id_seq RESTART WITH 1;
ALTER SEQUENCE w_gauge_range_tbl_id_seq RESTART WITH 1;
ALTER SEQUENCE w_gauge_point_tbl_id_seq RESTART WITH 1;
ALTER SEQUENCE email_notification_tbl_id_seq RESTART WITH 1;
ALTER SEQUENCE client_credential_tbl_id_seq RESTART WITH 1;
ALTER SEQUENCE export_job_tbl_id_seq RESTART WITH 1;





