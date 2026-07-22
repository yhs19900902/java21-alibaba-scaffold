package com.yhs.base.config;

import io.undertow.servlet.api.DeploymentInfo;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration;
import org.springframework.boot.autoconfigure.task.TaskSchedulingAutoConfiguration;
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.*;

/**
 * @author 03952-yehuasheng
 * @version Id: VirtualThreadConfiguration.java, v 0.1 2026/7/20 20:04 yehuasheng Exp $
 * <p>启用时：</p>
 * <ul>
 *     <li>Undertow Servlet 业务链路使用虚拟线程；IO/XNIO 线程不变。</li>
 *     <li>未指定执行器名称的 {@code @Async} 使用虚拟线程。</li>
 *     <li>{@code @Scheduled} 的计时/触发使用平台线程，业务逻辑使用虚拟线程。</li>
 * </ul>
 *
 * <p>禁用时：整个配置类不加载，交由 Spring Boot 与 Undertow 使用默认平台线程配置。</p>
 */
@AutoConfiguration(
        before = {
                TaskExecutionAutoConfiguration.class,
                TaskSchedulingAutoConfiguration.class
        }
)
@ConditionalOnProperty(
        prefix = "app.virtual-threads",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true
)
public class VirtualThreadConfiguration {

    private static final long EXECUTOR_SHUTDOWN_TIMEOUT_SECONDS = 30;

    /**
     * 先给任务一个有界的优雅结束窗口；超时后才请求中断；
     * 再等待同样长度的窗口。
     *
     * <p>Java 的中断是协作式的：忽略中断的业务代码无法被强制终止。
     * 此时明确抛出异常，避免静默隐藏未结束任务。</p>
     */
    private static void shutdownExecutor(
            ExecutorService executor,
            String executorName) {

        boolean interrupted = false;

        executor.shutdown();

        try {
            if (!executor.awaitTermination(
                    EXECUTOR_SHUTDOWN_TIMEOUT_SECONDS,
                    TimeUnit.SECONDS)) {

                executor.shutdownNow();

                if (!executor.awaitTermination(
                        EXECUTOR_SHUTDOWN_TIMEOUT_SECONDS,
                        TimeUnit.SECONDS)) {

                    throw new IllegalStateException(
                            executorName
                                    + " did not terminate after graceful "
                                    + "shutdown and interruption"
                    );
                }
            }
        } catch (InterruptedException ex) {
            interrupted = true;
            executor.shutdownNow();

            try {
                if (!executor.awaitTermination(
                        EXECUTOR_SHUTDOWN_TIMEOUT_SECONDS,
                        TimeUnit.SECONDS)) {

                    throw new IllegalStateException(
                            executorName
                                    + " did not terminate after shutdown "
                                    + "was interrupted",
                            ex
                    );
                }
            } catch (InterruptedException nestedEx) {
                interrupted = true;

                throw new IllegalStateException(
                        "Interrupted again while waiting for "
                                + executorName
                                + " to terminate",
                        nestedEx
                );
            }
        } finally {
            if (interrupted) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Undertow Servlet 业务链路的虚拟线程执行器。
     *
     * <p>不修改 Undertow IO/XNIO 线程。</p>
     */
    @Bean(destroyMethod = "")
    ExecutorService undertowVirtualThreadExecutor() {
        return Executors.newThreadPerTaskExecutor(
                Thread.ofVirtual()
                        .name("undertow-vt-", 0)
                        .factory()
        );
    }

    /**
     * 有界关闭 Undertow 业务执行器。
     *
     * <p>这里显式禁用了 Bean 的 close 推断销毁方法，避免 JDK 21
     * {@code ExecutorService.close()} 因任务不响应中断而无限等待。</p>
     */
    @Bean
    DisposableBean undertowVirtualThreadExecutorShutdown(
            @Qualifier("undertowVirtualThreadExecutor")
            ExecutorService undertowVirtualThreadExecutor) {

        return () -> shutdownExecutor(
                undertowVirtualThreadExecutor,
                "Undertow virtual-thread executor"
        );
    }

    @Bean
    WebServerFactoryCustomizer<UndertowServletWebServerFactory>
    undertowVirtualThreadCustomizer(
            @Qualifier("undertowVirtualThreadExecutor")
            ExecutorService undertowVirtualThreadExecutor) {

        return factory -> factory.addDeploymentInfoCustomizers(
                deploymentInfo -> customizeUndertowDeployment(
                        deploymentInfo,
                        undertowVirtualThreadExecutor
                )
        );
    }

    private void customizeUndertowDeployment(
            DeploymentInfo deploymentInfo,
            Executor executor) {

        deploymentInfo.setExecutor(executor);
    }

    /**
     * 不命名为 taskExecutor，避免与 Boot 自动配置冲突。
     *
     * <p>不要对该实例调用 setConcurrencyLimit(...): 当前实现只保证默认的无限并发模式。
     * Spring 6.0 在有限并发模式下，如关闭期间任务被拒绝，可能造成并发许可无法释放。</p>
     */
    @Bean
    SimpleAsyncTaskExecutor virtualThreadAsyncExecutor() {
        ThreadFactory virtualThreadFactory = Thread.ofVirtual()
                .name("async-vt-", 0)
                .factory();

        return new LifecycleAwareSimpleAsyncTaskExecutor(
                virtualThreadFactory
        );
    }

    /**
     * 显式指定默认 @Async 使用的执行器。
     *
     * <p>不会影响 {@code @Async("customExecutor")} 这种显式指定执行器的调用。</p>
     */
    @Bean
    AsyncConfigurer virtualThreadAsyncConfigurer(
            @Qualifier("virtualThreadAsyncExecutor")
            SimpleAsyncTaskExecutor virtualThreadAsyncExecutor) {

        return new AsyncConfigurer() {
            @Override
            public Executor getAsyncExecutor() {
                return virtualThreadAsyncExecutor;
            }
        };
    }

    /**
     * 不命名为 taskScheduler，避免与 Boot 自动配置冲突。
     */
    @Bean
    TaskScheduler virtualThreadTaskScheduler() {
        return new VirtualThreadTaskScheduler();
    }

    /**
     * 显式指定 @Scheduled 使用的调度器。
     */
    @Bean
    SchedulingConfigurer virtualThreadSchedulingConfigurer(
            @Qualifier("virtualThreadTaskScheduler")
            TaskScheduler virtualThreadTaskScheduler) {

        return new SchedulingConfigurer() {
            @Override
            public void configureTasks(
                    ScheduledTaskRegistrar taskRegistrar) {

                taskRegistrar.setTaskScheduler(
                        virtualThreadTaskScheduler
                );
            }
        };
    }

    /**
     * 保留 Spring 原生 SimpleAsyncTaskExecutor 类型，并补充容器关闭生命周期。
     */
    private static final class LifecycleAwareSimpleAsyncTaskExecutor
            extends SimpleAsyncTaskExecutor implements DisposableBean {

        private final ExecutorService delegate;

        private LifecycleAwareSimpleAsyncTaskExecutor(
                ThreadFactory threadFactory) {

            super(threadFactory);
            this.delegate = Executors.newThreadPerTaskExecutor(
                    threadFactory
            );
        }

        @Override
        protected void doExecute(Runnable task) {
            try {
                this.delegate.execute(task);
            } catch (RejectedExecutionException ex) {
                throw new TaskRejectedException(
                        "The virtual-thread @Async executor is shutting down",
                        ex
                );
            }
        }

        @Override
        public void destroy() {
            shutdownExecutor(
                    this.delegate,
                    "Virtual-thread @Async executor"
            );
        }
    }

    /**
     * 平台线程仅负责 cron/fixedRate/fixedDelay 的计时和触发；
     * 每次实际业务执行提交到一个虚拟线程。
     *
     * <p>包装器等待业务任务结束，因此 fixedDelay 仍保持：
     * “上次业务真正完成后，才开始计算下一次延迟”的语义。</p>
     */
    private static final class VirtualThreadTaskScheduler
            implements TaskScheduler, InitializingBean, DisposableBean {

        private final ThreadPoolTaskScheduler triggerScheduler;

        private final ExecutorService businessExecutor;

        private VirtualThreadTaskScheduler() {
            this.triggerScheduler = new ThreadPoolTaskScheduler();
            this.triggerScheduler.setPoolSize(1);
            this.triggerScheduler.setThreadFactory(
                    Thread.ofPlatform()
                            .name("scheduling-trigger-", 0)
                            .factory()
            );

            this.businessExecutor = Executors.newThreadPerTaskExecutor(
                    Thread.ofVirtual()
                            .name("scheduled-vt-", 0)
                            .factory()
            );
        }

        private static void rethrow(Throwable cause) {
            if (cause instanceof RuntimeException runtimeException) {
                throw runtimeException;
            }

            if (cause instanceof Error error) {
                throw error;
            }

            throw new IllegalStateException(
                    "Scheduled task failed",
                    cause
            );
        }

        @Override
        public void afterPropertiesSet() {
            this.triggerScheduler.afterPropertiesSet();
        }

        @Override
        public void destroy() {
            try {
                this.triggerScheduler.destroy();
            } finally {
                shutdownExecutor(
                        this.businessExecutor,
                        "Scheduled virtual-thread executor"
                );
            }
        }

        @Override
        public ScheduledFuture<?> schedule(
                Runnable task,
                Trigger trigger) {

            return this.triggerScheduler.schedule(
                    wrap(task),
                    trigger
            );
        }

        @Override
        public ScheduledFuture<?> schedule(
                Runnable task,
                Instant startTime) {

            return this.triggerScheduler.schedule(
                    wrap(task),
                    startTime
            );
        }

        @Override
        public ScheduledFuture<?> scheduleAtFixedRate(
                Runnable task,
                Instant startTime,
                Duration period) {

            return this.triggerScheduler.scheduleAtFixedRate(
                    wrap(task),
                    startTime,
                    period
            );
        }

        @Override
        public ScheduledFuture<?> scheduleAtFixedRate(
                Runnable task,
                Duration period) {

            return this.triggerScheduler.scheduleAtFixedRate(
                    wrap(task),
                    period
            );
        }

        @Override
        public ScheduledFuture<?> scheduleWithFixedDelay(
                Runnable task,
                Instant startTime,
                Duration delay) {

            return this.triggerScheduler.scheduleWithFixedDelay(
                    wrap(task),
                    startTime,
                    delay
            );
        }

        @Override
        public ScheduledFuture<?> scheduleWithFixedDelay(
                Runnable task,
                Duration delay) {

            return this.triggerScheduler.scheduleWithFixedDelay(
                    wrap(task),
                    delay
            );
        }

        private Runnable wrap(Runnable task) {
            return () -> {
                Future<?> future;

                try {
                    future = this.businessExecutor.submit(task);
                } catch (RejectedExecutionException ex) {
                    throw new IllegalStateException(
                            "Scheduled task was rejected during shutdown",
                            ex
                    );
                }

                try {
                    future.get();
                } catch (InterruptedException ex) {
                    future.cancel(true);
                    Thread.currentThread().interrupt();

                    throw new IllegalStateException(
                            "Interrupted while waiting for scheduled task",
                            ex
                    );
                } catch (ExecutionException ex) {
                    rethrow(ex.getCause());
                }
            };
        }
    }
}