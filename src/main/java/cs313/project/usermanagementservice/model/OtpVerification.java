package cs313.project.usermanagementservice.model;

import lombok.Data;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document("otp_verification")
@Data
public class OtpVerification {
    @Id
    private String otpId;
    @DBRef
    private User user;
    private String OTP;
    private LocalDateTime createdAt;
    private LocalDateTime expiryTime;

    public OtpVerification(User user) {
        this.user = user;
        this.OTP = RandomStringUtils.randomNumeric(6);
        this.createdAt = LocalDateTime.now();
        this.expiryTime = this.createdAt.plusMinutes(5);
    }
}
