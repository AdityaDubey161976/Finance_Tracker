package com.financetracker.notification_service.dto;

import com.financetracker.notification_service.model.Notification;
import com.financetracker.notification_service.model.Notification.NotificationType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequest {

    @NotNull(message = "UserId is required")
    private Long userId;

    @Email(message = "Valid email required")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Subject is required")
    private String subject;

    @NotBlank(message = "Message is requried")
    private String message;

    @NotNull(message = "Type is required")
    private NotificationType type;
}
