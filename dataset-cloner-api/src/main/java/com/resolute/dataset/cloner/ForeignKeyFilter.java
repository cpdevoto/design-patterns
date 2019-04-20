package com.resolute.dataset.cloner;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.joining;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

public class ForeignKeyFilter implements Filter {
  private static final Logger log = LoggerFactory.getLogger(ForeignKeyFilter.class);
  private final String name;
  private final List<KeyMap> keyMaps;

  static Builder builder(KeyMaps keyMapRegistry) {
    return new Builder(keyMapRegistry);
  }

  private ForeignKeyFilter(Builder builder) {
    this.name = builder.name;
    this.keyMaps = builder.keyMaps;
  }

  String getName() {
    return name;
  }

  List<KeyMap> getKeyMaps() {
    return keyMaps;
  }

  @Override
  public String toSql() {
    if (keyMaps.stream()
        .filter(keyMap -> !keyMap.isMaterialized())
        .findAny()
        .isPresent()) {
      return "FALSE";
    }
    return name + " IN " +
        keyMaps.stream()
            .flatMap(keyMap -> keyMap.getSourceIds().stream())
            .map(String::valueOf)
            .collect(joining(", ", "(", ")"));
  }

  public List<ForeignKeyFilter> getForeignKeyFilters() {
    return ImmutableList.of(this);
  }

  public static class Builder {
    private final KeyMaps keyMapRegistry;
    private String name;
    private List<KeyMap> keyMaps;

    private Builder(KeyMaps keyMapRegistry) {
      this.keyMapRegistry = requireNonNull(keyMapRegistry, "keyMapRegistry cannot be null");
    }

    Builder withName(String name) {
      requireNonNull(name, "name cannot be null");
      this.name = name;
      return this;
    }

    Builder withTables(String... tables) {
      checkArgument(tables.length > 0, "table must include at least one element");

      this.keyMaps = Arrays.stream(tables)
          .map(table -> {
            KeyMap keyMap = keyMapRegistry.get(table);
            checkArgument(keyMap != null,
                "table " + table + " is not referenced in the key map registry");
            return keyMap;
          })
          .collect(collectingAndThen(Collectors.toList(), ImmutableList::copyOf));
      return this;
    }

    ForeignKeyFilter build() {
      requireNonNull(name, "name cannot be null");
      requireNonNull(keyMaps, "keyMaps cannot be null");
      List<KeyMap> warningMaps = keyMaps.stream()
          .filter(keyMap -> !keyMap.isMaterialized())
          .collect(Collectors.toList());
      if (!warningMaps.isEmpty()) {
        warningMaps.stream()
            .forEach(keyMap -> log.warn("The foreign key filter for {} has no records",
                keyMap.getName()));
      }
      return new ForeignKeyFilter(this);
    }
  }
}
