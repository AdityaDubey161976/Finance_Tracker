package com.financetracker.notification_service.service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${notification.mail.from}")
    private String fromEmail;

    public void sendHtmlEmail(String toEmail, String subject, String htmlContent){
        try{
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Email sent succesfully to: {}", toEmail);
        }catch(Exception e){
            log.error("Failed to send email to {}: {}", toEmail , e.getMessage());
            throw new RuntimeException("Failed to send email: " + e.getMessage());
        }
    }

    public void sendSimpleEmail(String toEmail, String subject, String text) {
        try{
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject(subject);
            message.setText(text);

            mailSender.send(message);
            log.info("Simple email sent to: {}", toEmail);
        }catch (Exception e){
            log.error("Failed to send email  {}: {}", toEmail, e.getMessage());
            throw new RuntimeException("Failed to send email: " + e.getMessage());
        }
    }

    public String buildBudgetAlertEmail(String userName, String category,
                                        double percentageUsed, String limitAmount,
                                        String spentAmount) {
        String color = percentageUsed >= 100 ? "#e74c3c" : "#f39c12";
        String emoji = percentageUsed >= 100 ? "🚨" : "⚠️";
        String status = percentageUsed >= 100 ? "EXCEEDED" : "WARNING";

        return """
                <html>
                <body style="font-family: Arial, sans-serif; padding: 20px;">
                    <div style="max-width: 600px; margin: auto; border: 1px solid #ddd; border-radius: 8px; padding: 24px;">
                        <h2 style="color: %s;">%s Budget %s</h2>
                        <p>Hi <strong>%s</strong>,</p>
                        <p>Your <strong>%s</strong> budget has reached <strong>%.1f%%</strong>.</p>
                        <table style="width:100%%; border-collapse: collapse; margin: 16px 0;">
                            <tr style="background: #f8f8f8;">
                                <td style="padding: 8px; border: 1px solid #ddd;">Budget Limit</td>
                                <td style="padding: 8px; border: 1px solid #ddd;">₹%s</td>
                            </tr>
                            <tr>
                                <td style="padding: 8px; border: 1px solid #ddd;">Amount Spent</td>
                                <td style="padding: 8px; border: 1px solid #ddd; color: %s;">₹%s</td>
                            </tr>
                        </table>
                        <p style="color: #666; font-size: 13px;">
                            This is an automated alert from your Finance Tracker.
                        </p>
                    </div>
                </body>
                </html>
                """.formatted(color, emoji, status, userName, category,
                percentageUsed, limitAmount, color, spentAmount);
    }
}
