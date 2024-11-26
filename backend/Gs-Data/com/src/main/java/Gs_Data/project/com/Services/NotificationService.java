package Gs_Data.project.com.Services;

import Gs_Data.project.com.Entities.Notification;
import Gs_Data.project.com.Repositories.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    // Récupérer les notifications d'un utilisateur
    public List<Notification> getNotificationsByUser(Long userId) {
        return notificationRepository.findByUserId(userId);
    }

    // Ajouter une nouvelle notification
    public Notification addNotification(Notification notification) {
        notification.setDate(LocalDateTime.now()); // Date de la notification
        notification.setStatus("UNREAD"); // Statut initial
        return notificationRepository.save(notification);
    }

    // Marquer une notification comme lue
    public Notification markAsRead(Long id) {
        Optional<Notification> notificationOptional = notificationRepository.findById(id);
        if (notificationOptional.isPresent()) {
            Notification notification = notificationOptional.get();
            notification.setStatus("READ");
            return notificationRepository.save(notification);
        } else {
            throw new RuntimeException("Notification not found");
        }
    }

    // Supprimer une notification
    public void deleteNotification(Long id) {
        notificationRepository.deleteById(id);
    }
}
