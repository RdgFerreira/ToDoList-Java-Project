package br.com.rodrigoaraujo.todolist.task;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.rodrigoaraujo.todolist.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private ITaskRepository taskRepository;
    
    @PostMapping("/create")
    public ResponseEntity create(@RequestBody TaskModel taskModel, HttpServletRequest request) {
        // Loads user id from request filter
        taskModel.setIdUser((UUID) request.getAttribute("idUser"));

        var currentDate = LocalDateTime.now();
        if(currentDate.isAfter(taskModel.getStartAt()) || currentDate.isAfter(taskModel.getEndAt())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Task must start and end after now");
        }
        else if(taskModel.getStartAt().isAfter(taskModel.getEndAt())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Task start date must come before task end date");
        }

        var task = this.taskRepository.save(taskModel);
        return ResponseEntity.status(HttpStatus.CREATED).body(task);
    }

    @GetMapping("/getFromUser")
    public ResponseEntity list(HttpServletRequest request) {
        var idUser = request.getAttribute("idUser");
        var tasks = this.taskRepository.findByIdUser((UUID) idUser);
        return ResponseEntity.status(HttpStatus.OK).body(tasks);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity update(@PathVariable UUID taskId, @RequestBody TaskModel taskModel, HttpServletRequest request) {
        var task = this.taskRepository.findById(taskId).orElse(null);

        if(task == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Task not found");
        }

        var idUser = request.getAttribute("idUser");

        if(!task.getIdUser().equals(idUser)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User not authorized to modify this task");
        }

        // keep other properties null so they are not altered in the database
        Utils.copyNonNullProperties(taskModel, task);

        var taskUpdated = this.taskRepository.save(task);
        return ResponseEntity.status(HttpStatus.OK).body(taskUpdated);
    }
}
