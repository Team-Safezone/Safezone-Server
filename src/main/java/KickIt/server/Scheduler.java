package KickIt.server;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
public class Scheduler {

    @Bean
    public ThreadPoolTaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(10); // 스레드 풀의 크기를 설정합니다.
        taskScheduler.setThreadNamePrefix("task-scheduler-"); // 스레드 이름의 접두사를 설정합니다.
        taskScheduler.initialize(); // 초기화
        return taskScheduler;
    }

}
