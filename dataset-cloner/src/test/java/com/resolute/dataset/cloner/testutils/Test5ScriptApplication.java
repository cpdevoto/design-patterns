package com.resolute.dataset.cloner.testutils;

import com.resolute.dataset.cloner.Application;
import com.resolute.dataset.cloner.Environment;
import com.resolute.dataset.cloner.engine.script.ScriptOperation;

public class Test5ScriptApplication extends Application {

  public static void main(String[] args) throws Exception {
    new Test5ScriptApplication().run(args);
  }

  @Override
  public void run(Environment env) {

    ScriptOperation.execute(env);

  }

}
