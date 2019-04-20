package com.resolute.dataset.cloner;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Consumer;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

public class KeyMaps {
  private final Map<String, KeyMap> keyMaps;

  public static KeyMaps forTables(String... tables) {
    return new Builder()
        .forTables(tables)
        .build();
  }

  public static Builder builder(KeyMaps viewMaps) {
    return new Builder(viewMaps);
  }

  private KeyMaps(Builder builder) {
    this.keyMaps = ImmutableMap.copyOf(builder.keyMaps);
  }

  public Map<String, KeyMap> getKeyMaps() {
    return keyMaps;
  }

  public KeyMap get(String viewName) {
    return keyMaps.get(viewName);
  }

  public static class Builder {
    private Map<String, KeyMap> keyMaps = Maps.newHashMap();

    private Builder() {}

    private Builder(KeyMaps keyMaps) {
      requireNonNull(keyMaps, "keyMaps cannot be null");
      this.keyMaps = keyMaps.keyMaps;
    }

    public Builder with(Consumer<Builder> consumer) {
      requireNonNull(consumer, "consumer cannot be null");
      consumer.accept(this);
      return this;
    }

    public Builder forTables(String... tables) {
      checkArgument(tables.length > 0, "tables must have at least one element");
      Arrays.stream(tables)
          .forEach(table -> keyMaps.put(table, new KeyMap(table)));
      return this;
    }

    public KeyMaps build() {
      return new KeyMaps(this);
    }
  }
}

