package com.example.taskService;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import com.example.taskService.Main.Task;

import jakarta.annotation.PostConstruct;


@Component
public class TaskSubmission implements Main.TaskExecutor {
	
  @Autowired
  @Qualifier("threadPoolTaskSubmission")
  private ThreadPoolTaskExecutor executorServiceTaskSubmission;	
 
  
  @PostConstruct
	public void intializeTasksSubmission() {
		tasksSubmission();
	}





  	private void tasksSubmission() {
	// TODO Auto-generated method stub
	//execute submitTasks from here in an unblocking way
	
  	}





	@Override
	public <T> Future<T> submitTask(Task<T> task) {
		return executorServiceTaskSubmission.submit(task.taskAction());
	}

}
