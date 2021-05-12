package com.resolute.dataset.cloner.utils;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

import java.util.Optional;
import java.util.Set;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableSet;

class KeyMap {
  private final String name;
  private final BiMap<Key, Key> keyMap = HashBiMap.create();

  KeyMap(String name) {
    requireNonNull(name, "name cannot be null");
    this.name = name;
  }

  String getName() {
    return name;
  }

  boolean isEmpty() {
    return keyMap.isEmpty();
  }

  int size() {
    return keyMap.size();
  }

  Set<Key> getSourceKeys() {
    return ImmutableSet.copyOf(keyMap.keySet());
  }

  Set<Key> getTargetKeys() {
    return ImmutableSet.copyOf(keyMap.values());
  }

  KeyMap put(Key sourceKey, Key targetKey) {
    requireNonNull(sourceKey, "sourceKey cannot be null");
    requireNonNull(targetKey, "targetKey cannot be null");
    checkArgument(sourceKey.hasSameFields(targetKey),
        "expected sourceKey to have the same fields as targetKey, even if the field values are different");
    keyMap.put(sourceKey, targetKey);
    return this;
  }

  Optional<Key> getTargetKey(Key sourceKey) {
    requireNonNull(sourceKey, "sourceKey cannot be null");
    return Optional.ofNullable(keyMap.get(sourceKey));
  }

  Optional<Key> getSourceKey(Key targetKey) {
    requireNonNull(targetKey, "targetKey cannot be null");
    return Optional.ofNullable(keyMap.inverse().get(targetKey));
  }

}
