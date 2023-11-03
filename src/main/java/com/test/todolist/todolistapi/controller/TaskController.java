package com.test.todolist.todolistapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.test.todolist.todolistapi.contant.UrlConstant;
import com.test.todolist.todolistapi.model.ResponseModel;
import com.test.todolist.todolistapi.model.Task;
import com.test.todolist.todolistapi.service.TaskService;

@Controller
@RequestMapping(UrlConstant.ROOT)
public class TaskController {

	@Autowired
	private TaskService taskService;

	@GetMapping(UrlConstant.ALL_TASKS)
	public ResponseEntity<ResponseModel> getAllTasks() {
		return ResponseEntity.ok().body(taskService.getAllTask());
	}

	@GetMapping(UrlConstant.COMPLETED_TASKS)
	public ResponseEntity<ResponseModel> getAllCompletedTasks() {
		return ResponseEntity.ok().body(taskService.findAllCompletedTask());
	}

	@GetMapping(UrlConstant.IN_COMPLETE_TASKS)
	public ResponseEntity<ResponseModel> getAllIncompleteTasks() {
		return ResponseEntity.ok().body(taskService.findAllInCompleteTask());
	}

	@PostMapping(UrlConstant.CREATE_TASK)
	public ResponseEntity<ResponseModel> createTask(@RequestBody Task task) {
		return ResponseEntity.ok().body(taskService.createNewTask(task));
	}

	//Using @PathVariable
	@GetMapping(UrlConstant.GET_TASK_BY_ID + "/{id}")
	public ResponseEntity<ResponseModel> getTaskById(@PathVariable Long id) {
		return ResponseEntity.ok().body(taskService.findTaskById(id));
	}

	//Using @RequestParam
	@GetMapping(UrlConstant.GET_TASKS_BY_USER)
	public ResponseEntity<ResponseModel> getTaskByUser(@RequestParam String owner) {
		return ResponseEntity.ok().body(taskService.findTaskByUser(owner));
	}

	@PutMapping(UrlConstant.UPDATE_TASK + "/{id}")
	public ResponseEntity<ResponseModel> updateTask(@PathVariable Long id, @RequestBody Task task) {
		return ResponseEntity.ok().body(taskService.updateTask(task, id));
	}

	@DeleteMapping(UrlConstant.DELETE_TASK + "/{id}")
	public ResponseEntity<ResponseModel> deleteTask(@PathVariable Long id) {
		return ResponseEntity.ok().body(taskService.deleteTask(id));
	}
}
