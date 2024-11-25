package Gs_Data.project.com.Services;

import Gs_Data.project.com.Entities.FileMetaData;
import Gs_Data.project.com.dto.ResourceDto;
import Gs_Data.project.com.Entities.Ressource;
import Gs_Data.project.com.Repositories.FileMetaDataRepository;
import Gs_Data.project.com.Repositories.RessourceRepository;
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
import java.util.List;
import java.util.Optional;

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

    public List<Ressource> findAll() {
        return ressourceRepository.findAll();
    }

    public Ressource findById(Long id) {
        return ressourceRepository.findById(id).orElse(null);
    }

    public boolean Modify(Long id, Ressource ressource) {
        Ressource r = findById(id);
        if (r != null) {
            // dateCreation & dateModification are constant
            r.setNom(ressource.getNom());
            r.setDescription(ressource.getDescription());
            r.setCategorie(ressource.getCategorie());

            // Save the updated resource
            ressourceRepository.save(r);
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


    public Boolean save(Ressource ressource,MultipartFile file) throws IOException {
        String FileUrlId = uploadFileData(file); // upload File to mongodb
        FileMetaData savedMeta = uploadFileMeta(file,FileUrlId); // upload FileMeta to MySQL
        if (savedMeta != null && FileUrlId != null) {
            ressource.setFileMetaData(savedMeta); // link fileMeta to ressource table
            ressourceRepository.save(ressource);
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

    public List<Ressource> searchResources(String query) {
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


}