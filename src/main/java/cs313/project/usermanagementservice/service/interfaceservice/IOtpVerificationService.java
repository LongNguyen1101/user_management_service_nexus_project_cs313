package cs313.project.usermanagementservice.service.interfaceservice;

import cs313.project.usermanagementservice.model.OtpVerification;
import cs313.project.usermanagementservice.payload.RequestVerifyOtp;
import cs313.project.usermanagementservice.payload.ResponseVerifyOtp;

public interface IOtpVerificationService {
    OtpVerification saveOtp(OtpVerification Otp);
    ResponseVerifyOtp verifyOtp(RequestVerifyOtp requestVerifyOtp);
}
