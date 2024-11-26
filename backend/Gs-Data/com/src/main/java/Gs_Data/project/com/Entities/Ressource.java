package Gs_Data.project.com.Entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class Ressource {
    @Id
    private Long id;

    private String nom;
    private String description;

    @ManyToOne
    @JoinColumn(name = "categorie_id")
    private Categorie categorie;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    @JsonManagedReference
    private FileMetaData fileMetaData;

  //  @ManyToOne
   // @JoinColumn(name = "user_id") // Association avec l'utilisateur
    private Long UserId=1L;

    @CreationTimestamp
    private LocalDateTime creeLe;

    @UpdateTimestamp
    private LocalDateTime modifieLe;

    private String status = "en_attente"; // Statut par défaut
}
