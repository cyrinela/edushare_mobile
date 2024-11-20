package iset.project.Gs_User.controllers;

import iset.project.Gs_User.entities.User;
import iset.project.Gs_User.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            // Utiliser un message structuré avec un champ "message"
            return ResponseEntity.badRequest().body(new ApiResponse("Email déjà utilisé !"));
        }
        if (!user.getRole().equals("STUDENT") && !user.getRole().equals("ADMIN")) {
            // Utiliser un message structuré avec un champ "message"
            return ResponseEntity.badRequest().body(new ApiResponse("Rôle invalide !"));
        }
        userRepository.save(user);
        // Utiliser un message structuré avec un champ "message"
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

        // Constructeur
        public ApiResponse(String message) {
            this.message = message;
        }

        // Getter et Setter
        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

}
