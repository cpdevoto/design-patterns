package com.resolute.test.dataset.cloner;

import static com.resolute.test.dataset.cloner.Table.AD_RULE_INSTANCES;
import static com.resolute.test.dataset.cloner.Table.AD_RULE_INSTANCE_INPUT_CONSTS;
import static com.resolute.test.dataset.cloner.Table.AD_RULE_INSTANCE_INPUT_POINTS;
import static com.resolute.test.dataset.cloner.Table.AD_RULE_INSTANCE_OUTPUT_POINTS;
import static com.resolute.test.dataset.cloner.Table.ASYNC_COMPUTED_POINTS;
import static com.resolute.test.dataset.cloner.Table.ASYNC_COMPUTED_POINT_CONFIGS;
import static com.resolute.test.dataset.cloner.Table.BUILDINGS;
import static com.resolute.test.dataset.cloner.Table.CUSTOMERS;
import static com.resolute.test.dataset.cloner.Table.CUSTOMER_TIMEZONES;
import static com.resolute.test.dataset.cloner.Table.CUSTOMER_UTILITIES;
import static com.resolute.test.dataset.cloner.Table.EQUIPMENT;
import static com.resolute.test.dataset.cloner.Table.FLOORS;
import static com.resolute.test.dataset.cloner.Table.MAPPABLE_POINTS;
import static com.resolute.test.dataset.cloner.Table.METERS;
import static com.resolute.test.dataset.cloner.Table.NODE_TAGS;
import static com.resolute.test.dataset.cloner.Table.PORTFOLIOS;
import static com.resolute.test.dataset.cloner.Table.RAW_POINTS;
import static com.resolute.test.dataset.cloner.Table.RECURRENCE_RULES;
import static com.resolute.test.dataset.cloner.Table.RECURRENCE_RULE_EXCEPTIONS;
import static com.resolute.test.dataset.cloner.Table.SCHEDULED_ASYNC_COMPUTED_POINTS;
import static com.resolute.test.dataset.cloner.Table.SCHEDULED_EVENTS;
import static com.resolute.test.dataset.cloner.Table.SITES;
import static com.resolute.test.dataset.cloner.Table.SUB_BUILDINGS;
import static com.resolute.test.dataset.cloner.Table.SYNC_COMPUTED_POINTS;
import static com.resolute.test.dataset.cloner.Table.SYNC_COMPUTED_POINT_INPUTS;
import static com.resolute.test.dataset.cloner.Table.SYNC_COMPUTED_POINT_INPUT_TRANSFORMERS;
import static com.resolute.test.dataset.cloner.Table.TEMPORAL_ASYNC_COMPUTED_POINT_CONFIGS;
import static com.resolute.test.dataset.cloner.Table.TEMPORAL_ASYNC_COMPUTED_POINT_VARS;
import static com.resolute.test.dataset.cloner.Table.ZONES;

import org.junit.Test;

import com.resolute.dataset.cloner.Cloner;
import com.resolute.dataset.cloner.KeyMaps;

public class ClonerTest extends AbstractCloneOperationTest {


  @Test
  public void test_cloner() {
    // There are no assertions in this test; if an exception is thrown, it denotes either a problem
    // with the library (if changes to the library have been made), or a change in the database
    // structure (if no changes to the library have been made).
    int sourceCustomerId = 1;
    String customerNameSuffix = " 2";

    KeyMaps keyMaps = KeyMaps.forTables(CUSTOMERS, PORTFOLIOS, SITES, BUILDINGS, SUB_BUILDINGS,
        FLOORS, ZONES, METERS, EQUIPMENT, RAW_POINTS, MAPPABLE_POINTS, ASYNC_COMPUTED_POINTS,
        SCHEDULED_ASYNC_COMPUTED_POINTS, SYNC_COMPUTED_POINTS,
        TEMPORAL_ASYNC_COMPUTED_POINT_CONFIGS, SYNC_COMPUTED_POINT_INPUTS, SCHEDULED_EVENTS,
        AD_RULE_INSTANCES);

    Cloner cloner = Cloner.getInstance(dataSource, keyMaps);

    cloner.clone(CUSTOMERS)
        .withSelectSpecification(spec -> spec.fieldFilter("id", sourceCustomerId))
        .withInsertSpecification(spec -> spec.updateKeyMap()
            .skipFields("uuid", "key", "created_at", "updated_at")
            .mutateField("name", value -> value + customerNameSuffix))
        .execute();

    int customerId = keyMaps.get(CUSTOMERS).getTargetIds().iterator().next();

    int componentId = statementFactory.newStatement()
        .withSql("INSERT INTO components (customer_id, component_type_id, name) VALUES ("
            + customerId + ", 6, 'Cloudfill') RETURNING id")
        .withErrorMessage("A problem occurred while attempting to insert a component")
        .executeQuery(result -> result.toObject((idx, rs) -> rs.getInt("id")));

    cloner.clone(CUSTOMER_TIMEZONES)
        .withSelectSpecification(spec -> spec.foreignKeyFilter("customer_id", CUSTOMERS))
        .withInsertSpecification(spec -> spec.skipFields("id", "created_at", "updated_at"))
        .execute();

    cloner.clone(CUSTOMER_UTILITIES)
        .withSelectSpecification(spec -> spec.foreignKeyFilter("customer_id", CUSTOMERS))
        .execute();

    cloner.clone(PORTFOLIOS)
        .withSelectSpecification(spec -> spec.foreignKeyFilter("customer_id", CUSTOMERS))
        .withInsertSpecification(spec -> spec.updateKeyMap()
            .skipFields("uuid", "created_at", "updated_at")
            .mutateField("name", value -> value + customerNameSuffix)
            .mutateField("display_name", value -> value + customerNameSuffix))
        .execute();

    cloner.clone(SITES)
        .withSelectSpecification(spec -> spec.foreignKeyFilter("customer_id", CUSTOMERS))
        .withInsertSpecification(spec -> spec.updateKeyMap()
            .skipFields("uuid", "created_at", "updated_at")
            .foreignKeyRef("parent_id", PORTFOLIOS))
        .execute();

    cloner.clone(BUILDINGS)
        .withSelectSpecification(spec -> spec.foreignKeyFilter("customer_id", CUSTOMERS))
        .withInsertSpecification(spec -> spec.updateKeyMap()
            .skipFields("uuid", "created_at", "updated_at")
            .foreignKeyRef("parent_id", PORTFOLIOS, SITES))
        .execute();

    cloner.clone(SUB_BUILDINGS)
        .withSelectSpecification(spec -> spec.foreignKeyFilter("customer_id", CUSTOMERS))
        .withInsertSpecification(spec -> spec.updateKeyMap()
            .skipFields("uuid", "created_at", "updated_at")
            .foreignKeyRef("parent_id", BUILDINGS))
        .execute();

    cloner.clone(FLOORS)
        .withSelectSpecification(spec -> spec.foreignKeyFilter("customer_id", CUSTOMERS))
        .withInsertSpecification(spec -> spec.updateKeyMap()
            .skipFields("uuid", "created_at", "updated_at")
            .foreignKeyRef("parent_id", BUILDINGS, SUB_BUILDINGS))
        .execute();

    cloner.clone(ZONES)
        .withSelectSpecification(spec -> spec.foreignKeyFilter("customer_id", CUSTOMERS))
        .withInsertSpecification(spec -> spec.updateKeyMap()
            .skipFields("uuid", "created_at", "updated_at")
            .foreignKeyRef("parent_id", FLOORS))
        .execute();

    cloner.clone(METERS)
        .withSelectSpecification(spec -> spec.foreignKeyFilter("customer_id", CUSTOMERS))
        .withInsertSpecification(spec -> spec.updateKeyMap()
            .skipFields("uuid", "created_at", "updated_at")
            .foreignKeyRef("parent_id", SITES, BUILDINGS, SUB_BUILDINGS, FLOORS, ZONES, METERS))
        .execute();

    cloner.clone(EQUIPMENT)
        .withSelectSpecification(spec -> spec.foreignKeyFilter("customer_id", CUSTOMERS))
        .withInsertSpecification(spec -> spec.updateKeyMap()
            .skipFields("uuid", "created_at", "updated_at")
            .foreignKeyRef("parent_equipment_id", EQUIPMENT)
            .foreignKeyRef("parent_id", SITES, BUILDINGS, SUB_BUILDINGS, FLOORS, ZONES, METERS,
                EQUIPMENT))
        .execute();

    cloner.clone(RAW_POINTS)
        .withSelectSpecification(spec -> spec.foreignKeyFilter("customer_id", CUSTOMERS))
        .withInsertSpecification(spec -> spec.updateKeyMap()
            .skipFields("created_at", "updated_at")
            .mutateField("component_id", value -> componentId))
        .execute();

    cloner.clone(MAPPABLE_POINTS)
        .withSelectSpecification(spec -> spec.foreignKeyFilter("customer_id", CUSTOMERS))
        .withInsertSpecification(spec -> spec.updateKeyMap()
            .skipFields("uuid", "created_at", "updated_at")
            .foreignKeyRef("raw_point_id", RAW_POINTS)
            .foreignKeyRef("parent_id", PORTFOLIOS, SITES, BUILDINGS, SUB_BUILDINGS, FLOORS, ZONES,
                METERS, EQUIPMENT))
        .execute();

    cloner.clone(ASYNC_COMPUTED_POINTS)
        .withSelectSpecification(spec -> spec.foreignKeyFilter("customer_id", CUSTOMERS))
        .withInsertSpecification(spec -> spec.updateKeyMap()
            .skipFields("uuid", "created_at", "updated_at")
            .foreignKeyRef("parent_id", PORTFOLIOS, SITES, BUILDINGS, SUB_BUILDINGS, FLOORS, ZONES,
                METERS, EQUIPMENT))
        .execute();

    cloner.clone(ASYNC_COMPUTED_POINT_CONFIGS)
        .withSelectSpecification(spec -> spec.foreignKeyFilter("id", ASYNC_COMPUTED_POINTS))
        .execute();

    cloner.clone(TEMPORAL_ASYNC_COMPUTED_POINT_CONFIGS)
        .withSelectSpecification(
            spec -> spec.foreignKeyFilter("async_computed_point_config_id", ASYNC_COMPUTED_POINTS))
        .withInsertSpecification(spec -> spec.updateKeyMap())
        .execute();

    cloner.clone(TEMPORAL_ASYNC_COMPUTED_POINT_VARS)
        .withSelectSpecification(
            spec -> spec.foreignKeyFilter("temporal_async_computed_point_config_id",
                TEMPORAL_ASYNC_COMPUTED_POINT_CONFIGS))
        .withInsertSpecification(spec -> spec.foreignKeyRef("point_id", MAPPABLE_POINTS))
        .execute();

    cloner.clone(SCHEDULED_ASYNC_COMPUTED_POINTS)
        .withSelectSpecification(spec -> spec.foreignKeyFilter("customer_id", CUSTOMERS))
        .withInsertSpecification(spec -> spec.updateKeyMap()
            .skipFields("uuid", "created_at", "updated_at")
            .foreignKeyRef("parent_id", PORTFOLIOS, SITES, BUILDINGS, SUB_BUILDINGS, FLOORS, ZONES,
                METERS, EQUIPMENT))
        .execute();

    cloner.clone(SCHEDULED_EVENTS)
        .withSelectSpecification(
            spec -> spec.foreignKeyFilter("point_id", SCHEDULED_ASYNC_COMPUTED_POINTS))
        .withInsertSpecification(spec -> spec.updateKeyMap())
        .execute();

    cloner.clone(RECURRENCE_RULES)
        .withSelectSpecification(
            spec -> spec.foreignKeyFilter("id", SCHEDULED_EVENTS))
        .execute();

    cloner.clone(RECURRENCE_RULE_EXCEPTIONS)
        .withSelectSpecification(
            spec -> spec.foreignKeyFilter("recurrence_rule_id", SCHEDULED_EVENTS))
        .withInsertSpecification(spec -> spec.skipFields("id"))
        .execute();

    cloner.clone(SYNC_COMPUTED_POINTS)
        .withSelectSpecification(spec -> spec.foreignKeyFilter("customer_id", CUSTOMERS))
        .withInsertSpecification(spec -> spec.updateKeyMap()
            .skipFields("uuid", "created_at", "updated_at")
            .foreignKeyRef("parent_id", PORTFOLIOS, SITES, BUILDINGS, SUB_BUILDINGS, FLOORS, ZONES,
                METERS, EQUIPMENT))
        .execute();

    cloner.clone(SYNC_COMPUTED_POINT_INPUTS)
        .withSelectSpecification(
            spec -> spec.foreignKeyFilter("sync_computed_point_id", SYNC_COMPUTED_POINTS))
        .withInsertSpecification(spec -> spec.updateKeyMap()
            .foreignKeyRef("input_point_id", MAPPABLE_POINTS, ASYNC_COMPUTED_POINTS,
                SCHEDULED_ASYNC_COMPUTED_POINTS))
        .execute();

    cloner.clone(SYNC_COMPUTED_POINT_INPUT_TRANSFORMERS)
        .withSelectSpecification(
            spec -> spec.foreignKeyFilter("sync_computed_point_input_id",
                SYNC_COMPUTED_POINT_INPUTS))
        .withInsertSpecification(spec -> spec.skipFields("id"))
        .execute();

    cloner.clone(NODE_TAGS)
        .withSelectSpecification(
            spec -> spec.foreignKeyFilter("customer_id", CUSTOMERS)
                .sql("SELECT node_id, tag_id FROM nodes JOIN node_tags ON id = node_id"))
        .withInsertSpecification(spec -> spec
            .foreignKeyRef("node_id", PORTFOLIOS, SITES, BUILDINGS, SUB_BUILDINGS, FLOORS, ZONES,
                METERS, EQUIPMENT, MAPPABLE_POINTS, ASYNC_COMPUTED_POINTS,
                SCHEDULED_ASYNC_COMPUTED_POINTS))
        .execute();

    cloner.clone(AD_RULE_INSTANCES)
        .withSelectSpecification(spec -> spec.foreignKeyFilter("customer_id", CUSTOMERS))
        .withInsertSpecification(spec -> spec.updateKeyMap()
            .foreignKeyRef("node_id", EQUIPMENT))
        .execute();

    cloner.clone(AD_RULE_INSTANCE_INPUT_POINTS)
        .withSelectSpecification(
            spec -> spec.foreignKeyFilter("ad_rule_instance_id", AD_RULE_INSTANCES))
        .withInsertSpecification(spec -> spec
            .foreignKeyRef("point_id", MAPPABLE_POINTS))
        .execute();

    cloner.clone(AD_RULE_INSTANCE_INPUT_CONSTS)
        .withSelectSpecification(
            spec -> spec.foreignKeyFilter("ad_rule_instance_id", AD_RULE_INSTANCES))
        .execute();

    cloner.clone(AD_RULE_INSTANCE_OUTPUT_POINTS)
        .withSelectSpecification(
            spec -> spec.foreignKeyFilter("ad_rule_instance_id", AD_RULE_INSTANCES))
        .withInsertSpecification(spec -> spec
            .foreignKeyRef("point_id", ASYNC_COMPUTED_POINTS))
        .execute();

  }
}
