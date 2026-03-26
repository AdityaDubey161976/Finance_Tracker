package com.financetracker.notification_service.controller;

import com.financetracker.notification_service.dto.NotificationRequest;
import com.financetracker.notification_service.dto.NotificationResponse;
import com.financetracker.notification_service.repository.NotificationRepository;
import com.financetracker.notification_service.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class NotificationController {

    private final NotificationService notificationService;
    private final NotificationRepository notificationRepository;

    @PostMapping("/send")
    public ResponseEntity<NotificationResponse> sendNotification(
            @Valid @RequestBody NotificationRequest request){
        NotificationResponse response = notificationService.sendNotification(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getNotifications(@RequestHeader("X-User-Id") Long userId){

        List<NotificationResponse> notifications =  notificationService.getRecentNotifications(userId);
        return ResponseEntity.ok(notifications);

    }

    @GetMapping("/recent")
    public ResponseEntity<List<NotificationResponse>> getRecentNotification(@RequestHeader("X-User-Id") Long userId){

        List<NotificationResponse> notifications = notificationService.getRecentNotifications(userId);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/check")
    public ResponseEntity<Boolean> checkAlreadySent(
            @RequestParam Long userId,
            @RequestParam String subject){
        boolean exists = notificationRepository.existsTodayByUserIdAndSubject(userId, subject);
        return ResponseEntity.ok(exists);
    }
}
