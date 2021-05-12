package com.resolute.dataset.cloner.utils;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import com.google.common.collect.ImmutableList;
import com.resolute.database.crawler.model.Edge;
import com.resolute.database.crawler.model.Field;
import com.resolute.database.crawler.model.ForeignKey;
import com.resolute.database.crawler.model.ForeignKeyField;
import com.resolute.database.crawler.model.Node;
import com.resolute.dataset.cloner.integration.AbstractDatabaseTest;

public abstract class AbstractNodeUtilsTest extends AbstractDatabaseTest {

  @Test
  void test_get_foreign_key() {
    Node node = schemaGraph.getNode("node_tbl2").get();

    Optional<Edge> optEdge;
    Edge edge;
    ForeignKey foreignKey;
    ForeignKeyField foreignKeyField;


    optEdge = NodeUtils.getForeignKey(node, "customer_id");
    assertThat(optEdge).isPresent();
    edge = optEdge.get();
    assertThat(edge.getFrom().getName().equals("customer_tbl"));
    assertThat(edge.getTo().getName().equals("node_tbl2"));
    foreignKey = edge.getForeignKey();
    assertThat(foreignKey.getFields()).hasSize(1);
    foreignKeyField = foreignKey.getFields().get(0);
    assertThat(foreignKeyField.getFromField()).isEqualTo("id");
    assertThat(foreignKeyField.getToField()).isEqualTo("customer_id");

    optEdge = NodeUtils.getForeignKey(node, "node_type_id");
    assertThat(optEdge).isPresent();
    edge = optEdge.get();
    assertThat(edge.getFrom().getName().equals("node_type_tbl"));
    assertThat(edge.getTo().getName().equals("node_tbl2"));
    foreignKey = edge.getForeignKey();
    assertThat(foreignKey.getFields()).hasSize(1);
    foreignKeyField = foreignKey.getFields().get(0);
    assertThat(foreignKeyField.getFromField()).isEqualTo("id");
    assertThat(foreignKeyField.getToField()).isEqualTo("node_type_id");

    optEdge = NodeUtils.getForeignKey(node, "parent_id");
    assertThat(optEdge).isPresent();
    edge = optEdge.get();
    assertThat(edge.getFrom().getName().equals("node_tbl2"));
    assertThat(edge.getTo().getName().equals("node_tbl2"));
    foreignKey = edge.getForeignKey();
    assertThat(foreignKey.getFields()).hasSize(1);
    foreignKeyField = foreignKey.getFields().get(0);
    assertThat(foreignKeyField.getFromField()).isEqualTo("id");
    assertThat(foreignKeyField.getToField()).isEqualTo("parent_id");

    optEdge = NodeUtils.getForeignKey(node, "name");
    assertThat(optEdge).isEmpty();

    node = schemaGraph.getNode("test3_tbl").get();

    optEdge = NodeUtils.getForeignKey(node, "test2_id");
    assertThat(optEdge).isPresent();
    edge = optEdge.get();
    assertThat(edge.getFrom().getName().equals("test2_tbl"));
    assertThat(edge.getTo().getName().equals("test3_tbl"));
    foreignKey = edge.getForeignKey();
    assertThat(foreignKey.getFields()).hasSize(1);
    foreignKeyField = foreignKey.getFields().get(0);
    assertThat(foreignKeyField.getFromField()).isEqualTo("id");
    assertThat(foreignKeyField.getToField()).isEqualTo("test2_id");

  }


  @Test
  void test_get_unique_indeces() {
    Node node = schemaGraph.getNode("node_tbl2").get();

    List<List<Field>> indeces;
    List<Field> index;
    Field field;

    indeces = NodeUtils.getUniqueIndeces(node, "uuid");
    assertThat(indeces).hasSize(1);
    index = indeces.get(0);
    assertThat(index).hasSize(1);
    field = index.get(0);
    assertThat(field.getName()).isEqualTo("uuid");

    indeces = NodeUtils.getUniqueIndeces(node, "parent_id");
    assertThat(indeces).hasSize(1);
    index = indeces.get(0);
    assertThat(index).hasSize(2);
    field = index.get(0);
    assertThat(field.getName()).isEqualTo("parent_id");
    field = index.get(1);
    assertThat(field.getName()).isEqualTo("name");

    indeces = NodeUtils.getUniqueIndeces(node, "name");
    assertThat(indeces).hasSize(1);
    index = indeces.get(0);
    assertThat(index).hasSize(2);
    field = index.get(0);
    assertThat(field.getName()).isEqualTo("parent_id");
    field = index.get(1);
    assertThat(field.getName()).isEqualTo("name");

    indeces = NodeUtils.getUniqueIndeces(node, "id");
    assertThat(indeces).hasSize(0);

    indeces = NodeUtils.getUniqueIndeces(node, "display_name");
    assertThat(indeces).hasSize(0);
  }

  @Test
  void test_has_primary_key_field() {
    Node node = schemaGraph.getNode("node_tbl2").get();

    List<Field> fields;

    fields = ImmutableList.of("id", "customer_id").stream()
        .map(fieldName -> node.getField(fieldName).get())
        .collect(Collectors.toList());

    assertThat(NodeUtils.hasPrimaryKeyField(node, fields)).isTrue();

    fields = ImmutableList.of("parent_id", "name").stream()
        .map(fieldName -> node.getField(fieldName).get())
        .collect(Collectors.toList());

    assertThat(NodeUtils.hasPrimaryKeyField(node, fields)).isFalse();

  }

  @Test
  void test_has_foreign_key_field() {
    Node node = schemaGraph.getNode("node_tbl2").get();

    List<Field> fields;

    fields = ImmutableList.of("id", "customer_id").stream()
        .map(fieldName -> node.getField(fieldName).get())
        .collect(Collectors.toList());

    assertThat(NodeUtils.hasForeignKeyField(node, fields)).isTrue();

    fields = ImmutableList.of("parent_id", "name").stream()
        .map(fieldName -> node.getField(fieldName).get())
        .collect(Collectors.toList());

    assertThat(NodeUtils.hasForeignKeyField(node, fields)).isTrue();

    fields = ImmutableList.of("id", "display_name").stream()
        .map(fieldName -> node.getField(fieldName).get())
        .collect(Collectors.toList());

    assertThat(NodeUtils.hasForeignKeyField(node, fields)).isFalse();
  }

  @Test
  void test_is_part_of_unary_foreign_key() {
    Node node = schemaGraph.getNode("node_tbl2").get();

    assertThat(NodeUtils.isPartOfUnaryForeignKey(node, "id")).isFalse();
    assertThat(NodeUtils.isPartOfUnaryForeignKey(node, "uuid")).isFalse();
    assertThat(NodeUtils.isPartOfUnaryForeignKey(node, "customer_id")).isFalse();
    assertThat(NodeUtils.isPartOfUnaryForeignKey(node, "node_type_id")).isFalse();
    assertThat(NodeUtils.isPartOfUnaryForeignKey(node, "parent_id")).isTrue();
    assertThat(NodeUtils.isPartOfUnaryForeignKey(node, "name")).isFalse();
    assertThat(NodeUtils.isPartOfUnaryForeignKey(node, "display_name")).isFalse();
    assertThat(NodeUtils.isPartOfUnaryForeignKey(node, "created_at")).isFalse();
    assertThat(NodeUtils.isPartOfUnaryForeignKey(node, "updated_at")).isFalse();

  }

  @Test
  void test_sql_value_test() {
    Node node = schemaGraph.getNode("clone_test_tbl").get();

    assertThat(NodeUtils.toSqlValue(node.getField("id").get(), "1")).isEqualTo("1");
    assertThat(NodeUtils.toSqlValue(node.getField("int_array_field").get(), "{1, 2, 3}"))
        .isEqualTo("'{1, 2, 3}'");
    assertThat(NodeUtils.toSqlValue(node.getField("text_array_field").get(),
        "{\"dog\", \"cat\", \"mouse\"}")).isEqualTo("'{\"dog\", \"cat\", \"mouse\"}'");
    assertThat(NodeUtils.toSqlValue(node.getField("character_varying_array_field").get(),
        "{\"dog\", \"cat\", \"mouse\"}")).isEqualTo("'{\"dog\", \"cat\", \"mouse\"}'");
    assertThat(NodeUtils.toSqlValue(node.getField("user_defined_field").get(), "ONLINE"))
        .isEqualTo("'ONLINE'");
    assertThat(NodeUtils.toSqlValue(node.getField("bigint_field").get(), "1234567"))
        .isEqualTo("1234567");
    assertThat(NodeUtils.toSqlValue(node.getField("boolean_field").get(), "TRUE"))
        .isEqualTo("TRUE");
    assertThat(NodeUtils.toSqlValue(node.getField("character_varying_field").get(),
        "Character Varying Field")).isEqualTo("'Character Varying Field'");
    assertThat(NodeUtils.toSqlValue(node.getField("date_field").get(), "2017-03-14"))
        .isEqualTo("'2017-03-14'");
    assertThat(NodeUtils.toSqlValue(node.getField("double_precision_field").get(), "123.4567"))
        .isEqualTo("123.4567");
    assertThat(NodeUtils.toSqlValue(node.getField("inet_field").get(), "192.168.2.1"))
        .isEqualTo("'192.168.2.1'");
    assertThat(NodeUtils.toSqlValue(node.getField("integer_field").get(), "6789"))
        .isEqualTo("6789");
    assertThat(NodeUtils.toSqlValue(node.getField("json_field").get(),
        "{\"email\": \"thom22@gmail.com\", \"country\": \"US\"}"))
            .isEqualTo("'{\"email\": \"thom22@gmail.com\", \"country\": \"US\"}'");
    assertThat(NodeUtils.toSqlValue(node.getField("money_field").get(), "345.67"))
        .isEqualTo("345.67");
    assertThat(NodeUtils.toSqlValue(node.getField("numeric_field").get(), "1234"))
        .isEqualTo("1234");
    assertThat(NodeUtils.toSqlValue(node.getField("numeric_field2").get(), "234.56"))
        .isEqualTo("234.56");
    assertThat(NodeUtils.toSqlValue(node.getField("smallint_field").get(), "12")).isEqualTo("12");
    assertThat(NodeUtils.toSqlValue(node.getField("text_field").get(), "Text Field"))
        .isEqualTo("'Text Field'");
    assertThat(NodeUtils.toSqlValue(node.getField("time_without_timezone_field").get(), "02:03:04"))
        .isEqualTo("'02:03:04'");
    assertThat(NodeUtils.toSqlValue(node.getField("timestamp_without_timezone_field").get(),
        "03/03/2014 02:03:04")).isEqualTo("'03/03/2014 02:03:04'");
    assertThat(NodeUtils.toSqlValue(node.getField("uuid_field").get(),
        "4b36afc8-5205-49c1-af16-4dc6f96db982"))
            .isEqualTo("'4b36afc8-5205-49c1-af16-4dc6f96db982'");
    assertThat(NodeUtils.toSqlValue(node.getField("null_field").get(), null)).isEqualTo("NULL");

    // Test that single quotes are properly escaped!
    assertThat(NodeUtils.toSqlValue(node.getField("text_field").get(), "O'Connor and O'Toole"))
        .isEqualTo("'O''Connor and O''Toole'");


  }
}
