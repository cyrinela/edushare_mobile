package Gs_Data.project.com.Controllers;
import Gs_Data.project.com.Entities.Ressource;
import Gs_Data.project.com.Services.RessourceService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/ressources")
public class RessourceController {

    @Autowired
    private RessourceService ressourceService;

    @GetMapping("/search")
    public ResponseEntity<List<Ressource>> searchResources(
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "false", required = false) Boolean searchCategorie) {
        List<Ressource> resources = ressourceService.searchResources(query, searchCategorie);
        return ResponseEntity.ok(resources);
    }

    @GetMapping
    public ResponseEntity<List<Ressource>> getAll() {
        List<Ressource> ressources = ressourceService.findAll();
        if (ressources.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        }
        return ResponseEntity.ok(ressources);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ressource> findById(@PathVariable Long id) {
        Ressource ressource = ressourceService.findById(id);
        if (ressource != null) {
            return ResponseEntity.ok(ressource);
        }
        throw new ResourceNotFoundException("Ressource not found with ID: " + id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, String>> modify(@RequestBody Ressource ressource, @PathVariable Long id) {
        if (ressourceService.Modify(id, ressource)) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Resource successfully modified");
            return ResponseEntity.ok(response);
        }
        throw new ResourceNotFoundException("Ressource not found with ID: " + id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable Long id) {
        if (ressourceService.Delete(id)) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Ressource successfully deleted");
            return ResponseEntity.ok(response);
        }
        throw new ResourceNotFoundException("Ressource not found with ID: " + id);
    }

    @GetMapping(path = "/download/{id}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long id) {
        GridFsResource resultFile = ressourceService.downloadFile(id);
        if (resultFile != null) {
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(resultFile.getContentType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resultFile.getFilename() + "\"")
                    .body(resultFile);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null);
    }

    @PostMapping(path = "/add", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> create(@RequestPart(name = "ressource") String ressourceJson,
                                         @RequestPart(name = "file") MultipartFile file) {
        try {
            // Vérifier que le fichier n'est pas vide
            if (file.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("File is required and cannot be empty");
            }

            // Désérialiser le JSON de la ressource en objet Ressource
            ObjectMapper objectMapper = new ObjectMapper();
            Ressource ressource = objectMapper.readValue(ressourceJson, Ressource.class);

            // Vérifier si l'ID utilisateur est présent, sinon utiliser un ID par défaut (par exemple, 1)
            if (ressource.getUserId() == null) {
                ressource.setUserId(1L);  // ID utilisateur par défaut
            }

            // Sauvegarder la ressource et le fichier
            boolean isSaved = ressourceService.save(ressource, file, ressource.getUserId());
            if (isSaved) {
                return ResponseEntity.ok("Ressource saved successfully");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Error occurred while saving the resource");
            }
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid data format: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred: " + e.getMessage());
        }
    }




    // Exception class for handling resource not found error
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public static class ResourceNotFoundException extends RuntimeException {
        public ResourceNotFoundException(String message) {
            super(message);
        }
    }
}
