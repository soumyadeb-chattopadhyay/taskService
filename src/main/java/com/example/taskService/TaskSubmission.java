package com.example.taskService;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import com.example.taskService.Main.Task;


@Component
public class TaskSubmission implements Main.TaskExecutor {
	

	public void submitTasks(ConcurrentLinkedQueue<Task<String>> taskList) {
		// TODO Auto-generated method stub
		taskList.stream().forEach(i -> {
		 CompletableFuture.supplyAsync(() -> submitTask(i),getAsyncTaskExecutor()).thenApply(response -> {
			try {
				return response.get();
			} catch (InterruptedException | ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return i;
		});
		});
		
	}

	public ThreadPoolTaskExecutor getAsyncTaskExecutor() {
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



	@Override
	public <T> Future<T> submitTask(Task<T> task) {
		return getAsyncTaskExecutor().submit(task.taskAction());
	}

}
