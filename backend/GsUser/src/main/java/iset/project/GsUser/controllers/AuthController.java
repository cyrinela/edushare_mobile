package iset.project.GsUser.controllers;

import iset.project.GsUser.entities.User;
import iset.project.GsUser.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body(new ApiResponse("Email déjà utilisé !"));
        }
        if (!user.getRole().equals("STUDENT") && !user.getRole().equals("ADMIN")) {
            return ResponseEntity.badRequest().body(new ApiResponse("Rôle invalide !"));
        }
        userRepository.save(user);
        return ResponseEntity.ok(new ApiResponse("Utilisateur enregistré avec succès !"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        User existingUser = userRepository.findByEmail(user.getEmail()).orElse(null);

        if (existingUser == null || !existingUser.getPassword().equals(user.getPassword())) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Identifiants invalides !"));
        }

        return ResponseEntity.ok(new SuccessResponse("Connexion réussie !"));
    }

    @GetMapping("/users/email/{email}")
    public ResponseEntity<?> getUserByEmail(@PathVariable String email) {
        Optional<User> user = userRepository.findByEmail(email);

        if (user.isPresent()) {
            return ResponseEntity.ok(user.get()); // Retourne directement l'utilisateur trouvé
        } else {
            return ResponseEntity.status(404).body(new ErrorResponse("Utilisateur introuvable avec cet email."));
        }
    }

    // SuccessResponse class
    public class SuccessResponse {
        private String message;

        public SuccessResponse(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }

    // ErrorResponse class
    public class ErrorResponse {
        private String error;

        public ErrorResponse(String error) {
            this.error = error;
        }

        public String getError() {
            return error;
        }
    }

    public class ApiResponse {
        private String message;

        public ApiResponse(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
