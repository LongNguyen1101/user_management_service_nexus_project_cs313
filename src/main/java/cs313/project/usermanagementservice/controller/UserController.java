package cs313.project.usermanagementservice.controller;

import cs313.project.usermanagementservice.payload.*;
import cs313.project.usermanagementservice.service.OtpVerificationServiceImpl;
import cs313.project.usermanagementservice.service.UserServiceImpl;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserServiceImpl userServiceImpl;
    private final OtpVerificationServiceImpl otpVerificationServiceImpl;

    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created", content =  @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = ResponseRegistration.class)))
            ),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/register")
    public ResponseEntity<?> saveUser(@RequestBody RequestRegistration requestUser) {
        ResponseRegistration responseRegistration = userServiceImpl.saveUser(requestUser);

        if (responseRegistration.getToken() == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(responseRegistration.getErrorMessage());
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(responseRegistration);
    }


    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Verify successfully"),
            @ApiResponse(responseCode = "403", description = "Wrong OTP || OTP has expired"),
            @ApiResponse(responseCode = "404", description = "Cannot find OTP in database"),
    })
    @PatchMapping("/verify_otp")
    public ResponseEntity<?> verifyOtp(@RequestBody RequestVerifyOtp requestVerifyOtp) {
        ResponseVerifyOtp responseVerifyOtp = otpVerificationServiceImpl.verifyOtp(requestVerifyOtp);

        if (responseVerifyOtp.getErrorMessage() != null) {
            return ResponseEntity.status(responseVerifyOtp.getHttpCode())
                    .body(responseVerifyOtp.getErrorMessage());
        }

        return ResponseEntity.status(responseVerifyOtp.getHttpCode()).body("Verify successfully");
    }


    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful", content =  @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = ResponseLogin.class)))
            ),
            @ApiResponse(responseCode = "403", description = "Please verify otp", content =  @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = ResponseLogin.class)))
            ),
            @ApiResponse(responseCode = "404", description ="User not found"),
            @ApiResponse(responseCode = "500", description ="The error occurred while creating the otp"),
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody RequestLogin requestLogin) {
        ResponseLogin responseLogin = userServiceImpl.login(requestLogin);

        if (responseLogin.getToken() == null) {
            return ResponseEntity.status(responseLogin.getHttpCode()).body(responseLogin.getErrMessage());
        }

        return ResponseEntity.status(responseLogin.getHttpCode()).body(responseLogin);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Chat-bot response successfully", content =  @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = ResponseChatBot.class)))
            ),
            @ApiResponse(responseCode = "404", description ="User not found"),
            @ApiResponse(responseCode = "500", description ="The error occurred while creating the otp"),
    })
    @PostMapping("/chat-bot")
    public ResponseEntity<?> chatBot(@RequestBody RequestChat requestChat) {

        ResponseChatBot response = userServiceImpl.chatBot(requestChat);

       if (response.getHttpCode() != 202) {
           return ResponseEntity.status(response.getHttpCode()).body(response.getErrMessage());
       }

       return ResponseEntity.status(HttpStatus.ACCEPTED).body(response.getResponse());
    }
}
