package Gs_Data.project.com.Services;

import Gs_Data.project.com.Entities.Categorie;
import Gs_Data.project.com.Entities.Commentaire;
import Gs_Data.project.com.Repositories.CommentaireRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentaireService {
    @Autowired
    private CommentaireRepository commentaireRepository;

    public List<Commentaire> findAll() {
        return commentaireRepository.findAll();
    }

    public Commentaire findById(Long id) {
        return commentaireRepository.findById(id).orElse(null);
    }

    public boolean Modify(Long id,Commentaire commentaire) {
        Commentaire c = findById(id);
        if (c != null) {
            // utilisateurID & dateCreation sont constant!
            c.setContenu(commentaire.getContenu());
            c.setRessource(commentaire.getRessource());
            return true;
        }
        return false;
    }

    public boolean Delete(Long id) {
        Commentaire c = findById(id);
        if (c != null) {
            commentaireRepository.deleteById(c.getId());
            return true;
        }
        return false;
    }

    public Commentaire save(Commentaire commentaire) {
        return commentaireRepository.save(commentaire);
    }
}
