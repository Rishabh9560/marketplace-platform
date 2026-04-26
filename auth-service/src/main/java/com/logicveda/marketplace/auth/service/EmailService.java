package com.logicveda.marketplace.auth.service;

// import com.sendgrid.SendGrid;
// import com.sendgrid.helpers.mail.Email;
// import com.sendgrid.helpers.mail.Mail;
// import com.sendgrid.helpers.mail.Content;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Service for sending emails via SendGrid.
 * TODO: Implement proper email sending when SendGrid is configured
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    // private final SendGrid sendGrid;

    @Value("${app.email.from:noreply@marketplace.logicveda.com}")
    private String fromEmail;

    @Value("${app.frontend.url:http://localhost:5173}")
    private String frontendUrl;

    /**
     * Send email verification link to user.
     * TODO: Implement with proper email service
     */
    public void sendVerificationEmail(String recipientEmail, String verificationToken) {
        log.info("Email service stub: Would send verification email to {}", recipientEmail);
    }

    /**
     * Send password reset link to user.
     * TODO: Implement with proper email service
     */
    public void sendPasswordResetEmail(String recipientEmail, String resetToken) {
        log.info("Email service stub: Would send password reset email to {}", recipientEmail);
    }

    /**
     * Send welcome email after successful signup.
     * TODO: Implement with proper email service
     */
    public void sendWelcomeEmail(String recipientEmail, String fullName) {
        log.info("Email service stub: Would send welcome email to {}", recipientEmail);
    }

    /**
     * Generate HTML for email verification email.
     */
    private String generateVerificationEmailHtml(String email, String verificationLink) {
        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <style>
                        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                        .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                        .header { background: #2563eb; color: white; padding: 20px; text-align: center; border-radius: 5px 5px 0 0; }
                        .content { background: #f9fafb; padding: 20px; border-radius: 0 0 5px 5px; }
                        .button { display: inline-block; background: #2563eb; color: white; padding: 12px 30px; text-decoration: none; border-radius: 5px; margin: 20px 0; }
                        .footer { text-align: center; color: #6b7280; font-size: 12px; margin-top: 20px; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>Verify Your Email</h1>
                        </div>
                        <div class="content">
                            <p>Hello,</p>
                            <p>Thank you for signing up at Marketplace! Please verify your email address to activate your account.</p>
                            <a href="%s" class="button">Verify Email Address</a>
                            <p>If you didn't create this account, please ignore this email.</p>
                            <p>This link expires in 24 hours.</p>
                        </div>
                        <div class="footer">
                            <p>&copy; 2026 Marketplace Platform. All rights reserved.</p>
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(verificationLink);
    }

    /**
     * Generate HTML for password reset email.
     */
    private String generatePasswordResetEmailHtml(String email, String resetLink) {
        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <style>
                        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                        .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                        .header { background: #f59e0b; color: white; padding: 20px; text-align: center; border-radius: 5px 5px 0 0; }
                        .content { background: #f9fafb; padding: 20px; border-radius: 0 0 5px 5px; }
                        .button { display: inline-block; background: #f59e0b; color: white; padding: 12px 30px; text-decoration: none; border-radius: 5px; margin: 20px 0; }
                        .warning { background: #fef3c7; border-left: 4px solid #f59e0b; padding: 15px; margin: 20px 0; }
                        .footer { text-align: center; color: #6b7280; font-size: 12px; margin-top: 20px; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>Reset Your Password</h1>
                        </div>
                        <div class="content">
                            <p>Hello,</p>
                            <p>We received a request to reset your password. Click the button below to create a new password.</p>
                            <a href="%s" class="button">Reset Password</a>
                            <div class="warning">
                                <strong>Security Notice:</strong> If you didn't request this password reset, please ignore this email. This link expires in 1 hour.
                            </div>
                        </div>
                        <div class="footer">
                            <p>&copy; 2026 Marketplace Platform. All rights reserved.</p>
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(resetLink);
    }

    /**
     * Generate HTML for welcome email.
     */
    private String generateWelcomeEmailHtml(String fullName) {
        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <style>
                        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                        .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                        .header { background: linear-gradient(135deg, #2563eb 0%, #1d4ed8 100%); color: white; padding: 40px 20px; text-align: center; border-radius: 5px 5px 0 0; }
                        .content { background: #f9fafb; padding: 20px; border-radius: 0 0 5px 5px; }
                        .features { margin: 30px 0; }
                        .feature { background: white; padding: 15px; margin: 10px 0; border-left: 4px solid #2563eb; border-radius: 3px; }
                        .button { display: inline-block; background: #2563eb; color: white; padding: 12px 30px; text-decoration: none; border-radius: 5px; margin: 20px 0; }
                        .footer { text-align: center; color: #6b7280; font-size: 12px; margin-top: 20px; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>Welcome to Marketplace! 🎉</h1>
                        </div>
                        <div class="content">
                            <p>Hi %s,</p>
                            <p>Welcome to Marketplace Platform! We're excited to have you on board.</p>
                            <div class="features">
                                <div class="feature">
                                    <strong>🛍️ Browse Products</strong><br/>
                                    Explore our vast catalog of products from multiple vendors.
                                </div>
                                <div class="feature">
                                    <strong>💳 Secure Checkout</strong><br/>
                                    Shop with confidence with our secure payment system.
                                </div>
                                <div class="feature">
                                    <strong>📦 Track Orders</strong><br/>
                                    Real-time tracking for all your purchases.
                                </div>
                            </div>
                            <a href="%s" class="button">Start Shopping</a>
                        </div>
                        <div class="footer">
                            <p>&copy; 2026 Marketplace Platform. All rights reserved.</p>
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(fullName, frontendUrl);
    }
}
