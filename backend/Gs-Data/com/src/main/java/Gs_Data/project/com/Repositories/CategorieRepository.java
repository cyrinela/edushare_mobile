package Gs_Data.project.com.Repositories;

import Gs_Data.project.com.Entities.Categorie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

public interface CategorieRepository extends JpaRepository<Categorie, Long> {
}
