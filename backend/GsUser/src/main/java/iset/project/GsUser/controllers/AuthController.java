package iset.project.GsUser.controllers;

import iset.project.GsUser.entities.User;
import iset.project.GsUser.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            return ResponseEntity.ok(user.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Utilisateur non trouvé");
        }
    }
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
    public ResponseEntity<?> login(@RequestBody User user, HttpSession session) {
        User existingUser = userRepository.findByEmail(user.getEmail()).orElse(null);

        if (existingUser == null || !existingUser.getPassword().equals(user.getPassword())) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Identifiants invalides !"));
        }

        // Enregistrer l'ID utilisateur dans la session
        session.setAttribute("userId", existingUser.getId());

        // Retourner un message et l'ID utilisateur
        return ResponseEntity.ok(new LoginResponse("Connexion réussie !", existingUser.getId()));
    }

    @GetMapping("/current-user")
    public ResponseEntity<?> getCurrentUser(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Aucun utilisateur authentifié !"));
        }

        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            return ResponseEntity.ok(user.get());
        } else {
            return ResponseEntity.status(404).body(new ErrorResponse("Utilisateur introuvable !"));
        }
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping("/logout")
    public ResponseEntity<SuccessResponse> logout(HttpSession session) {
        session.invalidate();  // Détruire la session
        return ResponseEntity.ok(new SuccessResponse("Déconnexion réussie !"));
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

    // Classes de réponse
    public class SuccessResponse {

        private String message;

        // Constructeur
        public SuccessResponse(String message) {
            this.message = message;
        }

        // Getter
        public String getMessage() {
            return message;
        }

        // Setter
        public void setMessage(String message) {
            this.message = message;
        }
    }


    public static class ErrorResponse {
        private String error;

        public ErrorResponse(String error) {
            this.error = error;
        }

        public String getError() {
            return error;
        }
    }

    public static class ApiResponse {
        private String message;

        public ApiResponse(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }

    public static class LoginResponse {
        private String message;
        private Long userId;

        public LoginResponse(String message, Long userId) {
            this.message = message;
            this.userId = userId;
        }

        public String getMessage() {
            return message;
        }

        public Long getUserId() {
            return userId;
        }
    }
}
