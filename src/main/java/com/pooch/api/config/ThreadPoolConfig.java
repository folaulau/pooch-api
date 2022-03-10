package com.pooch.api.config;

import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

import org.slf4j.MDC;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableAsync
@EnableScheduling
public class ThreadPoolConfig implements AsyncConfigurer {

    /*
     * corePoolSize parameter is the amount of core threads which will be instantiated and kept in the pool. If all core
     * threads are busy and more tasks are submitted, then the pool is allowed to grow up to a maximumPoolSize.
     */
    @Bean(name = "taskExecutor")
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // initial number of threads
        executor.setCorePoolSize(25);

        // max number of threads
        executor.setMaxPoolSize(Integer.MAX_VALUE);

        // some tasks may be put into a queue to wait for their turn.
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("Pooch-Thread-");
        executor.setAllowCoreThreadTimeOut(true);
        executor.setKeepAliveSeconds(60);
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setTaskDecorator(new LogTaskDecorator());
        executor.initialize();

        return executor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {

        return (ex, method, listOfObjects) -> {
            log.error("****** AsyncConfig - handleUncaughtException(...) ******");
            log.error("*  Class name: {}", method.getDeclaringClass());
            log.error("*  Method name - {}", method.getName());
            log.error("*  Exception message - {}", ex.getMessage());

            for (Object param : listOfObjects) {
                log.error("*  Param - {}", param);
            }
            log.error("*************************************");
        };
    }

    /**
     * Keep context tags to use in the new thread<br>
     * For example, we are using the memberUuid in the new thread.
     */
    class LogTaskDecorator implements TaskDecorator {

        @Override
        public Runnable decorate(Runnable runnable) {
            Map<String, String> contextMap = MDC.getCopyOfContextMap();
            return () -> {
                try {
                    if (contextMap != null) {
                        MDC.setContextMap(contextMap);
                    }
                    runnable.run();
                } catch (Exception e) {

                } finally {
                    try {
                        MDC.clear();
                    } catch (Exception e) {
                        // TODO: handle exception
                    }

                }
            };
        }

    }
}
