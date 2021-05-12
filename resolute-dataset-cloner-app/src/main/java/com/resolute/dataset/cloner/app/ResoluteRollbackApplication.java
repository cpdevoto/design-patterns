package com.resolute.dataset.cloner.app;

import com.resolute.dataset.cloner.Application;
import com.resolute.dataset.cloner.Environment;
import com.resolute.dataset.cloner.engine.RollbackOperation;
import com.resolute.utils.simple.ElapsedTimeUtils;

public class ResoluteRollbackApplication extends Application {

  public static void main(String[] args) throws Exception {
    new ResoluteRollbackApplication().run(args);
  }

  @Override
  public void run(Environment env) {

    long start = System.currentTimeMillis();
    System.out.println("Rollback started...");

    RollbackOperation.forGraph(env.getSchemaGraph())
        .withDataSource(env.getDataSource())
        .withDebug(env.getDebug())
        .executeFromLogFile(env.getLogger().getFile());

    System.out.println(
        "Rollback completed in " + ElapsedTimeUtils.format(System.currentTimeMillis() - start));
  }

}
