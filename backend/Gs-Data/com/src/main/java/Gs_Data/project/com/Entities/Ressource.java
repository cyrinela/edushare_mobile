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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Identifiant unique pour la ressource

    private String nom;
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categorie_id")
    private Categorie categorie; // Relation Many-to-One avec la table Categorie

    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    @JsonManagedReference
    private FileMetaData fileMetaData; // Relation One-to-One avec FileMetaData

    @CreationTimestamp
    private LocalDateTime creeLe; // Date de création de la ressource

    @UpdateTimestamp
    private LocalDateTime modifieLe; // Date de dernière modification de la ressource

    private String status = "en_attente"; // Statut par défaut

    private Long userId; // ID de l'utilisateur qui partage la ressource
}
