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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequestMapping("/ressources")
public class RessourceController {
    @Autowired
    private RessourceService ressourceService;


    @GetMapping("/search")
    public ResponseEntity<List<Ressource>> searchResources(@RequestParam(required = false) String query) {
        List<Ressource> resources = ressourceService.searchResources(query);
        return ResponseEntity.ok(resources);
    }
    @GetMapping
    public List<Ressource> getAll() {
        return ressourceService.findAll();
    }

    @GetMapping("/{id}")
    public Ressource findById(@PathVariable Long id) {
        return ressourceService.findById(id);
    }
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, String>> modify(@RequestBody Ressource ressource, @PathVariable Long id) {
        if (ressourceService.Modify(id, ressource)) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Resource successfully modified");
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }


    @ResponseStatus(HttpStatus.NOT_FOUND)
    public class ResourceNotFoundException extends RuntimeException {
        public ResourceNotFoundException(String message) {
            super(message);
        }
    }



    @DeleteMapping("/{id}")
    public void Delete(@PathVariable("id") Long id) {
        if (ressourceService.Delete(id)) {
            // If necessary, handle any success response here
        } else {
            // Handle the case where the resource was not found
            throw new ResourceNotFoundException("Ressource not found");
        }
    }

    @GetMapping(path = "/download/{id}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long id) {
        GridFsResource ResultFile = ressourceService.downloadFile(id);
        if (ResultFile != null) {
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(ResultFile.getContentType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + ResultFile.getFilename() + "\"")
                    .body(ResultFile);
        }
        return ResponseEntity.status(500).body(null);
    }


    @PostMapping(path = "/add", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE }, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> create(@RequestPart(name = "ressource") String ressourceJson,
                                         @RequestPart(name = "file") MultipartFile file) throws IOException {
        try {
            // Deserialize the ressource JSON to a Ressource object
            ObjectMapper objectMapper = new ObjectMapper();
            Ressource ressource = objectMapper.readValue(ressourceJson, Ressource.class);

            // Check if file is not empty and pass both ressource and file to save method
            if (ressourceService.save(ressource, file)) {
                return ResponseEntity.ok("Ressource saved successfully");
            } else {
                return ResponseEntity.status(500).body("Error occurred while saving the resource");
            }
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Invalid data: " + e.getMessage());
        }
    }
}
