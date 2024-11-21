package Gs_Data.project.com.Controllers;

import Gs_Data.project.com.Entities.Categorie;
import Gs_Data.project.com.Entities.Commentaire;
import Gs_Data.project.com.Services.CommentaireService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/commentaires")
public class CommentaireController {
    @Autowired
    private CommentaireService commentaireService;

    @GetMapping
    public List<Commentaire> getAll() {
        return commentaireService.findAll();
    }

    @GetMapping("/{id}")
    public Commentaire findById(@PathVariable Long id) {
        return commentaireService.findById(id);
    }

    @PostMapping("/modify/{id}")
    public ResponseEntity<String> modify(@RequestBody Commentaire commentaire, @PathVariable Long id) {
        if (commentaireService.Modify(id,commentaire)) {
            return ResponseEntity.ok("Commentaire modified");
        }
        return ResponseEntity.status(404).body("error occurred");
    }

    @GetMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        if (commentaireService.Delete(id)) {
            return ResponseEntity.ok("Commentaire deleted");
        }
        return ResponseEntity.status(404).body("error occurred");
    }

    @PostMapping(path = "/add")
    public Commentaire create(@RequestBody Commentaire commentaire) {
        return commentaireService.save(commentaire);
    }
}
