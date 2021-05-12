package com.resolute.dataset.cloner.engine;

import static com.google.common.base.Preconditions.checkArgument;
import static com.resolute.dataset.cloner.utils.Constants.DEFAULT_MAX_RECS_PER_DELETE;
import static com.resolute.dataset.cloner.utils.Constants.DEFAULT_MAX_RECS_PER_INSERT;
import static java.util.Objects.requireNonNull;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import com.google.common.collect.Sets;
import com.resolute.database.crawler.model.Graph;
import com.resolute.database.crawler.model.Node;
import com.resolute.dataset.cloner.Environment;
import com.resolute.dataset.cloner.log.Logger;
import com.resolute.dataset.cloner.utils.KeyMaps;

public abstract class DatasetCloner {

  private final DatasetClonerHelper delegate;

  protected <T extends DatasetCloner, B extends Builder<T, B>> DatasetCloner(B builder,
      Initializer initializer) {
    requireNonNull(builder, "builder cannot be null");
    Logger logger = builder.getLogger();
    if (logger == null) {
      logger = new Logger(builder.getLogFile());
    }
    DatasetClonerHelper.Builder helperBuilder =
        DatasetClonerHelper.builder(builder.getSchemaGraph())
            .withDataSource(builder.getDataSource())
            .withLogger(logger)
            .withDebug(builder.getDebug())
            .withNumberOfCopies(builder.getNumberOfCopies())
            .withOutputFile(builder.getOutputFile())
            .withPureCopyMode(builder.getPureCopyMode())
            .withSuperclassWarningsHandled(builder.getSuperclassWarningsHandled())
            .withMaxRecsPerInsert(builder.getMaxRecsPerInsert())
            .withMaxRecsPerDelete(builder.getMaxRecsPerDelete())
            .withBeforeAllListener(builder.getBeforeAllListener())
            .withAfterAllListener(builder.getAfterAllListener())
            .withBeforeEachListener(builder.getBeforeEachListener())
            .withAfterEachListener(builder.getAfterEachListener());
    if (initializer != null) {
      initializer.initialize(builder.getSchemaGraph(), helperBuilder);
    }
    this.delegate = helperBuilder.build();
  }

  public final List<SourceSet> getSourceSets() {
    return delegate.getSourceSets();
  }

  public final Map<Node, List<Node>> getOrphanedSuperclassNodes() {
    return delegate.getOrphanedSuperclassNodes();
  }

  public final int getTableNamePrefix() {
    return delegate.getTableNamePrefix();
  }

  public final KeyMaps getKeyMaps() {
    return delegate.getKeyMaps();
  }

  public final void execute() {
    delegate.execute();
  }

  public final void computeDataToBeCloned() {
    delegate.computeDataToBeCloned();
  }

  public final void cloneData() {
    delegate.cloneData();
  }

  public final void cleanup() {
    delegate.cleanup();
  }

  public final void rollback() {
    delegate.rollback();
  }

  public abstract static class Builder<T extends DatasetCloner, B extends Builder<T, B>> {
    private Graph schemaGraph;
    private DataSource dataSource;
    private File logFile = new File("dataset-cloner.log");
    private Logger logger;
    private Set<String> superclassWarningsHandled = Sets.newHashSet();
    private boolean debug = false;
    private int numberOfCopies = 1;
    private boolean pureCopyMode = false;
    private Logger outputFile = new Logger("dataset-cloner.sql");
    private int maxRecsPerInsert = DEFAULT_MAX_RECS_PER_INSERT;
    private int maxRecsPerDelete = DEFAULT_MAX_RECS_PER_DELETE;
    private LifecycleListeners.BeforeAllListener beforeAllListener;
    private LifecycleListeners.AfterAllListener afterAllListener;
    private LifecycleListeners.BeforeEachListener beforeEachListener;
    private LifecycleListeners.AfterEachListener afterEachListener;

    protected Builder(Environment env) {
      requireNonNull(env, "env cannot be null");
      this.schemaGraph = env.getSchemaGraph();
      this.dataSource = env.getDataSource();
      this.debug = env.getDebug();
      this.logger = env.getLogger();
      this.numberOfCopies = env.getNumCopies();
      this.outputFile = env.getOutputFile();
      this.pureCopyMode = env.getPureCopyMode();
    }

    protected Builder(Graph schemaGraph) {
      this.schemaGraph = requireNonNull(schemaGraph, "schemaGraph cannot be null");
    }

    public final B withDataSource(DataSource dataSource) {
      this.dataSource = requireNonNull(dataSource, "dataSource cannot be null");
      return getThis();
    }

    public final B withLogger(Logger logger) {
      this.logger = requireNonNull(logger, "logger cannot be null");
      return getThis();
    }

    public final B withLogFile(String logFile) {
      requireNonNull(logFile, "logFile cannot be null");
      return withLogFile(new File(logFile));
    }

    public final B withLogFile(File logFile) {
      this.logFile = requireNonNull(logFile, "logFile cannot be null");
      return getThis();
    }

    public final B withOutputFile(String logFile) {
      requireNonNull(logFile, "logFile cannot be null");
      return withOutputFile(new File(logFile));
    }

    public final B withOutputFile(File logFile) {
      requireNonNull(logFile, "logFile cannot be null");
      return withOutputFile(new Logger(logFile));
    }

    public final B withOutputFile(Logger outputFile) {
      this.outputFile = requireNonNull(outputFile, "outputFile cannot be null");
      return getThis();
    }

    public final B withPureCopyMode(boolean pureCopyMode) {
      this.pureCopyMode = pureCopyMode;
      return getThis();
    }


    public final B withSuperclassWarningHandled(String superclassTable) {
      this.superclassWarningsHandled
          .add(requireNonNull(superclassTable, "superclassTable cannot be null"));
      return getThis();
    }

    public final B withDebug(boolean debug) {
      this.debug = debug;
      return getThis();
    }

    public final B withNumberOfCopies(int numberOfCopies) {
      checkArgument(numberOfCopies > 0, "expected at least one copy");
      this.numberOfCopies = numberOfCopies;
      return getThis();
    }

    public final B withMaxRecsPerInsert(int maxRecsPerInsert) {
      checkArgument(maxRecsPerInsert > 0, "expected a positive integer for maxRecsPerInsert");
      this.maxRecsPerInsert = maxRecsPerInsert;
      return getThis();
    }

    public final B withMaxRecsPerDelete(int maxRecsPerDelete) {
      checkArgument(maxRecsPerDelete > 0, "expected a positive integer for maxRecsPerDelete");
      this.maxRecsPerDelete = maxRecsPerDelete;
      return getThis();
    }

    public final B withBeforeAllListener(LifecycleListeners.BeforeAllListener beforeAllListener) {
      this.beforeAllListener =
          requireNonNull(beforeAllListener, "beforeAllListener cannot be null");
      return getThis();
    }

    public final B withAfterAllListener(LifecycleListeners.AfterAllListener afterAllListener) {
      this.afterAllListener = requireNonNull(afterAllListener, "afterAllListener cannot be null");
      return getThis();
    }

    public final B withBeforeEachListener(
        LifecycleListeners.BeforeEachListener beforeEachListener) {
      this.beforeEachListener =
          requireNonNull(beforeEachListener, "beforeEachListener cannot be null");
      return getThis();
    }

    public final B withAfterEachListener(LifecycleListeners.AfterEachListener afterEachListener) {
      this.afterEachListener =
          requireNonNull(afterEachListener, "afterEachListener cannot be null");
      return getThis();
    }

    public final T build() {
      requireNonNull(dataSource, "dataSource cannot be null");
      return newInstance();
    }

    protected abstract B getThis();

    protected abstract T newInstance();

    protected final Graph getSchemaGraph() {
      return schemaGraph;
    }

    protected final DataSource getDataSource() {
      return dataSource;
    }

    protected final Logger getLogger() {
      return logger;
    }

    protected final File getLogFile() {
      return logFile;
    }

    protected final Set<String> getSuperclassWarningsHandled() {
      return superclassWarningsHandled;
    }

    protected final boolean getDebug() {
      return debug;
    }

    protected final int getNumberOfCopies() {
      return numberOfCopies;
    }

    protected final Logger getOutputFile() {
      return outputFile;
    }

    protected final boolean getPureCopyMode() {
      return pureCopyMode;
    }

    protected final int getMaxRecsPerInsert() {
      return maxRecsPerInsert;
    }

    protected final int getMaxRecsPerDelete() {
      return maxRecsPerDelete;
    }

    protected final LifecycleListeners.BeforeAllListener getBeforeAllListener() {
      return beforeAllListener;
    }

    protected final LifecycleListeners.AfterAllListener getAfterAllListener() {
      return afterAllListener;
    }

    protected final LifecycleListeners.BeforeEachListener getBeforeEachListener() {
      return beforeEachListener;
    }

    protected final LifecycleListeners.AfterEachListener getAfterEachListener() {
      return afterEachListener;
    }

  }
}
