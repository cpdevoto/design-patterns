package com.resolute.dataset.cloner.utils;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class KeyMapsTest {

  @Test
  void test_put() {
    KeyMaps keyMaps = new KeyMaps();

    Key sourceKey1 = Key.of("field1", 1);
    Key targetKey1 = Key.of("field1", 2);
    Key sourceKey2 = Key.of("field1", 3);
    Key targetKey2 = Key.of("field1", 4);
    Key sourceKey3 = Key.of("field10", 10, "field11", 11);
    Key targetKey3 = Key.of("field10", 12, "field11", 13);

    keyMaps
        .put("table1", sourceKey1, targetKey1)
        .put("table1", sourceKey2, targetKey2)
        .put("table2", sourceKey3, targetKey3);

    assertThat(keyMaps.getTargetKey("table1", sourceKey1))
        .hasValueSatisfying(key -> key.equals(targetKey1));
    assertThat(keyMaps.getTargetKey("table1", sourceKey2))
        .hasValueSatisfying(key -> key.equals(targetKey2));
    assertThat(keyMaps.getTargetKey("table1", sourceKey3)).isEmpty();
    assertThat(keyMaps.getTargetKey("table2", sourceKey1)).isEmpty();
    assertThat(keyMaps.getTargetKey("table2", sourceKey2)).isEmpty();
    assertThat(keyMaps.getTargetKey("table2", sourceKey3))
        .hasValueSatisfying(key -> key.equals(targetKey3));


  }

  @Test
  void test_get_target_keys() {
    KeyMaps keyMaps = new KeyMaps();

    Key sourceKey1 = Key.of("field1", 1);
    Key targetKey1 = Key.of("field1", 2);
    Key sourceKey2 = Key.of("field1", 3);
    Key targetKey2 = Key.of("field1", 4);
    Key sourceKey3 = Key.of("field10", 10, "field11", 11);
    Key targetKey3 = Key.of("field10", 12, "field11", 13);

    keyMaps
        .put("table1", sourceKey1, targetKey1)
        .put("table1", sourceKey2, targetKey2)
        .put("table2", sourceKey3, targetKey3);

    assertThat(keyMaps.getTargetKeys("table1")).hasSize(2).contains(targetKey1, targetKey2);
    assertThat(keyMaps.getTargetKeys("table2")).hasSize(1).contains(targetKey3);
    assertThat(keyMaps.getTargetKeys("table3")).isEmpty();
  }

  @Test
  void test_get_target_key() {
    KeyMaps keyMaps = new KeyMaps();

    Key sourceKey1 = Key.of("field1", 1);
    Key targetKey1 = Key.of("field1", 2);
    Key sourceKey2 = Key.of("field1", 3);
    Key targetKey2 = Key.of("field1", 4);
    Key sourceKey3 = Key.of("field10", 10, "field11", 11);
    Key targetKey3 = Key.of("field10", 12, "field11", 13);

    keyMaps
        .put("table1", sourceKey1, targetKey1)
        .put("table1", sourceKey2, targetKey2)
        .put("table2", sourceKey3, targetKey3);

    assertThat(keyMaps.getTargetKey("table1", sourceKey1))
        .hasValueSatisfying(key -> key.equals(targetKey1));
    assertThat(keyMaps.getTargetKey("table1", sourceKey2))
        .hasValueSatisfying(key -> key.equals(targetKey2));
    assertThat(keyMaps.getTargetKey("table1", sourceKey3)).isEmpty();
    assertThat(keyMaps.getTargetKey("table2", sourceKey1)).isEmpty();
    assertThat(keyMaps.getTargetKey("table2", sourceKey2)).isEmpty();
    assertThat(keyMaps.getTargetKey("table2", sourceKey3))
        .hasValueSatisfying(key -> key.equals(targetKey3));
  }

  @Test
  void test_get_source_keys() {
    KeyMaps keyMaps = new KeyMaps();

    Key sourceKey1 = Key.of("field1", 1);
    Key targetKey1 = Key.of("field1", 2);
    Key sourceKey2 = Key.of("field1", 3);
    Key targetKey2 = Key.of("field1", 4);
    Key sourceKey3 = Key.of("field10", 10, "field11", 11);
    Key targetKey3 = Key.of("field10", 12, "field11", 13);

    keyMaps
        .put("table1", sourceKey1, targetKey1)
        .put("table1", sourceKey2, targetKey2)
        .put("table2", sourceKey3, targetKey3);

    assertThat(keyMaps.getSourceKeys("table1")).hasSize(2).contains(sourceKey1, sourceKey2);
    assertThat(keyMaps.getSourceKeys("table2")).hasSize(1).contains(sourceKey3);
    assertThat(keyMaps.getSourceKeys("table3")).isEmpty();
  }


  @Test
  void test_get_source_key() {
    KeyMaps keyMaps = new KeyMaps();

    Key sourceKey1 = Key.of("field1", 1);
    Key targetKey1 = Key.of("field1", 2);
    Key sourceKey2 = Key.of("field1", 3);
    Key targetKey2 = Key.of("field1", 4);
    Key sourceKey3 = Key.of("field10", 10, "field11", 11);
    Key targetKey3 = Key.of("field10", 12, "field11", 13);

    keyMaps
        .put("table1", sourceKey1, targetKey1)
        .put("table1", sourceKey2, targetKey2)
        .put("table2", sourceKey3, targetKey3);

    assertThat(keyMaps.getSourceKey("table1", targetKey1))
        .hasValueSatisfying(key -> key.equals(sourceKey1));
    assertThat(keyMaps.getSourceKey("table1", targetKey2))
        .hasValueSatisfying(key -> key.equals(sourceKey2));
    assertThat(keyMaps.getSourceKey("table1", targetKey3)).isEmpty();
    assertThat(keyMaps.getSourceKey("table2", targetKey1)).isEmpty();
    assertThat(keyMaps.getSourceKey("table2", targetKey2)).isEmpty();
    assertThat(keyMaps.getSourceKey("table2", targetKey3))
        .hasValueSatisfying(key -> key.equals(sourceKey3));
  }

  @Test
  void test_size() {
    KeyMaps keyMaps = new KeyMaps();

    Key sourceKey1 = Key.of("field1", 1);
    Key targetKey1 = Key.of("field1", 2);
    Key sourceKey2 = Key.of("field1", 3);
    Key targetKey2 = Key.of("field1", 4);
    Key sourceKey3 = Key.of("field10", 10, "field11", 11);
    Key targetKey3 = Key.of("field10", 12, "field11", 13);

    keyMaps
        .put("table1", sourceKey1, targetKey1)
        .put("table1", sourceKey2, targetKey2)
        .put("table2", sourceKey3, targetKey3);

    assertThat(keyMaps.size("table1")).isEqualTo(2);
    assertThat(keyMaps.size("table2")).isEqualTo(1);
    assertThat(keyMaps.size("table3")).isEqualTo(0);
  }

  @Test
  void test_is_empty() {
    KeyMaps keyMaps = new KeyMaps();

    Key sourceKey1 = Key.of("field1", 1);
    Key targetKey1 = Key.of("field1", 2);
    Key sourceKey2 = Key.of("field1", 3);
    Key targetKey2 = Key.of("field1", 4);
    Key sourceKey3 = Key.of("field10", 10, "field11", 11);
    Key targetKey3 = Key.of("field10", 12, "field11", 13);

    keyMaps
        .put("table1", sourceKey1, targetKey1)
        .put("table1", sourceKey2, targetKey2)
        .put("table2", sourceKey3, targetKey3);

    assertThat(keyMaps.isEmpty("table1")).isFalse();
    assertThat(keyMaps.isEmpty("table2")).isFalse();
    assertThat(keyMaps.isEmpty("table3")).isTrue();
  }

}
