package com.resolute.dataset.cloner.testutils;

import com.resolute.dataset.cloner.Application;
import com.resolute.dataset.cloner.Environment;
import com.resolute.dataset.cloner.engine.RollbackOperation;

public class Test5RollbackApplication extends Application {

  public static void main(String[] args) throws Exception {
    new Test5RollbackApplication().run(args);
  }

  @Override
  public void run(Environment env) {

    RollbackOperation.forGraph(env.getSchemaGraph())
        .withDataSource(env.getDataSource())
        .withDebug(env.getDebug())
        .executeFromLogFile(env.getLogger().getFile());

  }

}
