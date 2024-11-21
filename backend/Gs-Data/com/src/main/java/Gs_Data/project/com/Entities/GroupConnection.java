package Gs_Data.project.com.Entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@IdClass(CompositeKey.class)
public class GroupConnection {
    @Id
    @Column(name = "userId",nullable = false)
    private Long userId;

    @Id
    @Column(name = "groupId",nullable = false)
    private Long groupId;
}
