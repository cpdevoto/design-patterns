package org.devoware.homonculus.core.setup;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;

import org.devoware.homonculus.core.lifecycle.ExecutorServiceManager;
import org.devoware.homonculus.validators.util.Duration;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

public class ScheduledExecutorServiceBuilder {
    private final Environment environment;
    private final String nameFormat;
    private int poolSize;
    private ThreadFactory threadFactory;
    private Duration shutdownTime;
    private RejectedExecutionHandler handler;

    public ScheduledExecutorServiceBuilder(Environment environment, String nameFormat, ThreadFactory factory) {
        this.environment = environment;
        this.nameFormat = nameFormat;
        this.poolSize = 1;
        this.threadFactory = factory;
        this.shutdownTime = Duration.seconds(5);
        this.handler = new ThreadPoolExecutor.AbortPolicy();
    }

    public ScheduledExecutorServiceBuilder(Environment environment, String nameFormat, boolean useDaemonThreads) {
        this(environment, nameFormat, new ThreadFactoryBuilder().setNameFormat(nameFormat).setDaemon(useDaemonThreads).build());
    }

    public ScheduledExecutorServiceBuilder threads(int threads) {
        this.poolSize = threads;
        return this;
    }

    public ScheduledExecutorServiceBuilder shutdownTime(Duration time) {
        this.shutdownTime = time;
        return this;
    }

    public ScheduledExecutorServiceBuilder rejectedExecutionHandler(RejectedExecutionHandler handler) {
        this.handler = handler;
        return this;
    }

    public ScheduledExecutorServiceBuilder threadFactory(ThreadFactory threadFactory) {
        this.threadFactory = threadFactory;
        return this;
    }

    public ScheduledExecutorService build() {
        final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(poolSize, threadFactory, handler);
        environment.manage(new ExecutorServiceManager(executor, shutdownTime, nameFormat));
        return executor;
    }
}
