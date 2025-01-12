package com.example.taskService;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.example.taskService.Main.Task;


public class TaskSubmission implements Main.TaskExecutor {

	
	private static ThreadPoolTaskExecutor executorTaskServiceSubmission = getAsyncTaskExecutorForSubmission() ;
	private static ThreadPoolTaskExecutor executorTaskServiceExecution = getAsyncTaskExecutorForExecution() ;

	public void submitTasks(ConcurrentLinkedQueue<Task<String>> taskList) {
		// TODO Auto-generated method stub
		taskList.stream().forEach(i -> {
		 CompletableFuture.supplyAsync(() -> submitTask(i),executorTaskServiceSubmission).thenApply(response -> {
			try {
				System.out.println(response.get());
			} catch (InterruptedException | ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return i;
		});
		});
		
	}


	public static ThreadPoolTaskExecutor getAsyncTaskExecutorForSubmission() {
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
	
	public static ThreadPoolTaskExecutor getAsyncTaskExecutorForExecution() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(15);
		executor.setMaxPoolSize(40);
		executor.setQueueCapacity(100);
		executor.setAwaitTerminationSeconds(60);
		executor.setWaitForTasksToCompleteOnShutdown(true);
		executor.setThreadNamePrefix("taskService-taskExecutor");
		executor.initialize();
		return executor;
	}




	@Override
	public <T> Future<T> submitTask(Task<T> task) {
		return executorTaskServiceExecution.submit(task.taskAction());
	}

}
