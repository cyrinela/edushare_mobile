package Gs_Data.project.com.Entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "fileMetaData")
public class FileMetaData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fileName;
    @Column(nullable = false)
    private String fileType;
    @Column(nullable = false)
    private Long fileSize;

    private String fileUrlId;

    @OneToOne(mappedBy = "fileMetaData", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    private Ressource ressource;
}
