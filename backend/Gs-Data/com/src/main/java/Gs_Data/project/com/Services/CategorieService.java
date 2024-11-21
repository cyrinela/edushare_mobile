package Gs_Data.project.com.Services;

import Gs_Data.project.com.Entities.Categorie;
import Gs_Data.project.com.Repositories.CategorieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategorieService {
    @Autowired
    private CategorieRepository categorieRepository;

    public List<Categorie> findAll() {
        return categorieRepository.findAll();
    }

    public Categorie findById(Long id) {
        return categorieRepository.findById(id).orElse(null);
    }

    public boolean Modify(Long id,Categorie categorie) {
        Categorie c = findById(id);
        if (c != null) {
            c.setNom(categorie.getNom());
            c.setDescription(categorie.getDescription());
            categorieRepository.save(c);
            return true;
        }
        return false;
    }

    public boolean Delete(Long id) {
        Categorie c = findById(id);
        if (c != null) {
            categorieRepository.deleteById(c.getId());
            return true;
        }
        return false;
    }

    public Categorie save(Categorie categorie) {
        return categorieRepository.save(categorie);
    }
}
