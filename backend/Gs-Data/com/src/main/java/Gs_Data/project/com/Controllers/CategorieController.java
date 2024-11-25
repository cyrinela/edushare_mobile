package Gs_Data.project.com.Controllers;

import Gs_Data.project.com.Entities.Categorie;
import Gs_Data.project.com.Services.CategorieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategorieController {
    @Autowired
    private CategorieService categorieService;

    @GetMapping
    public List<Categorie> getAll() {
        return categorieService.findAll();
    }

    @GetMapping("/{id}")
    public Categorie findById(@PathVariable Long id) {
        return categorieService.findById(id);
    }

    @PostMapping("/modify/{id}")
    public ResponseEntity<String> modify(@RequestBody Categorie categorie,@PathVariable Long id) {
        if (categorieService.Modify(id,categorie)) {
            return ResponseEntity.ok("Categorie modified");
        }
        return ResponseEntity.status(404).body("error occurred");
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        if (categorieService.Delete(id)) {
            return ResponseEntity.ok("Categorie deleted");
        }
        return ResponseEntity.status(404).body("Error occurred");
    }


    /*@PostMapping(path = "/add")
    public Categorie create(@RequestBody Categorie categorie) {
        return categorieService.save(categorie);
    }*/
    @PostMapping
    public ResponseEntity<Categorie> addCategory(@RequestBody Categorie categorie) {
        Categorie createdCategory = categorieService.save(categorie);
        return new ResponseEntity<>(createdCategory, HttpStatus.CREATED);
    }


    @PutMapping("/update/{id}")
    public ResponseEntity<String> updateCategory(@PathVariable Long id, @RequestBody Categorie category) {
        if (categorieService.Modify(id, category)) {
            return ResponseEntity.ok("Catégorie mise à jour avec succès");
        }
        return ResponseEntity.status(404).body("Erreur lors de la mise à jour");
    }

}
