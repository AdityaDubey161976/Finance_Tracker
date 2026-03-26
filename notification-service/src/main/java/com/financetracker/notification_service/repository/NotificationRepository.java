package com.financetracker.notification_service.repository;

import com.financetracker.notification_service.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification,Long> {

    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<Notification> findTop10ByUserIdOrderByCreatedAtDesc(Long userId);

    @Query("SELECT COUNT(n) > 0 FROM Notification n WHERE n.userId = :userId " +
           "AND n.subject = :subject " +
           "AND n.createdAt BETWEEN CURRENT_DATE AND CURRENT_TIMESTAMP" +
           "AND n.status = 'SENT'")
    boolean existsTodayByUserIdAndSubject(
            @Param("userId") Long userId,
            @Param("subject") String subject
    );
}
