package com.test.todolist.todolistapi.service.impl;

import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.test.todolist.todolistapi.contant.ResponseStatus;
import com.test.todolist.todolistapi.model.ResponseModel;
import com.test.todolist.todolistapi.model.Task;
import com.test.todolist.todolistapi.service.TaskService;

@Service
public class TaskServiceImpl implements TaskService {

	private final static Logger logger = Logger.getLogger(TaskServiceImpl.class);
	private static final String PATH_TO_TASKS_FILE = System.getProperty("user.dir") + "\\src\\files\\tasks.json";

	@Override
	public ResponseModel createNewTask(Task task) {
		ResponseModel response = new ResponseModel();
		File file = new File(PATH_TO_TASKS_FILE);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				logger.error("Error while creating json tasks file: " + e.toString());
				response.setStatus(ResponseStatus.FAIL);
				response.setMessage("Error while creating json tasks file");
				return response;
			}
		}
		try {
			// Read JSON to get current tasks
			List<Task> tasks = readTasksFromJsonFile();
			Long id = 1L;
			if (tasks == null) {
				tasks = new ArrayList<Task>();
			} else {
				id = tasks.get(tasks.size() - 1).getId() + 1;
			}
			task.setId(id);
			tasks.add(task);

			// Write tasks to JSON file
			writeTasksToJsonFile(tasks);

			logger.info("Task is created successfully!");
			// Set response for API
			response.setStatus(ResponseStatus.SUCCESS);
			response.setMessage("Task is created successfully!");
			response.setData(task);
		} catch (FileNotFoundException | EOFException e) {
			logger.error("Error while reading json tasks file: " + e.toString());
			response.setStatus(ResponseStatus.FAIL);
			response.setMessage("Error while reading json tasks file");
			return response;
		} catch (JsonIOException | IOException e) {
			logger.error("Error while writing json tasks file: " + e.toString());
			response.setStatus(ResponseStatus.FAIL);
			response.setMessage("Error while reading json tasks file");
			return response;
		}

		return response;
	}

	@Override
	public ResponseModel getAllTask() throws JsonSyntaxException {
		ResponseModel response = new ResponseModel();
		File file = new File(PATH_TO_TASKS_FILE);
		if (!file.exists()) {
			logger.info("No tasks file!");
			response.setStatus(ResponseStatus.SUCCESS);
			response.setMessage("No tasks!");
			return response;
		}
		try {
			// Read JSON to get current tasks
			List<Task> tasks = readTasksFromJsonFile();
			if (tasks == null) {
				response.setStatus(ResponseStatus.SUCCESS);
				response.setMessage("No tasks!");
				return response;
			}
			List<Task> finalTasks = tasks.stream().filter(t -> (!t.isDeleted())).collect(Collectors.toList());
			logger.info("Get all tasks successfully!");
			// Set response for API
			response.setStatus(ResponseStatus.SUCCESS);
			response.setMessage("Get all tasks successfully!");
			response.setData(finalTasks);
		} catch (JsonSyntaxException e) {
			logger.error("Error while reading json tasks file: " + e.toString());
			response.setStatus(ResponseStatus.FAIL);
			response.setMessage("Error while reading json tasks file");
			return response;
		} catch (IOException e) {
			logger.error("Error while reading json tasks file: " + e.toString());
			response.setStatus(ResponseStatus.FAIL);
			response.setMessage("Error while reading json tasks file");
			return response;
		}

		return response;
	}

	@Override
	public ResponseModel findTaskById(Long id) {
		ResponseModel response = new ResponseModel();
		File file = new File(PATH_TO_TASKS_FILE);
		if (!file.exists()) {
			logger.info("No tasks file!");
			response.setStatus(ResponseStatus.SUCCESS);
			response.setMessage("No tasks!");
			return response;
		}
		try {
			// Read JSON to get current tasks
			List<Task> tasks = readTasksFromJsonFile();
			if (tasks == null) {
				response.setStatus(ResponseStatus.SUCCESS);
				response.setMessage("No tasks!");
				return response;
			}
			Task task = tasks.stream().filter(t -> (t.getId().equals(id) && !t.isDeleted())).findAny().orElse(null);
			if (task == null)
			{
				response.setStatus(ResponseStatus.SUCCESS);
				response.setMessage("No task with id: " + id);
				return response;
			}
			logger.info("Get task successfully!");
			// Set response for API
			response.setStatus(ResponseStatus.SUCCESS);
			response.setMessage("Get task successfully!");
			response.setData(task);
		} catch (JsonSyntaxException e) {
			logger.error("Error while reading json tasks file: " + e.toString());
			response.setStatus(ResponseStatus.FAIL);
			response.setMessage("Error while reading json tasks file");
			return response;
		} catch (IOException e) {
			logger.error("Error while reading json tasks file: " + e.toString());
			response.setStatus(ResponseStatus.FAIL);
			response.setMessage("Error while reading json tasks file");
			return response;
		}

		return response;
	}
	
	@Override
	public ResponseModel findTaskByUser(String owner) {
		ResponseModel response = new ResponseModel();
		File file = new File(PATH_TO_TASKS_FILE);
		if (!file.exists()) {
			logger.info("No tasks file!");
			response.setStatus(ResponseStatus.SUCCESS);
			response.setMessage("No tasks!");
			return response;
		}
		try {
			// Read JSON to get current tasks
			List<Task> tasks = readTasksFromJsonFile();
			if (tasks == null) {
				response.setStatus(ResponseStatus.SUCCESS);
				response.setMessage("No tasks!");
				return response;
			}
			List<Task> finalTasks = tasks.stream().filter(t -> (t.getOwner().equals(owner) && !t.isDeleted())).collect(Collectors.toList());
			if (finalTasks.isEmpty())
			{
				response.setStatus(ResponseStatus.SUCCESS);
				response.setMessage("No task with user: " + owner);
				return response;
			}
			logger.info("Get task successfully!");
			// Set response for API
			response.setStatus(ResponseStatus.SUCCESS);
			response.setMessage("Get task successfully!");
			response.setData(finalTasks);
		} catch (JsonSyntaxException e) {
			logger.error("Error while reading json tasks file: " + e.toString());
			response.setStatus(ResponseStatus.FAIL);
			response.setMessage("Error while reading json tasks file");
			return response;
		} catch (IOException e) {
			logger.error("Error while reading json tasks file: " + e.toString());
			response.setStatus(ResponseStatus.FAIL);
			response.setMessage("Error while reading json tasks file");
			return response;
		}

		return response;
	}

	@Override
	public ResponseModel findAllCompletedTask() {
		ResponseModel response = new ResponseModel();
		File file = new File(PATH_TO_TASKS_FILE);
		if (!file.exists()) {
			logger.info("No tasks file!");
			response.setStatus(ResponseStatus.SUCCESS);
			response.setMessage("No tasks!");
			return response;
		}
		try {
			// Read JSON to get current tasks
			List<Task> tasks = readTasksFromJsonFile();
			if (tasks == null) {
				response.setStatus(ResponseStatus.SUCCESS);
				response.setMessage("No tasks!");
				return response;
			}
			List<Task> completedTasks = tasks.stream().filter(t -> (t.isCompleted() && !t.isDeleted())).collect(Collectors.toList());
			logger.info("Get completed tasks successfully!");
			// Set response for API
			response.setStatus(ResponseStatus.SUCCESS);
			response.setMessage("Get completed tasks successfully!");
			response.setData(completedTasks);
		} catch (JsonSyntaxException e) {
			logger.error("Error while reading json tasks file: " + e.toString());
			response.setStatus(ResponseStatus.FAIL);
			response.setMessage("Error while reading json tasks file");
			return response;
		} catch (IOException e) {
			logger.error("Error while reading json tasks file: " + e.toString());
			response.setStatus(ResponseStatus.FAIL);
			response.setMessage("Error while reading json tasks file");
			return response;
		}

		return response;
	}

	@Override
	public ResponseModel findAllInCompleteTask() {
		ResponseModel response = new ResponseModel();
		File file = new File(PATH_TO_TASKS_FILE);
		if (!file.exists()) {
			logger.info("No tasks file!");
			response.setStatus(ResponseStatus.SUCCESS);
			response.setMessage("No tasks!");
			return response;
		}
		try {
			// Read JSON to get current tasks
			List<Task> tasks = readTasksFromJsonFile();
			if (tasks == null) {
				response.setStatus(ResponseStatus.SUCCESS);
				response.setMessage("No tasks!");
				return response;
			}
			List<Task> completedTasks = tasks.stream().filter(t -> (!t.isCompleted() && !t.isDeleted())).collect(Collectors.toList());
			logger.info("Get incomplete tasks successfully!");
			// Set response for API
			response.setStatus(ResponseStatus.SUCCESS);
			response.setMessage("Get incomplete tasks successfully!");
			response.setData(completedTasks);
		} catch (JsonSyntaxException e) {
			logger.error("Error while reading json tasks file: " + e.toString());
			response.setStatus(ResponseStatus.FAIL);
			response.setMessage("Error while reading json tasks file");
			return response;
		} catch (IOException e) {
			logger.error("Error while reading json tasks file: " + e.toString());
			response.setStatus(ResponseStatus.FAIL);
			response.setMessage("Error while reading json tasks file");
			return response;
		}

		return response;
	}

	@Override
	public ResponseModel deleteTask(Long id) {
		ResponseModel response = new ResponseModel();
		File file = new File(PATH_TO_TASKS_FILE);
		if (!file.exists()) {
			logger.info("No tasks file!");
			response.setStatus(ResponseStatus.SUCCESS);
			response.setMessage("No tasks!");
			return response;
		}
		try {
			// Read JSON to get current tasks
			List<Task> tasks = readTasksFromJsonFile();
			if (tasks == null) {
				response.setStatus(ResponseStatus.SUCCESS);
				response.setMessage("No tasks!");
				return response;
			}
			Task task = tasks.stream().filter(t -> (t.getId().equals(id) && !t.isDeleted())).findAny().orElse(null);
			if (task == null)
			{
				response.setStatus(ResponseStatus.SUCCESS);
				response.setMessage("No task with id: " + id);
				return response;
			}
			for (Task currentTask : tasks) {
				if (currentTask.getId().equals(id)) {
					currentTask.setDeleted(true);
					break;
				}
			}
			// Write tasks to JSON file
			writeTasksToJsonFile(tasks);
			logger.info("Delete task successfully!");
			// Set response for API
			response.setStatus(ResponseStatus.SUCCESS);
			response.setMessage("Delete task successfully!");
		} catch (JsonSyntaxException e) {
			logger.error("Error while reading json tasks file: " + e.toString());
			response.setStatus(ResponseStatus.FAIL);
			response.setMessage("Error while reading json tasks file");
			return response;
		} catch (IOException e) {
			logger.error("Error while reading json tasks file: " + e.toString());
			response.setStatus(ResponseStatus.FAIL);
			response.setMessage("Error while reading json tasks file");
			return response;
		}

		return response;
	}

	@Override
	public ResponseModel updateTask(Task task, Long id) {
		ResponseModel response = new ResponseModel();
		File file = new File(PATH_TO_TASKS_FILE);
		if (!file.exists()) {
			logger.info("No tasks file!");
			response.setStatus(ResponseStatus.SUCCESS);
			response.setMessage("No tasks!");
			return response;
		}
		try {
			// Read JSON to get current tasks
			Gson gson = new Gson();
			JsonReader reader = new JsonReader(new FileReader(PATH_TO_TASKS_FILE));
			List<Task> tasks = gson.fromJson(reader, new TypeToken<List<Task>>() {}.getType());
			reader.close();
			if (tasks == null) {
				response.setStatus(ResponseStatus.SUCCESS);
				response.setMessage("No tasks!");
				return response;
			}
			Task currentTask = tasks.stream().filter(t -> (t.getId().equals(id) && !t.isDeleted())).findAny().orElse(null);
			if (currentTask == null)
			{
				response.setStatus(ResponseStatus.SUCCESS);
				response.setMessage("No task with id: " + id);
				return response;
			}
			for (Task curTask : tasks) {
				if (curTask.getId().equals(id)) {
					curTask.setTaskName(task.getTaskName());
					curTask.setUpdatedDate(task.getUpdatedDate());
					curTask.setCompleted(task.isCompleted());
					task = curTask;
					break;
				}
			}
			// Write tasks to JSON file
			writeTasksToJsonFile(tasks);
			logger.info("Update task successfully!");
			// Set response for API
			response.setStatus(ResponseStatus.SUCCESS);
			response.setMessage("Update task successfully!");
			response.setData(task);
		} catch (JsonSyntaxException e) {
			logger.error("Error while reading json tasks file: " + e.toString());
			response.setStatus(ResponseStatus.FAIL);
			response.setMessage("Error while reading json tasks file");
			return response;
		} catch (IOException e) {
			logger.error("Error while reading json tasks file: " + e.toString());
			response.setStatus(ResponseStatus.FAIL);
			response.setMessage("Error while reading json tasks file");
			return response;
		}

		return response;
	}

	private List<Task> readTasksFromJsonFile() throws JsonSyntaxException, IOException {
		logger.info("Reading tasks from json file...");
		Gson gson = new Gson();
		JsonReader reader = new JsonReader(new FileReader(PATH_TO_TASKS_FILE));
		List<Task> tasks = gson.fromJson(reader, new TypeToken<List<Task>>() {}.getType());
		reader.close();
		logger.info("Read tasks from json file...");
		return tasks;
	}
	
	private void writeTasksToJsonFile(List<Task> tasks) throws IOException {
		logger.info("Writing tasks to json file...");
		Gson gson = new Gson();
		Writer writer = new FileWriter(PATH_TO_TASKS_FILE);
		gson.toJson(tasks, writer);
		writer.close();
		logger.info("Wrote tasks to json file...");
	}
}
