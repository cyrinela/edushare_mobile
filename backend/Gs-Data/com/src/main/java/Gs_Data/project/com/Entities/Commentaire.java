package Gs_Data.project.com.Entities;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class Commentaire {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String contenu;
    @CreationTimestamp
    private LocalDateTime dateCreation;

    @ManyToOne
    @JoinColumn(name = "ressource_id")
    private Ressource ressource;

    private Long utilisateurId = 1L; // ID utilisateur statique

}
