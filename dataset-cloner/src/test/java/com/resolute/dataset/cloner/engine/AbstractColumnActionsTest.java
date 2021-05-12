package com.resolute.dataset.cloner.engine;

import static com.resolute.dataset.cloner.engine.ColumnAction.COPY;
import static com.resolute.dataset.cloner.engine.ColumnAction.FK_LOOKUP;
import static com.resolute.dataset.cloner.engine.ColumnAction.MUTATE;
import static com.resolute.dataset.cloner.engine.ColumnAction.OMIT;
import static com.resolute.dataset.cloner.engine.ColumnAction.RESOLVE_AT_ROW_LEVEL;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.resolute.database.crawler.model.Node;
import com.resolute.dataset.cloner.integration.AbstractDatabaseTest;

public abstract class AbstractColumnActionsTest extends AbstractDatabaseTest {

  @Test
  void test_get() {
    Node node = schemaGraph.getNode("node_tbl2").get();


    ColumnActions actions =
        ColumnActions.create(node, FieldLevelMutators.NONE, false);

    assertThat(actions.get("id")).isEqualTo(OMIT);
    assertThat(actions.get("uuid")).isEqualTo(OMIT);
    assertThat(actions.get("customer_id")).isEqualTo(FK_LOOKUP);
    assertThat(actions.get("node_type_id")).isEqualTo(FK_LOOKUP);
    assertThat(actions.get("parent_id")).isEqualTo(RESOLVE_AT_ROW_LEVEL);
    assertThat(actions.get("name")).isEqualTo(RESOLVE_AT_ROW_LEVEL);
    assertThat(actions.get("display_name")).isEqualTo(COPY);
    assertThat(actions.get("created_at")).isEqualTo(COPY);
    assertThat(actions.get("updated_at")).isEqualTo(COPY);

  }

  @Test
  void test_get_with_pure_copy_mode() {
    Node node = schemaGraph.getNode("node_tbl2").get();


    ColumnActions actions =
        ColumnActions.create(node, FieldLevelMutators.NONE, true);

    assertThat(actions.get("id")).isEqualTo(COPY);
    assertThat(actions.get("uuid")).isEqualTo(COPY);
    assertThat(actions.get("customer_id")).isEqualTo(COPY);
    assertThat(actions.get("node_type_id")).isEqualTo(COPY);
    assertThat(actions.get("parent_id")).isEqualTo(COPY);
    assertThat(actions.get("name")).isEqualTo(COPY);
    assertThat(actions.get("display_name")).isEqualTo(COPY);
    assertThat(actions.get("created_at")).isEqualTo(COPY);
    assertThat(actions.get("updated_at")).isEqualTo(COPY);

  }

  @Test
  void test_get2() {
    Node node = schemaGraph.getNode("test1_tbl").get();

    ColumnActions actions =
        ColumnActions.create(node, FieldLevelMutators.NONE, false);

    assertThat(actions.get("id")).isEqualTo(OMIT);
    assertThat(actions.get("name")).isEqualTo(MUTATE);

  }

}
