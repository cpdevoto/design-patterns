package com.resolute.dataset.cloner.app;

import static com.resolute.dataset.cloner.app.Mutators.NODE_TBL_MUTATOR;
import static com.resolute.dataset.cloner.app.SelectSpecs.Customer.ROOT_SELECT_AC_TAG;
import static com.resolute.dataset.cloner.app.SelectSpecs.Customer.ROOT_SELECT_CLIENT_CREDENTIAL;
import static com.resolute.dataset.cloner.app.SelectSpecs.Customer.ROOT_SELECT_EMAIL_NOTIFICATION;
import static com.resolute.dataset.cloner.app.SelectSpecs.Customer.ROOT_SELECT_USER;
import static java.util.Objects.requireNonNull;

import com.resolute.dataset.cloner.Environment;
import com.resolute.dataset.cloner.engine.DatasetCloner;
import com.resolute.dataset.cloner.utils.Key;

public class CustomerCloner extends DatasetCloner {

  static Builder builder(Environment env) {
    return new Builder(env);
  }

  private CustomerCloner(Builder builder) {
    super(builder, (schemaGraph, sourceSetsBuilder) -> {
      sourceSetsBuilder
          .withSourceSet(sourceSetBuilder -> sourceSetBuilder
              .withRootSelectStatement("customer_tbl", Key.of("id", builder.customerId))
              .withTupleLevelMutator("node_tbl", NODE_TBL_MUTATOR)
              .withFieldLevelMutator("point_tbl", "metric_id", Mutators.METRIC_ID_MUTATOR)
              .withFieldLevelMutator("raw_point_tbl", "metric_id", Mutators.METRIC_ID_MUTATOR))
          .withSourceSet(sourceSetBuilder -> sourceSetBuilder
              .withRootSelectStatement("email_notification_tbl", ROOT_SELECT_EMAIL_NOTIFICATION))
          .withSourceSet(sourceSetBuilder -> sourceSetBuilder
              .withRootSelectStatement("client_credential_tbl", ROOT_SELECT_CLIENT_CREDENTIAL))
          .withSourceSet(sourceSetBuilder -> sourceSetBuilder
              .withRootSelectStatement("ac_tag_tbl", ROOT_SELECT_AC_TAG))
          .withSourceSet(sourceSetBuilder -> sourceSetBuilder
              .withRootSelectStatement("user_tbl", ROOT_SELECT_USER));
    });
  }

  static class Builder extends DatasetCloner.Builder<CustomerCloner, Builder> {

    private Integer customerId;

    private Builder(Environment env) {
      super(env);
    }

    public Builder withCustomerId(int customerId) {
      this.customerId = customerId;
      return this;
    }

    @Override
    protected Builder getThis() {
      return this;
    }

    @Override
    protected CustomerCloner newInstance() {
      requireNonNull(customerId, "customerId cannot be null");
      return new CustomerCloner(this);
    }

  }



}
