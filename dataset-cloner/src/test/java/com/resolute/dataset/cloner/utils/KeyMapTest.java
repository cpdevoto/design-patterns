package com.resolute.dataset.cloner.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

public class KeyMapTest {

  @Test
  void test_construction() {
    KeyMap keyMap = new KeyMap("table1");

    assertThat(keyMap).isNotNull();
  }

  @Test
  void test_get_name() {
    KeyMap keyMap = new KeyMap("table1");

    assertThat(keyMap.getName()).isEqualTo("table1");
  }

  @Test
  void test_put() {
    KeyMap keyMap = new KeyMap("table1");

    Key sourceKey = Key.of("field1", 1);
    Key targetKey = Key.of("field1", 2);
    keyMap.put(sourceKey, targetKey);

    assertThat(keyMap.getSourceKey(targetKey)).hasValueSatisfying(key -> key.equals(sourceKey));
    assertThat(keyMap.getTargetKey(sourceKey)).hasValueSatisfying(key -> key.equals(targetKey));
  }

  @Test
  void test_put_keys_with_different_fields() {
    KeyMap keyMap = new KeyMap("table1");

    Key sourceKey = Key.of("field1", 1);
    Key targetKey = Key.of("field2", 2);

    assertThatThrownBy(() -> {
      keyMap.put(sourceKey, targetKey);
    }).isInstanceOf(IllegalArgumentException.class)
        .hasMessage(
            "expected sourceKey to have the same fields as targetKey, even if the field values are different");

  }

  @Test
  void test_is_empty() {
    KeyMap keyMap = new KeyMap("table1");

    assertThat(keyMap.isEmpty()).isTrue();

    Key sourceKey = Key.of("field1", 1);
    Key targetKey = Key.of("field1", 2);
    keyMap.put(sourceKey, targetKey);

    assertThat(keyMap.isEmpty()).isFalse();

  }

  @Test
  void test_is_size() {
    KeyMap keyMap = new KeyMap("table1");

    assertThat(keyMap.size()).isEqualTo(0);

    Key sourceKey = Key.of("field1", 1);
    Key targetKey = Key.of("field1", 2);
    keyMap.put(sourceKey, targetKey);

    assertThat(keyMap.size()).isEqualTo(1);

    sourceKey = Key.of("field1", 3);
    targetKey = Key.of("field1", 4);
    keyMap.put(sourceKey, targetKey);

    assertThat(keyMap.size()).isEqualTo(2);
  }

  @Test
  void test_get_source_keys() {
    KeyMap keyMap = new KeyMap("table1");

    Key sourceKey1 = Key.of("field1", 1);
    Key targetKey1 = Key.of("field1", 2);
    Key sourceKey2 = Key.of("field1", 3);
    Key targetKey2 = Key.of("field1", 4);
    keyMap
        .put(sourceKey1, targetKey1)
        .put(sourceKey2, targetKey2);

    assertThat(keyMap.getSourceKeys())
        .hasSize(2)
        .contains(sourceKey1, sourceKey2);
  }

  @Test
  void test_get_source_key() {
    KeyMap keyMap = new KeyMap("table1");

    Key sourceKey1 = Key.of("field1", 1);
    Key targetKey1 = Key.of("field1", 2);
    Key sourceKey2 = Key.of("field1", 3);
    Key targetKey2 = Key.of("field1", 4);
    Key targetKey3 = Key.of("field1", 5);
    keyMap
        .put(sourceKey1, targetKey1)
        .put(sourceKey2, targetKey2);

    assertThat(keyMap.getSourceKey(targetKey1)).hasValueSatisfying(key -> key.equals(sourceKey1));
    assertThat(keyMap.getSourceKey(targetKey2)).hasValueSatisfying(key -> key.equals(sourceKey2));
    assertThat(keyMap.getSourceKey(targetKey3)).isEmpty();
  }

  @Test
  void test_get_target_keys() {
    KeyMap keyMap = new KeyMap("table1");

    Key sourceKey1 = Key.of("field1", 1);
    Key targetKey1 = Key.of("field1", 2);
    Key sourceKey2 = Key.of("field1", 3);
    Key targetKey2 = Key.of("field1", 4);
    keyMap
        .put(sourceKey1, targetKey1)
        .put(sourceKey2, targetKey2);

    assertThat(keyMap.getTargetKeys())
        .hasSize(2)
        .contains(targetKey1, targetKey2);
  }

  @Test
  void test_get_target_key() {
    KeyMap keyMap = new KeyMap("table1");

    Key sourceKey1 = Key.of("field1", 1);
    Key targetKey1 = Key.of("field1", 2);
    Key sourceKey2 = Key.of("field1", 3);
    Key targetKey2 = Key.of("field1", 4);
    Key sourceKey3 = Key.of("field1", 5);
    keyMap
        .put(sourceKey1, targetKey1)
        .put(sourceKey2, targetKey2);

    assertThat(keyMap.getTargetKey(sourceKey1)).hasValueSatisfying(key -> key.equals(targetKey1));
    assertThat(keyMap.getTargetKey(sourceKey2)).hasValueSatisfying(key -> key.equals(targetKey2));
    assertThat(keyMap.getTargetKey(sourceKey3)).isEmpty();
  }
}
