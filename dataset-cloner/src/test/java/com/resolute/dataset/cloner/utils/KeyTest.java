package com.resolute.dataset.cloner.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.junit.jupiter.api.Test;

public class KeyTest {


  @Test
  public void test_builder() {
    Key key = Key.builder()
        .withFieldValue("field1", 1)
        .withFieldValue("field2", 2)
        .build();

    assertThat(key).isNotNull();
    assertThat(key.getFieldNames()).hasSize(2);
    assertThat(key.getFieldNames().get(0)).isEqualTo("field1");
    assertThat(key.getFieldNames().get(1)).isEqualTo("field2");
    assertThat(key.getFieldValue("field1")).isEqualTo("1");
    assertThat(key.getFieldValue("field2")).isEqualTo("2");

  }

  @Test
  void test_builder_with_no_field_values() {
    assertThatThrownBy(() -> {
      Key.builder()
          .build();
    }).isInstanceOf(IllegalStateException.class)
        .hasMessage("expected at least one field value");
  }

  @Test
  void test_builder_with_null_field_name() {
    assertThatThrownBy(() -> {
      Key.builder()
          .withFieldValue(null, 1)
          .build();
    }).isInstanceOf(NullPointerException.class)
        .hasMessage("fieldName cannot be null");
  }

  @Test
  void test_builder_with_null_field_value() {
    assertThatThrownBy(() -> {
      Key.builder()
          .withFieldValue("field1", null)
          .build();
    }).isInstanceOf(NullPointerException.class)
        .hasMessage("fieldValue cannot be null");
  }

  @Test
  void test_of_with_one_entry() {
    Key key = Key.of("field1", 1);

    assertThat(key).isNotNull();
    assertThat(key.getFieldNames()).hasSize(1);
    assertThat(key.getFieldNames().get(0)).isEqualTo("field1");
    assertThat(key.getFieldValue("field1")).isEqualTo("1");

  }

  @Test
  void test_of_with_one_entry_and_null_field_name() {
    assertThatThrownBy(() -> {
      Key.of(null, 1);
    }).isInstanceOf(NullPointerException.class)
        .hasMessage("fieldName1 cannot be null");

  }

  @Test
  void test_of_with_one_entry_and_null_field_value() {
    assertThatThrownBy(() -> {
      Key.of("field1", null);
    }).isInstanceOf(NullPointerException.class)
        .hasMessage("fieldValue1 cannot be null");

  }

  @Test
  void test_of_with_two_entries() {
    Key key = Key.of(
        "field1", 1,
        "field2", 2);

    assertThat(key).isNotNull();
    assertThat(key.getFieldNames()).hasSize(2);
    assertThat(key.getFieldNames().get(0)).isEqualTo("field1");
    assertThat(key.getFieldNames().get(1)).isEqualTo("field2");
    assertThat(key.getFieldValue("field1")).isEqualTo("1");
    assertThat(key.getFieldValue("field2")).isEqualTo("2");

  }

  @Test
  void test_of_with_two_entries_and_null_field_name() {
    assertThatThrownBy(() -> {
      Key.of(
          null, 1,
          "field2", 2);
    }).isInstanceOf(NullPointerException.class)
        .hasMessage("fieldName1 cannot be null");

    assertThatThrownBy(() -> {
      Key.of(
          "field1", 1,
          null, 2);
    }).isInstanceOf(NullPointerException.class)
        .hasMessage("fieldName2 cannot be null");
  }

  @Test
  void test_of_with_two_entries_and_null_field_value() {
    assertThatThrownBy(() -> {
      Key.of(
          "field1", null,
          "field2", 2);
    }).isInstanceOf(NullPointerException.class)
        .hasMessage("fieldValue1 cannot be null");

    assertThatThrownBy(() -> {
      Key.of(
          "field1", 1,
          "field2", null);
    }).isInstanceOf(NullPointerException.class)
        .hasMessage("fieldValue2 cannot be null");
  }

  @Test
  void test_of_with_three_entries() {
    Key key = Key.of(
        "field1", 1,
        "field2", 2,
        "field3", 3);

    assertThat(key).isNotNull();
    assertThat(key.getFieldNames()).hasSize(3);
    assertThat(key.getFieldNames().get(0)).isEqualTo("field1");
    assertThat(key.getFieldNames().get(1)).isEqualTo("field2");
    assertThat(key.getFieldNames().get(2)).isEqualTo("field3");
    assertThat(key.getFieldValue("field1")).isEqualTo("1");
    assertThat(key.getFieldValue("field2")).isEqualTo("2");
    assertThat(key.getFieldValue("field3")).isEqualTo("3");

  }

  @Test
  void test_of_with_three_entries_and_null_field_name() {
    assertThatThrownBy(() -> {
      Key.of(
          null, 1,
          "field2", 2,
          "field3", 3);
    }).isInstanceOf(NullPointerException.class)
        .hasMessage("fieldName1 cannot be null");

    assertThatThrownBy(() -> {
      Key.of(
          "field1", 1,
          null, 2,
          "field3", 3);
    }).isInstanceOf(NullPointerException.class)
        .hasMessage("fieldName2 cannot be null");

    assertThatThrownBy(() -> {
      Key.of(
          "field1", 1,
          "field2", 2,
          null, 3);
    }).isInstanceOf(NullPointerException.class)
        .hasMessage("fieldName3 cannot be null");
  }

  @Test
  void test_of_with_three_entries_and_null_field_value() {
    assertThatThrownBy(() -> {
      Key.of(
          "field1", null,
          "field2", 2,
          "field3", 3);
    }).isInstanceOf(NullPointerException.class)
        .hasMessage("fieldValue1 cannot be null");

    assertThatThrownBy(() -> {
      Key.of(
          "field1", 1,
          "field2", null,
          "field3", 3);
    }).isInstanceOf(NullPointerException.class)
        .hasMessage("fieldValue2 cannot be null");

    assertThatThrownBy(() -> {
      Key.of(
          "field1", 1,
          "field2", 2,
          "field3", null);
    }).isInstanceOf(NullPointerException.class)
        .hasMessage("fieldValue3 cannot be null");
  }

  @Test
  void test_of_with_four_entries() {
    Key key = Key.of(
        "field1", 1,
        "field2", 2,
        "field3", 3,
        "field4", 4);

    assertThat(key).isNotNull();
    assertThat(key.getFieldNames()).hasSize(4);
    assertThat(key.getFieldNames().get(0)).isEqualTo("field1");
    assertThat(key.getFieldNames().get(1)).isEqualTo("field2");
    assertThat(key.getFieldNames().get(2)).isEqualTo("field3");
    assertThat(key.getFieldNames().get(3)).isEqualTo("field4");
    assertThat(key.getFieldValue("field1")).isEqualTo("1");
    assertThat(key.getFieldValue("field2")).isEqualTo("2");
    assertThat(key.getFieldValue("field3")).isEqualTo("3");
    assertThat(key.getFieldValue("field4")).isEqualTo("4");
  }

  @Test
  void test_of_with_four_entries_and_null_field_name() {
    assertThatThrownBy(() -> {
      Key.of(
          null, 1,
          "field2", 2,
          "field3", 3,
          "field4", 4);
    }).isInstanceOf(NullPointerException.class)
        .hasMessage("fieldName1 cannot be null");

    assertThatThrownBy(() -> {
      Key.of(
          "field1", 1,
          null, 2,
          "field3", 3,
          "field4", 4);
    }).isInstanceOf(NullPointerException.class)
        .hasMessage("fieldName2 cannot be null");

    assertThatThrownBy(() -> {
      Key.of(
          "field1", 1,
          "field2", 2,
          null, 3,
          "field4", 4);
    }).isInstanceOf(NullPointerException.class)
        .hasMessage("fieldName3 cannot be null");

    assertThatThrownBy(() -> {
      Key.of(
          "field1", 1,
          "field2", 2,
          "field3", 3,
          null, 4);
    }).isInstanceOf(NullPointerException.class)
        .hasMessage("fieldName4 cannot be null");

  }

  @Test
  void test_of_with_four_entries_and_null_field_value() {
    assertThatThrownBy(() -> {
      Key.of(
          "field1", null,
          "field2", 2,
          "field3", 3,
          "field4", 4);
    }).isInstanceOf(NullPointerException.class)
        .hasMessage("fieldValue1 cannot be null");

    assertThatThrownBy(() -> {
      Key.of(
          "field1", 1,
          "field2", null,
          "field3", 3,
          "field4", 4);
    }).isInstanceOf(NullPointerException.class)
        .hasMessage("fieldValue2 cannot be null");

    assertThatThrownBy(() -> {
      Key.of(
          "field1", 1,
          "field2", 2,
          "field3", null,
          "field4", 4);
    }).isInstanceOf(NullPointerException.class)
        .hasMessage("fieldValue3 cannot be null");

    assertThatThrownBy(() -> {
      Key.of(
          "field1", 1,
          "field2", 2,
          "field3", 3,
          "field4", null);
    }).isInstanceOf(NullPointerException.class)
        .hasMessage("fieldValue4 cannot be null");

  }

  @Test
  void test_of_with_five_entries() {
    Key key = Key.of(
        "field1", 1,
        "field2", 2,
        "field3", 3,
        "field4", 4,
        "field5", 5);

    assertThat(key).isNotNull();
    assertThat(key.getFieldNames()).hasSize(5);
    assertThat(key.getFieldNames().get(0)).isEqualTo("field1");
    assertThat(key.getFieldNames().get(1)).isEqualTo("field2");
    assertThat(key.getFieldNames().get(2)).isEqualTo("field3");
    assertThat(key.getFieldNames().get(3)).isEqualTo("field4");
    assertThat(key.getFieldNames().get(4)).isEqualTo("field5");
    assertThat(key.getFieldValue("field1")).isEqualTo("1");
    assertThat(key.getFieldValue("field2")).isEqualTo("2");
    assertThat(key.getFieldValue("field3")).isEqualTo("3");
    assertThat(key.getFieldValue("field4")).isEqualTo("4");
    assertThat(key.getFieldValue("field5")).isEqualTo("5");

  }

  @Test
  void test_of_with_five_entries_and_null_field_name() {
    assertThatThrownBy(() -> {
      Key.of(
          null, 1,
          "field2", 2,
          "field3", 3,
          "field4", 4,
          "field5", 5);
    }).isInstanceOf(NullPointerException.class)
        .hasMessage("fieldName1 cannot be null");

    assertThatThrownBy(() -> {
      Key.of(
          "field1", 1,
          null, 2,
          "field3", 3,
          "field4", 4,
          "field5", 5);
    }).isInstanceOf(NullPointerException.class)
        .hasMessage("fieldName2 cannot be null");

    assertThatThrownBy(() -> {
      Key.of(
          "field1", 1,
          "field2", 2,
          null, 3,
          "field4", 4,
          "field5", 5);
    }).isInstanceOf(NullPointerException.class)
        .hasMessage("fieldName3 cannot be null");

    assertThatThrownBy(() -> {
      Key.of(
          "field1", 1,
          "field2", 2,
          "field3", 3,
          null, 4,
          "field5", 5);
    }).isInstanceOf(NullPointerException.class)
        .hasMessage("fieldName4 cannot be null");

    assertThatThrownBy(() -> {
      Key.of(
          "field1", 1,
          "field2", 2,
          "field3", 3,
          "field4", 4,
          null, 5);
    }).isInstanceOf(NullPointerException.class)
        .hasMessage("fieldName5 cannot be null");
  }

  @Test
  void test_of_with_five_entries_and_null_field_value() {
    assertThatThrownBy(() -> {
      Key.of(
          "field1", null,
          "field2", 2,
          "field3", 3,
          "field4", 4,
          "field5", 5);
    }).isInstanceOf(NullPointerException.class)
        .hasMessage("fieldValue1 cannot be null");

    assertThatThrownBy(() -> {
      Key.of(
          "field1", 1,
          "field2", null,
          "field3", 3,
          "field4", 4,
          "field5", 5);
    }).isInstanceOf(NullPointerException.class)
        .hasMessage("fieldValue2 cannot be null");

    assertThatThrownBy(() -> {
      Key.of(
          "field1", 1,
          "field2", 2,
          "field3", null,
          "field4", 4,
          "field5", 5);
    }).isInstanceOf(NullPointerException.class)
        .hasMessage("fieldValue3 cannot be null");

    assertThatThrownBy(() -> {
      Key.of(
          "field1", 1,
          "field2", 2,
          "field3", 3,
          "field4", null,
          "field5", 5);
    }).isInstanceOf(NullPointerException.class)
        .hasMessage("fieldValue4 cannot be null");

    assertThatThrownBy(() -> {
      Key.of(
          "field1", 1,
          "field2", 2,
          "field3", 3,
          "field4", 4,
          "field5", null);
    }).isInstanceOf(NullPointerException.class)
        .hasMessage("fieldValue5 cannot be null");
  }

  @Test
  void test_get_field_names() {
    Key key = Key.of("field1", 1, "field2", 2, "field3", 3);

    List<String> fieldNames = key.getFieldNames();
    assertThat(fieldNames).hasSize(3);
    assertThat(fieldNames.get(0)).isEqualTo("field1");
    assertThat(fieldNames.get(1)).isEqualTo("field2");
    assertThat(fieldNames.get(2)).isEqualTo("field3");

  }

  @Test
  void test_get_field_value() {
    Key key = Key.of("field1", 1, "field2", 2);

    String fieldValue = key.getFieldValue("field1");
    assertThat(fieldValue).isEqualTo("1");

    fieldValue = key.getFieldValue("field2");
    assertThat(fieldValue).isEqualTo("2");


  }

  @Test
  void test_get_field_value_with_invalid_field_name() {
    Key key = Key.of("field1", 1, "field2", 2);

    assertThatThrownBy(() -> {
      key.getFieldValue("field3");
    }).isInstanceOf(IllegalArgumentException.class)
        .hasMessage("fieldName must be one of field1, field2");

  }

  @Test
  void test_equals() {
    Key key1 = Key.of("field1", 1, "field2", 2);
    Key key2 = Key.of("field1", 1, "field2", 2);
    Key key3 = Key.of("field1", 1, "field2", 3);
    Key key4 = Key.of("field1", 3, "field2", 2);
    Key key5 = Key.of("field1", 1, "field2", 2, "field3", 3);
    Key key6 = Key.of("field1", 1);
    Key key7 = Key.of("field2", 2, "field1", 1);


    assertThat(key1).isEqualTo(key2);
    assertThat(key1).isNotEqualTo(key3);
    assertThat(key1).isNotEqualTo(key4);
    assertThat(key1).isNotEqualTo(key5);
    assertThat(key1).isNotEqualTo(key6);
    assertThat(key1).isEqualTo(key7);
  }

  @Test
  void test_hash_code() {
    Key key1 = Key.of("field1", 1, "field2", 2);
    Key key2 = Key.of("field1", 1, "field2", 2);
    Key key3 = Key.of("field1", 1);
    Key key4 = Key.of("field1", 1);
    Key key5 = Key.of("field1", 1, "field2", 2, "field3", 3);
    Key key6 = Key.of("field1", 1, "field2", 2, "field3", 3);
    Key key7 = Key.of("field2", 2, "field1", 1);

    assertThat(key1.hashCode()).isEqualTo(key2.hashCode());
    assertThat(key1.hashCode()).isEqualTo(key7.hashCode());
    assertThat(key3.hashCode()).isEqualTo(key4.hashCode());
    assertThat(key5.hashCode()).isEqualTo(key6.hashCode());
  }

  @Test
  void test_to_string() {
    Key key = Key.of("field1", 1, "field2", 2);
    assertThat(key.toString()).isEqualTo("Key {field1=1, field2=2}");
  }

  @Test
  void test_has_same_fields() {
    Key key1 = Key.of("field1", 1, "field2", 2);
    Key key2 = Key.of("field1", 3, "field2", 4);
    Key key3 = Key.of("field1", 5, "field2", 6, "field3", 7);
    Key key4 = Key.of("field1", 8);
    Key key5 = Key.of("field2", 9, "field1", 10);

    assertThat(key1.hasSameFields(key2)).isTrue();
    assertThat(key2.hasSameFields(key1)).isTrue();
    assertThat(key1.hasSameFields(key3)).isFalse();
    assertThat(key3.hasSameFields(key1)).isFalse();
    assertThat(key1.hasSameFields(key4)).isFalse();
    assertThat(key4.hasSameFields(key1)).isFalse();
    assertThat(key1.hasSameFields(key5)).isTrue();
    assertThat(key5.hasSameFields(key1)).isTrue();

  }


}
