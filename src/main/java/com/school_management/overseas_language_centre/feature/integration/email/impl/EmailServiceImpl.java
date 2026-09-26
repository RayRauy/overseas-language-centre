package com.school_management.overseas_language_centre.feature.integration.email.impl;

import com.school_management.overseas_language_centre.feature.integration.email.EmailService;
import com.school_management.overseas_language_centre.property.OtpProperties;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final OtpProperties otpProperties;
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:no-reply@example.com}")
    private String fromAddress;

    @Override
    public void sendOtp(String toEmail, String otp) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromAddress);
            helper.setTo(toEmail);
            helper.setSubject("Otp for OLC");
            helper.setText(buildHtml(otp), true);
            mailSender.send(message);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to send OTP", e);
        }
    }

    private String buildHtml(String otp) {
        return """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                  <meta charset="UTF-8"/>
                  <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
                  <title>Password Reset Code</title>
                </head>
                
                <body style="
                  margin:0;
                  padding:0;
                  background:#eef2f7;
                  font-family:Arial,Helvetica,sans-serif;
                  color:#1e293b;
                ">
                
                  <table width="100%%" cellpadding="0" cellspacing="0" border="0"
                         style="background:#eef2f7;padding:25px 10px;">
                    <tr>
                      <td align="center">
                
                        <!-- Main Card -->
                        <table width="420" cellpadding="0" cellspacing="0" border="0"
                               style="
                                 max-width:420px;
                                 width:100%%;
                                 background:#ffffff;
                                 border-radius:10px;
                                 overflow:hidden;
                                 box-shadow:0 5px 20px rgba(15,23,42,0.08);
                               ">
                
                          <!-- Header -->
                          <tr>
                            <td style="
                              background:#0f2747;
                              padding:22px 25px;
                              text-align:center;
                            ">
                
                              <div style="
                                width:40px;
                                height:40px;
                                margin:0 auto 10px;
                                background:#1e5aa8;
                                border-radius:50%%;
                                line-height:40px;
                                color:#ffffff;
                                font-size:18px;
                                font-weight:bold;
                              ">
                                🔐
                              </div>
                
                              <h1 style="
                                margin:0;
                                color:#ffffff;
                                font-size:19px;
                                font-weight:700;
                              ">
                                Password Reset
                              </h1>
                
                              <p style="
                                margin:5px 0 0;
                                color:#b9c8dc;
                                font-size:11px;
                              ">
                                Secure verification code
                              </p>
                
                            </td>
                          </tr>
                
                          <!-- Content -->
                          <tr>
                            <td style="padding:25px 28px 24px;">
                
                              <p style="
                                margin:0 0 8px;
                                color:#172b4d;
                                font-size:14px;
                                font-weight:600;
                              ">
                                Hello,
                              </p>
                
                              <p style="
                                margin:0 0 18px;
                                color:#64748b;
                                font-size:12px;
                                line-height:1.6;
                              ">
                                Use the verification code below to continue.
                              </p>
                
                              <!-- OTP Box -->
                              <div style="
                                background:#f3f7fc;
                                border:1px solid #d7e2f0;
                                border-radius:8px;
                                padding:14px 12px;
                                text-align:center;
                                margin-bottom:16px;
                              ">
                
                                <p style="
                                  margin:0 0 5px;
                                  color:#64748b;
                                  font-size:9px;
                                  font-weight:700;
                                  letter-spacing:1.2px;
                                  text-transform:uppercase;
                                ">
                                  Verification Code
                                </p>
                                <div style="
                                                          color:#0f2747;
                                                          font-size:28px;
                                                          font-weight:800;
                                                          letter-spacing:6px;
                                                          line-height:1.2;
                                                        ">
                                                          %s
                                                        </div>
                
                                                      </div>
                
                                                      <!-- Expiration -->
                                                      <table width="100%%" cellpadding="0" cellspacing="0" border="0"
                                                             style="
                                                               background:#fff8e6;
                                                               border-left:3px solid #e5a900;
                                                               border-radius:5px;
                                                               margin-bottom:16px;
                                                             ">
                                                        <tr>
                                                          <td style="
                                                            padding:9px 11px;
                                                            color:#795b00;
                                                            font-size:11px;
                                                            line-height:1.4;
                                                          ">
                                                            <strong>Important:</strong>
                                                            This code expires in %d minutes.
                                                          </td>
                                                        </tr>
                                                      </table>
                
                                                      <p style="
                                                        margin:0;
                                                        color:#64748b;
                                                        font-size:11px;
                                                        line-height:1.6;
                                                      ">
                                                        Never share this code with anyone.
                                                        Our team will never ask for it.
                                                      </p>
                
                                                      <p style="
                                                        margin:14px 0 0;
                                                        color:#94a3b8;
                                                        font-size:10px;
                                                        line-height:1.5;
                                                      ">
                                                        If you did not request a password reset,
                                                        please ignore this email.
                                                      </p>
                
                                                    </td>
                                                  </tr>
                
                                                  <!-- Footer -->
                                                  <tr>
                                                    <td style="
                                                      background:#f8fafc;
                                                      border-top:1px solid #e8edf3;
                                                      padding:14px 25px;
                                                      text-align:center;
                                                    ">
                
                                                      <p style="
                                                        margin:0;
                                                        color:#64748b;
                                                        font-size:10px;
                                                        line-height:1.5;
                                                      ">
                                                        This is an automated message. Please do not reply.
                                                      </p>
                
                                                      <p style="
                                                        margin:5px 0 0;
                                                        color:#0f2747;
                                                        font-size:10px;
                                                        font-weight:600;
                                                      ">
                                                        Overseas Language Centre
                                                      </p>
                
                                                    </td>
                                                  </tr>
                
                                                </table>
                
                                                <!-- Bottom Text -->
                                                <p style="
                                                  margin:12px 0 0;
                                                  color:#94a3b8;
                                                  font-size:9px;
                                                  text-align:center;
                                                ">
                                                  © Overseas Language Centre. All rights reserved.
                                                </p>
                
                                              </td>
                                            </tr>
                                          </table>
                
                                        </body>
                                        </html>
                """.formatted(otp, otpProperties.getTtlMinutes());
    }

    ;
}
