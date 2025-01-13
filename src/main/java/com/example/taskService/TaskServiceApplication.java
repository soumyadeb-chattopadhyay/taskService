package com.example.taskService;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.example.taskService.Main.Task;
import com.example.taskService.Main.TaskGroup;
import com.example.taskService.Main.TaskType;


@SpringBootApplication
public class TaskServiceApplication {
	
	@Autowired
	static
	TaskSubmission taskSubmission;
	
	private static Integer TASK_LIST_CAPACITY = 10;
	private static Integer CALLABLE_EXECUTION_DURATION_MS = 1000;

	public static void main(String[] args) {
		SpringApplication.run(TaskServiceApplication.class, args);
		
		//create sample 10 tasks for submission
		ConcurrentLinkedQueue<Task<String>>	taskList = new ConcurrentLinkedQueue<Task<String>>();
		
		int  i = 0;
		while(i < TASK_LIST_CAPACITY) {
			UUID uuid = UUID.fromString("84c3d943-a255-4302-8a19-5992726bf2f7");
			Main.TaskGroup taskGoup = new TaskGroup(uuid);
			String callableReturnString = "Task executed "+i+" ";
			Main.Task<String> taskInstance = new Main.Task<String>(UUID.randomUUID(),taskGoup, generateTaskType(), () -> {
				 ConcurrentHashMap<UUID, ReentrantLock> locks = new ConcurrentHashMap<>();
				 ReentrantLock lock = locks.computeIfAbsent(taskGoup.groupUUID(), k -> new ReentrantLock()); 
			        lock.lock(); 
			        try {
			    TimeUnit.MILLISECONDS.sleep(CALLABLE_EXECUTION_DURATION_MS);
			        }catch(InterruptedException e) {
			            Thread.currentThread().interrupt();
			        } finally {
			            lock.unlock();
			        }
			    return callableReturnString.concat("task groudId "+taskGoup.groupUUID());
			}) ;
			taskList.add(taskInstance);
			i++;
		}

		TaskSubmission taskSubmissionInstance = new TaskSubmission();
		taskSubmissionInstance.submitTasks(taskList);
		taskSubmissionInstance.gracefulShutdown();
		
	}
	
	public static TaskType generateTaskType() {
		return TaskType.values()[new Random().nextInt(TaskType.values().length)];	
	}
	
}
