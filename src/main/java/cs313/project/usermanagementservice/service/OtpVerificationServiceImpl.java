package cs313.project.usermanagementservice.service;

import cs313.project.usermanagementservice.model.OtpVerification;
import cs313.project.usermanagementservice.model.User;
import cs313.project.usermanagementservice.repository.IOtpVerification;
import cs313.project.usermanagementservice.repository.IUserRepository;
import cs313.project.usermanagementservice.payload.RequestVerifyOtp;
import cs313.project.usermanagementservice.payload.ResponseVerifyOtp;
import cs313.project.usermanagementservice.service.interfaceservice.IOtpVerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OtpVerificationServiceImpl implements IOtpVerificationService {
    private final IOtpVerification otpVerification;
    private final IUserRepository userRepository;

    public OtpVerification saveOtp(OtpVerification Otp) {
        return otpVerification.save(Otp);
    }

    public ResponseVerifyOtp verifyOtp(RequestVerifyOtp requestVerifyOtp) {
        Optional<OtpVerification> otpObjectOptional = otpVerification.findById(requestVerifyOtp.getOtpId());

        return otpObjectOptional.map(otpVerification -> {
            LocalDateTime currentTime = LocalDateTime.now();
            if (otpVerification.getExpiryTime().isBefore(currentTime)) {
                return new ResponseVerifyOtp(403, "OTP has expired");
            }

            if (!otpVerification.getOTP().equals(requestVerifyOtp.getOTP())) {
                return new ResponseVerifyOtp(403, "Wrong OTP");
            }

            User user = otpVerification.getUser();
            user.setIsVerified(true);
            userRepository.save(user);

            return new ResponseVerifyOtp(200, null);
        }).orElse(new ResponseVerifyOtp(404, "Cannot find OTP in database"));
    }

}
