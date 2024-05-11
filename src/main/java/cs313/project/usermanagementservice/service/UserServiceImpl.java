package cs313.project.usermanagementservice.service;

import cs313.project.usermanagementservice.model.OtpVerification;
import cs313.project.usermanagementservice.model.Role;
import cs313.project.usermanagementservice.model.User;
import cs313.project.usermanagementservice.payload.RequestRegistration;
import cs313.project.usermanagementservice.repository.IUserRepository;
import cs313.project.usermanagementservice.payload.RequestLogin;
import cs313.project.usermanagementservice.payload.ResponseLogin;
import cs313.project.usermanagementservice.payload.ResponseRegistration;
import cs313.project.usermanagementservice.service.interfaceservice.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {
    private final IUserRepository userRepository;
    private final OtpVerificationServiceImpl otpVerificationServiceImpl;
    private final EmailServiceImpl emailService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;


    public ResponseRegistration saveUser(RequestRegistration requestRegistration) {
        if (!requestRegistration.getEmail().equals("jjiulmbi@gmail.com")) {
            // Check duplicated Username
            User existingUserByUsername = userRepository.findByUsername(requestRegistration.getUsername());
            if (existingUserByUsername != null) {
                return ResponseRegistration.builder()
                        .otpId(null)
                        .token(null)
                        .errorMessage("Username already exists")
                        .build();
            }

            // Check duplicated Email
            User existingUserByEmail = userRepository.findByEmail(requestRegistration.getEmail());
            if (existingUserByEmail != null) {
                return ResponseRegistration.builder()
                        .otpId(null)
                        .token(null)
                        .errorMessage("Email address already exists")
                        .build();
            }
        }

        User user = User.builder()
                .username(requestRegistration.getUsername())
                .email(requestRegistration.getEmail())
                .password(passwordEncoder.encode(requestRegistration.getPassword()))
                .createdAt(LocalDateTime.now())
                .favoriteGenres(requestRegistration.getFavoriteGenres())
                .isVerified(false)
                .role(Role.USER.toString())
                .build();

        User savedUser = userRepository.save(user);
        OtpVerification otpVerification = otpVerificationServiceImpl.saveOtp(new OtpVerification(savedUser));
        if (otpVerification == null) {
            return ResponseRegistration.builder()
                    .errorMessage("Sorry, The error occurred while creating the otp")
                    .build();
        }

        new Thread(() -> emailService.sendMail(requestRegistration.getEmail(),
                requestRegistration.getUsername(),
                otpVerification.getOTP())).start();

        String jwtToken = jwtService.generateToken(savedUser);

        return ResponseRegistration.builder()
                .otpId(otpVerification.getOtpId())
                .token(jwtToken)
                .errorMessage("null")
                .build();
    }

    public ResponseLogin login(RequestLogin requestLogin) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        requestLogin.getEmail(),
                        requestLogin.getPassword()
                )
        );

        User user = userRepository.findByEmail(requestLogin.getEmail());
        if (user == null) {
            return ResponseLogin.builder()
                    .httpCode(404)
                    .errMessage("User not found")
                    .build();
        }

        String jwtToken = jwtService.generateToken(user);

        if (!user.getIsVerified()) {
            OtpVerification otpVerification = otpVerificationServiceImpl.saveOtp(new OtpVerification(user));

            if (otpVerification == null) {
                return ResponseLogin.builder()
                        .httpCode(403)
                        .errMessage("Sorry, The error occurred while creating the otp")
                        .build();
            }

            new Thread(() -> emailService.sendMail(user.getEmail(),
                    user.getUsername(),
                    otpVerification.getOTP())).start();

            return ResponseLogin.builder()
                    .token(jwtToken)
                    .httpCode(403)
                    .errMessage("Please verify otp")
                    .otpId(otpVerification.getOtpId())
                    .build();
        }

        return ResponseLogin.builder()
                .token(jwtToken)
                .username(user.getUsername())
                .favoriteGenres(user.getFavoriteGenres())
                .role(user.getRole())
                .httpCode(200)
                .build();
    }
}
