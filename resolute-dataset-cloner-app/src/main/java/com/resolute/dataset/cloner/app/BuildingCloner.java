package com.resolute.dataset.cloner.app;

import static com.resolute.dataset.cloner.app.Mutators.METRIC_ID_MUTATOR;
import static com.resolute.dataset.cloner.app.Mutators.NODE_TBL_MUTATOR;
import static com.resolute.dataset.cloner.app.SelectSpecs.Building.ROOT_SELECT_EMAIL_NOTIFICATION;
import static com.resolute.dataset.cloner.app.SelectSpecs.Building.ROOT_SELECT_RAW_POINT;
import static java.util.Objects.requireNonNull;

import java.util.Set;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.resolute.database.crawler.model.ForeignKey;
import com.resolute.database.crawler.model.ForeignKeyField;
import com.resolute.database.crawler.model.Graph;
import com.resolute.database.crawler.model.IgnoredEdge;
import com.resolute.dataset.cloner.Environment;
import com.resolute.dataset.cloner.engine.DatasetCloner;
import com.resolute.dataset.cloner.utils.Key;

class BuildingCloner extends DatasetCloner {


  static Builder builder(Environment env) {
    return new Builder(env);
  }

  private BuildingCloner(Builder builder) {
    super(builder, (schemaGraph, sourceSetsBuilder) -> {
      Set<IgnoredEdge> ignored = ImmutableSet.of(
          new IgnoredEdge("node_tbl", "portfolio_tbl",
              new ForeignKey(ImmutableList.of(new ForeignKeyField("id", "id")))),
          new IgnoredEdge("node_tbl", "site_tbl",
              new ForeignKey(ImmutableList.of(new ForeignKeyField("id", "id")))),
          new IgnoredEdge("node_tbl", "standard_perspective_customer_node_tbl",
              new ForeignKey(ImmutableList.of(new ForeignKeyField("id", "node_id")))),
          new IgnoredEdge("building_tbl", "batch_job_schedule_tbl",
              new ForeignKey(ImmutableList.of(new ForeignKeyField("id", "building_id")))));

      Graph widgetGraph =
          schemaGraph.getSubgraphReachableFrom("widget_tbl");

      Graph datasetGraph =
          schemaGraph.getSubgraphReachableFrom("dataset_tbl");

      Graph subgraph =
          schemaGraph.getSubgraphReachableFrom("node_tbl", ignored)
              .difference(widgetGraph, false)
              .difference(datasetGraph, false);

      sourceSetsBuilder
          .withSourceSet(sourceSetBuilder -> sourceSetBuilder
              .withRootSelectStatement("node_tbl",
                  Key.of("id", String.valueOf(builder.buildingId)))
              .withTupleLevelMutator("node_tbl", NODE_TBL_MUTATOR)
              .withFieldLevelMutator("point_tbl", "metric_id", METRIC_ID_MUTATOR)
              .withGraph(subgraph))
          .withSourceSet(sourceSetBuilder -> sourceSetBuilder
              .withRootSelectStatement("email_notification_tbl", ROOT_SELECT_EMAIL_NOTIFICATION))
          .withSourceSet(sourceSetBuilder -> sourceSetBuilder
              .withRootSelectStatement("raw_point_tbl", ROOT_SELECT_RAW_POINT)
              .withFieldLevelMutator("raw_point_tbl", "metric_id", METRIC_ID_MUTATOR));
    });
  }

  static class Builder extends DatasetCloner.Builder<BuildingCloner, Builder> {

    private Integer buildingId;

    private Builder(Environment env) {
      super(env);
    }

    public Builder withBuildingId(int buildingId) {
      this.buildingId = buildingId;
      return this;
    }

    @Override
    protected Builder getThis() {
      return this;
    }

    @Override
    protected BuildingCloner newInstance() {
      requireNonNull(buildingId, "buildingId cannot be null");
      return new BuildingCloner(this);
    }

  }

}
