package com.resolute.dataset.cloner;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.collectingAndThen;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

public class ForeignKeyReference {
  private static final Logger log = LoggerFactory.getLogger(ForeignKeyReference.class);
  private final String name;
  private final List<KeyMap> keyMaps;

  public static Builder builder() {
    return new Builder();
  }

  public static Builder builder(ForeignKeyReference foreignKeyFilter) {
    return new Builder(foreignKeyFilter);
  }

  private ForeignKeyReference(Builder builder) {
    this.name = builder.name;
    this.keyMaps = builder.keyMaps;
  }

  public String getName() {
    return name;
  }

  public List<KeyMap> getKeyMaps() {
    return keyMaps;
  }

  public static class Builder {
    private String name;
    private List<KeyMap> keyMaps;


    private Builder() {}

    private Builder(ForeignKeyReference foreignKeyFilter) {
      requireNonNull(foreignKeyFilter, "foreignKeyFilter cannot be null");
      this.name = foreignKeyFilter.name;
      this.keyMaps = foreignKeyFilter.keyMaps;
    }

    public Builder with(Consumer<Builder> consumer) {
      requireNonNull(consumer, "consumer cannot be null");
      consumer.accept(this);
      return this;
    }

    public Builder withName(String name) {
      requireNonNull(name, "name cannot be null");
      this.name = name;
      return this;
    }

    public Builder withKeyMaps(KeyMap... keyMaps) {
      checkArgument(keyMaps.length > 0, "keyMaps must include at least one element");
      this.keyMaps = Arrays.stream(keyMaps)
          .collect(collectingAndThen(Collectors.toList(), ImmutableList::copyOf));
      return this;
    }

    public ForeignKeyReference build() {
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
      return new ForeignKeyReference(this);
    }
  }
}
