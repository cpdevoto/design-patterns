INSERT INTO customers (id, distributor_id, uuid, name, resolute_start_date, fiscal_year_start) VALUES 
  (51435, 1, 'b558664d-fcf7-48e8-9807-e7a7614f22eb', 'Redico', '2014-05-01T00:00:00.000', 3);
  
INSERT INTO components (id, customer_id, component_type_id) VALUES (51435, 51435, 1);  

INSERT INTO portfolios (id, customer_id, name) VALUES (51435, 51435, 'Redico');

INSERT INTO buildings (id, customer_id, parent_id, name) VALUES (51436, 51435, 51435, 'Building1');
  
INSERT INTO tag_tbl (id, tag_group_id, tag_type_id, name, ui_inferred, scoped_to_constraint) VALUES 
  (51338, 6, 1, 'ad_rule_output', false, null);

INSERT INTO equipment (id, customer_id, parent_id, parent_equipment_id, name) VALUES 
   (51335, 1, 3, null, 'AHU1'),
   (51341, 1, 3, 51335, 'AHU2'),
   (51342, 1, 3, 51335, 'VAV1'),
   (51437, 51435, 51436, null, 'AHU1'),
   (51345, 1, 3, null, 'AHU4');

INSERT INTO raw_points (id, customer_id, component_id, metric_id, unit_type, point_type, "value", range, value_timestamp) VALUES
  (51336, 1, 1, '/Drivers/NiagaraNetwork/Mclaren/Bldg1/points/ElecMeters/BldgMeter/kWH_IntHour', 'kWh', 'NumericPoint', 98.35546875, '', '2016-05-13T09:58:24.738'),
  (51337, 1, 1, '/Drivers/NiagaraNetwork/Mclaren/Bldg1/points/AHU1/CoolingValveCmd', '', 'BooleanPoint', 1.0, '{"trueText":"On","falseText":"Off"}', '2016-05-13T09:58:24.738'),
  (51338, 1, 1, '/Drivers/NiagaraNetwork/Mclaren/Bldg1/points/AHU1/HeatingValveCmd', '', 'BooleanPoint', 1.0, '{"trueText":"On","falseText":"Off"}', '2016-05-13T09:58:24.738'),
  (51535, 1, 1, '/Drivers/NiagaraNetwork/Mclaren/Bldg1/points/ElecMeters/AHU2/kWH_IntHour', 'kWh', 'NumericPoint', 98.35546875, '', '2016-05-13T09:58:24.738'),
  (51536, 51435, 1, '/Drivers/NiagaraNetwork/Redico/Bldg1/points/ElecMeters/AHU1/kWH_IntHour', 'kWh', 'NumericPoint', 98.35546875, '', '2016-05-13T09:58:24.738'),
  (51537, 1, 1, '/Drivers/NiagaraNetwork/Mclaren/Bldg1/points/ElecMeters/BldgMeter2/kWH_IntHour', 'kWh', 'NumericPoint', 98.35546875, '', '2016-05-13T09:58:24.738'),
  (51339, 1, 1, '/Drivers/NiagaraNetwork/Mclaren/Bldg1/points/AHU1/CoolingValveCmd2', '', 'BooleanPoint', 1.0, '{"trueText":"On","falseText":"Off"}', '2016-05-13T09:58:24.738'),
  (51340, 1, 1, '/Drivers/NiagaraNetwork/Mclaren/Bldg1/points/AHU1/CoolingValveCmd3', '', 'BooleanPoint', 1.0, '{"trueText":"On","falseText":"Off"}', '2016-05-13T09:58:24.738');
  
INSERT INTO mappable_points (id, customer_id, parent_id, name, data_type_id, metric_id, unit_type, range, raw_point_id) VALUES 
  (51336, 1, 51335, 'kWH_IntHour', 1, '/Drivers/NiagaraNetwork/Mclaren/Bldg1/points/ElecMeters/BldgMeter/kWH_IntHour', 'kWh', null, 51336),
  (51337, 1, 51335, 'CoolingValveCmd', 2, '/Drivers/NiagaraNetwork/Mclaren/Bldg1/points/AHU1/CoolingValveCmd', '', '{"trueText":"On","falseText":"Off"}', 51337),
  (51338, 1, 51335, 'HeatingValveCmd', 2, '/Drivers/NiagaraNetwork/Mclaren/Bldg1/points/AHU1/HeatingValveCmd', '', '{"trueText":"On","falseText":"Off"}', 51338),
  (51535, 1, 51341, 'kWH_IntHour', 1, '/Drivers/NiagaraNetwork/Mclaren/Bldg1/points/ElecMeters/AHU2/kWH_IntHour', 'kWh', null, 51535),
  (51536, 51435, 51437, 'kWH_IntHour', 1, '/Drivers/NiagaraNetwork/Redico/Bldg1/points/ElecMeters/AHU2/kWH_IntHour', 'kWh', null, 51536),
  (51537, 1, 51341, 'kWH_IntHour2', 1, '/Drivers/NiagaraNetwork/Redico/Bldg1/points/ElecMeters/AHU2/kWH_IntHour2', 'kWh', null, null),
  (51538, 1, 51335, 'kWH_IntHour2', 1, '/Drivers/NiagaraNetwork/Mclaren/Bldg1/points/ElecMeters/BldgMeter2/kWH_IntHour', 'kWh', null, 51537),
  (51343, 1, 51345, 'CoolingValveCmd2', 2, '/Drivers/NiagaraNetwork/Mclaren/Bldg1/points/AHU1/CoolingValveCmd2', '', '{"trueText":"On","falseText":"Off"}', 51339),
  (51344, 1, 51345, 'CoolingValveCmd3', 2, '/Drivers/NiagaraNetwork/Mclaren/Bldg1/points/AHU1/CoolingValveCmd3', '', '{"trueText":"On","falseText":"Off"}', 51340);
  
INSERT INTO async_computed_points (id, customer_id, parent_id, configurable, name, display_name, data_type_id, metric_id, unit_type, range) VALUES 
  (51339, 1, 51335, FALSE, 'AD_51335_51335', 'AD_51335_51335', 2, '/Resolute/Computed/AD_Invariant_51335_51335', '', '{"trueText":"On","falseText":"Off"}'),
  (51340, 1, 51335, FALSE, 'AD_51336_51336', 'AD_51336_51336', 2, '/Resolute/Computed/AD_Simlutaneous_Heating_Cooling_51336_51336', '', '{"trueText":"On","falseText":"Off"}');

INSERT INTO node_tags (node_id, tag_id) VALUES 
  (51335, 42),
  (51335, 123),
  (51337, 51),
  (51338, 50),
  (51339, 51338),
  (51340, 51338),
  (51341, 42),
  (51342, 43),
  (51343, 52),
  (51344, 52),
  (51345, 42);

INSERT INTO ad_rule_tbl (id, name, description) VALUES
  (51335, 'Invariant2', 'Computes an invariant rule for a specified point'),
  (51336, 'Simultaneous Heating and Cooling2', 'Computes a simultaneous heating and cooling rule for a specified point');

INSERT INTO ad_rule_template_tbl (id, ad_rule_id, name, display_name, description, node_filter_expression) VALUES
  (51335, 51335, 'Invariant2', 'Invariant', 'Computes an invariant rule for a specified point', NULL),
  (51336, 51336, 'Simultaneous_Heating_Cooling2', 'Simultaneous Heating and Cooling', 'Computes a simultaneous heating and cooling rule for a specified point', 'dxCool'),
  (51337, 51336, 'Simultaneous_Heating_Cooling3', 'Simultaneous Heating and Cooling', 'Computes a simultaneous heating and cooling rule for a specified point', NULL),
  (51338, 51336, 'Simultaneous_Heating_Cooling4', 'Simultaneous Heating and Cooling', 'Computes a simultaneous heating and cooling rule for a specified point', NULL);

INSERT INTO ad_rule_template_tag_tbl (ad_rule_template_id, tag_id) VALUES
  (51335, 42),
  (51336, 42),
  (51337, 43),
  (51338, 42);
  
INSERT INTO ad_rule_template_input_point_tbl (id, ad_rule_template_id, seq_no, name, description, current_object_expression, is_required, is_array) VALUES
  (51335, 51335, 1, 'A_numeric_point', 'A numeric point', NULL, TRUE, FALSE),
  (51336, 51336, 1, 'A_cooling_point', 'A cooling point', NULL, TRUE, FALSE),
  (51337, 51336, 2, 'A_heating_point', 'A heating point', NULL, TRUE, FALSE),
  (51338, 51337, 1, 'A_cooling_point', 'A cooling point', 'ancestorEquipment(tags=ahu|dxCool)', TRUE, FALSE),
  (51339, 51337, 2, 'A_heating_point', 'A heating point', 'ancestorEquipment(tags=ahu|dxCool)', TRUE, FALSE),
  (51340, 51338, 1, 'A_cooling_point', 'A cooling point', NULL, FALSE, TRUE),
  (51341, 51338, 2, 'A_heating_point', 'A heating point', NULL, TRUE, TRUE);
  
INSERT INTO ad_rule_template_input_point_tag_tbl (ad_rule_template_input_point_id, tag_id) VALUES
  (51336, 51),
  (51337, 50),
  (51338, 51),
  (51339, 50),
  (51340, 52),
  (51341, 50);
  
INSERT INTO ad_rule_template_input_const_tbl (id, ad_rule_template_id, data_type_id, unit_id, seq_no, name, description, default_value, is_required) VALUES
  (51335, 51335, 1, 1, 1, 'The_low_threshold', 'The low threshold', NULL, TRUE),
  (51336, 51335, 1, 1, 2, 'The_high_threshold', 'The high threshold', NULL, TRUE),
  (51337, 51335, 1, 1, 3, 'The_delay_in_minutes', 'The delay in minutes', '15', TRUE),
  (51338, 51336, 1, 1, 1, 'The_delay_in_minutes', 'The delay in minutes', '15', TRUE),
  (51339, 51337, 1, 1, 1, 'The_delay_in_minutes', 'The delay in minutes', '15', TRUE),
  (51340, 51338, 1, 1, 1, 'The_delay_in_minutes', 'The delay in minutes', '15', FALSE);
  
INSERT INTO ad_rule_template_output_point_tbl (id, ad_rule_template_id, data_type_id, unit_id, seq_no, description, range) VALUES
  (51335, 51335, 2, 1, 1, 'The anomaly detection rule output', '{"trueText":"On","falseText":"Off"}'),
  (51336, 51336, 2, 1, 1, 'The anomaly detection rule output', '{"trueText":"On","falseText":"Off"}'),
  (51337, 51337, 2, 1, 1, 'The anomaly detection rule output', '{"trueText":"On","falseText":"Off"}'),
  (51338, 51338, 2, 1, 1, 'The anomaly detection rule output', '{"trueText":"On","falseText":"Off"}');
  
INSERT INTO ad_rule_template_output_point_tag_tbl (ad_rule_template_output_point_id, tag_id) VALUES
  (51335, 51338),
  (51336, 51338),
  (51337, 51338),
  (51338, 51338);  

INSERT INTO ad_rule_instances (id, ad_rule_template_id, customer_id, node_id, active) VALUES
  (51335, 51335, 1, 51335, TRUE),
  (51336, 51336, 1, 51335, TRUE);
  
INSERT INTO ad_rule_instance_input_points (ad_rule_instance_id, ad_rule_template_input_point_id, point_id) VALUES
  (51335, 51335, 51336),
  (51336, 51336, 51337),
  (51336, 51337, 51338);

INSERT INTO ad_rule_instance_input_consts (ad_rule_instance_id, ad_rule_template_input_const_id, value) VALUES
  (51335, 51335, '50.0'),
  (51335, 51336, '110.0'),
  (51335, 51337, '15.0'),
  (51336, 51338, '15.0');
  
INSERT INTO ad_rule_instance_output_points (ad_rule_instance_id, ad_rule_template_output_point_id, point_id) VALUES
  (51335, 51335, 51339),
  (51336, 51336, 51340);  