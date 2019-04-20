DELETE FROM ad_rule_instance_output_points WHERE ad_rule_instance_id IN (SELECT id FROM ad_rule_instances);
DELETE FROM ad_rule_instance_input_consts WHERE ad_rule_instance_id IN (SELECT id FROM ad_rule_instances);
DELETE FROM ad_rule_instance_input_points WHERE ad_rule_instance_id IN (SELECT id FROM ad_rule_instances);
DELETE FROM ad_rule_instances WHERE id IN (SELECT id FROM ad_rule_instances);
DELETE FROM async_computed_points WHERE id IN (SELECT point_id FROM ad_rule_instance_output_points WHERE ad_rule_instance_id IN (SELECT id FROM ad_rule_instances));

DELETE FROM ad_rule_instance_candidate_tbl WHERE rule_template_id IN (51335, 51336, 51337, 51338);
DELETE FROM ad_rule_template_output_point_tag_tbl WHERE ad_rule_template_output_point_id IN (51335, 51336, 51337, 51338);
DELETE FROM ad_rule_template_output_point_tbl WHERE id IN (51335, 51336, 51337, 51338);
DELETE FROM ad_rule_template_input_const_tbl WHERE id IN (51335, 51336, 51337, 51338, 51339, 51340);
DELETE FROM ad_rule_template_input_point_tag_tbl WHERE ad_rule_template_input_point_id IN (51336, 51337, 51338, 51339, 51340, 51341);
DELETE FROM ad_rule_template_input_point_tbl WHERE id IN (51335, 51336, 51337, 51338, 51339, 51340, 51341);
DELETE FROM ad_rule_template_tag_tbl WHERE ad_rule_template_id IN (51335, 51336, 51337, 51338);
DELETE FROM ad_rule_template_tbl WHERE id IN (51335, 51336, 51337, 51338);
DELETE FROM ad_rule_tbl WHERE id IN (51335, 51336);

DELETE FROM node_tags WHERE node_id IN (51335, 51337, 51338, 51339, 51340, 51341, 51343, 51344, 51345);
DELETE FROM async_computed_points WHERE id IN (51339, 51340);
DELETE FROM mappable_points WHERE id IN (51336, 51337, 51338, 51535, 51536, 51537, 51538, 51343, 51344);
DELETE FROM raw_points WHERE id IN (51336, 51337, 51338, 51535, 51536, 51537, 51339, 51340);
DELETE FROM equipment WHERE id IN (51335, 51341, 51342, 51437, 51345);
DELETE FROM tag_tbl WHERE id IN (51338);
DELETE FROM buildings WHERE id IN (51436);
DELETE FROM portfolios WHERE id IN (51435);
DELETE FROM components WHERE id IN (51435);
DELETE FROM customers WHERE id IN (51435);