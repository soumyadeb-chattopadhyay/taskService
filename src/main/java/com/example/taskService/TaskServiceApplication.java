package com.example.taskService;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.example.taskService.Main.Task;
import com.example.taskService.Main.TaskGroup;
import com.example.taskService.Main.TaskType;


@SpringBootApplication
public class TaskServiceApplication {
	
	@Autowired
	static
	TaskSubmission taskSubmission;
	
	private static Integer TASK_LIST_CAPACITY = 10;
	private static Integer CALLABLE_EXECUTION_DURATION_MS = 300;

	public static void main(String[] args) {
		SpringApplication.run(TaskServiceApplication.class, args);
		
		//create sample 10 tasks for submission
		ConcurrentLinkedQueue<Task<String>>	taskList = new ConcurrentLinkedQueue<Task<String>>();
		
		int  i = 0;
		String callableReturnString = "Task executed "+i+" ";
		while(i < TASK_LIST_CAPACITY) {
			Main.TaskGroup taskGoup = new TaskGroup(UUID.randomUUID());
			Main.Task<String> taskInstance = new Main.Task<String>(UUID.randomUUID(),taskGoup, generateTaskType(), () -> {
			    TimeUnit.MILLISECONDS.sleep(CALLABLE_EXECUTION_DURATION_MS);
			    return callableReturnString;
			}) ;
			taskList.add(taskInstance);
			i++;
		}

		TaskSubmission taskSubmissionInstance = new TaskSubmission();
		taskSubmissionInstance.submitTasks(taskList);
		
	}
	
	public static TaskType generateTaskType() {
		return TaskType.values()[new Random().nextInt(TaskType.values().length)];	
	}
	
	@Bean
	@Qualifier("threadPoolTaskSubmission")
	public ThreadPoolTaskExecutor getAsyncNotificationEvent() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(15);
		executor.setMaxPoolSize(40);
		executor.setQueueCapacity(100);
		executor.setAwaitTerminationSeconds(60);
		executor.setWaitForTasksToCompleteOnShutdown(true);
		executor.setThreadNamePrefix("taskService-taskSubmission");
		executor.initialize();
		return executor;
	}

}
