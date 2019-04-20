package com.resolute.dataset.cloner;

import static java.util.Objects.requireNonNull;

import java.util.Set;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableSet;

public class KeyMap {
  private final String name;
  private final BiMap<Integer, Integer> keyMap = HashBiMap.create();

  public KeyMap(String name) {
    requireNonNull(name, "name cannot be null");
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public boolean isMaterialized() {
    return !keyMap.isEmpty();
  }


  public Set<Integer> getSourceIds() {
    return ImmutableSet.copyOf(keyMap.keySet());
  }

  public Set<Integer> getTargetIds() {
    return ImmutableSet.copyOf(keyMap.values());
  }

  public void put(int sourceId, int targetId) {
    keyMap.put(sourceId, targetId);
  }

  public Integer getTargetId(int sourceId) {
    return keyMap.get(sourceId);
  }

  public Integer getSourceId(int targetId) {
    return keyMap.inverse().get(targetId);
  }

  @Override
  public String toString() {
    return "KeyMap [name=" + name + ", keyMap=" + keyMap + "]";
  }

}
