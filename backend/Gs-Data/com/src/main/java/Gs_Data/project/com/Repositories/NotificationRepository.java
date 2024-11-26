package Gs_Data.project.com.Repositories;

import Gs_Data.project.com.Entities.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserId(Long userId); // Récupérer les notifications par utilisateur
}
