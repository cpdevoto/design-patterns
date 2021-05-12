package com.resolute.dataset.cloner.utils;

import static java.util.Objects.requireNonNull;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

public class KeyMaps {
  private final Map<String, KeyMap> keyMaps = Maps.newHashMap();

  public KeyMaps() {}

  public KeyMaps put(String tableName, Key sourceKey, Key targetKey) {
    requireNonNull(tableName, "tableName cannot be null");
    requireNonNull(sourceKey, "sourceKey cannot be null");
    requireNonNull(targetKey, "targetKey cannot be null");
    KeyMap keyMap = keyMaps.computeIfAbsent(tableName, tName -> new KeyMap(tName));
    keyMap.put(sourceKey, targetKey);
    return this;
  }

  public Optional<Key> getTargetKey(String tableName, Key sourceKey) {
    requireNonNull(tableName, "tableName cannot be null");
    requireNonNull(sourceKey, "sourceKey cannot be null");
    if (!keyMaps.containsKey(tableName)) {
      return Optional.empty();
    }
    return keyMaps.get(tableName).getTargetKey(sourceKey);
  }

  public Optional<Key> getSourceKey(String tableName, Key targetKey) {
    requireNonNull(tableName, "tableName cannot be null");
    requireNonNull(targetKey, "targetKey cannot be null");
    if (!keyMaps.containsKey(tableName)) {
      return Optional.empty();
    }
    return keyMaps.get(tableName).getSourceKey(targetKey);
  }

  public Set<Key> getSourceKeys(String tableName) {
    requireNonNull(tableName, "tableName cannot be null");
    if (!keyMaps.containsKey(tableName)) {
      return ImmutableSet.of();
    }
    return keyMaps.get(tableName).getSourceKeys();
  }

  public Set<Key> getTargetKeys(String tableName) {
    requireNonNull(tableName, "tableName cannot be null");
    if (!keyMaps.containsKey(tableName)) {
      return ImmutableSet.of();
    }
    return keyMaps.get(tableName).getTargetKeys();
  }

  public boolean isEmpty(String tableName) {
    requireNonNull(tableName, "tableName cannot be null");
    if (!keyMaps.containsKey(tableName)) {
      return true;
    }
    return keyMaps.get(tableName).isEmpty();
  }

  public int size(String tableName) {
    requireNonNull(tableName, "tableName cannot be null");
    if (!keyMaps.containsKey(tableName)) {
      return 0;
    }
    return keyMaps.get(tableName).size();
  }

  public Set<String> getTableNames() {
    return keyMaps.keySet();
  }
}
