package Gs_Data.project.com.Repositories;

import Gs_Data.project.com.Entities.Ressource;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RessourceRepository extends JpaRepository<Ressource, Long> {
    // query to find items where the name matches the search text (case-insensitive)
    List<Ressource> findByNomContainingIgnoreCase(String searchText);

    // query to find items where the name categorie matches the search text (case-insensitive)
    List<Ressource> findByCategorie_NomContainingIgnoreCase(String searchText);
}
