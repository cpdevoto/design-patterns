package com.resolute.coord;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.util.function.Consumer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.common.base.Charsets;

@JsonInclude(Include.NON_NULL)
@JsonDeserialize(builder = Shard.Builder.class)
public class Shard {
  private static final ObjectMapper mapper = new ObjectMapper();
  private final int id;
  private final int workerCount;

  public static byte[] toBytes(Shard shard) {
    try {
      String json = mapper.writeValueAsString(shard);
      return json.getBytes(Charsets.UTF_8);
    } catch (JsonProcessingException e) {
      throw new DistributedCoordinationException(e);
    }
  }

  public static Shard fromBytes(byte[] b) {
    try {
      return mapper.readValue(b, Shard.class);
    } catch (IOException e) {
      throw new DistributedCoordinationException(e);
    }
  }

  @JsonCreator
  public static Builder builder() {
    return new Builder();
  }

  public static Builder builder(Shard shard) {
    return new Builder(shard);
  }

  private Shard(Builder builder) {
    this.id = builder.id;
    this.workerCount = builder.workerCount;
  }

  public int getId() {
    return id;
  }

  public int getWorkerCount() {
    return workerCount;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + id;
    result = prime * result + workerCount;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Shard other = (Shard) obj;
    if (id != other.id)
      return false;
    if (workerCount != other.workerCount)
      return false;
    return true;
  }

  @JsonPOJOBuilder
  public static class Builder {
    private Integer id;
    private Integer workerCount;

    private Builder() {}

    private Builder(Shard shard) {
      requireNonNull(shard, "shard cannot be null");
      this.id = shard.id;
      this.workerCount = shard.workerCount;
    }

    public Builder with(Consumer<Builder> consumer) {
      requireNonNull(consumer, "consumer cannot be null");
      consumer.accept(this);
      return this;
    }

    public Builder withId(int id) {
      this.id = id;
      return this;
    }

    public Builder withWorkerCount(int workerCount) {
      this.workerCount = workerCount;
      return this;
    }

    public Shard build() {
      requireNonNull(id, "id cannot be null");
      requireNonNull(workerCount, "workerCount cannot be null");
      return new Shard(this);
    }
  }
}
