package br.com.thayrandev.todolist.user;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private IUserRepository userRepository;

    @PostMapping("/create")
    public ResponseEntity create(@RequestBody UserModel userModel) {
        var user = this.userRepository.findByUsername(userModel.getUsername());

        if (user != null) {
            System.out.println("Usuário já existe na base de dados");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Usuário já existe");
        }

        var userCreated = this.userRepository.save(userModel);
        return ResponseEntity.status(HttpStatus.CREATED).body(userCreated);
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity update(@PathVariable UUID id, @RequestBody UserModel userModel) {
        Optional<UserModel> optionalUser = this.userRepository.findById(id);

        if (!optionalUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Usuário não existe");
        }

        UserModel user = optionalUser.get();

        if (userModel.getName() != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Não se pode atualizar o UserName");
        }

        user.setName(userModel.getName());
        user.setPassword(userModel.getPassword());

        UserModel userUpdated = this.userRepository.save(user);
        return ResponseEntity.status(HttpStatus.OK).body(userUpdated);
    }

}
