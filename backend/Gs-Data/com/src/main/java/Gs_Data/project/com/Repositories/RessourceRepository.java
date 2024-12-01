package Gs_Data.project.com.Repositories;

import Gs_Data.project.com.dto.ResourceDto;
import Gs_Data.project.com.Entities.Ressource;
import jakarta.annotation.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RessourceRepository extends JpaRepository<Ressource, Long> {
    // Search by resource name starting with the given letters (case-insensitive)
    List<Ressource> findByNomStartingWithIgnoreCase(String nom);

    // Search by category name starting with the given letters (case-insensitive)
    List<Ressource> findByCategorieNomStartingWithIgnoreCase(String categorieNom);

    @Query("SELECT new Gs_Data.project.com.dto.ResourceDto(r.nom, r.description, c.nom, f.fileName) " +
            "FROM Ressource r " +
            "JOIN r.categorie c " +
            "JOIN r.fileMetaData f")
    List<ResourceDto> findAllResourcesWithCategoryAndFile();

    // Méthode pour récupérer les ressources par ID de catégorie
    List<Ressource> findByCategorieId(int categorieId);
    List<Ressource> findByUserId(Long userId);

    long count();

    // Recherche des ressources dont le nom contient la chaîne 'query' (insensible à la casse)


        // Recherche par nom (nom au lieu de name)
        List<Ressource> findByNomContainingIgnoreCase(String query);
        


}
