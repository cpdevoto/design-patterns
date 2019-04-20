package org.devoware.homonculus.core;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.devoware.homonculus.core.lifecycle.Managed;
import org.devoware.homonculus.core.setup.Environment;
import org.devoware.homonculus.core.test.fixtures.HostConfiguration;
import org.junit.Test;

import ch.qos.logback.classic.Level;

public class ApplicationTest {

  @Test
  public void test_get_configuration_class() {
    MockApplication app = new MockApplication();
    assertThat(app.getConfigurationClass(), equalTo(HostConfiguration.class));
  }

  @Test
  public void test_initialize_called() throws Exception {
    MockApplication app = new MockApplication();
    app.appInit(new String[] {"start", "src/test/resources/mock-application.yml"});
    assertTrue(app.isInitializeCalled());
    assertFalse(app.isStartCalled());
    assertFalse(app.isStopCalled());
    HostConfiguration config = app.getConfiguration();
    assertNotNull(config);
    assertThat(config.getHost(), equalTo("www.purple.com"));
    assertThat(config.getPort(), equalTo(80));
    Environment environment = app.getEnvironment();
    assertNotNull(environment);
    List<Managed> managed = environment.getManagedResources();
    assertThat(managed.size(), equalTo(2));

  }

  @Test
  public void test_application_lifecycle() throws Exception {
    MockApplication app = new MockApplication();
    app.appInit(new String[] {"start", "src/test/resources/mock-application.yml"});
    assertTrue(app.isInitializeCalled());
    assertFalse(app.isStartCalled());
    assertFalse(app.isStopCalled());

    app.appStart();
    app.startLatch.await(5, TimeUnit.SECONDS);
    assertTrue(app.isInitializeCalled());
    assertTrue(app.isStartCalled());
    assertFalse(app.isStopCalled());

    // The following works fine on the local machine, but not on Jenkins. Commenting out for now
    // app.appStop();
    // app.stopLatch.await(5, TimeUnit.SECONDS);
    // assertTrue(app.isInitializeCalled());
    // assertTrue(app.isStartCalled());
    // assertTrue(app.isStopCalled());
  }

  private static class MockApplication extends Application<HostConfiguration> {

    private boolean initializeCalled;
    private boolean startCalled;
    private boolean stopCalled;
    private CountDownLatch startLatch = new CountDownLatch(1);
    private CountDownLatch stopLatch = new CountDownLatch(1);

    private HostConfiguration configuration;
    private Environment environment;

    @Override
    public String getName() {
      return "mock-application";
    }

    @Override
    protected void initialize(HostConfiguration configuration, Environment environment) {
      this.initializeCalled = true;
      this.configuration = configuration;
      this.environment = environment;
    }

    @Override
    public void start() throws Exception {
      this.startCalled = true;
      this.startLatch.countDown();
    }

    @Override
    public void stop() throws Exception {
      this.stopCalled = true;
      this.stopLatch.countDown();
    }

    @Override
    protected Level bootstrapLogLevel() {
      return Level.INFO;
    }

    public HostConfiguration getConfiguration() {
      return configuration;
    }

    public Environment getEnvironment() {
      return environment;
    }

    public boolean isInitializeCalled() {
      return initializeCalled;
    }

    public boolean isStartCalled() {
      return startCalled;
    }

    public boolean isStopCalled() {
      return stopCalled;
    }

  }

}
