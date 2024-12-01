package Gs_Data.project.com.Services;

import Gs_Data.project.com.Entities.FileMetaData;
import Gs_Data.project.com.Entities.Notification;
import Gs_Data.project.com.Repositories.NotificationRepository;
import Gs_Data.project.com.dto.ResourceDto;
import Gs_Data.project.com.Entities.Ressource;
import Gs_Data.project.com.Repositories.FileMetaDataRepository;
import Gs_Data.project.com.Repositories.RessourceRepository;
import Gs_Data.project.com.feign.UserFeignClient;
import com.mongodb.client.gridfs.model.GridFSFile;
import jakarta.annotation.Resource;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class RessourceService {
    @Autowired
    private RessourceRepository ressourceRepository;
    @Autowired
    private FileMetaDataRepository fileMetaDataRepository;
    @Autowired
    private GridFsTemplate gridFsTemplate;
    @Autowired
    private GridFsOperations gridFsOperations;


    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserFeignClient userFeignClient;


    public List<Ressource> findAll() {
        return ressourceRepository.findAll();
    }

    public Ressource findById(Long id) {
        return ressourceRepository.findById(id).orElse(null);
    }
    public boolean Modify(Long id, Ressource ressource) {
        Ressource existingRessource = findById(id);
        if (existingRessource != null) {
            // Normalisation du statut pour éviter les erreurs d'espace ou de casse
            String newStatus = ressource.getStatus().trim().toLowerCase();
            String existingStatus = existingRessource.getStatus().trim().toLowerCase();

            boolean statusChanged = !existingStatus.equals(newStatus);

            // Vérifier si d'autres champs ont changé
            boolean otherFieldsChanged =
                    !existingRessource.getNom().equals(ressource.getNom()) ||
                            !existingRessource.getDescription().equals(ressource.getDescription()) ||
                            (existingRessource.getCategorie() == null ? ressource.getCategorie() != null
                                    : !existingRessource.getCategorie().equals(ressource.getCategorie()));

            // Mise à jour des champs
            existingRessource.setNom(ressource.getNom());
            existingRessource.setDescription(ressource.getDescription());
            existingRessource.setCategorie(ressource.getCategorie());
            existingRessource.setStatus(newStatus);  // Sauvegarde du statut normalisé

            // Sauvegarder la ressource mise à jour
            ressourceRepository.save(existingRessource);

            // Notification si le statut a changé
            if (statusChanged) {
                String message = String.format(
                        "Votre ressource '%s' a été %s.",
                        existingRessource.getNom(),
                        newStatus.equals("accepté") ? "acceptée" : "refusée"
                );

                Notification notification = new Notification();
                notification.setUserId(existingRessource.getUserId());
                notification.setMessage(message);
                notification.setStatus("UNREAD");
                notification.setDate(LocalDateTime.now());
                notificationRepository.save(notification);
            }

            // Notification si d'autres champs ont changé
            if (otherFieldsChanged) {
                String message = String.format(
                        "Il y a un changement dans la ressource '%s'.",
                        existingRessource.getNom()
                );

                Notification notification = new Notification();
                notification.setUserId(existingRessource.getUserId());
                notification.setMessage(message);
                notification.setStatus("UNREAD");
                notification.setDate(LocalDateTime.now());
                notificationRepository.save(notification);
            }

            return true;
        }
        return false;
    }







    public boolean Delete(Long id) {
        Ressource r = findById(id);
        if (r != null) {
            FileMetaData FileMeta = r.getFileMetaData();

            gridFsTemplate.delete(new Query(Criteria.where("_id").is(new ObjectId(FileMeta.getFileUrlId())))); // delete file from mongoDB
            fileMetaDataRepository.deleteById(FileMeta.getId()); // delete FileMeta & ressource rows
            return true;
        }
        return false;
    }


    public Boolean save(Ressource ressource, MultipartFile file) throws IOException {
        String FileUrlId = uploadFileData(file); // Upload du fichier dans MongoDB
        FileMetaData savedMeta = uploadFileMeta(file, FileUrlId); // Enregistrement des métadonnées dans MySQL

        if (savedMeta != null && FileUrlId != null) {
            ressource.setFileMetaData(savedMeta); // Lier les métadonnées à la ressource
            ressourceRepository.save(ressource); // Enregistrer la ressource avec userId
            return true;
        }
        return false;
    }


    FileMetaData uploadFileMeta(MultipartFile file,String FileUrlId) {
        try {

        FileMetaData meta = new FileMetaData();

        meta.setFileName(file.getOriginalFilename());
        meta.setFileType(file.getContentType());
        meta.setFileSize(file.getSize());
        meta.setFileUrlId(FileUrlId);

        fileMetaDataRepository.save(meta);

        return meta;
        }
        catch (Exception e) {
            return null;
        }
    }

    String uploadFileData(MultipartFile upload) throws IOException {

        Object fileID = gridFsTemplate.store(upload.getInputStream(), upload.getOriginalFilename(), upload.getContentType());
        return fileID.toString();
    }

    public GridFsResource downloadFile(Long RessourceId) {
        Ressource r = findById(RessourceId);
        if (r != null) {
            FileMetaData FileMeta = r.getFileMetaData();

            GridFSFile file = gridFsTemplate.findOne(new Query(Criteria.where("_id").is(FileMeta.getFileUrlId())));

            return gridFsOperations.getResource(file);
        }
        return null;
    }

    public List<Ressource> searchResources(String query, Boolean searchCategorie) {
        if (query == null || query.trim().isEmpty()) {
            return ressourceRepository.findAll();  // Return all if the query is empty
        }

        // Search resources by name or category name
        List<Ressource> resourcesByName = ressourceRepository.findByNomStartingWithIgnoreCase(query);
        List<Ressource> resourcesByCategory = ressourceRepository.findByCategorieNomStartingWithIgnoreCase(query);

        // Combine results (you could remove duplicates if necessary)
        resourcesByName.addAll(resourcesByCategory);
        return resourcesByName;
    }

    public List<ResourceDto> getAllResources() {
        return ressourceRepository.findAllResourcesWithCategoryAndFile();
    }

    // Méthode pour récupérer les ressources par ID de catégorie
    // Méthode pour récupérer les ressources par ID de catégorie
    public List<Ressource> getResourcesByCategory(int categoryId) {
        // Utilisation du repository pour récupérer les ressources associées à la catégorie
        return ressourceRepository.findByCategorieId(categoryId);
    }

    public List<Ressource> findByUserId(Long userId) {
        return ressourceRepository.findByUserId(userId); // Assurez-vous que le repository dispose de cette méthode.
    }

    public long getTotalResourcesCount() {
        return ressourceRepository.count(); // Compte toutes les ressources
    }

    public FileMetaData getFileMetaDataByResourceId(Long resourceId) {
        Ressource ressource = findById(resourceId);
        if (ressource != null) {
            return ressource.getFileMetaData();
        }
        return null;
    }

    public List<Ressource> searchResourcesByName(String query) {
        if (query == null || query.isEmpty()) {
            return ressourceRepository.findAll();  // Si la requête est vide, retourne toutes les ressources
        } else {
            return ressourceRepository.findByNomContainingIgnoreCase(query); // Recherche par nom
        }
    }


}