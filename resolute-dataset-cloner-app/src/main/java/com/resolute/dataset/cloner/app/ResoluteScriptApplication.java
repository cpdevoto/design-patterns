package com.resolute.dataset.cloner.app;

import com.resolute.dataset.cloner.Application;
import com.resolute.dataset.cloner.Environment;
import com.resolute.dataset.cloner.engine.script.ScriptOperation;

public class ResoluteScriptApplication extends Application {

  public static void main(String[] args) throws Exception {
    new ResoluteScriptApplication().run(args);
  }

  @Override
  public void run(Environment env) {
    ScriptOperation.execute(env);
  }
}
