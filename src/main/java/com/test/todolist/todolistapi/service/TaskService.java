package com.test.todolist.todolistapi.service;

import com.test.todolist.todolistapi.model.ResponseModel;
import com.test.todolist.todolistapi.model.Task;

public interface TaskService {
	public ResponseModel createNewTask(Task task);
      
    public ResponseModel getAllTask();
      
    public ResponseModel findTaskById(Long id);
    
    public ResponseModel findTaskByUser(String owner);
      
    public ResponseModel findAllCompletedTask();
      
    public ResponseModel findAllInCompleteTask();
      
    public ResponseModel deleteTask(Long id);
      
    public ResponseModel updateTask(Task task, Long id);
}
