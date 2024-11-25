package Gs_Data.project.com.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResourceDto {
    private String resourceName;
    private String description;
    private String categoryName;
    private String fileName;

    @JsonCreator
    public ResourceDto(
            @JsonProperty("resourceName") String resourceName,
            @JsonProperty("description") String description,
            @JsonProperty("categoryName") String categoryName,
            @JsonProperty("fileName") String fileName) {
        this.resourceName = resourceName;
        this.description = description;
        this.categoryName = categoryName;
        this.fileName = fileName;
    }
}
