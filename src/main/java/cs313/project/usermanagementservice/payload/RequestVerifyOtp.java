package cs313.project.usermanagementservice.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RequestVerifyOtp {
    String otpId;
    String OTP;
}
