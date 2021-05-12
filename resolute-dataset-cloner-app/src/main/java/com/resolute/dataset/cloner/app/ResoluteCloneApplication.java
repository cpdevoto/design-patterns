package com.resolute.dataset.cloner.app;

import java.util.List;

import com.google.common.collect.Lists;
import com.resolute.dataset.cloner.Application;
import com.resolute.dataset.cloner.Environment;
import com.resolute.dataset.cloner.engine.DatasetCloner;

public class ResoluteCloneApplication extends Application {

  public static void main(String[] args) throws Exception {
    boolean rollback = false;
    boolean script = false;
    List<String> argList = Lists.newArrayList();
    for (String arg : args) {
      if ("--rollback".equalsIgnoreCase(arg)) {
        rollback = true;
      } else if ("--script".equalsIgnoreCase(arg)) {
        script = true;
      } else {
        argList.add(arg);
      }
    }

    if (rollback && script) {
      System.err.println(
          "ERROR: You can include the '--rollback' switch or the '--script' switch but not both");
      System.exit(0);
    }

    if (rollback) {
      ResoluteRollbackApplication.main(argList.toArray(new String[argList.size()]));
    } else if (script) {
      ResoluteScriptApplication.main(argList.toArray(new String[argList.size()]));
    } else {
      new ResoluteCloneApplication().run(args);
    }
  }

  @Override
  public void run(Environment env) {
    DatasetCloner cloner = DatasetCloners.create(env);
    try {
      cloner.execute();
    } catch (Throwable t) {
      t.printStackTrace();
      cloner.rollback();
    }
  }

}
