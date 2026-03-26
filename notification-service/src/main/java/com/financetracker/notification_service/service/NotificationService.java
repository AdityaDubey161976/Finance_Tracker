package com.financetracker.notification_service.service;

import com.financetracker.notification_service.dto.NotificationRequest;
import com.financetracker.notification_service.dto.NotificationResponse;
import com.financetracker.notification_service.model.Notification;
import com.financetracker.notification_service.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final EmailService emailService;

    public NotificationResponse sendNotification(NotificationRequest request){
        Notification notification = new Notification();
        notification.setUserId(request.getUserId());
        notification.setEmail(request.getEmail());
        notification.setSubject(request.getSubject());
        notification.setMessage(request.getMessage());
        notification.setType(request.getType());

        try{
            emailService.sendHtmlEmail(request.getEmail(),
                    request.getSubject(),
                    request.getMessage());

            notification.setStatus(Notification.NotificationStatus.SENT);
            log.info("Notification set to userId: {} at {}", request.getUserId(),request.getEmail());
        }catch (Exception e) {
            notification.setStatus(Notification.NotificationStatus.FAILED);
            log.error("Failed to send notification to {}: {}", request.getEmail(), e.getMessage());
        }

        Notification saved = notificationRepository.save(notification);
        return mapToResponse(saved);
    }

    public List<NotificationResponse> getNotification(Long userId){
        return notificationRepository
                .findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    public List<NotificationResponse> getRecentNotifications(Long userId){
        return notificationRepository
                .findTop10ByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private NotificationResponse mapToResponse(Notification n){
        return new NotificationResponse(
                n.getId(),
                n.getSubject(),
                n.getMessage(),
                n.getType(),
                n.getStatus(),
                n.getCreatedAt()
        );
    }
}
