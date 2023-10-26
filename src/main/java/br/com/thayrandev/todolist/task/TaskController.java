package br.com.thayrandev.todolist.task;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private ITaskRepository taskRepository;

    @PostMapping("/create")
    public ResponseEntity create(@RequestBody TaskModel taskModel, HttpServletRequest request){
        System.out.println(request.getAttribute("userId"));

        // var userId = UUID.fromString(request.getAttribute("userId").toString());
        var userId = request.getAttribute("userId");
        taskModel.setUserId( (UUID) userId);

        var currentDate = LocalDateTime.now();

        if(taskModel.getStartAt().isBefore(currentDate) || currentDate.isAfter(taskModel.getEndAt())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("A data de ínício/término deve ser maior que a data atual");
        }

        if(taskModel.getStartAt().isAfter(taskModel.getEndAt())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("A data de ínício deve ser menor que a data de término");
        }

        var task = this.taskRepository.save(taskModel);
        return ResponseEntity.status(HttpStatus.OK).body(task);
    }

    @GetMapping("/getall")
    public ResponseEntity getAllTasks(){
        List<TaskModel> tasks = this.taskRepository.findAll();

        if(tasks.isEmpty()){
            return ResponseEntity.status(HttpStatus.OK)
                .body("Não há nenhuma tarefa cadastrada");
        }
        return ResponseEntity.status(HttpStatus.OK).body(tasks);
    }

    @GetMapping("/getByuserId")
    public ResponseEntity getByuserId(HttpServletRequest request){
        var userId = request.getAttribute("userId");

        List<TaskModel> tasks = this.taskRepository.findAllByuserId((UUID)userId);

        if(tasks == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Não há nenhuma tarefa cadastrada para este usuário");
        }

        return ResponseEntity.ok().body(tasks);
    }
}
