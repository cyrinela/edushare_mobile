package Gs_Data.project.com.Entities;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
@Entity
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;          // ID de l'utilisateur lié
    private String title;         // Titre de la notification
    private String message;       // Message principal
    private String status;        // "READ" ou "UNREAD"
    private LocalDateTime date;   // Date de création
}
